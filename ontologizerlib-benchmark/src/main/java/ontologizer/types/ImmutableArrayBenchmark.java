package ontologizer.types;

import static ontologizer.types.ImmutableArray.immutable;

import org.openjdk.jmh.annotations.Benchmark;

public class ImmutableArrayBenchmark
{
	private static final Integer [] integerArray = new Integer[]
	{
		10, 12, 14, 15, 19, 20, 21, 23, 25, 39, 43, 56, 30
	};

	private static final ImmutableArray<Integer> immutableIntegerArray = immutable(integerArray);

	private static final int [] nativeIntegerArray = new int[integerArray.length];

	{
		for (int i = 0; i < integerArray.length; i++)
		{
			nativeIntegerArray[i] = integerArray[i];
		}
	}

	@Benchmark
	public int benchmarkImmutableIntArraySimpleLoop()
	{
		int sum = 0;

		for (int i = 0; i < immutableIntegerArray.length(); i++)
		{
			sum += immutableIntegerArray.get(i);
		}
		return sum;
	}

	@Benchmark
	public int benchmarkMutableIntArraySimpleLoop()
	{
		int sum = 0;

		for (int i = 0; i < integerArray.length; i++)
		{
			sum += integerArray[i];
		}
		return sum;
	}

	@Benchmark
	public int benchmarkMutableNativeIntArraySimpleLoop()
	{
		int sum = 0;

		for (int i = 0; i < nativeIntegerArray.length; i++)
		{
			sum += nativeIntegerArray[i];
		}
		return sum;
	}
}
