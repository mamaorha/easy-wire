package co.il.nmh.easy.wire.core.utils;

import java.util.Map;
import java.util.Properties;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.core.env.MutablePropertySources;

import co.il.nmh.easy.wire.core.EasywireBeanFactory;

/**
 * @author Maor Hamami
 */

public class EasywireEnvironment implements ConfigurableEnvironment
{
	private Properties getProperties()
	{
		return EasywireBeanFactory.INSTANCE.getProperties();
	}

	@Override
	public boolean containsProperty(String key)
	{
		Properties properties = getProperties();

		return null != properties && properties.containsKey(key);
	}

	@Override
	public String getProperty(String key)
	{
		return EasywireBeanFactory.INSTANCE.getPropertyValue(key);
	}

	@Override
	public String getProperty(String key, String defaultValue)
	{
		String value = getProperty(key);

		if (null == value)
		{
			value = defaultValue;
		}

		return value;
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType)
	{
		String value = getProperty(key);

		if (null == value)
		{
			return null;
		}

		return targetType.cast(value);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue)
	{
		T value = getProperty(key, targetType);

		if (null == value)
		{
			value = defaultValue;
		}

		return value;
	}

	@Override
	public <T> Class<T> getPropertyAsClass(String key, Class<T> targetType)
	{
		getProperty(key, targetType);
		return targetType;
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException
	{
		return getProperty(key);
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException
	{
		return getProperty(key, targetType);
	}

	@Override
	public String resolvePlaceholders(String text)
	{
		throw new RuntimeException("resolvePlaceholders - not implemented");
	}

	@Override
	public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException
	{
		throw new RuntimeException("resolvePlaceholders - not implemented");
	}

	@Override
	public String[] getActiveProfiles()
	{
		return new String[] { EasywireBeanFactory.INSTANCE.getLoadedProfile() };
	}

	@Override
	public String[] getDefaultProfiles()
	{
		return new String[] { EasywireBeanFactory.INSTANCE.getLoadedProfile() };
	}

	@Override
	public boolean acceptsProfiles(String... profiles)
	{
		return false;
	}

	@Override
	public MutablePropertySources getPropertySources()
	{
		return EasywireBeanFactory.INSTANCE.getPropertySources();
	}

	@Override
	public ConfigurableConversionService getConversionService()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setConversionService(ConfigurableConversionService conversionService)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setPlaceholderPrefix(String placeholderPrefix)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setPlaceholderSuffix(String placeholderSuffix)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setValueSeparator(String valueSeparator)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setRequiredProperties(String... requiredProperties)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void validateRequiredProperties() throws MissingRequiredPropertiesException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setActiveProfiles(String... profiles)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void addActiveProfile(String profile)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setDefaultProfiles(String... profiles)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Map<String, Object> getSystemEnvironment()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Map<String, Object> getSystemProperties()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void merge(ConfigurableEnvironment parent)
	{
		throw new RuntimeException("Method is not implemented");
	}
}
