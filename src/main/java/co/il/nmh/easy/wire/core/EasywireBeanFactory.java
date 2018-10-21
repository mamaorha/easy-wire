package co.il.nmh.easy.wire.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;

import co.il.nmh.easy.utils.reflection.FieldsInvestigator;
import co.il.nmh.easy.wire.core.base.BeanFactoryStub;
import co.il.nmh.easy.wire.core.utils.BeanProvider;
import co.il.nmh.easy.wire.core.utils.LoggerTest;
import co.il.nmh.easy.wire.core.utils.properties.PropertyManager;
import co.il.nmh.easy.wire.core.utils.properties.PropertyManager.PropertyValue;
import co.il.nmh.easy.wire.data.BasePackageMap;
import co.il.nmh.easy.wire.data.BeanInformation;
import co.il.nmh.easy.wire.data.OrderObject;
import co.il.nmh.easy.wire.exception.EasywireBeanNotFoundException;
import co.il.nmh.easy.wire.exception.EasywireException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Maor Hamami
 */

@Slf4j
public class EasywireBeanFactory extends BeanFactoryStub
{
	public static final EasywireBeanFactory INSTANCE = new EasywireBeanFactory();

	private String basePackage = null;
	private BasePackageMap<BeanHolder> beanOverrideMap;
	private BasePackageMap<Boolean> prepareMap;
	private Map<Class<?>, Boolean> beanInfoMap; // class -> is bean annotated
	private ReflectionManager reflectionManager;
	private PropertyManager propertyManager;

	private LoggerTest loggerTest;
	private FieldsInvestigator fieldsInvestigator;

	public EasywireBeanFactory()
	{
		beanOverrideMap = new BasePackageMap<>();
		beanInfoMap = new ConcurrentHashMap<>();
		prepareMap = new BasePackageMap<>();
		reflectionManager = new ReflectionManager();
		propertyManager = new PropertyManager();

		loggerTest = new LoggerTest();
		fieldsInvestigator = FieldsInvestigator.INSTANCE;

		clean();
	}

	public void clean()
	{
		if (null != basePackage)
		{
			beanOverrideMap.remove(basePackage);
			prepareMap.remove(basePackage);

			Map<Class<?>, BeanInformation> beans = reflectionManager.getBeans(basePackage);

			if (null != beans)
			{
				for (BeanInformation beanInformation : beans.values())
				{
					beanInformation.clear();
				}
			}
		}
	}

	public void buildPropertySources(String propertyFile)
	{
		propertyManager.buildPropertySources(propertyFile, null);
	}

	public void buildPropertySources(String propertyFile, String profile)
	{
		propertyManager.buildPropertySources(propertyFile, profile);
	}

	public final Properties getProperties()
	{
		return propertyManager.getProperties();
	}

	public String getPropertyValue(String value)
	{
		PropertyValue propertyValue = propertyManager.getPropertyValue(value);

		if (null != propertyValue)
		{
			return propertyValue.getValue();
		}

		return null;
	}

	public String getLoadedProfile()
	{
		return propertyManager.getLoadedProfile();
	}

	public void setBasePackage(String basePackage)
	{
		if (null == basePackage || basePackage.equals(this.basePackage))
		{
			return;
		}

		this.basePackage = basePackage;

		pushBean(ApplicationContext.class, this, false);
		pushBean(FieldsInvestigator.class, fieldsInvestigator, false);
		pushBean(LoggerTest.class, loggerTest, false);
	}

	public void overrideBean(Class<?> clazz, Class<?> overridingClass)
	{
		Object bean = initializeClass(overridingClass, false, new LinkedHashSet<>());
		pushBean(clazz, bean);
	}

	public void pushBean(Class<?> clazz, Object bean)
	{
		pushBean(clazz, bean, true);
	}

	public void pushBean(Class<?> clazz, Object bean, boolean force)
	{
		if (!clazz.isInstance(bean))
		{
			throw new RuntimeException("bean must be instance of " + clazz.getName());
		}

		Map<Class<?>, BeanInformation> beansMap = reflectionManager.getBeans(basePackage);

		if ((!beansMap.containsKey(clazz) && !beanOverrideMap.containsKey(basePackage, clazz)) || force)
		{
			beanOverrideMap.put(basePackage, clazz, new BeanHolder(bean, null));
		}
	}

