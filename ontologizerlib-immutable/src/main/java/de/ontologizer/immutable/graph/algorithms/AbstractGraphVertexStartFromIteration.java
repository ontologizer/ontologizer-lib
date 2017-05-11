package de.ontologizer.immutable.graph.algorithms;

import de.ontologizer.immutable.graph.ChildEdgeNeighborSelector;
import de.ontologizer.immutable.graph.DirectedGraph;
import de.ontologizer.immutable.graph.Edge;
import de.ontologizer.immutable.graph.NeighborSelector;
import de.ontologizer.immutable.graph.ParentEdgeNeighborSelector;

/**
 * Provide default implementations for the <code>startFrom()</code> and
 * <code>startFromReverse()</code> functions without {@link NeighborSelector}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public abstract class AbstractGraphVertexStartFromIteration<VertexType, EdgeType extends Edge<VertexType>,
		GraphType extends DirectedGraph<VertexType, EdgeType>>
		implements GraphVertexStartFromIteration<VertexType, EdgeType, GraphType> {

	@Override
	public void startFrom(GraphType g, VertexType v, VertexVisitor<VertexType, EdgeType> visitor) {
		final NeighborSelector<VertexType, EdgeType, GraphType> neighborSelector =
				new ParentEdgeNeighborSelector<VertexType, EdgeType, GraphType>(g);
		startFrom(g, v, neighborSelector, visitor);
	}

	@Override
	public void startFromReverse(GraphType g, VertexType v, VertexVisitor<VertexType, EdgeType> visitor) {
		final NeighborSelector<VertexType, EdgeType, GraphType> neighborSelector =
				new ChildEdgeNeighborSelector<VertexType, EdgeType, GraphType>(g);
		startFrom(g, v, neighborSelector, visitor);
	}

}
