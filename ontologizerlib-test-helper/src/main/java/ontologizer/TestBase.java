package ontologizer;

import java.io.IOException;

import org.junit.Rule;
import org.junit.rules.TestName;

import sonumina.test.TestClass;

public class TestBase
{
	@Rule
	public TestName testName = new TestName();

	@Rule
	public TestClass testClass = new TestClass();

	/**
	 * Return the path to a file that contains the comment of the current test.
	 *
	 * @return an absolute path.
	 *
	 * @throws IOException
	 */
	protected String getTestCommentAsPath() throws IOException
	{
		/* TODO: We could also make the source avaible as a dedicated rule */
		return TestSourceUtils.getCommentOfTestAsTmpFilePath(testClass.getTestClass(),
			testName.getMethodName());
	}
}
