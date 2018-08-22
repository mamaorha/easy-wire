package co.il.nmh.easy.wire.core.base;

/**
 * @author Maor Hamami
 * 
 *         class that will be loaded before the tests, usually used to override beans before the flow
 */

public interface IEasywireInitializer
{
	void initialize();
}
