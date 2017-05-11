package de.ontologizer.immutable.graph;

import java.util.Iterator;

/**
 * {@link NeighborSelector} implementation that uses reverse edges/edges to children.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ParentEdgeNeighborSelector<VertexType, EdgeType extends Edge<VertexType>,
		GraphType extends DirectedGraph<VertexType, EdgeType>>
		implements NeighborSelector<VertexType, EdgeType, GraphType> {

	private final GraphType graph;
	
	/**
	 * Construct forward edge neighbor selector with underlying graph.
	 * 
	 * @param graph underlying graph
	 */
	public ParentEdgeNeighborSelector(GraphType graph) {
		this.graph = graph;
	}

	@Override
	public Iterator<VertexType> selectNeighbors(VertexType v) {
		return graph.parentVertexIterator(v);
	}

}
