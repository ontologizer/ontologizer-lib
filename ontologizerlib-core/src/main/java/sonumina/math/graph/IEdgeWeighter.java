package sonumina.math.graph;

/**
 * An interface to be used to weight edges for algorithms
 * that take advantage of it.
 *
 * @author Sebastian Bauer
 *
 * @param <V> the edge type
 */
public interface IEdgeWeighter<V, ED>
{
	/** Return the weight of the given edge */
	public int getWeight(Edge<V, ED> edge);
}

