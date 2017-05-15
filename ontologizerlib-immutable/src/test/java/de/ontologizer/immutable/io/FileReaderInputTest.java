package de.ontologizer.immutable.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for the {@link OntologyOboReader} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class FileReaderInputTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	// Object under test
	private FileReaderInput input;

	@Before
	public void setUp() throws Exception {
		File newFile = tmpFolder.newFile();
		PrintWriter printWriter = new PrintWriter(new FileWriter(newFile));
		printWriter.println("First line");
		printWriter.println("Second line");
		printWriter.println("Third line");
		printWriter.close();

		input = new FileReaderInput(newFile);
	}

	@Test
	public void test() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(input.inputStream()));
		Assert.assertEquals("First line", br.readLine());
		Assert.assertEquals("Second line", br.readLine());
		Assert.assertEquals("Third line", br.readLine());
		Assert.assertNull(br.readLine());
		br.close();
	}

}
