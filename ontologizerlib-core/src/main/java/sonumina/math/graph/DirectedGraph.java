package sonumina.math.graph;

import static sonumina.math.graph.Edge.newEdge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

final class VertexAttributes<V,ED> implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** All edges where the vertex is appearing as dest */
	public ArrayList<Edge<V,ED>> inEdges = new ArrayList<Edge<V,ED>>();

	/** All edges where the vertex is appearing as source */
	public ArrayList<Edge<V,ED>> outEdges = new ArrayList<Edge<V,ED>>();
};

/**
 * This class holds the structure of a directed graph. No multi-edges are allowed.
 *
 * @param <V> the type of the vertices
 * @param <ED> the type of the data that can be associated with each edge.
 *
 * @author Sebastian Bauer
 * @author Sebastian Koehler
 */
public class DirectedGraph<V,ED> extends AbstractGraph<V> implements Iterable<V>, Serializable
{
	private static final long serialVersionUID = 1L;

	/** Contains the vertices associated to meta information (edges) */
	private LinkedHashMap<V,VertexAttributes<V,ED>> vertices;

	/**
	 * Constructs the directed graph.
	 */
	public DirectedGraph()
	{
		vertices = new LinkedHashMap<V,VertexAttributes<V,ED>>();
	}

	/**
	 * Adds the given vertex to the graph. Nothing happens if the graph
	 * already contains the vertex.
	 *
	 * @param vertex
	 */
	public void addVertex(V vertex)
	{
		if (!vertices.containsKey(vertex))
		{
			VertexAttributes<V,ED> va = new VertexAttributes<V,ED>();
			vertices.put(vertex,va);
		}
	}

	/**
	 * Removed the given vertex and all edges associated to it.
	 *
	 * @param vertex
	 */
	public void removeVertex(V vertex)
	{
		VertexAttributes<V,ED> va = vertices.get(vertex);
		if (va != null)
		{
			/* Remove each in edge */
			while (va.inEdges.size() > 0)
			{
				int lastPos = va.inEdges.size() - 1;
				Edge<V,ED> last = va.inEdges.get(lastPos);
				removeConnections(last.getSource(), last.getDest());
			}

			/* Remove each out edge */
			while (va.outEdges.size() > 0)
			{
				int lastPos = va.outEdges.size() - 1;
				Edge<V,ED> last = va.outEdges.get(lastPos);
				removeConnections(last.getSource(), last.getDest());
			}

			vertices.remove(vertex);
		}
	}

	/**
	 * Removed the given vertex and all edges associated to it.
	 *
	 * @param vertex
	 */
	private void removeVertexMaintainConnectivity(V vertex)
	{
		VertexAttributes<V,ED> va = vertices.get(vertex);
		if (va != null)
		{
			/* Connect each the source of each in edges to the dest of each out edge */
			for (Edge<V,ED> i : va.inEdges)
			{
				for (Edge<V,ED> o : va.outEdges)
				{
					if (!hasEdge(i.getSource(),o.getDest()))
						addEdge(i.getSource(),o.getDest(), null /* FIXME */);
				}
			}

			removeVertex(vertex);
		}
	}

	/**
	 * @return the vertices as an iterable object.
	 */
	public Iterable<V> getVertices()
	{
		return vertices.keySet();
	}

	/**
	 * Returns a copy of the graph. The actual vertices are not copied, neigher are the
	 * edge data.
	 *
	 * Change in the structure of the copy will not affect the origianl graph.
	 *
	 * @return the duplicated graph.
	 */
	public DirectedGraph<V,ED> copyGraph()
	{
		DirectedGraph<V,ED> copy = new DirectedGraph<V,ED>();
		Iterator<V> nodeIt = this.getVertexIterator();
		while (nodeIt.hasNext())
		{
			copy.addVertex(nodeIt.next());
		}

		nodeIt = this.getVertexIterator();
		while (nodeIt.hasNext())
		{
			V node = nodeIt.next();

			Iterator<Edge<V,ED>> edgeIt = getOutEdges(node);
			while (edgeIt.hasNext())
			{
				Edge<V,ED> e = edgeIt.next();
				copy.addEdge(e.getSource(), e.getDest(), e.getData());
			}
		}
		return copy;
	}

