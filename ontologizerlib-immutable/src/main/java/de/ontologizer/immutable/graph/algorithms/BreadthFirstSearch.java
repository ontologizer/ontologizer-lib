package de.ontologizer.immutable.graph.algorithms;

import de.ontologizer.immutable.graph.DirectedGraph;
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
public class BreadthFirstSearch<Vertex, Graph extends DirectedGraph<Vertex>>
		implements
			GraphVertexStartFromIteration<Vertex, Graph> {

	/** Whether or not to iterate in reverse order. */
	final private boolean inReverse;

	/**
	 * Construct BFS in forward direction.
	 * 
	 * @param inReverse
	 *            Whether or not to iterate edges in reverse direction.
	 */
	public BreadthFirstSearch() {
		this(false);
	}

	/**
	 * Construct BFS object
	 * 
	 * @param inReverse
	 *            Whether or not to iterate edges in reverse direction.
	 */
	public BreadthFirstSearch(boolean inReverse) {
		this.inReverse = inReverse;
	}

	@Override
	public void startFrom(Graph g, Vertex v, VertexVisitor<Vertex> visitor) {
		final Set<Vertex> seen = new HashSet<Vertex>();
		final Queue<Vertex> queue = new ArrayDeque<Vertex>();
		queue.add(v);
		while (!queue.isEmpty()) {
			v = queue.poll();
			if (!seen.contains(v)) { // skip seen ones
				seen.add(v);
				if (!visitor.visit(g, v)) {
					break;
				}
				final Iterator<Vertex> it;
				if (inReverse) {
					it = g.childVertexIterator(v);
				} else {
					it = g.parentVertexIterator(v);
				}
				while (it.hasNext()) {
					queue.add(it.next());
				}
			}
		}
	}

}
