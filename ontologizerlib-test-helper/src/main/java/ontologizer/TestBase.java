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
	 * @param options or'ed mask of options from TextSourceUtils.
	 * @param suffix the suffix of the file name to generate (should contain the dot)
	 * @return an absolute path.
	 *
	 * @throws IOException
	 */
	protected String getTestCommentAsPath(String suffix, int options) throws IOException
	{
		/* TODO: We could also make the source avaible as a dedicated rule */
		return TestSourceUtils.getCommentOfTestAsTmpFilePath(testClass.getTestClass(),
			testName.getMethodName(), suffix, options);
	}

	/**
	 * Return the path to a file that contains the comment of the current test.
	 *
	 * @param suffix the suffix of the file name to generate (should contain the dot)
	 *
	 * @return an absolute path.
	 *
	 * @throws IOException
	 */
	protected String getTestCommentAsPath(String suffix) throws IOException
	{
		return getTestCommentAsPath(suffix, 0);
	}
}
