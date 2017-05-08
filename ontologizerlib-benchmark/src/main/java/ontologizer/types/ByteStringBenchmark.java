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

	private static final byte PIPE = (byte)'|';
	private static final ByteString TO_SPLIT_1 = b("str1|str2|str3|str4|str5");
	private static final ByteString TO_SPLIT_2 = b("str1|str2|str3|str4|str5|str6|str7|str8|str9|str10");

	@Benchmark
	public ByteString [] benchmarkSplitOf1()
	{
		return TO_SPLIT_1.split(PIPE);
	}

	@Benchmark
	public ByteString [] benchmarkSplitOf2()
	{
		return TO_SPLIT_2.split(PIPE);
	}
}
