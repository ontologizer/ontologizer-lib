package de.ontologizer.immutable.graph.algorithms;

import de.ontologizer.immutable.graph.DirectedGraph;
import de.ontologizer.immutable.graph.Edge;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Visit all vertices of a {@link DirectedGraph} in topological order.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TopologicalSorting<VertexType, EdgeType extends Edge<VertexType>,
		GraphType extends DirectedGraph<VertexType, EdgeType>>
		implements GraphVertexAllIteration<VertexType, EdgeType, GraphType> {

	/**
	 * Implementation of Tarjan's algorithm for topological sorting.
	 */
	@Override
	public void start(GraphType g, VertexVisitor<VertexType, EdgeType> visitor) {
		final Set<VertexType> tmpMarked = new HashSet<VertexType>();

		// Collect unmarked vertices
		final Set<VertexType> unmarked = new HashSet<VertexType>();
		final Iterator<VertexType> vertexIterator = g.vertexIterator();
		while (vertexIterator.hasNext()) {
			unmarked.add(vertexIterator.next());
		}

		// Perform visiting
		while (!unmarked.isEmpty()) {
			final VertexType v = unmarked.iterator().next();
			startFrom(g, unmarked, tmpMarked, v, visitor);
		}
	}

	/**
	 * Tarjan's <code>visit()</code>.
	 */
	private void startFrom(GraphType g, Set<VertexType> unmarked, Set<VertexType> tmpMarked, VertexType v,
			VertexVisitor<VertexType, EdgeType> visitor) {
		if (tmpMarked.contains(v)) {
			throw new GraphNotDagException("Graph is not a DAG");
		}
		if (unmarked.contains(v)) {
			tmpMarked.add(v);
			Iterator<VertexType> nextVertices = g.childVertexIterator(v);
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
