package co.il.nmh.easy.wire.core.utils.properties;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.validation.BindException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Maor Hamami
 */
@Slf4j
public class PropertyManager
{
	private String loadedProfile;
	private String loadedPropertyFile;
	private Properties properties;
	private MutablePropertySources propertySources;

	public void buildPropertySources(String propertyFile, String profile)
	{
		// check if properties are already loaded
		if (propertyFile.equals(loadedPropertyFile) && ((null == profile && null == loadedProfile) || (null != profile && profile.equals(loadedProfile))))
		{
			return;
		}

		this.loadedProfile = profile;
		this.loadedPropertyFile = propertyFile;
		this.properties = null;
		this.propertySources = null;

		if (SpringPropertiesV1.isAvailable())
		{
			properties = SpringPropertiesV1.buildPropertySources(propertyFile, profile);
		}

		else if (SpringPropertiesV2.isAvailable())
		{
			properties = SpringPropertiesV2.buildPropertySources(propertyFile, profile);
		}

		else
		{
			log.error("couldn't load properties, spring version is not supported");
		}

		if (null != properties)
		{
			propertySources = new MutablePropertySources();
			propertySources.addLast(new PropertiesPropertySource("apis", properties));
		}
	}

	public <T> void handleConfigurationProperties(T bean) throws BindException
	{
		ConfigurationProperties configurationProperties = bean.getClass().getAnnotation(ConfigurationProperties.class);
		handleConfigurationProperties(bean, configurationProperties);
	}

	public <T> void handleConfigurationProperties(T bean, ConfigurationProperties configurationProperties) throws BindException
	{
		if (null != configurationProperties && null != propertySources)
		{
			String prefix = configurationProperties.prefix();
			String value = configurationProperties.value();

			if (null == value || value.isEmpty())
			{
				value = prefix;
			}

			if (SpringPropertiesV1.isAvailable())
			{
				SpringPropertiesV1.handleConfigurationProperties(bean, value, propertySources);
			}

			else if (SpringPropertiesV2.isAvailable())
			{
				SpringPropertiesV2.handleConfigurationProperties(bean, value, properties);
			}

			else
			{
				log.error("couldn't load properties, spring version is not supported");
			}
		}
	}

	public final Properties getProperties()
	{
		return properties;
	}

	public PropertyValue getPropertyValue(String value)
	{
		PropertyValue propertyValue = null;

		if (null != properties && null != value)
		{
			String defaultValue = null;

			int i = value.indexOf("${");

			if (i > -1)
			{
				value = value.substring(i + 2);
				i = value.lastIndexOf("}");

				if (i > -1)
				{
					value = value.substring(0, i);
				}
			}

			i = value.indexOf(":");

			if (i > -1)
			{
				defaultValue = value.substring(i + 1);
				value = value.substring(0, i);
			}

			if (null != defaultValue)
			{
				defaultValue = defaultValue.trim();
			}

			value = value.trim();

			Object result = properties.getOrDefault(value, defaultValue);

			propertyValue = new PropertyValue();
			propertyValue.setKey(value);

			if (null != result)
			{
				propertyValue.setValue(String.valueOf(result));
			}
		}

		return propertyValue;
	}

	public String getLoadedProfile()
	{
		return loadedProfile;
	}

	public MutablePropertySources getPropertySources()
	{
		return propertySources;
	}

	@Data
	public static class PropertyValue
	{
		private String key;
		private String value;
	}
}
