package co.il.nmh.easy.wire.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import co.il.nmh.easy.wire.utils.ClassHelper;

/**
 * @author Maor Hamami
 */

public class TempConfigurationManager
{
	private Map<Class<?>, Object> configurationInstances = new ConcurrentHashMap<>();

	public <T> T getTempConfiguration(Class<T> clazz, Set<Class<?>> classTrace) throws ClassNotFoundException
	{
		if (!configurationInstances.containsKey(clazz))
		{
			synchronized (configurationInstances)
			{
				if (!configurationInstances.containsKey(clazz))
				{
					Object tempConfigurationInstance = ClassHelper.buildInstance(clazz, false, classTrace);
					configurationInstances.put(clazz, tempConfigurationInstance);

					EasywireBeanFactory.INSTANCE.initBean(tempConfigurationInstance, true, true, classTrace);
				}
			}
		}

		return clazz.cast(configurationInstances.get(clazz));
	}
}