	public int getNumberEdges()
	{
		int sum = 0;
		Iterator<V> nodeIt = this.getVertexIterator();
		while (nodeIt.hasNext()){
			V node = nodeIt.next();
			sum += vertices.get(node).outEdges.size();
		}
		return sum;
	}

	/**
	 * Add a new edge to the graph going from source to dest with the associcated data.
	 *
	 * @param source the source
	 * @param dest the dest
	 * @param data the associated data
	 * @throws IllegalArgumentException if the edge is a link between two vertices which
	 * 	 have already been added to the graph.
	 */
	public void addEdge(V source, V dest, ED data)
	{
		addEdge(newEdge(source,  dest,  data));
	}

	/**
	 * Add a new edge into the graph with null data.
	 * @param source
	 * @param dest
	 * @throws IllegalArgumentException if the edge is a link between two vertices which
	 * 	 have already been added to the graph.
	 */
	public void addEdge(V source, V dest)
	{
		addEdge(source, dest, null);
	}

	/**
	 * Add a new edge into the graph.
	 *
	 * @param edge the edge which links two vertices.
	 *
	 * @throws IllegalArgumentException if the edge is a link between two vertices which
	 * 	 have already been added to the graph.
	 */
	private void addEdge(Edge<V,ED> edge)
	{
		VertexAttributes<V,ED> vaSource = vertices.get(edge.getSource());
		VertexAttributes<V,ED> vaDest = vertices.get(edge.getDest());

		/* Ensure that the arguments are valid, i.e. both source
		 * and destination must be vertices within the graph  */
		if (vaSource == null || vaDest == null)
			throw new IllegalArgumentException("Error when trying to add edge between source: "+vaSource+" and destination: "+vaDest+".");

		vaSource.outEdges.add(edge);
		vaDest.inEdges.add(edge);
	}

	/**
	 * Returns true if there is a directed edge between source and dest.
	 *
	 * @param source
	 * @param dest
	 * @return true if the edge exists, else false
	 */
	public boolean hasEdge(V source, V dest)
	{
		VertexAttributes<V,ED> vaSource = vertices.get(source);
		for (Edge<V,ED> e : vaSource.outEdges)
		{
			if (e.getDest().equals(dest))
				return true;
		}
		return false;
	}

	/**
	 * Remove all edges between source and dest.
	 *
	 * @param source
	 * @param dest
	 */
	public void removeConnections(V source, V dest)
	{
		VertexAttributes<V,ED> vaSource = vertices.get(source);
		VertexAttributes<V,ED> vaDest = vertices.get(dest);

		if (vaSource == null || vaDest == null)
			throw new IllegalArgumentException();

		HashSet<Edge<V,ED>> deleteMe = new HashSet<Edge<V,ED>>();
		for (Edge<V,ED> edge : vaSource.outEdges)
		{
			if (edge.getDest().equals(dest))
			{
				deleteMe.add(edge);
			}
		}

		if (deleteMe.size() > 1)
			throw new RuntimeException(" found more than one edge to delete ("+deleteMe.size()+") --> "+deleteMe);

		for (Edge<V,ED> edge : deleteMe)
		{
			vaSource.outEdges.remove(edge);
		}

		deleteMe.clear();

		for (Edge<V,ED> edge : vaSource.inEdges)
		{
			if (edge.getSource().equals(dest))
			{
				deleteMe.add(edge);
			}
		}
		if (deleteMe.size() > 1)
			throw new RuntimeException(" found more than one edge to delete ("+deleteMe.size()+") --> "+deleteMe);
		for (Edge<V,ED> edge : deleteMe)
		{
			vaSource.inEdges.remove(edge);
		}

		deleteMe.clear();
		for (Edge<V,ED> edge : vaDest.outEdges){
			if (edge.getDest().equals(source)){
				deleteMe.add(edge);
			}
		}
		if (deleteMe.size() > 1)
			throw new RuntimeException(" found more than one edge to delete ("+deleteMe.size()+") --> "+deleteMe);
		for (Edge<V,ED> edge : deleteMe)
		{
			vaDest.outEdges.remove(edge);
		}

		deleteMe.clear();

		for (Edge<V,ED> edge : vaDest.inEdges)
		{
			if (edge.getSource().equals(source))
			{
				deleteMe.add(edge);
			}
		}
		if (deleteMe.size() > 1)
			throw new RuntimeException(" found more than one edge to delete! ("+deleteMe.size()+") --> "+deleteMe);

		for (Edge<V,ED> edge : deleteMe)
		{
			vaDest.inEdges.remove(edge);
		}
	}

