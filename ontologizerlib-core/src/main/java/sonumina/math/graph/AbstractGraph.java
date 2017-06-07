package sonumina.math.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import sonumina.collections.TinyQueue;

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
	 * vertex. Vertices occurring in loops are visited only once.
	 *
	 * @param vertex defines the vertex to start with.
	 *
	 * @param grabber a object of a class implementing INeighbourGrabber which
	 *        returns the nodes which should be visited next.
	 *
	 * @param visitor a object of a class implementing IVisitor. For every
	 *        vertex visited by the algorithm the visitor.visited() method is
	 *        called. Note that the method is also called for the vertex
	 *        represented by vertex.
	 *
	 * @see IVisitor
	 */
	public void bfs(V vertex,  INeighbourGrabber<V> grabber, IVisitor<V> visitor)
	{
		ArrayList<V> initial = new ArrayList<V>(1);
		initial.add(vertex);
		bfs(initial,grabber,visitor);
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
	public void bfs(Collection<V> initial, final boolean againstFlow, IVisitor<V> visitor)
	{
		bfs(initial,
				new INeighbourGrabber<V>()
				{
					public Iterator<V> grabNeighbours(V t)
					{
						/* If bfs is done against flow neighbours can be found via the
						 * in-going edges otherwise via the outgoing edges */
						if (againstFlow) return getParentNodes(t).iterator();
						else return getChildNodes(t).iterator();
					}
				}, visitor);
	}

	/**
	 * Performs a breadth-first search onto the graph starting at a given
	 * set of vertices. Vertices occurring in loops are visited only once.
     *
	 * @param initial defines the set of vertices to start with.
	 *
	 * @param grabber a object of a class implementing INeighbourGrabber which
	 *        returns the nodes which should be visited next.
     *
	 * @param visitor a object of a class implementing IVisitor. For every
	 *        vertex visited by the algorithm the visitor.visited() method is
	 *        called. Note that the method is also called for the vertices
	 *        specified by initialSet (in arbitrary order)
	 */
	public void bfs(Collection<V> initial, INeighbourGrabber<V> grabber, IVisitor<V> visitor)
	{
		HashSet<V> visited = new HashSet<V>();

		/* Add all nodes into the queue */
		TinyQueue<V> queue = new TinyQueue<V>();
		for (V vertex  : initial)
		{
			queue.offer(vertex);
			visited.add(vertex);
			if (!visitor.visited(vertex))
				return;
		}

		while (!queue.isEmpty())
		{
			/* Remove head of the queue */
			V head = queue.poll();

			/* Add not yet visited neighbors of old head to the queue
			 * and mark them as visited. */
			Iterator<V> neighbours = grabber.grabNeighbours(head);

			while (neighbours.hasNext())
			{
				V neighbour = neighbours.next();

				if (!visited.contains(neighbour))
				{
					queue.offer(neighbour);
					visited.add(neighbour);
					if (!visitor.visited(neighbour))
						return;
				}
			}
		}
	}

	/**
	 * Performs a depth-first like search starting at the given vertex.
	 *
	 * @param vertex
	 * @param visitor
	 */
	public void dfs(V vertex, INeighbourGrabber<V> grabber, IVisitor<V> visitor)
	{
		HashSet<V> visited = new HashSet<V>();
		Stack<V> stack = new Stack<V>();

		visited.add(vertex);
		stack.push(vertex);

		while (!stack.isEmpty())
		{
			V v = stack.pop();
			visitor.visited(v);

			Iterator<V> iter = grabber.grabNeighbours(v);
			while (iter.hasNext())
			{
				V n = iter.next();
				if (visited.contains(n)) continue;
				stack.push(n);
				visited.add(n);
			}

		}
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
	 * Returns the vertices in a topological order. Note that if the length
	 * of the returned differs from the number of vertices we have a cycle.
	 *
	 * @return a list of vertices in a topological order.
	 */
	public ArrayList<V> topologicalOrder()
	{
		/* Gather structure */
		HashMap<V,LinkedList<V>> vertex2Children 	= new HashMap<V,LinkedList<V>>();
		HashMap<V,Integer> vertex2NumParents 				= new HashMap<V,Integer>();
		LinkedList<V> verticesWithNoParents 				= new LinkedList<V>();

		for (V v : getVertices())
		{
			/* Build list of children */
			LinkedList<V> vChild 			= new LinkedList<V>();
			Iterator<V> childrenIterator 	= getChildNodes(v).iterator();
			while (childrenIterator.hasNext())
				vChild.add(childrenIterator.next());
			vertex2Children.put(v, vChild);

			/* Determine the number of parents for each node */
			int numParents 						= 0;
			Iterator<V> parentIterator = getParentNodes(v).iterator();
			while (parentIterator.hasNext())
			{
				parentIterator.next();
				numParents++;
			}

			if (numParents == 0){
				verticesWithNoParents.add(v);
			}
			else{
				vertex2NumParents.put(v,numParents);
			}
		}

		int numOfVertices 			= vertex2Children.size();
		ArrayList<V> order = new ArrayList<V>(numOfVertices);

		/* Take the first vertex in the queue verticesWithNoParents and to every
		 * vertex to which vertex is a parent decrease its current number of parents
		 * value by one.
		 */
		while (!verticesWithNoParents.isEmpty())
		{
			V top = verticesWithNoParents.poll();
			order.add(top);

			for (V p : vertex2Children.get(top))
			{
				int newNumParents = vertex2NumParents.get(p)-1;
				vertex2NumParents.put(p,newNumParents);

				if (newNumParents == 0)
					verticesWithNoParents.offer(p);
			}
		}

		return order;
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
