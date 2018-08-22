package co.il.nmh.easy.wire.data;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @author maorh
 *
 */
@ToString
@AllArgsConstructor
public class OrderObject implements Comparable<OrderObject>
{
	private int order;
	private Object object;

	public Object getObject()
	{
		return object;
	}

	@Override
	public int compareTo(OrderObject orderObject)
	{
		return ((Integer) order).compareTo(orderObject.order);  		
	}
}
