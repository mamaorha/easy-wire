package co.il.nmh.easy.wire.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import co.il.nmh.easy.wire.exception.EasywireException;

/**
 * @author Maor Hamami
 */

public class BasePackageMap<T>
{
	private Map<String, Map<Class<?>, T>> map;

	public BasePackageMap()
	{
		map = new ConcurrentHashMap<>();
	}

	public boolean containsKey(String basePackage, Class<?> key)
	{
		validateBasePackage(basePackage);

		return map.containsKey(basePackage) && map.get(basePackage).containsKey(key);
	}

	public T get(String basePackage, Class<?> key)
	{
		validateBasePackage(basePackage);

		if (map.containsKey(basePackage))
		{
			return map.get(basePackage).get(key);
		}

		return null;
	}

	public void put(String basePackage, Class<?> key, T value)
	{
		if (!map.containsKey(basePackage))
		{
			synchronized (map)
			{
				if (!map.containsKey(basePackage))
				{
					map.put(basePackage, new ConcurrentHashMap<>());
				}
			}
		}

		map.get(basePackage).put(key, value);
	}

	public void remove(String basePackage)
	{
		map.remove(basePackage);
	}

	private void validateBasePackage(String basePackage)
	{
		if (null == basePackage)
		{
			throw new EasywireException("basePackage must be set on the EasywireBeanFactory, consider using your top package.");
		}
	}
}
