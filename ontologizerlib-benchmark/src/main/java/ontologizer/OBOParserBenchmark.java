package ontologizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import ontologizer.io.OBOParserFileInput;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;

@State(Scope.Benchmark)
public class OBOParserBenchmark
{
	private static final String OBO_NAME = "gene_ontology.1_2.obo.gz";

	private static String oboFilename;

	static
	{
		/* Determine an oboFilename that can be used to directly read from the filesystem */
		try
		{
			ClassLoader cl = OBOParserBenchmark.class.getClassLoader();
			oboFilename = cl.getResource(OBO_NAME).getPath();
			if (oboFilename.contains("!/"))
			{
				/* The requested file is inside an archive, copy the contents to a temporary file */
				InputStream is = cl.getResourceAsStream(OBO_NAME);
				File tmpFile = File.createTempFile("obofile", ".obo.gz");
				tmpFile.deleteOnExit();
				Files.copy(is, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				oboFilename = tmpFile.getAbsolutePath();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Benchmark
	@Warmup(iterations=5)
	@Fork(value=1)
	@Measurement(time=2,timeUnit=TimeUnit.SECONDS)
	public OBOParser benchmarkOBOParser() throws IOException, OBOParserException
	{
		OBOParser oboParser = new OBOParser(new OBOParserFileInput(oboFilename));
		oboParser.doParse();
		return oboParser;
	}
}
