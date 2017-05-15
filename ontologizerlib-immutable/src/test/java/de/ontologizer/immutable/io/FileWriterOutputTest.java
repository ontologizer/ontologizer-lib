package de.ontologizer.immutable.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
public class FileWriterOutputTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	// Object under test
	private FileWriterOutput output;

	private File outputFile;

	@Before
	public void setUp() throws Exception {
		outputFile = tmpFolder.newFile();

		output = new FileWriterOutput(outputFile);
	}

	@Test
	public void test() throws IOException {
		final PrintWriter printer = new PrintWriter(output.outputStream());
		printer.println("First Line");
		printer.println("Second Line");
		printer.close();

		BufferedReader br = new BufferedReader(new FileReader(outputFile));
		Assert.assertEquals("First Line", br.readLine());
		Assert.assertEquals("Second Line", br.readLine());
		Assert.assertEquals(null, br.readLine());
		br.close();
	}

}
