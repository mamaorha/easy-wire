package co.il.nmh.easy.wire.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Maor Hamami
 *
 */

@Getter
@ToString
public class TypeWithAnnotation
{
	private Type type;
	private Map<Class<?>, Annotation> annotationsMap = new HashMap<>();

	private TypeWithAnnotation()
	{
	}

	public TypeWithAnnotation(Type type, Annotation[] annotations)
	{
		this.type = type;

		for (Annotation annotation : annotations)
		{
			annotationsMap.put(annotation.annotationType(), annotation);
		}
	}

	public TypeWithAnnotation(Type type, Map<Class<?>, Annotation> annotationsMap)
	{
		this.type = type;
		this.annotationsMap = annotationsMap;
	}

	public static List<TypeWithAnnotation> build(Type[] genericParameterTypes, Annotation[][] parameterAnnotations)
	{
		List<TypeWithAnnotation> array = new ArrayList<>();

		for (int i = 0; i < genericParameterTypes.length; i++)
		{
			TypeWithAnnotation typeWithAnnotation = new TypeWithAnnotation();
			typeWithAnnotation.type = genericParameterTypes[i];

			for (int j = 0; j < parameterAnnotations[i].length; j++)
			{
				Annotation annotation = parameterAnnotations[i][j];
				typeWithAnnotation.annotationsMap.put(annotation.annotationType(), annotation);
			}

			array.add(typeWithAnnotation);
		}

		return array;
	}

	public <T> T getAnnotation(Class<T> clazz)
	{
		return clazz.cast(annotationsMap.get(clazz));
	}
}
