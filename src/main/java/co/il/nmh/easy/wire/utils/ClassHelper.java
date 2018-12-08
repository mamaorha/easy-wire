package co.il.nmh.easy.wire.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import co.il.nmh.easy.wire.core.EasywireBeanFactory;
import co.il.nmh.easy.wire.data.TypeWithAnnotation;
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

		Constructor<?> validConstructor = null;
		Object[] objects = null;
		EasywireException exception = null;

		for (Constructor<?> constructor : constructors)
		{
			try
			{
				Type[] genericParameterTypes = constructor.getGenericParameterTypes();
				Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();

				objects = buildMethodParameters(TypeWithAnnotation.build(genericParameterTypes, parameterAnnotations), beanOnly, classTrace);
				validConstructor = constructor;

				break;
			}
			catch (Exception e)
			{
				exception = new EasywireException("failed to invoke constructor for class {}, error: {}", clazz, e.getMessage());
			}
		}

		if (null == validConstructor)
		{
			throw exception;
		}

		// try to instantiate class
		try
		{
			T instance = clazz.cast(validConstructor.newInstance(objects));
			return instance;
		}
		catch (Exception e)
		{
			String error = e.getMessage();

			if (null == error && e instanceof InvocationTargetException)
			{
				InvocationTargetException invocationTargetException = ((InvocationTargetException) e);

				if (null != invocationTargetException.getTargetException())
				{
					error = invocationTargetException.getTargetException().getMessage();
				}
			}

			throw new EasywireException("Failed to instanciate class {} with the following error: {}", clazz.getName(), error);
		}
	}

	public static Object[] buildMethodParameters(List<TypeWithAnnotation> typesWithAnnotation, boolean beanOnly, Set<Class<?>> classTrace) throws ClassNotFoundException
	{
		Object[] objects = new Object[typesWithAnnotation.size()];

		for (int i = 0; i < typesWithAnnotation.size(); i++)
		{
			TypeWithAnnotation typeWithAnnotation = typesWithAnnotation.get(i);

			Object beanByType = EasywireBeanFactory.INSTANCE.getBeanByType(typeWithAnnotation, beanOnly, classTrace);

			objects[i] = beanByType;
		}

		return objects;
	}
}
