package co.il.nmh.easy.wire.core.utils;

import javax.inject.Provider;

import co.il.nmh.easy.wire.core.EasywireBeanFactory;

/**
 * @author Maor Hamami
 */

public class BeanProvider<T> implements Provider<T>
{
	protected Class<T> classT;

	private BeanProvider(Class<T> classT)
	{
		this.classT = classT;
	}

	public static <T> BeanProvider<T> getBeanProviderObject(Class<T> clazz)
	{
		return new BeanProvider<T>(clazz);
	}

	@Override
	public T get()
	{
		return EasywireBeanFactory.INSTANCE.getBean(classT);
	}
}