	@Override
	public <T> T getBean(Class<T> requiredType)
	{
		return getBean(requiredType, true);
	}

	public <T> T getBean(Class<T> requiredType, boolean beanOnly)
	{
		return getBean(requiredType, beanOnly, new LinkedHashSet<>());
	}

	public <T> T getBean(Class<T> requiredType, boolean beanOnly, Set<Class<?>> classTrace)
	{
		synchronized (this)
		{
			Map<Class<?>, BeanInformation> beansMap = reflectionManager.getBeans(basePackage);

			prepare(beansMap, requiredType);

			BeanHolder beanHolder = beanOverrideMap.get(basePackage, requiredType);

			if (null != beanHolder)
			{
				return requiredType.cast(beanHolder.getBean(classTrace));
			}

			BeanInformation beanInformation = beansMap.get(requiredType);

			if (null != beanInformation)
			{
				try
				{
					if (classTrace.contains(requiredType))
					{
						StringBuilder circle = new StringBuilder();

						for (Class<?> clazz : classTrace)
						{
							circle.append(clazz.getName()).append("-->");
						}

						circle.delete(circle.length() - 3, circle.length());

						throw new EasywireException("circle dependency detected {}", circle.toString());
					}

					classTrace.add(requiredType);

					try
					{
						return beanInformation.getBean(requiredType, classTrace);
					}
					catch (EasywireBeanNotFoundException e)
					{
						T bean = getInterfaceMockImplementation(requiredType, true);

						if (null != bean)
						{
							return bean;
						}

						throw e;
					}
				}

				finally
				{
					classTrace.remove(requiredType);
				}
			}

			if (!beanOnly)
			{
				T insatnce = initializeClass(requiredType, beanOnly, classTrace);

				pushBean(requiredType, insatnce);
				return insatnce;
			}

			T bean = getInterfaceMockImplementation(requiredType, false);

			if (null != bean)
			{
				return bean;
			}

			throw new EasywireBeanNotFoundException(requiredType);
		}
	}

	private <T> T getInterfaceMockImplementation(Class<T> requiredType, boolean force)
	{
		BeanHolder beanHolder = beanOverrideMap.get(basePackage, requiredType);

		if (null != beanHolder)
		{
			return requiredType.cast(beanHolder.getBean(new HashSet<>()));
		}

		try
		{
			Class<?> clazz = Class.forName(requiredType.getName());

			// if the class is interface, try finding the implementing class
			if (clazz.isInterface())
			{
				log.warn("Please implement " + requiredType.getName() + ", using empty mock instead");

				T mock = requiredType.cast(Mockito.mock(clazz));
				pushBean(clazz, mock, force);

				return mock;
			}
		}
		catch (Exception e)
		{
		}

		return null;
	}

	private void prepare(Map<Class<?>, BeanInformation> beansMap, Class<?> requiredType)
	{
		if (prepareMap.containsKey(basePackage, requiredType))
		{
			return;
		}

		prepareMap.put(basePackage, requiredType, Boolean.TRUE);

		if (!beanOverrideMap.containsKey(basePackage, requiredType))
		{
			// check by interfaces
			Class<?> temp = requiredType;
			boolean found = false;

			do
			{
				Class<?>[] interfaces = temp.getInterfaces();

				for (Class<?> clazz : interfaces)
				{
					if (beanOverrideMap.containsKey(basePackage, clazz))
					{
						BeanHolder beanHolder = beanOverrideMap.get(basePackage, clazz);
						beanOverrideMap.put(basePackage, requiredType, beanHolder);

						found = true;
						break;
					}

					if (!beansMap.containsKey(requiredType) && beansMap.containsKey(clazz))
					{
						BeanInformation beanInformation = beansMap.get(clazz);
						beansMap.put(requiredType, beanInformation);
					}
				}

				temp = temp.getSuperclass();
			} while (!found && null != temp);

			if (!found)
			{
				// check by implementations
				List<Class<?>> findImplementingClasses = reflectionManager.findImplementingClasses(requiredType, basePackage);

				for (Class<?> clazz : findImplementingClasses)
				{
					if (beanOverrideMap.containsKey(basePackage, clazz))
					{
						BeanHolder beanHolder = beanOverrideMap.get(basePackage, clazz);
						beanOverrideMap.put(basePackage, requiredType, beanHolder);

						found = true;
						break;
					}

					if (!beansMap.containsKey(requiredType) && beansMap.containsKey(clazz))
					{
						BeanInformation beanInformation = beansMap.get(clazz);
						beansMap.put(requiredType, beanInformation);
					}
				}
			}
		}
	}

