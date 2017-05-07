package ontologizer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;

public class OBOParserBenchmark
{
	// FIXME: Bundle that resource
	private static final String obofile = "../../ontologizerlib-io/src/test/resources/gene_ontology.1_2.obo.gz";

	@Benchmark
	@Warmup(iterations=5)
	@Fork(value=1)
	@Measurement(time=2,timeUnit=TimeUnit.SECONDS)
	public OBOParser benchmarkOBOParser() throws IOException, OBOParserException
	{
		OBOParser oboParser = new OBOParser(new OBOParserFileInput(obofile));
		oboParser.doParse();
		return oboParser;
	}
}
