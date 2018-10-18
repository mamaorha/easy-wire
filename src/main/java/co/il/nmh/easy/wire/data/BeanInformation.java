package co.il.nmh.easy.wire.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import co.il.nmh.easy.wire.core.BeanHolder;
import co.il.nmh.easy.wire.core.EasywireBeanFactory;
import co.il.nmh.easy.wire.exception.EasywireException;
import lombok.ToString;

/**
 * @author Maor Hamami
 */

@ToString
public class BeanInformation
{
	private Class<?> beanClass;
	private List<Method> methods;
	private Map<Class<?>, Set<BeanHolder>> beanHolderMap;
	private Map<Class<?>, BeanHolder> primaryBeanHolderMap;
	private AtomicBoolean init = new AtomicBoolean(false);

	public BeanInformation(Class<?> beanClass)
	{
		this.beanClass = beanClass;
		this.methods = new ArrayList<>();
		this.primaryBeanHolderMap = new ConcurrentHashMap<>();
	}

	public void addMethod(Method beanMethod)
	{
		methods.add(beanMethod);
	}

	public Object getBean(Set<Class<?>> classTrace)
	{
		return getBean(beanClass, classTrace);
	}

	public <T> T getBean(Class<T> classT, Set<Class<?>> classTrace)
	{
		return classT.cast(getBeanHolder(classT, classTrace).getBean(classTrace));
	}

	public boolean isPrototype()
	{
		return getBeanHolder(beanClass, new LinkedHashSet<>()).isPrototype();
	}

	public int order(Class<?> clazz)
	{
		return getBeanHolder(clazz, new LinkedHashSet<>()).getOrder();
	}

	public void setBeanHolder(Class<?> clazz, BeanHolder beanHolder)
	{
		primaryBeanHolderMap.put(clazz, beanHolder);
	}

	private BeanHolder getBeanHolder(Class<?> clazz, Set<Class<?>> classTrace)
	{
		init(classTrace);

		if (!primaryBeanHolderMap.containsKey(clazz))
		{
			int matches = 0;
			boolean hasPrimary = false;
			BeanHolder selectedBeanHolder = null;

			for (Set<BeanHolder> beanHolderSet : beanHolderMap.values())
			{
				for (BeanHolder beanHolder : beanHolderSet)
				{
					if (clazz.isAssignableFrom(beanHolder.getRealClass()))
					{
						matches++;
						hasPrimary = hasPrimary || beanHolder.isPrimary();

						if (null != selectedBeanHolder)
						{
							if (selectedBeanHolder.isPrimary() && beanHolder.isPrimary())
							{
								throw new EasywireException("bean {} is defined as primary more than once, info {},{}", clazz, selectedBeanHolder, beanHolder);
							}

							else if (beanHolder.isPrimary())
							{
								selectedBeanHolder = beanHolder;
							}
						}

						else
						{
							selectedBeanHolder = beanHolder;
						}
					}
				}
			}

			if (!hasPrimary && matches > 1)
			{
				throw new EasywireException("bean {} has more than one implementation, please define one of them as primary");
			}

			if (null == selectedBeanHolder)
			{
				throw new EasywireException("couldn't find bean implementation for {}", clazz);
			}

			primaryBeanHolderMap.put(clazz, selectedBeanHolder);
		}

		return primaryBeanHolderMap.get(clazz);
	}

	private void init(Set<Class<?>> classTrace)
	{
		if (!init.get())
		{
			beanHolderMap = new HashMap<>();

			boolean isClassBean = EasywireBeanFactory.INSTANCE.isBean(beanClass);

			if (isClassBean)
			{
				if (!beanClass.isInterface())
				{
					BeanHolder beanHolder = new BeanHolder(beanClass, classTrace);
					Class<?> realClass = beanHolder.getRealClass();

					if (!beanHolderMap.containsKey(realClass))
					{
						beanHolderMap.put(realClass, new HashSet<>());
					}

					Set<BeanHolder> set = beanHolderMap.get(realClass);
					set.add(beanHolder);
				}
			}

			for (Method beanMethod : methods)
			{
				BeanHolder newHolder = new BeanHolder(beanMethod, classTrace);

				if (!beanHolderMap.containsKey(newHolder.getRealClass()))
				{
					beanHolderMap.put(newHolder.getRealClass(), new HashSet<>());
				}

				beanHolderMap.get(newHolder.getRealClass()).add(newHolder);
			}

			init.set(true);
		}
	}

	public void clear()
	{
		for (BeanHolder beanHolder : primaryBeanHolderMap.values())
		{
			beanHolder.clear();
		}
	}

	public boolean isLoaded(Class<?> clazz)
	{
		return getBeanHolder(clazz, new LinkedHashSet<>()).isLoaded();
	}
}
