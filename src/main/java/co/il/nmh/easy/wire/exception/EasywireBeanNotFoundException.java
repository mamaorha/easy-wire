package co.il.nmh.easy.wire.exception;

/**
 * @author Maor Hamami
 *
 */

public class EasywireBeanNotFoundException extends EasywireException
{
	private static final long serialVersionUID = -3962158825778175191L;

	public EasywireBeanNotFoundException(Class<?> clazz)
	{
		super("couldn't find bean implementation for {}", clazz);
	}
}