	public <T> List<T> getBeans(Class<T> requiredType, boolean beanOnly, Set<Class<?>> classTrace)
	{
		Map<Class<?>, BeanInformation> beansMap = reflectionManager.getBeans(basePackage);

		List<OrderObject> orderObjects = new ArrayList<>();

		List<Class<?>> implementingClasses = reflectionManager.findImplementingClasses(requiredType, basePackage);

		for (Class<?> implementingClass : implementingClasses)
		{
			if (Modifier.isAbstract(implementingClass.getModifiers()))
			{
				continue;
			}

			try
			{
				int order = Integer.MAX_VALUE;
				Object bean = getBean(implementingClass, beanOnly, classTrace);

				if (beanOverrideMap.containsKey(basePackage, bean.getClass()))
				{
					BeanHolder beanHolder = beanOverrideMap.get(basePackage, bean.getClass());
					order = beanHolder.getOrder();
				}

				else
				{
					BeanInformation beanInformation = beansMap.get(implementingClass);
					order = beanInformation.order(bean.getClass());
				}

				orderObjects.add(new OrderObject(order, bean));
			}
			catch (EasywireException e)
			{
				if (!(e instanceof EasywireBeanNotFoundException))
				{
					throw e;
				}
			}
		}

		Collections.sort(orderObjects);

		List<T> res = new ArrayList<>(orderObjects.size());

		for (OrderObject orderObject : orderObjects)
		{
			res.add(requiredType.cast(orderObject.getObject()));
		}

		return res;
	}

	public boolean isBean(Class<?> clazz)
	{
		if (null != clazz)
		{
			if (!beanInfoMap.containsKey(clazz))
			{
				synchronized (beanInfoMap)
				{
					if (!beanInfoMap.containsKey(clazz))
					{
						boolean bean = false;

						Annotation[] annotations = clazz.getAnnotations();

						for (Annotation annotation : annotations)
						{
							Class<? extends Annotation> annotationClass = annotation.annotationType();

							String name = annotationClass.getName();

							if ("javax.inject.Named".equals(name) || "org.springframework.stereotype.Component".equals(name) || "org.springframework.stereotype.Controller".equals(name) || "org.springframework.stereotype.Repository".equals(name) || "org.springframework.stereotype.Service".equals(name) || "org.springframework.web.bind.annotation.RestController".equals(name) || "org.springframework.context.annotation.Configuration".equals(name))
							{
								bean = true;
								break;
							}
						}

						beanInfoMap.put(clazz, bean);
					}
				}
			}

			return beanInfoMap.get(clazz);
		}

		return false;
	}

	private <T> T initializeClass(Class<T> requiredType, boolean beanOnly, Set<Class<?>> classTrace)
	{
		BeanHolder beanHolder = new BeanHolder(requiredType, beanOnly, classTrace);
		return requiredType.cast(beanHolder.getBean(classTrace));
	}

	public boolean isBeanLoaded(Class<?> requiredType)
	{
		Map<Class<?>, BeanInformation> beansMap = reflectionManager.getBeans(basePackage);

		if (beansMap.containsKey(requiredType))
		{
			BeanInformation beanInformation = beansMap.get(requiredType);
			return beanInformation.isLoaded(requiredType);
		}

		return false;
	}

