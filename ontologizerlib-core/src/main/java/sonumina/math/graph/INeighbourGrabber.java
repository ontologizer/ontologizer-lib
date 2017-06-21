package sonumina.math.graph;

import java.util.Iterator;

/**
 * This interface is used as a callback for the bfs and used to determine valid neighbors.
 *
 * @author Sebastian Bauer
 */
public interface INeighbourGrabber<V>
{
	Iterator<V> grabNeighbours(V v);
}
