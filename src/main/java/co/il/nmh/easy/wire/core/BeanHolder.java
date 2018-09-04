package co.il.nmh.easy.wire.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.mockito.Mockito;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;

import co.il.nmh.easy.wire.exception.EasywireException;
import co.il.nmh.easy.wire.utils.ClassHelper;

/**
 * @author Maor Hamami
 */

public class BeanHolder
{
	private Object bean; // method / class
	private Object beanInstance; // in case its not a prototype
	private boolean isPrototype;
	private boolean isPrimary;
	private Integer order = null;
	private Class<?> reallClass;
	private boolean beanOnly;

	public BeanHolder(Object primary, Set<Class<?>> classTrace)
	{
		this(primary, true, classTrace);
	}

	public BeanHolder(Object primary, boolean beanOnly, Set<Class<?>> classTrace)
	{
		this.beanOnly = beanOnly;

		Scope scope = null;

		// bean by method
		if (primary instanceof Method)
		{
			scope = ((Method) primary).getAnnotation(Scope.class);
			isPrimary = ((Method) primary).isAnnotationPresent(Primary.class);

			Order order = ((Method) primary).getAnnotation(Order.class);

			if (null != order)
			{
				this.order = order.value();
			}
		}

		// bean by class
		else if (primary instanceof Class<?>)
		{
			scope = ((Class<?>) primary).getAnnotation(Scope.class);
			isPrimary = ((Class<?>) primary).isAnnotationPresent(Primary.class);

			Order order = ((Class<?>) primary).getAnnotation(Order.class);

			if (null != order)
			{
				this.order = order.value();
			}
		}

		else
		{
			Order order = primary.getClass().getAnnotation(Order.class);

			if (null != order)
			{
				this.order = order.value();
			}

			beanInstance = primary;
		}

		if (null != scope)
		{
			isPrototype = "prototype".equalsIgnoreCase(scope.value());
		}

		this.bean = primary;

		getBean(classTrace);
	}

	public Object getBean(Set<Class<?>> classTrace)
	{
		if (null != beanInstance)
		{
			return beanInstance;
		}

		Object instance = getInstance(classTrace);

		if (null == reallClass)
		{
			reallClass = instance.getClass();

			if (null == order)
			{
				Order order = reallClass.getAnnotation(Order.class);

				if (null == order)
				{
					this.order = Integer.MAX_VALUE;
				}

				else
				{
					this.order = order.value();
				}
			}

			return instance;
		}

		if (!isPrototype)
		{
			beanInstance = instance;
		}

		return instance;
	}

	public boolean isPrototype()
	{
		return isPrototype;
	}

	public boolean isPrimary()
	{
		return isPrimary;
	}

	public int getOrder()
	{
		if (null == order)
		{
			order = Integer.MAX_VALUE;
		}

		return order;
	}

	public Class<?> getRealClass()
	{
		return reallClass;
	}

	private Object getInstance(Set<Class<?>> classTrace)
	{
		try
		{
			Object newInstance = null;

			if (bean instanceof Class<?>)
			{
				Class<?> clazz = (Class<?>) bean;
				newInstance = ClassHelper.buildInstance(clazz, beanOnly, classTrace);

				if (null != reallClass)
				{
					if (newInstance.getClass().isAnnotationPresent(Configuration.class))
					{
						beanInstance = newInstance;
					}

					EasywireBeanFactory.INSTANCE.initBean(newInstance, beanOnly, false, classTrace);
				}
			}

			else if (bean instanceof Method)
			{
				Method method = (Method) bean;
				Class<?> declaringConfigurationClass = method.getDeclaringClass();

				Object tempConfigurationInstance = null;

				if (EasywireBeanFactory.INSTANCE.isBeanLoaded(declaringConfigurationClass))
				{
					classTrace.remove(declaringConfigurationClass);
					tempConfigurationInstance = EasywireBeanFactory.INSTANCE.getBean(declaringConfigurationClass, true, classTrace);
				}

				else if (null == tempConfigurationInstance)
				{
					tempConfigurationInstance = EasywireBeanFactory.INSTANCE.getBean(TempConfigurationManager.class, false, classTrace).getTempConfiguration(declaringConfigurationClass, classTrace);
				}

				Object[] parameters = ClassHelper.buildMethodParameters(method.getGenericParameterTypes(), beanOnly, classTrace);

				try
				{
					Object proxy = getProxy(tempConfigurationInstance, classTrace);
					newInstance = method.invoke(proxy, parameters);
				}
				catch (Exception e)
				{
					String error = e.getMessage();

					if (null == error && e instanceof InvocationTargetException)
					{
						error = ((InvocationTargetException) e).getTargetException().getMessage();
					}

					throw new EasywireException("failed to invoke @Bean method {} from class {}, error: {}", method.getName(), declaringConfigurationClass, error);
				}
			}

			else
			{
				newInstance = bean;
			}

			return newInstance;
		}
		catch (ClassNotFoundException e)
		{
			throw new EasywireException(e);
		}
	}

	private Object getProxy(Object instance, Set<Class<?>> classTrace)
	{
		try
		{
			Object proxy = Mockito.spy(instance);
			Field methodInterceptorField = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
			methodInterceptorField.setAccessible(true);

			MethodInterceptor methodInterceptor = (MethodInterceptor) methodInterceptorField.get(proxy);

			MethodInterceptor myMethodInterceptor = new MethodInterceptor()
			{
				@Override
				public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
				{
					Object intercept = methodInterceptor.intercept(obj, method, args, proxy);

					if (method.isAnnotationPresent(Bean.class))
					{
						EasywireBeanFactory.INSTANCE.initBean(intercept, beanOnly, false, classTrace);
					}

					return intercept;
				}
			};

			methodInterceptorField.set(proxy, myMethodInterceptor);

			return proxy;
		}
		catch (Exception e)
		{
			throw new EasywireException("failed to create proxy for instance " + instance.getClass());
		}
	}

	public void clear()
	{
		beanInstance = null;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bean == null) ? 0 : bean.hashCode());
		result = prime * result + (isPrimary ? 1231 : 1237);
		result = prime * result + (isPrototype ? 1231 : 1237);
		result = prime * result + ((reallClass == null) ? 0 : reallClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeanHolder other = (BeanHolder) obj;
		if (bean == null)
		{
			if (other.bean != null)
				return false;
		}
		else if (!bean.equals(other.bean))
			return false;
		if (isPrimary != other.isPrimary)
			return false;
		if (isPrototype != other.isPrototype)
			return false;
		if (reallClass == null)
		{
			if (other.reallClass != null)
				return false;
		}
		else if (!reallClass.equals(other.reallClass))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "BeanHolder [bean=" + bean + "]";
	}

	public boolean isLoaded()
	{
		return null != beanInstance;
	}
}
