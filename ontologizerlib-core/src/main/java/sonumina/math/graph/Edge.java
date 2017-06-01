/*
 * Created on 21.08.2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package sonumina.math.graph;

import java.io.Serializable;

/**
 * The edge type that is used for the directed graph.
 *
 * @author Sebastian Bauer
 *
 * @param <V> the type of the nodes
 * @param <D> the type of the auxilary data
 */
public final class Edge<V,D> implements Serializable
{
	private static final long serialVersionUID = 1L;

	private V source;
	private V dest;
	private D data;

	public Edge(V source, V dest, D data)
	{
		this.source = source;
		this.dest = dest;
		this.data = data;
	}

	/**
	 * @return the edge's destination.
	 */
	public final V getDest()
	{
		return dest;
	}

	/**
	 * @return the edge's source.
	 */
	public final V getSource()
	{
		return source;
	}

	/**
	 * @return the data.
	 */
	public D getData()
	{
		return data;
	}

	/**
	 * Create a new edge with the associated data.
	 *
	 * @param source
	 * @param dest
	 * @param data
	 * @return the new edge.
	 */
	public static <V,ED> Edge<V,ED> newEdge(V source, V dest, ED data)
	{
		return new Edge<V,ED>(source, dest, data);
	}

	/**
	 * Create a new edge with null as the associated data.
	 *
	 * @param source
	 * @param dest
	 * @return the new edge.
	 */
	public static <V,ED> Edge<V,ED> newEdge(V source, V dest)
	{
		return new Edge<V,ED>(source, dest, null);
	}
}
