package co.il.nmh.easy.wire.core;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import co.il.nmh.easy.wire.EasywireRunner;
import co.il.nmh.easy.wire.data.BeanInformation;
import co.il.nmh.easy.wire.exception.EasywireException;

/**
 * @author Maor Hamami
 */

public class ReflectionManager
{
	private Map<String, Reflections> reflectionsByPackageMap;
	private Map<String, Map<Class<?>, BeanInformation>> packageBeanInformationMap; // basePackage -> class -> bean information
	private Map<String, Map<Class<?>, List<Class<?>>>> implementingClassesMapByPackage; // cache for the search - basePackage -> class -> implementing beans

	public ReflectionManager()
	{
		reflectionsByPackageMap = new ConcurrentHashMap<>();
		packageBeanInformationMap = new ConcurrentHashMap<>();
		implementingClassesMapByPackage = new ConcurrentHashMap<>();
	}

	public Map<Class<?>, BeanInformation> getBeans(String basePackage)
	{
		if (!packageBeanInformationMap.containsKey(basePackage))
		{
			synchronized (packageBeanInformationMap)
			{
				if (!packageBeanInformationMap.containsKey(basePackage))
				{
					Reflections reflections = getReflections(basePackage);

					Set<Method> beanMethods = reflections.getMethodsAnnotatedWith(Bean.class);

					Set<Class<?>> beans = new HashSet<>();
					beans.addAll(reflections.getTypesAnnotatedWith(Named.class, true));
					beans.addAll(reflections.getTypesAnnotatedWith(Component.class, true));
					beans.addAll(reflections.getTypesAnnotatedWith(Controller.class, true));
					beans.addAll(reflections.getTypesAnnotatedWith(Repository.class, true));
					beans.addAll(reflections.getTypesAnnotatedWith(Service.class, true));
					beans.addAll(reflections.getTypesAnnotatedWith(Configuration.class, true));

					Map<Class<?>, BeanInformation> beanInformationMap = new HashMap<>();

					for (Class<?> beanClass : beans)
					{
						if (!beanInformationMap.containsKey(beanClass))
						{
							beanInformationMap.put(beanClass, new BeanInformation(beanClass));
						}
					}

					for (Method beanMethod : beanMethods)
					{
						Class<?> returnType = beanMethod.getReturnType();

						if (!beanInformationMap.containsKey(returnType))
						{
							beanInformationMap.put(returnType, new BeanInformation(returnType));
						}

						beanInformationMap.get(returnType).addMethod(beanMethod);
					}

					packageBeanInformationMap.put(basePackage, beanInformationMap);
				}
			}
		}

		return packageBeanInformationMap.get(basePackage);
	}

	public List<Class<?>> findImplementingClasses(Class<?> clazz, String basePackage)
	{
		if (!implementingClassesMapByPackage.containsKey(basePackage))
		{
			synchronized (implementingClassesMapByPackage)
			{
				if (!implementingClassesMapByPackage.containsKey(basePackage))
				{
					implementingClassesMapByPackage.put(basePackage, new ConcurrentHashMap<>());
				}
			}
		}

		Map<Class<?>, List<Class<?>>> implementingClassesMap = implementingClassesMapByPackage.get(basePackage);

		if (!implementingClassesMap.containsKey(clazz))
		{
			synchronized (implementingClassesMap)
			{
				if (!implementingClassesMap.containsKey(clazz))
				{
					List<Class<?>> results = new ArrayList<>();

					Set<?> subTypesOf = getReflections(basePackage).getSubTypesOf(clazz);

					for (Object object : subTypesOf)
					{
						if (object instanceof Class<?>)
						{
							results.add((Class<?>) object);
						}
					}

					implementingClassesMap.put(clazz, results);

					return results;
				}
			}
		}

		return implementingClassesMap.get(clazz);
	}

	public Reflections getReflections(String basePackage)
	{
		if (null == basePackage)
		{
			throw new EasywireException("basePackage must be set on the EasywireBeanFactory, consider using your top package.");
		}

		if (!reflectionsByPackageMap.containsKey(basePackage))
		{
			synchronized (reflectionsByPackageMap)
			{
				if (!reflectionsByPackageMap.containsKey(basePackage))
				{
					Collection<URL> urls = new HashSet<>();

					urls.addAll(ClasspathHelper.forPackage(basePackage));

					// adding easy-wire base path
					urls.addAll(ClasspathHelper.forPackage(EasywireRunner.class.getPackage().getName()));

					ConfigurationBuilder reflectionsConfiguration = ConfigurationBuilder.build();
					reflectionsConfiguration.addScanners(new MethodAnnotationsScanner());
					reflectionsConfiguration.setUrls(urls);

					Reflections reflections = new Reflections(reflectionsConfiguration);
					reflectionsByPackageMap.put(basePackage, reflections);
				}
			}
		}

		return reflectionsByPackageMap.get(basePackage);
	}
}
