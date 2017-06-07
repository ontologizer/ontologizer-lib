package sonumina.math.graph;

/**
 * This interface is used as a callback mechanism by different search
 * methods.
 *
 * @author Sebastian Bauer
 */
public interface IVisitor<V>
{
	/**
	 * Called for every vertex visited by the algorithm.
	 *
	 * @param vertex the vertex that has been just visited.
	 *
	 * @return false if algorithm should be stopped (i.e. no further
	 *         calls to this method will be issued) otherwise true
	 */
	boolean visited(V vertex);
};
