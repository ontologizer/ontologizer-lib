/*
 * Created on 21.08.2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package sonumina.math.graph;

import java.io.Serializable;

public class Edge<V> implements Serializable
{
	private static final long serialVersionUID = 1L;

	private V source;
	private V dest;

	public Edge(V source, V dest)
	{
		this.source = source;
		this.dest = dest;
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

	void setSource(V source) {
		this.source = source;
	}

	void setDest(V dest) {
		this.dest = dest;
	}
}
