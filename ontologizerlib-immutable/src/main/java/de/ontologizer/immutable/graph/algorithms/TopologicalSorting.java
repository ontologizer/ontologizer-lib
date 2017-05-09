package de.ontologizer.immutable.graph.algorithms;

import de.ontologizer.immutable.graph.DirectedGraph;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Visit all vertices of a {@link DirectedGraph} in topological order.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TopologicalSorting<Vertex, Graph extends DirectedGraph<Vertex>>
		implements
			GraphVertexAllIteration<Vertex, Graph> {

	/**
	 * Implementation of Tarjan's algorithm for topological sorting.
	 */
	@Override
	public void start(Graph g, VertexVisitor<Vertex> visitor) {
		final Set<Vertex> tmpMarked = new HashSet<Vertex>();

		// Collect unmarked vertices
		final Set<Vertex> unmarked = new HashSet<Vertex>();
		final Iterator<Vertex> vertexIterator = g.vertexIterator();
		while (vertexIterator.hasNext()) {
			unmarked.add(vertexIterator.next());
		}

		// Perform visiting
		while (!unmarked.isEmpty()) {
			final Vertex v = unmarked.iterator().next();
			startFrom(g, unmarked, tmpMarked, v, visitor);
		}
	}

	/**
	 * Tarjan's <code>visit()</code>.
	 */
	private void startFrom(Graph g, Set<Vertex> unmarked, Set<Vertex> tmpMarked,
			Vertex v, VertexVisitor<Vertex> visitor) {
		if (tmpMarked.contains(v)) {
			throw new GraphNotDagException("Graph is not a DAG");
		}
		if (unmarked.contains(v)) {
			tmpMarked.add(v);
			Iterator<Vertex> nextVertices = g.childVertexIterator(v);
			while (nextVertices.hasNext()) {
			 	startFrom(g, unmarked, tmpMarked, nextVertices.next(), visitor);
			}
			unmarked.remove(v);
			tmpMarked.remove(v);
			if (!visitor.visit(g, v)) {
				return;
			}
		}
	}

}
