package sonumina.collections;

/**
 * Simple functional interface to provide a map form one type to another
 */
public interface Map<K,V>
{
	public V map(K key);
}
