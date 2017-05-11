package de.ontologizer.immutable.graph;

/**
 * Interfaces for directed graphs which support updates for vertices and edges.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface MutableDirectedGraph<VertexType, EdgeType extends Edge<? extends VertexType>>
		extends DirectedGraph<VertexType, EdgeType> {

	/**
	 * Insert <code>Vertex</code> into graph.
	 *
	 * @param v
	 *            <code>Vertex</code> to insert
	 */
	void addVertex(VertexType v);

	/**
	 * Remove <code>Vertex</code> from graph.
	 *
	 * @param v
	 *            <code>Vertex</code> to remove
	 */
	void removeVertex(VertexType v);

	/**
	 * Add {@link Edge} to the graph.
	 *
	 * @param edge
	 *            {@link Edge} to add to the graph.
	 */
	void addEdge(EdgeType edge);

	/**
	 * Remove the edge between <code>source</code> and <code>dest</code>.
	 *
	 * @param source
	 *            source <code>Vertex</code> of edge to remove
	 * @param dest
	 *            destination <code>Vertex</code> of edge to remove
	 * @return <code>true</code> if the edge could be found and was removed,
	 *         <code>false</code> otherwise
	 */
	boolean removeEdgeBetween(VertexType source, VertexType dest);

}