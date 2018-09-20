package co.il.nmh.easy.wire.core.utils.properties;

import java.util.Map;
import java.util.Properties;

import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Maor Hamami
 */

@Slf4j
public class SpringPropertiesV1
{
	public static Properties buildPropertySources(String propertyFile, String profile)
	{
		try
		{
			Properties properties = new Properties();
			YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

			// load common properties
			PropertySource<?> applicationYamlPropertySource = loader.load("properties", new ClassPathResource(propertyFile), null);
			Map<String, Object> source = ((MapPropertySource) applicationYamlPropertySource).getSource();

			properties.putAll(source);

			// load profile properties
			if (null != profile)
			{
				applicationYamlPropertySource = loader.load("properties", new ClassPathResource(propertyFile), profile);

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

	public static <T> void handleConfigurationProperties(T bean, String value, MutablePropertySources propertySources) throws BindException
	{
		PropertiesConfigurationFactory<?> configurationFactory = new PropertiesConfigurationFactory<>(bean);
		configurationFactory.setPropertySources(propertySources);
		configurationFactory.setTargetName(value);
		configurationFactory.bindPropertiesToTarget();
	}

	public static boolean isAvailable()
	{
		try
		{
			PropertiesConfigurationFactory.class.getName();
			return true;
		}
		catch (NoClassDefFoundError e)
		{
			return false;
		}
	}
}