	public void initBean(Object bean, boolean beanOnly, boolean skipBeanLoadingInConfiguration, Set<Class<?>> classTrace)
	{
		try
		{
			populateFields(bean, beanOnly, classTrace);

			if (!skipBeanLoadingInConfiguration)
			{
				handleConfiguration(bean, classTrace);
			}

			propertyManager.handleConfigurationProperties(bean);
			handlePostConstruct(bean);
		}
		catch (BindException e)
		{
			throw new EasywireException(e);
		}
	}

	private <T> void populateFields(T bean, boolean beanOnly, Set<Class<?>> classTrace)
	{
		Collection<Field> classFields = fieldsInvestigator.getClassFields(bean.getClass());

		for (Field field : classFields)
		{
			try
			{
				handleInject(bean, field, beanOnly, classTrace);
				handleAutowire(bean, field, beanOnly, classTrace);

				if (null != propertyManager.getProperties())
				{
					handleValue(bean, field);
				}

				handleMock(bean, field);
				handleSpy(bean, field);
			}
			catch (Exception e)
			{
				throw new EasywireException("failed to create bean {}, couldn't inject field '{}', error: {}", bean.getClass().getName(), field.getName(), e.getMessage());
			}
		}
	}

	private void handleConfiguration(Object bean, Set<Class<?>> classTrace)
	{
		Configuration configuration = bean.getClass().getAnnotation(Configuration.class);

		if (null == configuration)
		{
			return;
		}

		for (Method method : bean.getClass().getDeclaredMethods())
		{
			if (!method.isAnnotationPresent(Bean.class))
			{
				continue;
			}

			Class<?> returnType = method.getReturnType();

			if (isBeanLoaded(returnType))
			{
				continue;
			}

			Map<Class<?>, BeanInformation> beans = reflectionManager.getBeans(basePackage);
			BeanInformation beanInformation = beans.get(method.getReturnType());

			if (null == beanInformation)
			{
				beanInformation = new BeanInformation(method.getReturnType());
				beanInformation.setBeanHolder(method.getReturnType(), new BeanHolder(method, classTrace));
			}

			if (!beanInformation.isPrototype())
			{
				beanInformation.getBean(classTrace);
			}
		}
	}

	public <T> void handlePostConstruct(T bean)
	{
		List<Class<?>> classesHierarchy = new ArrayList<>();
		Class<?> temp = bean.getClass();

		while (null != temp)
		{
			if (temp != Object.class)
			{
				classesHierarchy.add(temp);
			}
			temp = temp.getSuperclass();
		}

		for (int i = classesHierarchy.size() - 1; i >= 0; i--)
		{
			temp = classesHierarchy.get(i);

			for (Method method : temp.getDeclaredMethods())
			{
				if (method.isAnnotationPresent(PostConstruct.class))
				{
					try
					{
						if (!method.isAccessible())
						{
							method.setAccessible(true);
						}

						method.invoke(bean);
					}
					catch (Exception e)
					{
						throw new RuntimeException("Failed to trigger PostConstruct for class " + bean.getClass());
					}
				}
			}
		}
	}

	private <T> void handleInject(T bean, Field field, boolean beanOnly, Set<Class<?>> classTrace) throws ClassNotFoundException
	{
		try
		{
			Inject.class.getName();
		}
		catch (NoClassDefFoundError e)
		{
			return;
		}

		Inject inject = field.getAnnotation(Inject.class);

		if (null == inject)
		{
			return;
		}

		populateBeanField(bean, field, beanOnly, classTrace);
	}

	private <T> void handleAutowire(T bean, Field field, boolean beanOnly, Set<Class<?>> classTrace) throws ClassNotFoundException
	{
		Autowired autowired = field.getAnnotation(Autowired.class);

		if (null == autowired)
		{
			return;
		}

		boolean required = autowired.required();

		try
		{
			populateBeanField(bean, field, beanOnly, classTrace);
		}

		catch (Exception e)
		{
			if (required)
			{
				throw e;
			}

			log.warn("failed to inject field {} in class {}, field marked as required=false, creation of field failed with exception: {}", field.getName(), bean.getClass(), e.getMessage());
		}
	}