	/**
	 * Returns the iterator which can be used for conveniently
	 * iterating over all vertices contained within the graph.
	 *
	 * @return the iterator.
	 */
	public Iterator<V> getVertexIterator()
	{
		return vertices.keySet().iterator();
	}


	/**
	 * Returns the edge connecting source to dest. As multi graphs are not
	 * supported this is unique.
     *
	 * @param source
	 * @param dest
	 * @return the edge or null if there is no edge between the specified nodes.
	 */
	public Edge<V,ED> getEdge(V source, V dest)
	{
		VertexAttributes<V,ED> va = vertices.get(source);

		for (Edge<V,ED> e : va.outEdges)
		{
			if (e.getDest().equals(dest))
				return e;
		}
		return null;
	}

	/**
	 * Returns the number of in-edges of the given vertex.
	 *
	 * @param v
	 * @return the number of in-edges
	 */
	public int getNumberOfInEdges(V v)
	{
		VertexAttributes<V,ED> va = vertices.get(v);
		assert(va != null);
		return va.inEdges.size();
	}

	/**
	 * Returns the iterator to iterate through all edges going
	 * into the given object (i.e. all edges where the object
	 * is the edge's destination)
	 *
	 * @param v the vertex for which to return the in edges.
	 *
	 * @return the iterator
	 */
	public Iterator<Edge<V,ED>> getInEdges(V v)
	{
		VertexAttributes<V,ED> va = vertices.get(v);
		assert(va != null);
		return va.inEdges.iterator();
	}

	@Override
	public Iterable<V> getParentNodes(V v)
	{
		final Iterator<Edge<V,ED>> iter = getInEdges(v);

		return new Iterable<V>()
		{
			@Override
			public Iterator<V> iterator()
			{
				return new Iterator<V>()
				{
					@Override
					public boolean hasNext()
					{
						return iter.hasNext();
					}

					@Override
					public V next()
					{
						return iter.next().getSource();
					}

					@Override
					public void remove()
					{
					}
				};
			}
		};
	}

	/**
	 * Returns the number of in-edges of the given vertex.
	 *
	 * @param v
	 * @return the number of out-edges
	 */
	public int getNumberOfOutEdges(V v)
	{
		VertexAttributes<V,ED> va = vertices.get(v);
		assert(va != null);
		return va.outEdges.size();
	}

