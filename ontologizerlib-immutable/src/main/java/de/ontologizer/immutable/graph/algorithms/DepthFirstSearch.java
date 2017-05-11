package de.ontologizer.immutable.graph.algorithms;

import de.ontologizer.immutable.graph.DirectedGraph;
import de.ontologizer.immutable.graph.Edge;
import de.ontologizer.immutable.graph.NeighborSelector;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * Visit vertices of a {@link DirectedGraph} in a depth-first manner
 * (pre-order).
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DepthFirstSearch<VertexType, EdgeType extends Edge<VertexType>,
		GraphType extends DirectedGraph<VertexType, EdgeType>>
		extends AbstractGraphVertexStartFromIteration<VertexType, EdgeType, GraphType> {

	@Override
	public void startFrom(GraphType g, VertexType v, NeighborSelector<VertexType, EdgeType, GraphType> neighborSelector,
			VertexVisitor<VertexType, EdgeType> visitor) {
		final Set<VertexType> seen = new HashSet<VertexType>();
		final Stack<VertexType> stack = new Stack<VertexType>();
		stack.push(v);
		while (!stack.empty()) {
			v = stack.pop();
			if (!seen.contains(v)) { // skip seen ones
				seen.add(v);
				if (!visitor.visit(g, v)) {
					return;
				}
				final Iterator<VertexType> it = neighborSelector.selectNeighbors(v);
				while (it.hasNext()) {
					stack.push(it.next());
				}
			}
		}
	}

}
