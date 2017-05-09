package de.ontologizer.immutable.graph;

import java.io.Serializable;

/**
 * Interface for graph edges with readable attributes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface Edge<Vertex> extends Serializable {

	/**
	 * Query for the destination vertex.
	 *
	 * @return <code>Vertex</code> at the tip of the edge (if directed).
	 */
	public Vertex getDest();

	/**
	 * Query for the source vertex.
	 *
	 * @return <code>Vertex</code> at the foot of the edge (if directed).
	 */
	public Vertex getSource();

	/**
	 * Query for the edge weight.
	 *
	 * @return weight of the edge
	 */
	public int getWeight();

}
