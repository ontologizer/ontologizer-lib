package sonumina.math.graph;

import java.util.List;

/**
 * Visitor to be used for shortest path etc.
 *
 * @author Sebastian Bauer
 *
 * @param <V>
 */
public interface IDistanceVisitor<V>
{
	/**
	 * @param vertex
	 * @param path
	 * @param distance
	 * @return false if visit should be never called again.
	 */
	boolean visit(V vertex, List<V> path, int distance);
}