	/**
	 * Returns clustering coefficient as described in the paper
	 * by Watts and Strogatz (1998 Nature).
	 *
	 * @param v
	 * @return The clustering coefficient of the vertex.
	 */
	public double getClusteringCoefficient(V v)
	{
		VertexAttributes<V,ED> va = vertices.get(v);
		assert(va != null);

		/*
		 * Determine the neighborhood of provided vertex.
		 */
		HashSet<V> neighborhood  = new HashSet<V>();

		Iterator<V> neighborsIt = getChildNodes(v).iterator();
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
		for (V neighbor : neighborhood){
			/*
			 * Get all nodes reachable from this node
			 */
			Iterator<V> neighborsNeighborsIt = getChildNodes(neighbor).iterator();
			while (neighborsNeighborsIt.hasNext()){
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
	 * Returns the iterator to iterate through all edges going
	 * out of given object. (i.e. all edges where the object
	 * is the edge's source)
	 *
	 * @param v the vertex for which all outgoing edges shall be returned
	 *
	 * @return the iterator for all outgoing edges
	 */
	public Iterator<Edge<V,ED>> getOutEdges(V v)
	{
		VertexAttributes<V,ED> va = vertices.get(v);
		assert(va != null);
		return va.outEdges.iterator();
	}

	@Override
	public Iterable<V> getChildNodes(V v)
	{
		final Iterator<Edge<V,ED>> iter = getOutEdges(v);

		return new Iterable<V>()
		{
			@Override
			public Iterator<V> iterator()
			{
				return new Iterator<V>()
				{
					@Override
					public boolean hasNext()
					{
						return iter.hasNext();
					}

					@Override
					public V next()
					{
						return iter.next().getDest();
					}

					@Override
					public void remove()
					{
					}
				};
			}
		};
	}

	/**
	 * Returns the vertices in an Iterable that are connected by the given node.
	 *
	 * FIXME: Check if this is really returning descendants
	 * FIXME: It does not
	 *
	 * @param v the vertex for which the descendants shall be returned
	 * @return the iterable
	 */
	public Iterable<V> getDescendantVertices(V v)
	{
		VertexAttributes<V,ED> va = vertices.get(v);
		assert(va != null);

		List<V> descendant = new ArrayList<V>(va.outEdges.size());
		for (Edge<V,ED> e : va.outEdges)
			descendant.add(e.getDest());
		return descendant;
	}

	/**
	 * Calculates the shortest path from the given vertex to all vertices. Note that
	 * negative weights are not supported!
	 *
	 * @param vertex defines the source
	 * @param againstFlow if specified the path is walked against the direction of the graph
	 * @param visitor object implementing IDistanceVisitor which can be used to process the
	 *        results
	 * @param weighter the edge weighter. If null, all weights are considered as 1.
	 */
	public void singleSourceShortestPath(V vertex, boolean againstFlow, IDistanceVisitor<V> visitor, IEdgeWeighter<V,ED> weighter)
	{		/**
		 * This class implements some meta information needed by Dijkstra's
		 * algorithm.
		 *
		 * @author Sebastian Bauer
		 */
		class VertexExtension implements Comparable<VertexExtension>
		{
			/** The vertex */
			public V vertex;

			/** The current distance of the vertex (to the source vertex) */
			public int distance;

			/** The current parent of the vertex */
			public V parent;

			VertexExtension(V vertex, int distance, V parent)
			{
				this.vertex = vertex;
				this.distance = distance;
				this.parent = parent;
			}

			public int compareTo(VertexExtension arg0)
			{
				return distance - arg0.distance;
			}

			public int hashCode()
			{
				return vertex.hashCode();
			}
		}

		if (!vertices.containsKey(vertex))
			throw new IllegalArgumentException(vertex + " not found.");

		/* This is the implementation of the Dijkstra algorithm */

		/* Within the priority queue we maintain the vertices which has been already
		 * discovered by the algorithm.
		 *
		 * TODO: Get rid of Java's PriorityQueue by using a better suited data structure */
		PriorityQueue<VertexExtension> queue = new PriorityQueue<VertexExtension>();
		HashMap<V,VertexExtension> map = new HashMap<V,VertexExtension>();

		/* Place the starting node into the priorty queue. It has a distance of 0 and no parent */
		VertexExtension ve = new VertexExtension(vertex,0,null);
		queue.offer(ve);
		map.put((V)ve.vertex,ve);

		while (!queue.isEmpty())
		{
			/* Take a node which has minimal distance to the starting node */
			VertexExtension next = queue.poll();

			/* We iterate over the edges of the chosen node to find the neighbours */
			Iterator<Edge<V,ED>> edgeIter;
			if (againstFlow) edgeIter = getInEdges((V)next.vertex);
			else edgeIter = getOutEdges((V)next.vertex);

			while (edgeIter.hasNext())
			{
				Edge<V,ED> edge = edgeIter.next();
				V neighbour;
				int weight = weightOf(edge, weighter);

				if (againstFlow) neighbour = edge.getSource();
				else neighbour = edge.getDest();

				/* Relax the neighbour (or add it if it is not available) */
				VertexExtension neighbourExt = map.get(neighbour);
				if (neighbourExt == null)
				{
					neighbourExt = new VertexExtension(neighbour, next.distance + weight, next.vertex);
					map.put(neighbour,neighbourExt);
					queue.offer(neighbourExt);
				} else
				{
					/* Would the edge from the current vertex to the neighbour
					 * make the path to the neighbour shorter? */
					if (neighbourExt.distance > next.distance + weight)
					{
						queue.remove(neighbourExt);
						neighbourExt.distance = next.distance + weight;
						neighbourExt.parent = next.vertex;
						queue.offer(neighbourExt);
					}
				}
			}
		}

		/* Now throw out the results */
		for (Entry<V,VertexExtension> v : map.entrySet())
		{
			/* Build the path by successively traversing the path from
			 * the current destination through the stored ancestors
			 * (parents) */
			LinkedList<V> ll = new LinkedList<V>();
			VertexExtension curVe = v.getValue();
			do
			{
				ll.addFirst((V)curVe.vertex);
				curVe = map.get(curVe.parent);
			} while (curVe != null);

			if (!visitor.visit((V)v.getValue().vertex,ll,v.getValue().distance))
				return;
		}
	}

	/**
	 * Return the weight of the edge via weighter.
	 *
	 * @param edge
	 * @param weighter
	 * @return the edge.
	 */
	private int weightOf(Edge<V,ED> edge, IEdgeWeighter<V,ED> weighter)
	{
		int weight;
		if (weighter != null) weight = weighter.getWeight(edge);
		else weight = 1;
		return weight;
	}

	/**
	 * The bellman-ford algorithm (computes single-source shortest paths in a weighted digraph)
	 *
	 * @param source
	 * @param weightMultiplier multiplies the weights by the given factor.
	 * @param visitor
	 * @param weighter the edge weighter. If null, all weights are considered as 1.
	 */
	public void bf(V source, int weightMultiplier, IDistanceVisitor<V> visitor, IEdgeWeighter<V,ED> weighter)
	{
		/**
		 * This class implements some meta information needed by the BF algorithm.
		 *
		 * @author Sebastian Bauer
		 */
		class VertexExtension implements Comparable<VertexExtension>
		{
			public V vertex;
			public int distance;
			public V parent;

			public VertexExtension(V vertex, int distance, V parent)
			{
				this.vertex = vertex;
				this.distance = distance;
				this.parent = parent;
			}

			public int compareTo(VertexExtension arg0)
			{
				return distance - arg0.distance;
			}

			public int hashCode()
			{
				return vertex.hashCode();
			}
		}

		HashMap<V,VertexExtension> map = new HashMap<V,VertexExtension>();
		map.put(source, new VertexExtension(source,0,null));

		/* Vertices loop */
		for (int i=0;i<vertices.size();i++)
		{
			boolean changed = false;

			/* Edge loop */
			for (Entry<V, VertexAttributes<V,ED>> ent : vertices.entrySet())
			{
				V u = ent.getKey();

				VertexExtension uExt = map.get(u);
				if (uExt == null) continue;

				for (Edge<V,ED> edge : ent.getValue().outEdges)
				{
					V v = edge.getDest();
					int weight = weightOf(edge, weighter);


					VertexExtension vExt = map.get(v);
					if (vExt == null)
					{
						vExt = new VertexExtension(v, uExt.distance + weight*weightMultiplier, u);
						map.put(v,vExt);
						changed = true;
					} else
					{
						if (vExt.distance > uExt.distance + weight * weightMultiplier)
						{
							vExt.distance = uExt.distance + weight * weightMultiplier;
							vExt.parent = u;
							changed = true;
						}
					}
				}
			}

			/* If this iteration doesn't affect a change, the next own won't change anything either */
			if (!changed)
				break;
		}

		/* Now throw out the results */
		for (Entry<V,VertexExtension> v : map.entrySet())
		{
			/* Build the path by successively traversing the path from
			 * the current destination through the stored ancestors
			 * (parents) */
			LinkedList<V> ll = new LinkedList<V>();
			VertexExtension curVe = v.getValue();
			do
			{
				ll.addFirst((V)curVe.vertex); /* FIXME: (VertexType) is for the java compiler */
				curVe = map.get(curVe.parent);
			} while (curVe != null);

			if (!visitor.visit((V)v.getValue().vertex,ll,v.getValue().distance))  /* FIXME: (VertexType) is for the java compiler */
				return;
		}
	}

	/**
	 * Calculates the shortest path from the given vertex to all vertices. Supports negative weights.
	 *
	 * @param source defines the source
	 * @param visitor object implementing IDistanceVisitor which can be used to process the
	 *        results
	 */
	public void singleSourceShortestPathBF(V source, IDistanceVisitor<V> visitor)
	{
		bf(source,1,visitor, null);
	}

	/**
	 * Calculates the longest path from the given vertex to all vertices.
	 *
	 * @param source defines the source
	 * @param visitor object implementing IDistanceVisitor which can be used to process the
	 *        results
	 */
	public void singleSourceLongestPath(V source, final IDistanceVisitor<V> visitor)
	{
		bf(source,-1,new IDistanceVisitor<V>()
				{
					public boolean visit(V vertex, java.util.List<V> path, int distance)
					{
						return visitor.visit(vertex, path, distance * -1);
					};
				}, null);
	}

	/**
	 * Returns the number of distinct paths from source to dest.
	 *
	 * @param source where to start.
	 * @param dest where to end.
	 * @return the number of paths.
	 */
	public int getNumberOfPaths(V source, V dest)
	{
		if (source.equals(dest))
			return 1;

		int paths = 0;
		for (V next : getDescendantVertices(source))
			paths += getNumberOfPaths(next,dest);
		return paths;
	}

	/**
	 * Returns all paths from source to dest.
	 *
	 * @param source where to start.
	 * @param dest where to end.
	 * @return the number of paths.
	 */
	public ArrayList<V> getAllPathes(V source, V dest, ArrayList<V> pathes)
	{
		if (source.equals(dest)){
			ArrayList<V> ret = new ArrayList<V>();
			ret.add(dest);
			return ret;
		}

		for (V next : getDescendantVertices(source)){
			ArrayList<V> rec = getAllPathes(next, dest, pathes);
			System.out.println("recur: "+rec);
			pathes.addAll(rec);
		}
		return pathes;
	}

	/**
	 * @return an arbitrary node of the graph
	 */
	public V getArbitaryNode()
	{
		return vertices.entrySet().iterator().next().getKey();
	}

	/**
	 * @return the number of vertices.
	 */
	public int getNumberOfVertices()
	{
		return vertices.size();
	}

	/**
	 * Allows convenient iteration.
	 */
	public Iterator<V> iterator()
	{
		return vertices.keySet().iterator();
	}

	/**
	 * Get the in-degree of the given vertex.
	 *
	 * @param v vertex for which the in-degree shall be determined
	 * @return the in-degree
	 */
	public int getInDegree(V v)
	{
		VertexAttributes<V,ED> va = vertices.get(v);
		if (va == null) return -1;
		return va.inEdges.size();
	}

	/**
	 * Get the out-degree of the given vertex.
	 *
	 * @param v vertex for which the out-degree shall be determined
	 * @return the out-degree
	 */
	public int getOutDegree(V v)
	{
		VertexAttributes<V,ED> va = vertices.get(v);
		if (va == null) return -1;
		return va.outEdges.size();
	}

	public int getMaxDegree()
	{
		int max = Integer.MIN_VALUE;
		for (V vertex : vertices.keySet())
		{
			int degreeOut = this.getNumberOfOutEdges(vertex);
			int degreeIn = this.getNumberOfInEdges(vertex);
			if (degreeIn != degreeOut)
				throw new RuntimeException("Vertex "+vertex+" has indegree:"+degreeIn+" and outdegree:"+degreeOut);
			if (degreeOut > max)
				max = degreeOut;
		}
		return max;
	}

	public boolean areNeighbors(V node1, V node2)
	{

		if (node1.equals(node2))
			return true;

		VertexAttributes<V,ED> va = vertices.get(node1);
		for (Edge<V,ED> e : va.inEdges){
			V ancestor = e.getSource();
			if (ancestor.equals(node2))
				return true;
		}
		for (Edge<V,ED> e : va.outEdges){
			V desc = e.getDest();
			if (desc.equals(node2))
				return true;
		}
		return false;
	}

	/**
	 * Returns a subgraph of the graph that includes all given vertices.
	 *
	 * @param verticesToBeIncluded
	 * @return the subgraph
	 */
	public DirectedGraph<V,ED> subGraph(Collection<V> verticesToBeIncluded)
	{
		return subGraph(new HashSet<V>(verticesToBeIncluded));
	}

	/**
	 * Returns a subgraph of the graph that includes all given vertices. Edges are included
	 * only, if it is spanned between two vertices in the given set.
	 *
	 * @param verticesToBeIncluded
	 * @return the subgraph
	 */
	public DirectedGraph<V,ED>subGraph(Set<V> verticesToBeIncluded)
	{
		DirectedGraph<V,ED> graph = new DirectedGraph<V,ED>();

		/* Add vertices that should be contained in the subgraph */
		for (V v : verticesToBeIncluded)
			graph.addVertex(v);

		/* Add edges (only one class of edges needs to be added) */
		for (V v : verticesToBeIncluded)
		{
			Iterator<Edge<V,ED>> edges = getInEdges(v);
			while (edges.hasNext())
			{
				Edge<V,ED> e = edges.next();
				if (verticesToBeIncluded.contains(e.getSource()))
					graph.addEdge(e);
			}
		}

		return graph;
	}

	/**
	 * Returns the path-transitivity-maintaining transitive closure of
	 * a subgraph that contains the given vertices.
	 *
	 * Note that the actual data of each edge will be nulled (for now).
	 *
	 * @param verticesToBeIncluded
	 * @return the transitive closure of the subgraph
	 */
	public DirectedGraph<V,ED> transitiveClosureOfSubGraph(final Set<V> verticesToBeIncluded)
	{
		/* This is a naive implementation */
		final DirectedGraph<V,ED> graph = new DirectedGraph<V,ED>();

		/* Add vertices that should be contained in the subgraph */
		for (V v : verticesToBeIncluded)
			graph.addVertex(v);

		for (final V v1 : verticesToBeIncluded)
		{
			bfs(v1,false,new IVisitor<V>() {
				public boolean visited(V vertex)
				{
					if (verticesToBeIncluded.contains(vertex))
					{
						/* FIXME: Find a better solution than to null the data, e.g., let the caller decide
						 * via appropriate interface */
						graph.addEdge(new Edge<V,ED>(v1,vertex,null));
					}
					return true;
				};
			});
		}

		return graph;
	}

	/**
	 *
	 * Returns a sub graph with selected vertices, in which path relationships are
	 * maintained. Basic version.
	 *
	 * @return
	 */
	private DirectedGraph<V,ED> compactedSubgraph(final Set<V> verticesToBeIncluded)
	{
		/* This is a naive implementation */
		DirectedGraph<V,ED> graph = copyGraph();

		/* Note that we iterate here over the nodes the this instance and
		 * not over the duplicated graph. We will remove nodes from there
		 * therefore iterating over those nodes is node safe.
		 */
		for (V v : this)
		{
			if (!verticesToBeIncluded.contains(v))
				graph.removeVertexMaintainConnectivity(v);
		}

		return graph;
	}

	/**
	 * Returns a sub graph with selected vertices, in which path relationships are
	 * maintained.
	 *
	 * @param verticesToBeIncluded
	 * @return the path maintaining subgraph
	 */
	public DirectedGraph<V,ED> pathMaintainingSubGraph(Set<V> verticesToBeIncluded)
	{
		DirectedGraph<V,ED> transitiveClosure = compactedSubgraph(verticesToBeIncluded);
		DirectedGraph<V,ED> transitivitySubGraph;
		boolean reducedInIteration;


		/* Here we also want to ensure that no redunancies are included */

		do
		{
			reducedInIteration = false;

			/* Here, the reduced graph structure is stored */
			transitivitySubGraph = new DirectedGraph<V,ED>();
			for (V v : verticesToBeIncluded)
				transitivitySubGraph.addVertex(v);

			/* Now add edges to the reduced graph structure, i.e., leave out
			 * edges that are redundant */
			for (V v : verticesToBeIncluded)
			{
				Set<V> vUpperVertices = transitiveClosure.getVerticesOfUpperInducedGraph(null,v);
				LinkedList<V> parents = new LinkedList<V>();
				Iterator<V> parentIterator = transitiveClosure.getParentNodes(v).iterator();
				while (parentIterator.hasNext())
					parents.add(parentIterator.next());

				/* Construct the upper graph by using only the parents. Always
				 * leave out a single parent. If that edge is redundant, the number
				 * of nodes in this newly created graph differs only by one.
				 */
				for (V p : parents)
				{
					HashSet<V> pUpperVertices = new HashSet<V>();

					for (V p2 : parents)
					{
						/* Skip parent that should be left out */
						if (p.equals(p2)) continue;

						pUpperVertices.addAll(transitiveClosure.getVerticesOfUpperInducedGraph(null,p2));
					}

					if (pUpperVertices.size() != vUpperVertices.size() - 1)
					{
						/* Here we know that the edge from p to v was relevant */
						transitivitySubGraph.addEdge(new Edge<V,ED>(p,v,null));
					} else
					{
						reducedInIteration = true;
					}
				}
			}
			transitiveClosure = transitivitySubGraph;
		} while (reducedInIteration);

		return transitivitySubGraph;
	}

	/**
	 * Returns a set of induced terms that are the terms of the induced graph.
	 *
	 * @param root the root term (all terms up to this are included)
	 * @param termID the inducing term.
	 * @return the set of vertices defining the upper induced graph
	 */
	public Set<V> getVerticesOfUpperInducedGraph(final V root, V termID)
	{
		/**
		 * Visitor which simply add all nodes to the nodeSet.
		 *
		 * @author Sebastian Bauer
		 */
		class Visitor implements IVisitor<V>
		{
			public HashSet<V> nodeSet = new HashSet<V>();

			public boolean visited(V vertex)
			{
				if (root != null)
				{
					if (vertex.equals(root) || existsPath(root, vertex))
						nodeSet.add(vertex);
				} else
					nodeSet.add(vertex);

				return true;
			}
		};

		Visitor visitor = new Visitor();
		bfs(termID,true,visitor);

		return visitor.nodeSet;
	}

	/**
	 * Merge equivalent vertices. First vertex given will be
	 * the representative and thus we inherit all the edges
	 * of the other equivalent vertices.
	 *
	 * @param vertex1
	 * @param eqVertices
	 */
	public void mergeVertices(V vertex1, Iterable<V> eqVertices)
	{
		for (V vertex2 : eqVertices)
		{
			/* New outgoing/ingoing edges to/from vertex1 */
			Map<V,ArrayList<ED>> newOutgoing = new HashMap<V,ArrayList<ED>>();
			Map<V,ArrayList<ED>> newIngoing = new HashMap<V,ArrayList<ED>>();

			if (!vertices.containsKey(vertex2))
				throw new IllegalArgumentException("Vertex " + vertex2 + " not contained within the graph");

			VertexAttributes<V,ED> vertexTwoAttributes = vertices.get(vertex2);
			for (Edge<V,ED> e : vertexTwoAttributes.inEdges)
			{
				/* Remove this particular edge from the source */
				vertices.get(e.getSource()).outEdges.remove(e);

				/* Remember that fact */
				ArrayList<ED> l = newIngoing.get(e.getSource());
				if (l == null)
				{
					l = new ArrayList<ED>();
					newIngoing.put(e.getSource(), l);
				}
				l.add(e.getData());
			}

			for (Edge<V,ED> e : vertexTwoAttributes.outEdges)
			{
				/* Remove this particular edge from the dest */
				vertices.get(e.getDest()).inEdges.remove(e);

				/* Remember that fact */
				ArrayList<ED> l = newOutgoing.get(e.getDest());
				if (l == null)
				{
					l = new ArrayList<ED>();
					newOutgoing.put(e.getDest(), l);
				}
				l.add(e.getData());
			}

			for (V v : newIngoing.keySet())
			{
				if (v == vertex1) continue;
				if (hasEdge(v, vertex1)) continue;

				ArrayList<ED> d = newIngoing.get(v);
				addEdge(newEdge(v, vertex1, d.get(0)));
			}

			for (V v : newOutgoing.keySet())
			{
				if (v == vertex1) continue;
				if (hasEdge(v, vertex1)) continue;

				ArrayList<ED> d = newOutgoing.get(v);
				addEdge(newEdge(vertex1, v, d.get(0)));
			}

			vertices.remove(vertex2);
		}
	}

	public boolean containsVertex(V vertex){
		return vertices.containsKey(vertex);
	}
}
