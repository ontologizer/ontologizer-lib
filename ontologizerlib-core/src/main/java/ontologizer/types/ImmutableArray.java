package ontologizer.types;

import java.util.Iterator;

/**
 * This class wraps a native array to be immutable.
 *
 * Note that the elements that it contains are not protected in any way.
 *
 * @author Sebastian Bauer
 *
 * @param <T> the type of the elements for the array. Obviously, this cannot be a native one.
 */
public final class ImmutableArray<T> implements Iterable<T>
{
	private T [] array;

	private ImmutableArray(T [] array)
	{
		this.array = array;
	}

	public int length()
	{
		return array.length;
	}

	public T get(int i)
	{
		return array[i];
	}

	@Override
	public Iterator<T> iterator()
	{
		return new Iterator<T>()
		{
			private int pos;

			@Override
			public boolean hasNext()
			{
				return pos != array.length;
			}

			@Override
			public T next()
			{
				T t = array[pos++];
				return t;
			}

			@Override
			public void remove()
			{
			}
		};
	}

	/**
	 * Wrap the given array into an immutable one.
	 *
	 * @param array the array that should be wrapped.
	 * @return the array that wraps the given array.
	 */
	public static <T> ImmutableArray<T> immutable(T [] array)
	{
		return new ImmutableArray<T>(array);
	}

	/* TODO: Conceive a method that converts the objects to different ones on the same call */
}
