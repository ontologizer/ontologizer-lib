package de.ontologizer.immutable.graph.algorithms;

import de.ontologizer.immutable.graph.DirectedGraph;
import de.ontologizer.immutable.graph.Edge;

/**
 * Interface for classes implementating vertex visitors for graphs.
 *
 * <p>
 * This interface is primarily used by {@link GraphVertexStartFromIteration}
 * implementations.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface VertexVisitor<VertexType, EdgeType extends Edge<? extends VertexType>> {

	/**
	 * Called when visiting vertex <code>v</code> in graph <code>graph</code>.
	 *
	 * <p>
	 * No vertex will be visited twice in the same <code>visit()</code> call.
	 * </p>
	 *
	 * @param g
	 *            {@link DirectedGraph} that <code>v</code> belongs to
	 * @param v
	 *            currently visited <code>Vertex</code>
	 * @return <code>false</code> if the outer algorithm is to be stopped,
	 *         <code>true</code> if it is to be continued.
	 */
	public boolean visit(DirectedGraph<VertexType, EdgeType> g, VertexType v);

}
