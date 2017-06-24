package sonumina.collections;

/**
 * Simple functional interface for IntMapper that maps one type
 * to another and gets the actual index.
 *
 * @author Sebastian Bauer
 */
public interface IntMap<K,V>
{
	public V map(K key, int index);
}