	private <T> void handleValue(T bean, Field field) throws ClassNotFoundException
	{
		Value value = field.getAnnotation(Value.class);

		if (null == value)
		{
			return;
		}

		String token = value.value();

		if (null != token)
		{
			PropertyValue propertyValue = propertyManager.getPropertyValue(token);

			if ("spring.application.name".equals(propertyValue.getKey()))
			{
				propertyValue.setValue("applicationDefaultName");
			}

			if (null == propertyValue.getValue())
			{
				throw new EasywireException("missing property " + propertyValue.getKey());
			}

			String strProperty = propertyValue.getValue();

			if (field.getType() == String.class)
			{
				fieldsInvestigator.setFieldValue(field, bean, strProperty);
			}

			else if (field.getType() == Long.class)
			{
				fieldsInvestigator.setFieldValue(field, bean, Long.valueOf(strProperty));
			}
			else if (field.getType() == Integer.class)
			{
				fieldsInvestigator.setFieldValue(field, bean, Integer.valueOf(strProperty));
			}
			else if (field.getType() == Double.class)
			{
				fieldsInvestigator.setFieldValue(field, bean, Double.valueOf(strProperty));
			}
			else if (field.getType() == Float.class)
			{
				fieldsInvestigator.setFieldValue(field, bean, Float.valueOf(strProperty));
			}
			else if (field.getType() == Boolean.class)
			{
				fieldsInvestigator.setFieldValue(field, bean, Boolean.valueOf(strProperty));
			}
			else
			{
				log.warn("Value " + propertyValue.getKey() + " is ignored, please report that to one of the framework devs");
			}
		}
	}

	private <T> void handleMock(T bean, Field field)
	{
		Mock mock = field.getAnnotation(Mock.class);

		if (null == mock)
		{
			return;
		}

		fieldsInvestigator.setFieldValue(field, bean, Mockito.mock(field.getType()));
	}

	private <T> void handleSpy(T bean, Field field)
	{
		Spy spy = field.getAnnotation(Spy.class);

		if (null == spy)
		{
			return;
		}

		Object newValue = null;
		Object fieldValue = fieldsInvestigator.getFieldValue(field, bean);

		if (null == fieldValue)
		{
			newValue = Mockito.spy(field.getType());
		}

		else
		{
			newValue = Mockito.spy(fieldValue);
		}

		fieldsInvestigator.setFieldValue(field, bean, newValue);
	}

	private <T> void populateBeanField(T bean, Field field, boolean beanOnly, Set<Class<?>> classTrace) throws ClassNotFoundException
	{
		Qualifier qualifier = field.getAnnotation(Qualifier.class);

		if (null != qualifier)
		{
			handleQualifier(bean, field, beanOnly, classTrace, qualifier.value());
			return;
		}

		boolean javaxAvailable = false;

		try
		{
			BeanProvider.class.getName();
			Provider.class.getName();

			javaxAvailable = true;
		}
		catch (NoClassDefFoundError e)
		{
		}

		if (javaxAvailable && (field.getType() == BeanProvider.class || field.getType() == Provider.class))
		{
			Class<?> clazz = Class.forName(((ParameterizedType) (field.getGenericType())).getActualTypeArguments()[0].getTypeName());
			fieldsInvestigator.setFieldValue(field, bean, BeanProvider.getBeanProviderObject(clazz));
		}

		else
		{
			Type genericType = field.getGenericType();

			if (genericType == bean.getClass())
			{
				fieldsInvestigator.setFieldValue(field, bean, bean);
			}

			else
			{
				Object beanByType = getBeanByType(genericType, beanOnly, classTrace);
				fieldsInvestigator.setFieldValue(field, bean, beanByType);
			}
		}
	}

	private <T> void handleQualifier(T bean, Field field, boolean beanOnly, Set<Class<?>> classTrace, String qualifierValue)
	{
		List<?> beans = getBeans(field.getType(), beanOnly, classTrace);

		for (Object currBean : beans)
		{
			if (currBean.getClass().getSimpleName().toLowerCase().equals(qualifierValue.toLowerCase()))
			{
				fieldsInvestigator.setFieldValue(field, bean, currBean);
				return;
			}
		}

		throw new EasywireException("failed to find matching beans with Qualifier value {}", qualifierValue);
	}

