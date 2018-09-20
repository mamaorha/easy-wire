package co.il.nmh.easy.wire;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import co.il.nmh.easy.utils.LoggerUtils;
import co.il.nmh.easy.wire.annotation.EasywireProperties;
import co.il.nmh.easy.wire.core.EasywireBeanFactory;
import co.il.nmh.easy.wire.core.base.IEasywireBaseInitializer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Maor Hamami
 */

@Slf4j
public class EasywireRunner extends BlockJUnit4ClassRunner
{
	private static Map<String, List<IEasywireBaseInitializer>> easywireBaseInitializersByPackage = new ConcurrentHashMap<>(); // basePackage -> list of initializers
	private static Map<String, Set<Class<?>>> easywireInitializersByPackage = new ConcurrentHashMap<>(); // basePackage -> initializedClasses
	private Object testClassInstance;
	private Order defaultOrder;

	public EasywireRunner(Class<?> clazz) throws InitializationError
	{
		super(clazz);

		LoggerUtils.INSTANCE.overrideLoggerLevel("org.springframework", "INFO");
		LoggerUtils.INSTANCE.overrideLoggerLevel("org.reflections.Reflections", "INFO");

		EasywireProperties easywireProperties = clazz.getAnnotation(EasywireProperties.class);
		loadProperties(easywireProperties, clazz);

		String basePackage = EasywireBeanFactory.INSTANCE.getBasePackage();

		if (!easywireInitializersByPackage.containsKey(basePackage))
		{
			easywireInitializersByPackage.put(basePackage, new HashSet<>());
		}

		loadBaseContextInitializers(basePackage);

		Set<Class<?>> easywireInitializers = easywireInitializersByPackage.get(basePackage);

		if (null != easywireProperties)
		{
			if (EasywireProperties.DEFAULT.class != easywireProperties.initializeClass())
			{
				if (!easywireInitializers.contains(easywireProperties.initializeClass()))
				{
					synchronized (easywireInitializers)
					{
						if (!easywireInitializers.contains(easywireProperties.initializeClass()))
						{
							EasywireBeanFactory.INSTANCE.getBean(easywireProperties.initializeClass(), false).initialize();
							easywireInitializers.add(easywireProperties.initializeClass());
						}
					}
				}
			}

			if (null != easywireProperties.loadBeans())
			{
				for (Class<?> bean : easywireProperties.loadBeans())
				{
					EasywireBeanFactory.INSTANCE.getBean(bean);
				}
			}
		}
	}

	private void loadBaseContextInitializers(String basePackage)
	{
		if (!easywireBaseInitializersByPackage.containsKey(basePackage))
		{
			getBaseContextInititalizers(basePackage);
		}

		List<IEasywireBaseInitializer> easywireBaseInitializers = easywireBaseInitializersByPackage.get(basePackage);
		Set<Class<?>> easywireInitializers = easywireInitializersByPackage.get(basePackage);

		for (IEasywireBaseInitializer easywireBaseInitializer : easywireBaseInitializers)
		{
			if (!easywireInitializers.contains(easywireBaseInitializer.getClass()))
			{
				synchronized (easywireInitializers)
				{
					if (!easywireInitializers.contains(easywireBaseInitializer.getClass()))
					{
						easywireBaseInitializer.initialize();
						easywireInitializers.add(easywireBaseInitializer.getClass());
					}
				}
			}
		}
	}

	private void getBaseContextInititalizers(String basePackage)
	{
		List<IEasywireBaseInitializer> easywireBaseInitializers = EasywireBeanFactory.INSTANCE.getBeans(IEasywireBaseInitializer.class, false, new LinkedHashSet<>());

		Collections.sort(easywireBaseInitializers, new Comparator<IEasywireBaseInitializer>()
		{
			@Override
			public int compare(IEasywireBaseInitializer o1, IEasywireBaseInitializer o2)
			{
				Order o1Order = o1.getClass().getAnnotation(Order.class);
				Order o2Order = o2.getClass().getAnnotation(Order.class);

				if (null == o1Order)
				{
					o1Order = getDefaultOrder();
				}

				if (null == o2Order)
				{
					o2Order = getDefaultOrder();
				}

				return Integer.compare(o1Order.value(), o2Order.value());
			}
		});

		easywireBaseInitializersByPackage.put(basePackage, easywireBaseInitializers);
	}

	private void loadProperties(EasywireProperties easywireProperties, Class<?> clazz)
	{
		String defaultBasePackage = "";

		if (null != clazz.getPackage())
		{
			defaultBasePackage = clazz.getPackage().getName();
		}

		if (null != easywireProperties)
		{
			String[] basePackages = easywireProperties.scanBasePackages();
			Arrays.sort(basePackages);

			StringBuilder basePackageBuilder = new StringBuilder();

			for (String currBasePackage : basePackages)
			{
				basePackageBuilder.append(currBasePackage).append(",");
			}

			String basePackage = null;

			if (basePackages.length == 0)
			{
				basePackage = defaultBasePackage;
				log.warn("basePackage was not set, using default {}", basePackage);
			}

			else
			{
				basePackage = basePackageBuilder.toString();
			}

			EasywireBeanFactory.INSTANCE.setBasePackage(basePackage);
			EasywireBeanFactory.INSTANCE.buildPropertySources(easywireProperties.propertyFile(), easywireProperties.propertyProfile());

			if (easywireProperties.cleanEnvironment())
			{
				easywireBaseInitializersByPackage.remove(basePackage);

				if (easywireInitializersByPackage.containsKey(basePackage))
				{
					easywireInitializersByPackage.get(basePackage).clear();
				}

				EasywireBeanFactory.INSTANCE.clean();
			}
		}

		else
		{
			log.warn("@EasywireProperties was not found, using default basePackage: {}, propertyFile: {}, propertyProfile: {}", defaultBasePackage, "application.yml", "test");

			EasywireBeanFactory.INSTANCE.setBasePackage(defaultBasePackage);
			EasywireBeanFactory.INSTANCE.buildPropertySources("application.yml");
		}
	}

	private Order getDefaultOrder()
	{
		if (null == defaultOrder)
		{
			defaultOrder = new Order()
			{
				@Override
				public Class<? extends Annotation> annotationType()
				{
					return Order.class;
				}

				@Override
				public int value()
				{
					return Ordered.LOWEST_PRECEDENCE;
				}
			};
		}

		return defaultOrder;
	}

	@Override
	protected Object createTest() throws Exception
	{
		if (null == testClassInstance)
		{
			testClassInstance = super.createTest();
			EasywireBeanFactory.INSTANCE.initBean(testClassInstance, false, false, new LinkedHashSet<>());
		}

		return testClassInstance;
	}
}
