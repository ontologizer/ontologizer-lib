/*
 * Created on 21.08.2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package sonumina.math.graph;

import java.io.Serializable;

public class Edge<Type> implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Type source;
	private Type dest;

	public Edge(Type source, Type dest)
	{
		this.source = source;
		this.dest = dest;
	}

	/**
	 * @return the edge's destination.
	 */
	public final Type getDest()
	{
		return dest;
	}

	/**
	 * @return the edge's source.
	 */
	public final Type getSource()
	{
		return source;
	}

	void setSource(Type source) {
		this.source = source;
	}

	void setDest(Type dest) {
		this.dest = dest;
	}
}
