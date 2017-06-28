package sonumina.math.graph;

import static sonumina.math.graph.Grabbers.inGrabber;
import static sonumina.math.graph.Grabbers.outGrabber;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * An abstract class for graphs.
 *
 * @author Sebastian Bauer
 *
 * @param <V> the type of the vertices.
 *
 */
public abstract class AbstractGraph<V> implements Serializable, IDirectedGraph<V>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Performs a breadth-first search onto the graph starting at a given
	 * vertex. Vertices occurring in loops are visited only once.
	 *
	 * @param vertex defines the vertex to start with.
	 *
	 * @param againstFlow the bfs in done against the direction of the edges.
	 *
	 * @param visitor a object of a class implementing IVisitor. For every
	 *        vertex visited by the algorithm the visitor.visited() method is
	 *        called. Note that the method is also called for the vertex
	 *        represented by <code>vertex</code> (the one given as parameter to this method).
	 *
	 * @see IVisitor
	 */
	public void bfs(V vertex, boolean againstFlow, IVisitor<V> visitor)
	{
		ArrayList<V> initial = new ArrayList<V>(1);
		initial.add(vertex);
		bfs(initial,againstFlow,visitor);
	}

	/**
	 * Performs a breadth-first search onto the graph starting at a given
	 * set of vertices. Vertices occurring in loops are visited only once.
	 *
	 * @param initial defines the set of vertices to start with.
	 *
	 * @param againstFlow the bfs in done against the direction of the edges.
	 *
	 * @param visitor a object of a class implementing IVisitor. For every
	 *        vertex visited by the algorithm the visitor.visited() method is
	 *        called. Note that the method is also called for the vertices
	 *        specified by initialSet (in arbitrary order)
	 *
	 * @see IVisitor
	 */
	public void bfs(Collection<V> initial, boolean againstFlow, IVisitor<V> visitor)
	{
		Algorithms.bfs(initial, againstFlow?inGrabber(this):outGrabber(this), visitor);
	}

	private void getDFSShotcutLinks(V v, HashMap<V,V> map, HashSet<V> visited, ArrayList<V> upwardQueue, INeighbourGrabber<V> grabber, IVisitor<V> visitor)
	{
		visitor.visited(v);

		Iterator<V> iter = grabber.grabNeighbours(v);
		while (iter.hasNext())
		{
			V n = iter.next();
			if (visited.contains(n)) continue;

			if (upwardQueue.size() > 0)
			{
				for (V t : upwardQueue)
					map.put(t, n);
				upwardQueue.clear();
			}

			visited.add(n);

			getDFSShotcutLinks(n, map, visited, upwardQueue, grabber, visitor);
		}

		upwardQueue.add(v);
	}

	/**
	 * Return map of short cut links. Short cuts are links from one node to a sibling node
	 *
	 * @param vt the start vertex
	 * @param grabber defines which nodes should be traversed.
	 * @param visitor defines which nodes were visisted
	 * @return the short cut link map.
	 */
	public HashMap<V,V> getDFSShotcutLinks(V vt, INeighbourGrabber<V> grabber, IVisitor<V> visitor)
	{
		HashMap<V,V> map = new HashMap<V,V>();
		ArrayList<V> upwardQueue = new ArrayList<V>();

		getDFSShotcutLinks(vt, map, new HashSet<V>(), upwardQueue, grabber, visitor);

		for (V t : upwardQueue)
			map.put(t, null);

		return map;
	}


	/**
	 * Returns whether there is a path from source to dest.
	 *
	 * @param source
	 * @param dest
	 * @return whether there is a path from source to dest or not
	 */
	public boolean existsPath(final V source, final V dest)
	{
		class ExistsPathVisitor implements IVisitor<V>
		{
			boolean found;

			public boolean visited(V vertex)
			{
				if (vertex.equals(dest))
				{
					found = true;
					return false;
				}
				return true;
			}
		}
		ExistsPathVisitor epv = new ExistsPathVisitor();

		bfs(source,false,epv);
		return epv.found;
	}

	/**
	 * The provider class for node and edge attributes that are used in the
	 * dot file.
	 *
	 * @author Sebastian Bauer
	 *
	 * @param <V>
	 */
	public static class DotAttributesProvider<V>
	{
		public String getDotNodeName(V vt)
		{
			return null;
		}

		public String getDotNodeAttributes(V vt)
		{
			return null;
		}

		public String getDotEdgeAttributes(V src, V dest)
		{
			return null;
		}

		public String getDotGraphAttributes()
		{
			return "nodesep=0.4; ranksep=0.4;";
		}

		public String getDotHeader()
		{
			return null;
		}
	}

	/**
	 * @return the vertices is an iterable object.
	 */
	public abstract Iterable<V> getVertices();
}
