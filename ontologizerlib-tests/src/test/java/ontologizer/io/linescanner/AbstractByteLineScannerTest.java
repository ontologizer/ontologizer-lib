package ontologizer.io.linescanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Test;
import ontologizer.io.linescanner.AbstractByteLineScanner;
import ontologizer.io.obo.OntologyTest;

public class AbstractByteLineScannerTest
{
	@Test
	public void testBigFile() throws FileNotFoundException, IOException
	{
		String oboPath = OntologyTest.class.
				getClassLoader().getResource("gene_ontology.1_2.obo.gz").getPath();
		InputStream	is = new GZIPInputStream(new FileInputStream(oboPath));

		final BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(oboPath))));

		class TestByteLineScanner extends AbstractByteLineScanner
		{
			public int actualLineCount;
			public int expectedLineCount;

			public TestByteLineScanner(InputStream is)
			{
				super(is);
			}

			@Override
			public boolean newLine(byte[] buf, int start, int len)
			{
				actualLineCount++;

				StringBuilder actualString = new StringBuilder();
				for (int i=start;i<start+len;i++)
					actualString.append((char)buf[i]);
				String expectedString;
				try {
					expectedString = br.readLine();
					expectedLineCount++;
				} catch (IOException e) { throw new RuntimeException(e);}
				Assert.assertEquals(expectedString,actualString.toString());
				return true;
			}
		};

		TestByteLineScanner tbls = new TestByteLineScanner(is);
		tbls.scan();

		assertEquals(tbls.expectedLineCount,tbls.actualLineCount);
		assertNull(br.readLine());

		br.close();
	}

	@Test
	public void testMissingNewLineAtLineEnd() throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream("test\ntest2".getBytes());
		class TestByteLineScanner extends AbstractByteLineScanner
		{
			public int lines;

			public TestByteLineScanner(InputStream is)
			{
				super(is);
			}

			@Override
			public boolean newLine(byte[] buf, int start, int len)
			{
				lines++;
				return true;
			}
		}

		TestByteLineScanner tbls = new TestByteLineScanner(bais);
		tbls.scan();
		assertEquals(2, tbls.lines);
	}

	@Test
	public void testAvailable() throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream("test\ntest2\n\test3\n".getBytes());
		class TestByteLineScanner extends AbstractByteLineScanner
		{
			public TestByteLineScanner(InputStream is)
			{
				super(is);
			}

			@Override
			public boolean newLine(byte[] buf, int start, int len)
			{
				return false;
			}
		}

		TestByteLineScanner tbls = new TestByteLineScanner(bais);
		tbls.scan();
		assertEquals(12, tbls.available());

		byte [] expected = "test2\n\test3\n".getBytes();
		byte [] actual = tbls.availableBuffer();
		assertEquals(12, expected.length);
		for (int i=0; i<12; i++)
			assertEquals(expected[i], actual[i]);
	}

	@Test
	public void testPush() throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream("test\ntest2".getBytes());
		class TestByteLineScanner extends AbstractByteLineScanner
		{
			public List<String> lines = new ArrayList<String>();

			public TestByteLineScanner(InputStream is)
			{
				super(is);
			}

			@Override
			public boolean newLine(byte[] buf, int start, int len)
			{
				lines.add(new String(buf, start,len));
				return true;
			}
		}

		TestByteLineScanner tbls = new TestByteLineScanner(bais);
		tbls.push("test-1\n\n".getBytes());
		tbls.scan();

		assertEquals(4, tbls.lines.size());
		assertEquals("test-1", tbls.lines.get(0));
		assertEquals("", tbls.lines.get(1));
		assertEquals("test", tbls.lines.get(2));
		assertEquals("test2", tbls.lines.get(3));
	}
}
