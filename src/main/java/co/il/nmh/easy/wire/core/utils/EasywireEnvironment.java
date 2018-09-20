package co.il.nmh.easy.wire.core.utils;

import java.util.Properties;

import org.springframework.core.env.Environment;

import co.il.nmh.easy.wire.core.EasywireBeanFactory;

/**
 * @author Maor Hamami
 */

public class EasywireEnvironment implements Environment
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
}
