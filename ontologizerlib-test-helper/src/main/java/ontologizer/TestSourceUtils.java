package ontologizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * This class holds some utils to deal with test sources within tests.
 * It mainly provides methods to extract some special comments of a test,
 * namely those that start with three backslashes and span until the
 * test annotation. For this to work, the Java source must be bundled within
 * the jar. Alternatively, it must be accessible in the "src/test/java" under
 * the current working directory.
 *
 * Note that several other assumption must be satisfied for these method to
 * work.
 *
 * @author Sebastian Bauer
 */
public class TestSourceUtils
{
	public final static int DECODE_TABS = 1 << 0;

	/**
	 * Return the java source to the given class.
	 *
	 * @param cl the class from which to extract the source
	 * @return the input stream of the source code of the given class
	 */
	public static InputStream getJavaSource(Class<?> cl)
	{
		/* In case we are running from an IDE such as Eclipse we should read
		 * directly from the source directory  (as testResources field in
		 * the pom file didn't work). We assume that there is a standard
		 * Maven structure.
		 */
		String wd = System.getProperty("user.dir");
		String path = wd + "/src/test/java/" + cl.getCanonicalName().replace('.', '/') + ".java";
		try
		{
			return new FileInputStream(path);
		} catch (FileNotFoundException e)
		{
			/* If this file couldn't been found, we try the bundled java source */
			String clName = cl.getSimpleName() + ".java";
			InputStream in = cl.getResourceAsStream(clName);
			if (in != null)
			{
				return in;
			}
		}
		throw new IllegalArgumentException("Could not find Java source for class \"" + cl.getCanonicalName() + "\"");
	}

	/**
	 * @param cl the class from which to extract the source
	 * @return access to the source via an InputStreamReader.
	 */
	public static InputStreamReader getJavaSourceReader(Class<?> cl)
	{
		return new InputStreamReader(getJavaSource(cl));
	}

	/**
	 * Return the comment of a given test as string.
	 *
	 * @param cl the class from which to extract the source
	 * @param testName the name of the test for which to extract the comment
	 * @return the comment
	 * @throws IOException
	 */
	public static String getCommentOfTest(Class<?> cl, String testName, int options) throws IOException
	{
		BufferedReader br = new BufferedReader(getJavaSourceReader(cl));
		StringBuilder comment = new StringBuilder();
		String line;
		try
		{
			while ((line = br.readLine()) != null)
			{
				line = line.trim();
				if (line.startsWith("///"))
				{
					comment.append(line.substring(3).trim());
					comment.append("\n");
					continue;
				}

				/* Tests must all be public. We assume they are written on the same line */
				if (line.contains("public") && line.contains(testName + "()"))
				{
					String str = comment.toString();
					if ((options & DECODE_TABS) != 0)
					{
						return str.replace("\\t", "\t");
					}
					return str;
				}

				if (line.startsWith("@"))
				{
					continue;
				}
				comment.setLength(0);
			}
		} finally
		{
			br.close();
		}
		throw new IllegalArgumentException("No comment found for method\"" + testName + "\"");
	}

	/**
	 * Return a path to a temporary file that contains the comment of the given test.
	 *
	 * @param cl the class from where to extract the comment.
	 * @param testName the name of the test from which to extract the comment.
	 * @param options or'ed mask of options like DECODE_TABS.
	 * @return the absolute path to a temporary file that contains the comments of the given test.
	 * @throws IOException
	 */
	public static String getCommentOfTestAsTmpFilePath(Class<?> cl, String testName, int options) throws IOException
	{
		String comment = getCommentOfTest(cl, testName, options);
		File tmp = File.createTempFile("onto", ".obo");
		PrintWriter pw = new PrintWriter(tmp);
		pw.append(comment);
		pw.close();
		return tmp.getCanonicalPath();
	}

	/**
	 * Return a path to a temporary file that contains the comment of the given test.
	 *
	 * @param cl the class from where to extract the comment.
	 * @param testName the name of the test from which to extract the comment.
	 * @return the absolute path to a temporary file that contains the comments of the given test.
	 * @throws IOException
	 */
	public static String getCommentOfTestAsTmpFilePath(Class<?> cl, String testName) throws IOException
	{
		return getCommentOfTestAsTmpFilePath(cl, testName);
	}
}
