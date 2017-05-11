package de.ontologizer.immutable.graph.algorithms;

import de.ontologizer.immutable.graph.DirectedGraph;
import de.ontologizer.immutable.graph.Edge;
import de.ontologizer.immutable.graph.NeighborSelector;

/**
 * Interface for iteration of {@link DirectedGraph} vertices using the Visitor
 * pattern, starting from specific vertex.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface GraphVertexStartFromIteration<VertexType, EdgeType extends Edge<VertexType>,
		GraphType extends DirectedGraph<VertexType, EdgeType>> {

	/**
	 * Start iterating vertices in forward direction (from child to parent)
	 * starting from <code>v</code>.
	 *
	 * <p>
	 * {@link VertexVisitor#visit(DirectedGraph, Object)} will be called for
	 * vertices of the graph starting from <code>v</code>
	 * </p>
	 *
	 * @param g
	 *            {@link DirectedGraph} to iterate over
	 * @param v
	 *            <code>Vertex</code> to start iterating at
	 * @param visitor
	 *            {@link VertexVisitor} to use for notifying about reaching a
	 *            vertex
	 */
	public void startFrom(GraphType g, VertexType v, VertexVisitor<VertexType, EdgeType> visitor);

	/**
	 * Start iterating vertices in reverse direction (from parent to child)
	 * starting from <code>v</code>.
	 *
	 * <p>
	 * {@link VertexVisitor#visit(DirectedGraph, Object)} will be called for
	 * vertices of the graph starting from <code>v</code>
	 * </p>
	 *
	 * @param g
	 *            {@link DirectedGraph} to iterate over
	 * @param v
	 *            <code>Vertex</code> to start iterating at
	 * @param visitor
	 *            {@link VertexVisitor} to use for notifying about reaching a
	 *            vertex
	 */
	public void startFromReverse(GraphType g, VertexType v, VertexVisitor<VertexType, EdgeType> visitor);

	/**
	 * Start iterating vertices starting from <code>v</code>, select neighbors
	 * using <code>neighborSelector</code>.
	 *
	 * <p>
	 * {@link VertexVisitor#visit(DirectedGraph, Object)} will be called for
	 * vertices of the graph starting from <code>v</code>
	 * </p>
	 *
	 * @param g
	 *            {@link DirectedGraph} to iterate over
	 * @param v
	 *            <code>Vertex</code> to start iterating at
	 * @param neighborSelector
	 *            {@link NeighborSelector} to use for selecting next vertices
	 * @param visitor
	 *            {@link VertexVisitor} to use for notifying about reaching a
	 *            vertex
	 */
	public void startFrom(GraphType g, VertexType v, NeighborSelector<VertexType, EdgeType, GraphType> neighborSelector,
			VertexVisitor<VertexType, EdgeType> visitor);

}
