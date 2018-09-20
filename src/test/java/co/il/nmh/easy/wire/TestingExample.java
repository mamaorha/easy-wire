package co.il.nmh.easy.wire;

import org.junit.Test;
import org.junit.runner.RunWith;

import co.il.nmh.easy.wire.annotation.EasywireProperties;

/**
 * @author Maor Hamami
 */

@RunWith(EasywireRunner.class)
@EasywireProperties(scanBasePackages = "co.il.nmh")
public class TestingExample
{
	@Test
	public void example()
	{
		// you tests goes here.
	}
}
