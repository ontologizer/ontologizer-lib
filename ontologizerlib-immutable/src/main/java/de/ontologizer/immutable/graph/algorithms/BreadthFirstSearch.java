package de.ontologizer.immutable.graph.algorithms;

import de.ontologizer.immutable.graph.DirectedGraph;
import de.ontologizer.immutable.graph.Edge;
import de.ontologizer.immutable.graph.NeighborSelector;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

/**
 * Visit vertices of a {@link DirectedGraph} in a breadth-first manner
 * (pre-order).
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class BreadthFirstSearch<VertexType, EdgeType extends Edge<VertexType>,
		GraphType extends DirectedGraph<VertexType, EdgeType>>
		extends AbstractGraphVertexStartFromIteration<VertexType, EdgeType, GraphType> {

	@Override
	public void startFrom(GraphType g, VertexType v, NeighborSelector<VertexType, EdgeType, GraphType> neighborSelector,
			VertexVisitor<VertexType, EdgeType> visitor) {
		final Set<VertexType> seen = new HashSet<VertexType>();
		final Queue<VertexType> queue = new ArrayDeque<VertexType>();
		queue.add(v);
		while (!queue.isEmpty()) {
			v = queue.poll();
			if (!seen.contains(v)) { // skip seen ones
				seen.add(v);
				if (!visitor.visit(g, v)) {
					break;
				}
				final Iterator<VertexType> it = neighborSelector.selectNeighbors(v);
				while (it.hasNext()) {
					queue.add(it.next());
				}
			}
		}
	}

}
