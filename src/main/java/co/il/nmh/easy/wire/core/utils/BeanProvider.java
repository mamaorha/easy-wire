package co.il.nmh.easy.wire.core.utils;

import javax.inject.Provider;

import co.il.nmh.easy.wire.core.EasywireBeanFactory;

/**
 * @author Maor Hamami
 */

public class BeanProvider<T> implements Provider<T>
{
	protected Class<T> classT;
	protected boolean beanOnly;

	private BeanProvider(Class<T> classT, boolean beanOnly)
	{
		this.classT = classT;
		this.beanOnly = beanOnly;
	}

	public static <T> BeanProvider<T> getBeanProviderObject(Class<T> clazz, boolean beanOnly)
	{
		return new BeanProvider<T>(clazz, beanOnly);
	}

	@Override
	public T get()
	{
		return EasywireBeanFactory.INSTANCE.getBean(classT, beanOnly);
	}
}
