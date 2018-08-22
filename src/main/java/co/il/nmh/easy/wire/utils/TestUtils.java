package co.il.nmh.easy.wire.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;

/**
 * @author Maor Hamami
 */

public class TestUtils
{
	public static String readClasspathResource(String resource)
	{
		try
		{
			ClassPathResource classPathResource = new ClassPathResource(resource);
			InputStream inputStream = classPathResource.getInputStream();

			return readInputStream(inputStream);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String readInputStream(InputStream resourceAsStream)
	{
		if (null == resourceAsStream)
		{
			return null;
		}

		Scanner scanner = null;

		try
		{
			scanner = new Scanner(resourceAsStream, "UTF-8");
			String resource = scanner.useDelimiter("\\A").next();

			return resource;
		}
		finally
		{
			scanner.close();

			try
			{
				resourceAsStream.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	public static String writeDataObjectTests(Class<?> clazz) throws ClassNotFoundException
	{
		StringBuilder stringBuilder = new StringBuilder().append("import org.junit.Before;").append(System.lineSeparator()).append(System.lineSeparator()).append("@RunWith(EasywireRunner.class)").append(System.lineSeparator()).append("public class ").append(clazz.getSimpleName()).append("Test").append(System.lineSeparator()).append("{").append(System.lineSeparator()).append("\tprivate ").append(clazz.getSimpleName()).append(" data;").append(System.lineSeparator()).append(System.lineSeparator()).append("\t@Before").append(System.lineSeparator()).append("\tpublic void setters()").append(System.lineSeparator()).append("\t{").append(System.lineSeparator()).append("\t\tdata = new ").append(clazz.getSimpleName()).append("();").append(System.lineSeparator());
		Method[] methods = clazz.getMethods();
		for (Method method : methods)
		{
			if (!method.getName().equals("wait") && !method.getName().equals("notify") && !method.getName().equals("notifyAll") && method.getParameterCount() == 1)
			{
				Class<?> parameterClass = method.getParameterTypes()[0];
				String simpleName = parameterClass.getSimpleName();
				if (Character.isLowerCase(simpleName.charAt(0)))
				{
					simpleName = String.valueOf(simpleName.charAt(0)).toUpperCase() + simpleName.substring(1, simpleName.length());
				}
				stringBuilder.append("\t\tdata.").append(method.getName()).append("(new ").append(simpleName).append("(");
				if (!parameterClass.isArray())
				{
					try
					{
						parameterClass.newInstance();
					}
					catch (Exception e)
					{
						stringBuilder.append("\"0\"");
					}
				}
				else
				{
					stringBuilder.append("{}");
				}
				stringBuilder.append(")").append(");").append(System.lineSeparator());
			}
		}
		stringBuilder.append("\t}").append(System.lineSeparator()).append(System.lineSeparator()).append("\t@Test").append(System.lineSeparator()).append("\tpublic void getters()").append(System.lineSeparator()).append("\t{").append(System.lineSeparator());
		for (Method method : methods)
		{
			if (!method.getName().equals("wait") && !method.getName().equals("notify") && !method.getName().equals("notifyAll") && method.getParameterCount() == 0)
			{
				stringBuilder.append("\t\tdata.").append(method.getName()).append("();").append(System.lineSeparator());
			}
		}
		stringBuilder.append("\t}").append(System.lineSeparator()).append("}");
		return stringBuilder.toString();
	}
}
