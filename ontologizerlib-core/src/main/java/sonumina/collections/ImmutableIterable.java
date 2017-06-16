package sonumina.collections;

import java.util.Iterator;

/**
 * A read only iterable is an iterable that shields a
 * companion iterable against modifications via
 * the iterator or via down-casting.
 *
 * @author Sebastian Bauer
 */
public class ImmutableIterable<T> implements Iterable<T>
{
	private Iterable<T> shieldedIterable;

	public ImmutableIterable(Iterable<T> shieldedIterable)
	{
		this.shieldedIterable = shieldedIterable;
	}

	@Override
	public Iterator<T> iterator()
	{
		final Iterator<T> shieldedIterator = shieldedIterable.iterator();

		return new Iterator<T>()
		{
			@Override
			public boolean hasNext()
			{
				return shieldedIterator.hasNext();
			}

			@Override
			public T next()
			{
				return shieldedIterator.next();
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	public static <T> ImmutableIterable<T> immutable(Iterable<T> shieldedIterable)
	{
		return new ImmutableIterable<T>(shieldedIterable);
	}
}
