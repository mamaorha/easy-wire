package co.il.nmh.easy.wire.core.utils.properties;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.beans.factory.config.YamlProcessor.DocumentMatcher;
import org.springframework.beans.factory.config.YamlProcessor.MatchStatus;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import co.il.nmh.easy.utils.LoggerUtils;
import co.il.nmh.easy.utils.reflection.FieldsInvestigator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Maor Hamami
 */
@Slf4j
public class SpringPropertiesV2
{
	public static Properties buildPropertySources(String propertyFile, String profile)
	{
		LoggerUtils.INSTANCE.overrideLoggerLevel(SpringPropertiesV2.class.getName(), "INFO");

		try
		{
			Properties properties = new Properties();

			// load common properties
			PropertySource<?> applicationYamlPropertySource = load("properties", new ClassPathResource(propertyFile), null);
			Map<String, Object> source = ((MapPropertySource) applicationYamlPropertySource).getSource();

			properties.putAll(source);

			// load profile properties
			if (null != profile)
			{
				applicationYamlPropertySource = load("properties", new ClassPathResource(propertyFile), profile);

				if (null != applicationYamlPropertySource)
				{
					source = ((MapPropertySource) applicationYamlPropertySource).getSource();

					properties.putAll(source);
				}
			}

			return properties;
		}
		catch (Exception e)
		{
			log.error("{} file cannot be found.", propertyFile);
			return null;
		}
	}

	public static <T> void handleConfigurationProperties(T bean, String value, Properties properties)
	{
		try
		{
			Class<?> mapConfigurationPropertySourceClass = Class.forName("org.springframework.boot.context.properties.source.MapConfigurationPropertySource");
			Constructor<?> mapConfigurationPropertySourceConstructor = mapConfigurationPropertySourceClass.getConstructor(Map.class);

			Object mapConfigurationPropertySource = mapConfigurationPropertySourceConstructor.newInstance(properties);

			Class<?> binderClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
			Constructor<?> binderConstructor = binderClass.getConstructor(Iterable.class);
			Method bindMethod = binderClass.getMethod("bind", String.class, Class.class);

			Object binder = binderConstructor.newInstance(Arrays.asList(mapConfigurationPropertySource));
			Object bindResult = bindMethod.invoke(binder, value, bean.getClass());

			Object proeprtyBean = bindResult.getClass().getMethod("get").invoke(bindResult);

			Collection<Field> classFields = FieldsInvestigator.INSTANCE.getClassFields(bean.getClass());

			for (Field field : classFields)
			{
				Object fieldValue = FieldsInvestigator.INSTANCE.getFieldValue(field, proeprtyBean);
				FieldsInvestigator.INSTANCE.setFieldValue(field, bean, fieldValue);
			}
		}
		catch (Exception e)
		{
		}
	}

	private static PropertySource<?> load(String name, Resource resource, String profile) throws IOException
	{
		if (ClassUtils.isPresent("org.yaml.snakeyaml.Yaml", null))
		{
			Processor processor = new Processor(resource, profile);
			Map<String, Object> source = processor.process();

			if (!source.isEmpty())
			{
				return new MapPropertySource(name, source);
			}
		}
		return null;
	}

	private static class Processor extends YamlProcessor
	{
		private Processor(Resource resource, String profile)
		{
			setMatchDefault(false);
			setDocumentMatchers(new EasywireSpringProfileDocumentMatcher(profile));

			setResources(resource);
		}

		public Map<String, Object> process()
		{
			final Map<String, Object> result = new LinkedHashMap<String, Object>();
			process(new MatchCallback()
			{
				@Override
				public void process(Properties properties, Map<String, Object> map)
				{
					result.putAll(getFlattenedMap(map));
				}

			});
			return result;
		}
	}

	private static class EasywireSpringProfileDocumentMatcher implements DocumentMatcher
	{
		private String profile;

		public EasywireSpringProfileDocumentMatcher(String profile)
		{
			this.profile = profile;
		}

		@Override
		public MatchStatus matches(Properties properties)
		{
			String property = properties.getProperty("spring.profiles", null);

			if (null == profile)
			{
				if (null == property)
				{
					return MatchStatus.FOUND;
				}
			}

			else if (profile.equals(property))
			{
				return MatchStatus.FOUND;
			}

			return MatchStatus.NOT_FOUND;
		}
	}

	public static boolean isAvailable()
	{
		try
		{
			PropertySource.class.getName();
			DocumentMatcher.class.getName();
			YamlProcessor.class.getName();

			Class.forName("org.springframework.boot.context.properties.bind.Binder");
			Class.forName("org.springframework.boot.context.properties.source.MapConfigurationPropertySource");

			return true;
		}
		catch (NoClassDefFoundError | ClassNotFoundException e)
		{
			return false;
		}
	}
}
