package de.ontologizer.immutable.graph;

/**
 * Interface for mutable graph edges.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface MutableEdge<VertexType> extends Edge<VertexType> {

	/**
	 * Set the destination vertex.
	 *
	 * @param dest
	 *            <code>Vertex</code> to set the vertex at the tip of the edge
	 *            to.
	 */
	void setDest(VertexType dest);

	/**
	 * Set the source vertex.
	 *
	 * @param source
	 *            <code>Vertex</code> to set the vertex at the foot of the edge
	 *            to.
	 */
	void setSource(VertexType source);

	/**
	 * Set the edge weight
	 *
	 * @param weight
	 *            of the edge to set.
	 */
	void setWeight(int weight);

}