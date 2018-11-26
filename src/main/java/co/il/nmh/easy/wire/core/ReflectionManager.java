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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

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
	private Map<String, Map<String, List<BeanInformation>>> packageBeansByNameMap; // basePackage -> bean name -> bean information
	private Map<String, Map<Class<?>, List<Class<?>>>> implementingClassesMapByPackage; // cache for the search - basePackage -> class -> implementing beans

	public ReflectionManager()
	{
		reflectionsByPackageMap = new ConcurrentHashMap<>();
		packageBeanInformationMap = new ConcurrentHashMap<>();
		packageBeansByNameMap = new ConcurrentHashMap<>();
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

					try
					{
						beans.addAll(reflections.getTypesAnnotatedWith(Named.class, true));
					}
					catch (NoClassDefFoundError e)
					{
					}

					try
					{
						beans.addAll(reflections.getTypesAnnotatedWith(RestController.class, true));
					}
					catch (NoClassDefFoundError e)
					{
					}

					try
					{
						beans.addAll(reflections.getTypesAnnotatedWith(Component.class, true));
						beans.addAll(reflections.getTypesAnnotatedWith(Controller.class, true));
						beans.addAll(reflections.getTypesAnnotatedWith(Repository.class, true));
						beans.addAll(reflections.getTypesAnnotatedWith(Service.class, true));
						beans.addAll(reflections.getTypesAnnotatedWith(Configuration.class, true));
					}
					catch (NoClassDefFoundError e)
					{
					}

					Map<String, List<BeanInformation>> beansByNames = new HashMap<>();

					Map<Class<?>, BeanInformation> beanInformationMap = new HashMap<>();

					for (Class<?> beanClass : beans)
					{
						if (!beanInformationMap.containsKey(beanClass))
						{
							BeanInformation beanInformation = new BeanInformation(beanClass);
							beanInformationMap.put(beanClass, beanInformation);

							saveBeanName(beanClass, beansByNames, beanInformation);
						}
					}

					for (Method beanMethod : beanMethods)
					{
						Class<?> returnType = beanMethod.getReturnType();

						if (!beanInformationMap.containsKey(returnType))
						{
							beanInformationMap.put(returnType, new BeanInformation(returnType));
						}

						BeanInformation beanInformation = beanInformationMap.get(returnType);
						beanInformation.addMethod(beanMethod);

						saveBeanName(beanMethod, beansByNames, beanInformation);
					}

					packageBeanInformationMap.put(basePackage, beanInformationMap);
					packageBeansByNameMap.put(basePackage, beansByNames);
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

					String[] basePackages = basePackage.split(",");

					for (String currBasePackage : basePackages)
					{
						urls.addAll(ClasspathHelper.forPackage(currBasePackage));
					}

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

	private void saveBeanName(Object object, Map<String, List<BeanInformation>> beansByNames, BeanInformation beanInformation)
	{
		String beanName = null;

		if (object instanceof Class<?>)
		{
			Class<?> clazz = (Class<?>) object;
			beanName = getClassBeanName(clazz);

			if (null == beanName || beanName.isEmpty())
			{
				beanName = clazz.getSimpleName();
				beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1, beanName.length());
			}
		}

		else if (object instanceof Method)
		{
			Method method = (Method) object;
			String[] beanNames = getMethodBeanName(method);

			if (null == beanNames || beanNames.length < 1)
			{
				beanName = method.getName();
				beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1, beanName.length());
			}
			else
			{
				for (String name : beanNames)
				{
					if (!beansByNames.containsKey(name))
					{
						beansByNames.put(name, new ArrayList<>());
					}

					beansByNames.get(name).add(beanInformation);
				}

				return;
			}
		}

		if (!beansByNames.containsKey(beanName))
		{
			beansByNames.put(beanName, new ArrayList<>());
		}

		beansByNames.get(beanName).add(beanInformation);
	}

	private String getClassBeanName(Class<?> clazz)
	{
		try
		{
			Qualifier qualifier = clazz.getAnnotation(Qualifier.class);

			if (null != qualifier)
			{
				return qualifier.value();
			}
		}
		catch (NoClassDefFoundError e)
		{
		}

		try
		{
			Named named = clazz.getAnnotation(Named.class);

			if (null != named)
			{
				return named.value();
			}
		}
		catch (NoClassDefFoundError e)
		{
		}

		try
		{
			RestController restController = clazz.getAnnotation(RestController.class);

			if (null != restController)
			{
				return restController.value();
			}
		}
		catch (NoClassDefFoundError e)
		{
		}

		try
		{
			Component component = clazz.getAnnotation(Component.class);

			if (null != component)
			{
				return component.value();
			}

			Controller controller = clazz.getAnnotation(Controller.class);

			if (null != controller)
			{
				return controller.value();
			}

			Repository repository = clazz.getAnnotation(Repository.class);

			if (null != repository)
			{
				return repository.value();
			}

			Service service = clazz.getAnnotation(Service.class);

			if (null != service)
			{
				return service.value();
			}
		}
		catch (NoClassDefFoundError e)
		{
		}

		return null;
	}

	private String[] getMethodBeanName(Method method)
	{
		try
		{
			Qualifier qualifier = method.getAnnotation(Qualifier.class);

			if (null != qualifier)
			{
				return new String[] { qualifier.value() };
			}
		}
		catch (NoClassDefFoundError e)
		{
		}

		Bean bean = method.getAnnotation(Bean.class);

		if (null != bean)
		{
			return bean.name();
		}

		return null;
	}

	public Map<String, List<BeanInformation>> getBeansByName(String basePackage)
	{
		return packageBeansByNameMap.get(basePackage);
	}
}
