package sonumina.math.graph;

import java.util.Iterator;

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
	 * @return iterator over all in-going edges.
	 */
	public Iterator<V> getParentNodes(V v);

	/**
	 * Returns the vertices to which the outgoing edges point to.
	 *
	 * @param v the vertex for which the outgoing edges should be returned.
	 * @return Iterator over all outgoing edges.
	 */
	public Iterator<V> getChildNodes(V v);
}
