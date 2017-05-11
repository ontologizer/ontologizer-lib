package de.ontologizer.immutable.graph;

import java.util.Iterator;

/**
 * Helper for selecting valid neighbors of vertices to go to next.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface NeighborSelector<VertexType, EdgeType extends Edge<VertexType>,
		GraphType extends DirectedGraph<VertexType, EdgeType>> {

	/**
	 * Query for neighbours to visit from <code>v</code>.
	 * 
	 * @param v
	 *            <code>Vertex</code> to query neighbors for.
	 * @return {@link Iterator} of <code>Vertex</code> objects.
	 */
	Iterator<VertexType> selectNeighbors(VertexType v);

}
