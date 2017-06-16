package sonumina.collections;

import java.util.Iterator;

/**
 * Similar to ImmutableIterable but allows to specify a map so
 * types can be converted.
 *
 * @author Sebastian Bauer
 *
 * @param <K> the source/key type
 * @param <V> the dest/value type
 */
public class ImmutableMappedIterable<K,V> implements Iterable<V>
{
	private final Iterable<K> iterable;
	private final Map<K,V> map;

	public ImmutableMappedIterable(Iterable<K> iterable, Map<K,V> map)
	{
		this.iterable = iterable;
		this.map = map;
	}

	@Override
	public Iterator<V> iterator()
	{
		final Iterator<K> iter = iterable.iterator();

		return new Iterator<V>()
		{
			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}

			@Override
			public V next()
			{
				return map.map(iter.next());
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	public static <K,V> ImmutableMappedIterable<K,V> immutable(Iterable<K> shieldedIterable, Map<K,V> map)
	{
		return new ImmutableMappedIterable<K,V>(shieldedIterable, map);
	}

}
