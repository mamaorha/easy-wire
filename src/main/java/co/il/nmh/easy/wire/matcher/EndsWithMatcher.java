package co.il.nmh.easy.wire.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author Maor Hamami
 */

public class EndsWithMatcher extends BaseMatcher<String>
{
	private String expected;

	public EndsWithMatcher(String expected)
	{
		this.expected = expected;
	}

	@Override
	public boolean matches(Object paramObject)
	{
		return ((String) paramObject).endsWith(expected);
	}

	@Override
	public void describeTo(Description paramDescription)
	{
		paramDescription.appendText("Exception message to end with : " + expected);
	}
}
