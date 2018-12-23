package co.il.nmh.easy.wire.core.base;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.AccessControlContext;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.util.StringValueResolver;

/**
 * @author Maor Hamami
 * 
 *         This class only exist so the EasywireBeanFactory class will look more clean
 */

public class BeanFactoryStub implements ConfigurableListableBeanFactory, ConfigurableApplicationContext
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

	@Override
	public void start()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void stop()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isRunning()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setId(String id)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setParent(ApplicationContext parent)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public ConfigurableEnvironment getEnvironment()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setEnvironment(ConfigurableEnvironment environment)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void addApplicationListener(ApplicationListener<?> listener)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void addProtocolResolver(ProtocolResolver resolver)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void refresh() throws BeansException, IllegalStateException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void registerShutdownHook()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void close()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isActive()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public <T> T createBean(Class<T> beanClass) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void autowireBean(Object existingBean) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object configureBean(Object existingBean, String beanName) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object initializeBean(Object existingBean, String beanName) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void destroyBean(Object existingBean)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setBeanClassLoader(ClassLoader beanClassLoader)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public ClassLoader getBeanClassLoader()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setTempClassLoader(ClassLoader tempClassLoader)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public ClassLoader getTempClassLoader()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setCacheBeanMetadata(boolean cacheBeanMetadata)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isCacheBeanMetadata()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setBeanExpressionResolver(BeanExpressionResolver resolver)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public BeanExpressionResolver getBeanExpressionResolver()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setConversionService(ConversionService conversionService)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public ConversionService getConversionService()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void copyRegisteredEditorsTo(PropertyEditorRegistry registry)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setTypeConverter(TypeConverter typeConverter)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public TypeConverter getTypeConverter()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void addEmbeddedValueResolver(StringValueResolver valueResolver)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean hasEmbeddedValueResolver()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String resolveEmbeddedValue(String value)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public int getBeanPostProcessorCount()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void registerScope(String scopeName, Scope scope)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getRegisteredScopeNames()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Scope getRegisteredScope(String scopeName)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public AccessControlContext getAccessControlContext()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void resolveAliases(StringValueResolver valueResolver)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void setCurrentlyInCreation(String beanName, boolean inCreation)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isCurrentlyInCreation(String beanName)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void registerDependentBean(String beanName, String dependentBeanName)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getDependentBeans(String beanName)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getDependenciesForBean(String beanName)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void destroyBean(String beanName, Object beanInstance)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void destroyScopedBean(String beanName)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void destroySingletons()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void registerSingleton(String beanName, Object singletonObject)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object getSingleton(String beanName)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean containsSingleton(String beanName)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public String[] getSingletonNames()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public int getSingletonCount()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Object getSingletonMutex()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void ignoreDependencyType(Class<?> type)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void ignoreDependencyInterface(Class<?> ifc)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue)
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public Iterator<String> getBeanNamesIterator()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void clearMetadataCache()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void freezeConfiguration()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public boolean isConfigurationFrozen()
	{
		throw new RuntimeException("Method is not implemented");
	}

	@Override
	public void preInstantiateSingletons() throws BeansException
	{
		throw new RuntimeException("Method is not implemented");
	}
}
