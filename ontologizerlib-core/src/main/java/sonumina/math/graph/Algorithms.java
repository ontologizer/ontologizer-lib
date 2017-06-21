package sonumina.math.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import sonumina.collections.TinyQueue;

/**
 * This class encompasses certain algorithms.
 *
 * @author Sebastian Köhler, Sebastian Bauer
 */
public class Algorithms
{
	/**
	 * Returns clustering coefficient as described in the paper
	 * by Watts and Strogatz (1998 Nature).
	 *
	 * @param v
	 * @return The clustering coefficient of the vertex.
	 */
	public static <V> double getClusteringCoefficient(IDirectedGraph<V> g, V v)
	{
		/*
		 * Determine the neighborhood of provided vertex.
		 */
		HashSet<V> neighborhood  = new HashSet<V>();

		Iterator<V> neighborsIt = g.getChildNodes(v).iterator();
		while (neighborsIt.hasNext())
			neighborhood.add( neighborsIt.next() );

		int numberNeighbors = neighborhood.size();

		/*
		 * For isolated nodes we set the CC to 0. This is kind of standard,
		 * even though in Brandes & Erlebach (Network Analysis) 2005 these
		 * are set to one. A discussion of this can be found in:
		 * Marcus Kaiser; New Journal of Physics (2008); "Mean clustering coefficients:
		 * the role of isolated nodes and leafs on clustering measures for small-world
		 * networks"
		 */
		if (numberNeighbors < 2)
			return 0;
		/*
		 * Now determine the number of links
		 * inside the neighborhood.
		 */
		int numEdgesNeighborhood = 0;
		for (V neighbor : neighborhood)
		{
			/*
			 * Get all nodes reachable from this node
			 */
			Iterator<V> neighborsNeighborsIt = g.getChildNodes(neighbor).iterator();
			while (neighborsNeighborsIt.hasNext())
			{
				V neighborsNeighbor = neighborsNeighborsIt.next();

				if (neighborsNeighbor == v)
					continue;
				if (neighborsNeighbor == neighbor)
					continue;

				if (neighborhood.contains(neighborsNeighbor))
					++numEdgesNeighborhood;
			}
		}

		/*
		 * Calculate clustering coefficient
		 */
		double denominator 	= (double)(numberNeighbors*(numberNeighbors-1));
		double C 			= (double)numEdgesNeighborhood / denominator;
		return C;
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
	public static <V> void bfs(Collection<V> initial, INeighbourGrabber<V> grabber, IVisitor<V> visitor)
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
	public static <V> void bfs(V vertex, INeighbourGrabber<V> grabber, IVisitor<V> visitor)
	{
		ArrayList<V> initial = new ArrayList<V>(1);
		initial.add(vertex);
		bfs(initial,grabber,visitor);
	}

	/**
	 * Performs a depth-first like search starting at the given vertex along nodes returned
	 * the grabber.
	 *
	 * @param vertex
	 * @param visitor
	 */
	public static <V> void dfs(V vertex, INeighbourGrabber<V> grabber, IVisitor<V> visitor)
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
}
