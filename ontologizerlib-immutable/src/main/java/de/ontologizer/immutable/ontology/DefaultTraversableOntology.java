package de.ontologizer.immutable.ontology;

import de.ontologizer.immutable.graph.DirectedGraph;
import de.ontologizer.immutable.graph.Edge;
import de.ontologizer.immutable.graph.NeighborSelector;
import de.ontologizer.immutable.graph.algorithms.BreadthFirstSearch;
import de.ontologizer.immutable.graph.algorithms.VertexVisitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import ontologizer.ontology.OntologyEdge;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermRelation;

/**
 * Decorator for {@link Ontology} that allows the traversal of the ontology
 * adhering to the {@link TraversableOntology} interface.
 * 
 * <h3>Usage</h3>
 * 
 * <pre>
 * final Ontology ontology = // ...
 * final OntologyTraversalDecorator oTraversal =
 *     new OntologyTraversalDecorator(ontology);
 *
 * // ...
 *
 * oTraversal.walkToSource(new TermID("GO:", 123), new VertexVisitor() {
 *     &#64;Override
 *     public boolean visit(DirectedGraph<Term> g, Term v) {
 *     // ...
 *     return true;
 * });
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DefaultTraversableOntology implements TraversableOntology {

	private static final long serialVersionUID = 1L;

	/** Decorated {@link ImmutableOntology} to wrap. */
	private final Ontology ontology;

	/**
	 * Construct decorator with underlying {@link ImmutableOntology}.
	 * 
	 * @param ontology
	 *            {@link ImmutableOntology} to decorate.
	 */
	public DefaultTraversableOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	@Override
	public Term get(TermID termId) {
		return ontology.get(termId);
	}

	@Override
	public int countTerms() {
		return ontology.countTerms();
	}

	@Override
	public int maximumTermID() {
		return ontology.maximumTermID();
	}

	@Override
	public Iterator<Term> iterator() {
		return ontology.iterator();
	}

	@Override
	public boolean doesPathExist(TermID sourceId, TermID destId) {
		// Some special cases because of the artificial root
		if (ontology.isRoot(destId)) {
			if (ontology.isRoot(sourceId)) {
				return true;
			}
			return false;
		}

		// We walk from the destination to the source against the graph
		// direction in BFS fashion.
		final boolean[] pathExists = new boolean[1]; // yay for Java lambdas...
		final Term source = ontology.get(sourceId);
		Term dest = ontology.get(destId);

		new BreadthFirstSearch<Term, DirectedGraph<Term>>(true).startFrom(
				ontology.getGraph(), dest, new VertexVisitor<Term>() {
					@Override
					public boolean visit(DirectedGraph<Term> g, Term v) {
						if (!v.equals(source)) {
							return true;
						}
						pathExists[0] = true;
						return false;
					}
				});

		return pathExists[0];
	}

	@Override
	public void walkToSource(TermID termId, VertexVisitor<Term> visitor) {
		new BreadthFirstSearch<Term, DirectedGraph<Term>>().startFrom(
				ontology.getGraph(), get(termId), new NeighborSelector<Term>() {
					@Override
					public Iterator<Term> selectNeighbors(Term v) {
						final Iterator<? extends Edge<Term>> inIter = ontology
								.getGraph().inEdgeIterator(v);
						final ArrayList<Term> termsToConsider = new ArrayList<Term>();
						while (inIter.hasNext()) {
							OntologyEdge edge = (OntologyEdge) inIter
									.next(); /* Ugly cast */
							if (relationsToFollow.contains(edge.getRelation()))
								termsToConsider.add(edge.getSource());
						}
						return termsToConsider.iterator();
					}

				}, visitor);

		graph.bfs(termIDsToTerms(termIDSet), new INeighbourGrabber<Term>() {
			public Iterator<Term> grabNeighbours(Term t) {
				Iterator<Edge<Term>> inIter = graph.getInEdges(t);
				ArrayList<Term> termsToConsider = new ArrayList<Term>();
				while (inIter.hasNext()) {
					OntologyEdge edge = (OntologyEdge) inIter
							.next(); /* Ugly cast */
					if (relationsToFollow.contains(edge.getRelation()))
						termsToConsider.add(edge.getSource());
				}
				return termsToConsider.iterator();
			}
		}, vistingVertex);
	}

	@Override
	public void walkToSource(TermID termId, VertexVisitor<Term> visitor,
			Collection<TermRelation> relationsToFollow) {
		// TODO Auto-generated method stub
	}

	@Override
	public void walkToSource(Collection<TermID> termIds,
			VertexVisitor<Term> visitor) {
		for (TermID termId : termIds) {
			walkToSource(termId, visitor);
		}

	}

	@Override
	public void walkToSource(Collection<TermID> termIds,
			VertexVisitor<Term> visitor,
			Collection<TermRelation> relationsToFollow) {
		for (TermID termId : termIds) {
			walkToSinks(termId, visitor, relationsToFollow);
		}
	}

	@Override
	public void walkToSinks(TermID termId, VertexVisitor<Term> visitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void walkToSinks(TermID termId, VertexVisitor<Term> visitor,
			Collection<TermRelation> relationsToFollow) {
		// TODO Auto-generated method stub

	}

	@Override
	public void walkToSinks(Collection<TermID> termIds,
			VertexVisitor<Term> visitor) {
		for (TermID termId : termIds) {
			walkToSinks(termId, visitor);
		}
	}

	@Override
	public void walkToSinks(Collection<TermID> termIds,
			VertexVisitor<Term> visitor,
			Collection<TermRelation> relationsToFollow) {
		for (TermID termId : termIds) {
			walkToSinks(termId, visitor, relationsToFollow);
		}
	}

}
