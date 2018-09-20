package co.il.nmh.easy.wire.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import co.il.nmh.easy.wire.core.base.IEasywireInitializer;

/**
 * @author Maor Hamami & Yakir Amar
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface EasywireProperties
{
	/**
	 * property file name, should be put under resources
	 */
	String propertyFile() default "application.yml";

	/**
	 * spring profile under the yaml
	 */
	String propertyProfile() default "test";

	/**
	 * bean scan will be set from this packages
	 */
	String[] scanBasePackages() default {};

	/**
	 * clean all beans and start a new context (when working with few junit classes Easywire uses the same context un less this is set to true)
	 */
	boolean cleanEnvironment() default false;

	/**
	 * load specific bean classes before the tests, Easywire only loads the beans that are required to run the test - if you wish to run specific singletons to perform logic or configuration classes, should announce them here.
	 */
	Class<?>[] loadBeans() default {};

	/**
	 * class that will be loaded before the tests, usually used to override beans before the flow
	 */
	Class<? extends IEasywireInitializer> initializeClass() default DEFAULT.class;

	static final class DEFAULT implements IEasywireInitializer
	{
		@Override
		public void initialize()
		{
		}
	}
}
