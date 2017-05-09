package de.ontologizer.immutable.graph.algorithms;

import de.ontologizer.immutable.graph.DirectedGraph;
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
public class DepthFirstSearch<Vertex, Graph extends DirectedGraph<Vertex>>
		implements
			GraphVertexStartFromIteration<Vertex, Graph> {

	@Override
	public void startFrom(Graph g, Vertex v, VertexVisitor<Vertex> visitor) {
		final Set<Vertex> seen = new HashSet<Vertex>();
		final Stack<Vertex> stack = new Stack<Vertex>();
		stack.push(v);
		while (!stack.empty()) {
			v = stack.pop();
			if (!seen.contains(v)) { // skip seen ones
				seen.add(v);
				if (!visitor.visit(g, v)) {
					return;
				}
				final Iterator<Vertex> it = g.childVertexIterator(v);
				while (it.hasNext()) {
					stack.push(it.next());
				}
			}
		}
	}

}
