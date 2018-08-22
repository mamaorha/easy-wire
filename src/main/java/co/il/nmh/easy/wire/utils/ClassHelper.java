package co.il.nmh.easy.wire.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Set;

import co.il.nmh.easy.wire.core.EasywireBeanFactory;
import co.il.nmh.easy.wire.exception.EasywireException;

/**
 * @author Maor Hamami
 */

public class ClassHelper
{
	public static <T> T buildInstance(Class<T> clazz, boolean beanOnly, Set<Class<?>> classTrace) throws ClassNotFoundException
	{
		Constructor<?>[] constructors = clazz.getConstructors();

		if (constructors.length < 1)
		{
			throw new RuntimeException("Bean " + clazz + " must have public constructor");
		}

		Constructor<?> constructor = constructors[0];
		Object[] objects = buildMethodParameters(constructor.getGenericParameterTypes(), beanOnly, classTrace);

		// try to instantiate class
		try
		{
			T instance = clazz.cast(constructor.newInstance(objects));
			return instance;
		}
		catch (Exception e)
		{
			throw new EasywireException("Failed to instanciate class {} with the following error {}", clazz.getName(), e.getMessage());
		}
	}

	public static Object[] buildMethodParameters(Type[] parameterTypes, boolean beanOnly, Set<Class<?>> classTrace) throws ClassNotFoundException
	{
		Object[] objects = new Object[parameterTypes.length];

		for (int i = 0; i < parameterTypes.length; i++)
		{
			Type parameter = parameterTypes[i];
			Object beanByType = EasywireBeanFactory.INSTANCE.getBeanByType(parameter, beanOnly, classTrace);

			objects[i] = beanByType;
		}

		return objects;
	}
}
