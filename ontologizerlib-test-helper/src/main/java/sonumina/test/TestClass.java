package sonumina.test;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * This rule makes the current class available inside test method.
 *
 * @author Sebastian Bauer
 */
public class TestClass implements TestRule
{
	private Class<?> clazz;

	@Override
	public Statement apply(Statement base, Description description)
	{
		clazz = description.getTestClass();
		return base;
	}

	public Class<?> getTestClass()
	{
		return clazz;
	}
}
