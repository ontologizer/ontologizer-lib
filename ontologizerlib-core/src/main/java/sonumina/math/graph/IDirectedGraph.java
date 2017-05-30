package sonumina.math.graph;

/**
 * Base interface for directed graphs.
 *
 * @author Sebastian Bauer
 *
 * @param <V> the type of the vertices.
 */
public interface IDirectedGraph<V>
{
	/**
	 * Returns the vertices to which the in-going edges point to.
	 *
	 * @param v the vertex for which the in-going edges should be returned.
	 * @return iterable for all in-going edges.
	 */
	public Iterable<V> getParentNodes(V v);

	/**
	 * Returns the vertices to which the outgoing edges point to.
	 *
	 * @param v the vertex for which the outgoing edges should be returned.
	 * @return iterable for all out-going edges.
	 */
	public Iterable<V> getChildNodes(V v);
}
