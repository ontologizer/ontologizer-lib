package ontologizer.types;

import static ontologizer.types.ByteString.b;

import org.openjdk.jmh.annotations.Benchmark;

public class ByteStringBenchmark
{
	private static final ByteString BS = b("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

	@Benchmark
	public String benchmarkToString()
	{
		return BS.toString();
	}
}
