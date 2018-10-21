package co.il.nmh.easy.wire.utils.mock.async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

import org.reflections.Reflections;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;

import co.il.nmh.easy.wire.core.EasywireBeanFactory;
import co.il.nmh.easy.wire.core.base.IEasywireBaseInitializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockSources implements IEasywireBaseInitializer
{
	@Override
	public void initialize()
	{
		try
		{
			Reflections reflections = EasywireBeanFactory.INSTANCE.getReflections();
			Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(Output.class);

			if (!methodsAnnotatedWith.isEmpty())
			{
				SubscribableChannel subscribableChannel = SubscribableChannelBuilder.build();

				for (Method currMethod : methodsAnnotatedWith)
				{
					Class<?> declaringClass = currMethod.getDeclaringClass();

					Object proxyInstance = Proxy.newProxyInstance(declaringClass.getClassLoader(), new Class[] { declaringClass }, new InvocationHandler()
					{
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
						{
							if (currMethod.getName() == method.getName())
							{
								return subscribableChannel;
							}

							return null;
						}
					});

					log.info("Using mock implementation for @Output annotation, '{}', method '{}'", currMethod.getDeclaringClass(), currMethod.getName());
					EasywireBeanFactory.INSTANCE.pushBean(declaringClass, proxyInstance, false);
				}
			}
		}
		catch (Exception | NoClassDefFoundError | NoSuchMethodError e)
		{
		}
	}

	private static class SubscribableChannelBuilder
	{
		public static SubscribableChannel build()
		{
			return new SubscribableChannel()
			{
				@Override
				public boolean send(Message<?> message, long timeout)
				{
					return false;
				}

				@Override
				public boolean send(Message<?> message)
				{
					return false;
				}

				@Override
				public boolean unsubscribe(MessageHandler handler)
				{
					return false;
				}

				@Override
				public boolean subscribe(MessageHandler handler)
				{
					return false;
				}
			};
		}
	}
}