package co.il.nmh.easy.wire.exception;

import co.il.nmh.easy.wire.core.utils.LoggerTest;

/**
 * @author Maor Hamami
 */

public class EasywireException extends RuntimeException
{
	private static final long serialVersionUID = -2082916465521225180L;

	public EasywireException(String error, Object... params)
	{
		super(LoggerTest.format(error, params));
	}

	public EasywireException(Exception e)
	{
		super(e.getMessage());

		e.printStackTrace();
	}
}
