package co.il.nmh.easy.wire.utils.mock.persistence;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;

import co.il.nmh.easy.utils.reflection.ClassInvestigator;
import co.il.nmh.easy.utils.reflection.FieldsInvestigator;
import co.il.nmh.easy.wire.exception.EasywireMockException;

/**
 * @author Maor Hamami
 *
 */

public class CrudRepositoryMock<V, K> implements CrudRepository<V, K>
{
	protected Map<K, V> entities;
	protected Field idField;

	@SuppressWarnings("unchecked")
	public CrudRepositoryMock()
	{
		entities = new LinkedHashMap<>();

		Class<V> classV = (Class<V>) ClassInvestigator.INSTANCE.getClassTypes(getClass()).get(0);
		Collection<Field> classFields = FieldsInvestigator.INSTANCE.getClassFields(classV);

		for (Field field : classFields)
		{
			if (field.isAnnotationPresent(Id.class))
			{
				idField = field;
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public K getKey(V entity)
	{
		if (null == idField)
		{
			throw new EasywireMockException("couldn't find field annotated with @Id to use as key");
		}

		return (K) FieldsInvestigator.INSTANCE.getFieldValue(idField, entity);
	}

	@Override
	public <S extends V> S save(S entity)
	{
		if (null != entity)
		{
			entities.put(getKey(entity), entity);
		}

		return entity;
	}

	@Override
	public <S extends V> Iterable<S> saveAll(Iterable<S> entities)
	{
		List<S> savedEntities = new ArrayList<>();

		for (S entity : entities)
		{
			savedEntities.add(save(entity));
		}

		return savedEntities;
	}

	@Override
	public Optional<V> findById(K id)
	{
		V entity = entities.get(id);

		if (null == entity)
		{
			return Optional.ofNullable(null);
		}

		return Optional.of(entity);
	}

	@Override
	public boolean existsById(K id)
	{
		return entities.containsKey(id);
	}

	@Override
	public Iterable<V> findAll()
	{
		return new ArrayList<>(entities.values());
	}

	@Override
	public Iterable<V> findAllById(Iterable<K> ids)
	{
		List<V> foundEntities = new ArrayList<>();

		for (K id : ids)
		{
			Optional<V> entity = findById(id);

			if (entity.isPresent())
			{
				foundEntities.add(entity.get());
			}
		}

		return foundEntities;
	}

	@Override
	public long count()
	{
		return entities.size();
	}

	@Override
	public void deleteById(K id)
	{
		entities.remove(id);
	}

	@Override
	public void delete(V entity)
	{
		entities.remove(getKey(entity));
	}

	@Override
	public void deleteAll(Iterable<? extends V> entities)
	{
		for (V entity : entities)
		{
			delete(entity);
		}
	}

	@Override
	public void deleteAll()
	{
		entities.clear();
	}
}
