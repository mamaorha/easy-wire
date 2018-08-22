package co.il.nmh.easy.wire.core.utils;

import java.util.Map;
import java.util.Properties;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

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

		YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

		try
		{
			properties = new Properties();

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

			propertySources = new MutablePropertySources();
			propertySources.addLast(new PropertiesPropertySource("apis", properties));
		}

		catch (Exception e)
		{
			log.error("{} file cannot be found.", propertyFile);
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
			String result = properties.getProperty(value, defaultValue);

			propertyValue = new PropertyValue();
			propertyValue.setKey(value);
			propertyValue.setValue(result);
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
