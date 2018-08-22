package co.il.nmh.easy.wire.core.base;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

/**
 * @author Maor Hamami
 * 
 *         This class only exist so the EasywireBeanFactory class will look more clean
 */

public class BeanFactoryStub implements BeanFactory, ApplicationContext
{
	@Override
	public Object getBean(String name) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean containsBean(String name)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getAliases(String name)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Environment getEnvironment()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean containsBeanDefinition(String beanName)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public int getBeanDefinitionCount()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getBeanDefinitionNames()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public BeanFactory getParentBeanFactory()
	{
		return this;
	}

	@Override
	public boolean containsLocalBean(String name)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void publishEvent(ApplicationEvent event)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void publishEvent(Object event)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Resource getResource(String location)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public ClassLoader getClassLoader()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String getId()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String getApplicationName()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String getDisplayName()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public long getStartupDate()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public ApplicationContext getParent()
	{
		return this;
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException
	{
		throw new RuntimeException("Method is not implemented");
	}
}
