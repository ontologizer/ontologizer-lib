package de.ontologizer.immutable.ontology;

import de.ontologizer.immutable.graph.DirectedGraph;
import de.ontologizer.immutable.graph.ImmutableDirectedGraph;
import java.util.Collection;
import java.util.Iterator;
import ontologizer.ontology.ParentTermID;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermRelation;

/**
 * Immutable implementation of {@link Ontology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableOntology implements Ontology {

	private static final long serialVersionUID = 1L;

	/** Wrapped {@link TermContainer}. */
	private final ImmutableTermContainer termContainer;

	/** Wrapped {@link DirectedGraph}. */
	private final ImmutableDirectedGraph<Term> graph;

	/**
	 * Construct the object with the given <code>termContainer</code> and
	 * </code>graph</code>
	 * 
	 * @param termContainer
	 *            {@link ImmutableTermContainer} with the ontology's
	 *            {@link Term}s
	 * @param graph
	 *            {@link ImmutableDirectedGraph} with the ontology's structure.
	 */
	public ImmutableOntology(ImmutableTermContainer termContainer,
			ImmutableDirectedGraph<Term> graph) {
		this.termContainer = termContainer;
		this.graph = graph;
	}

	@Override
	public Term get(TermID termId) {
		return termContainer.get(termId);
	}

	@Override
	public int countTerms() {
		return termContainer.countTerms();
	}

	@Override
	public int maximumTermID() {
		return termContainer.maximumTermID();
	}

	@Override
	public Iterator<Term> iterator() {
		return termContainer.iterator();
	}

	@Override
	public DirectedGraph<Term> getGraph() {
		return graph;
	}

	@Override
	public TermContainer getTermContainer() {
		return termContainer;
	}

	@Override
	public Collection<Term> getLeafTerms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getTermsInTopologicalOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRoot(TermID termId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isArtificialRoot(TermID termId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Term getTermOrRoot(TermID termId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getChildrTerms(TermID termId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getParentTerms(TermID termId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ParentTermID> getParentRelations(TermID termId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TermRelation getParentRelation(TermID termId, TermID parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<TermID> getTermSiblings(TermID termId) {
		// TODO Auto-generated method stub
		return null;
	}

}
