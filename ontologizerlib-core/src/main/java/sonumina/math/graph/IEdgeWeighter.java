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
	/**
	 * Return the weight of the edge spanning from src to dest and has data.
	 *
	 * @param src the source node
	 * @param dest the dest node
	 * @param data the data associated to the edge
	 * @return the edge weight
	 */
	public int getWeight(V src, V dest, ED data);
}