	public Object getBeanByType(Type fieldType, boolean beanOnly, Set<Class<?>> classTrace) throws ClassNotFoundException
	{
		if (fieldType instanceof Class)
		{
			Class<?> clazz = (Class<?>) fieldType;

			if (ClassUtils.isPrimitiveOrWrapper(clazz))
			{
				throw new EasywireException("can't instantiate primitive type {}", clazz.getName());
			}
		}

		// handle list - need to get all implementing beans
		if (fieldType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedField = (ParameterizedType) fieldType;
			Type[] actualTypeArguments = parameterizedField.getActualTypeArguments();

			if (actualTypeArguments.length > 0)
			{
				if (actualTypeArguments.length == 1)
				{
					String typeName = actualTypeArguments[0].getTypeName();

					Type rawType = parameterizedField.getRawType();

					if (rawType == List.class)
					{
						if (typeName.endsWith("<?>"))
						{
							typeName = typeName.substring(0, typeName.length() - "<?>".length());
						}

						return getBeans(Class.forName(typeName), beanOnly, classTrace);
					}

					if ("javax.inject.Provider".equals(rawType.getTypeName()))
					{
						return BeanProvider.getBeanProviderObject(Class.forName(typeName));
					}

					else if ("java.util.Optional".equals(rawType.getTypeName()))
					{
						try
						{
							Object optionalBean = getBeanByType(actualTypeArguments[0], beanOnly, classTrace);

							// Object optionalBean = getBean(Class.forName(typeName), beanOnly, classTrace);

							if (!Mockito.mockingDetails(optionalBean).isMock())
							{
								return Optional.of(optionalBean);
							}

							else
							{
								return Optional.ofNullable(null);
							}
						}
						catch (Exception e)
						{
							return Optional.ofNullable(null);
						}
					}

					else if ("org.springframework.beans.factory.ObjectFactory".equals(rawType.getTypeName()))
					{
						@SuppressWarnings("rawtypes")
						ObjectFactory objectFactory = Mockito.mock(ObjectFactory.class);
						Mockito.when(objectFactory.getObject()).thenReturn(getBean(Class.forName(typeName), beanOnly));
						return objectFactory;
					}

					// handle simple bean
					return getBean(Class.forName(typeName), beanOnly);
				}

				else if (actualTypeArguments.length == 2)
				{
					String typeName = actualTypeArguments[1].getTypeName();

					Type rawType = parameterizedField.getRawType();

					if (rawType == Map.class)
					{
						if (typeName.endsWith("<?>"))
						{
							typeName = typeName.substring(0, typeName.length() - "<?>".length());
						}

						List<?> beans = getBeans(Class.forName(typeName), beanOnly, classTrace);

						Map<String, Object> map = new HashMap<>();

						for (Object object : beans)
						{
							map.put(object.getClass().getSimpleName(), object);
						}

						return map;
					}
				}
			}
		}

		return getBean(Class.forName(fieldType.getTypeName()), beanOnly, classTrace);
	}

	public void overrideLogger(Class<?> clazz)
	{
		try
		{
			Field[] declaredFields = clazz.getDeclaredFields();

			for (Field field : declaredFields)
			{
				if (Modifier.isStatic(field.getModifiers()) && field.getType() == Logger.class)
				{
					Field modifiersField = Field.class.getDeclaredField("modifiers");
					modifiersField.setAccessible(true);
					modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

					field.setAccessible(true);
					field.set(null, loggerTest);
				}
			}
		}
		catch (Exception e)
		{
			log.warn("failed to override logger, error: {}", e);
		}
	}

	@Override
	public Environment getEnvironment()
	{
		return getBean(Environment.class);
	}

	public Reflections getReflections()
	{
		return reflectionManager.getReflections(basePackage);
	}

	public String getBasePackage()
	{
		return basePackage;
	}
}
