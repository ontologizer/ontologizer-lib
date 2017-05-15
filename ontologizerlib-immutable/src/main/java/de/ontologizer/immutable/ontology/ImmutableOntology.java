package de.ontologizer.immutable.ontology;

import com.google.common.collect.ImmutableList;
import de.ontologizer.immutable.graph.DirectedGraph;
import de.ontologizer.immutable.graph.ImmutableDirectedGraph;
import de.ontologizer.immutable.graph.algorithms.TopologicalSorting;
import de.ontologizer.immutable.graph.algorithms.VertexVisitor;
import de.ontologizer.immutable.ontology.OntologySingleRootEnforcer.Result;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import ontologizer.ontology.ParentTermID;
import ontologizer.ontology.Subset;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermRelation;

/**
 * Immutable implementation of {@link Ontology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ImmutableOntology implements Ontology<ImmutableOntologyEdge> {

	private static final long serialVersionUID = 1L;

	private static Logger LOGGER = Logger.getLogger(ImmutableOntology.class.getName());

	/** Wrapped {@link TermContainer}. */
	private final ImmutableTermContainer termContainer;

	/** Wrapped {@link DirectedGraph}. */
	private final ImmutableDirectedGraph<Term, ImmutableOntologyEdge> graph;

	/** The {@link Term}s on the first level. */
	private final ImmutableList<Term> level1Terms;

	/** The ontology's root {@link Term}. */
	private final Term rootTerm;

	/**
	 * Construction of {@link Ontology} from {@link ImmutableTermContainer}.
	 * 
	 * @param termContainer
	 *            {@link ImmutableTermContainer} to construct from.
	 * @return Constructed {@link ImmutableOntology} built from {@link ImmutableTermContainer}
	 */
	public static ImmutableOntology constructFromTerms(ImmutableTermContainer termContainer) {
		// Construct underlying graph using graph builder
		final ImmutableDirectedGraph.Builder<Term, ImmutableOntologyEdge> builder = ImmutableDirectedGraph
				.builder(ImmutableOntologyEdge.factory());

		// Add all terms to the graph builder
		for (Term term : termContainer) {
			builder.addVertex(term);
		}

		// Now add the edges for linking the terms
		final Set<Subset> availableSubsets = new HashSet<Subset>();
		int skippedEdges = 0;
		for (Term term : termContainer) {
			// Register all subsets
			if (term.getSubsets() != null) {
				for (Subset subset : term.getSubsets()) {
					availableSubsets.add(subset);
				}
			}

			for (ParentTermID parent : term.getParents()) {
				// Ignore loops
				if (term.getID().equals(parent.getTermID())) {
					LOGGER.log(Level.INFO,
							"Detected self-loop in the definition of the ontology (term {}). "
									+ "This link has been ignored.",
							new Object[] { term.getID().toString() });
					skippedEdges += 1;
					continue;
				}

				// Skip links to missing vertices
				// TODO: We may want to add a new vertex to the graph here
				// instead eventually
				if (termContainer.get(parent.getTermID()) == null) {
					LOGGER.log(Level.INFO,
							"Could not add a link from term {} to {} as the latter's definition is missing.",
							new Object[] { term.toString(), parent.termid.toString() });
					skippedEdges += 1;
					continue;
				}

				builder.addEdge(new ImmutableOntologyEdge(termContainer.get(parent.getTermID()),
						term, parent.getTermRelation()));
			}
		}

		if (skippedEdges > 0) {
			LOGGER.log(Level.INFO, "A total of {} edges were skipped.",
					new Object[] { skippedEdges });
		}

		return new ImmutableOntology(termContainer, builder.build());
	}

	/**
	 * Construct the object with the given <code>termContainer</code> and </code>graph</code>.
	 * 
	 * <p>
	 * Note that the <code>termContainer</code> and <code>graph</code> might end up as extended
	 * copies in the constructed <code>Ontology</code> if an artificial root term is introduced.
	 * </p>
	 * 
	 * @param termContainer
	 *            {@link ImmutableTermContainer} with the ontology's {@link Term}s
	 * @param graph
	 *            {@link ImmutableDirectedGraph} with the ontology's structure.
	 */
	private ImmutableOntology(ImmutableTermContainer termContainer,
			ImmutableDirectedGraph<Term, ImmutableOntologyEdge> graph) {
		final OntologySingleRootEnforcer<ImmutableDirectedGraph<Term, ImmutableOntologyEdge>> enforcer = new ImmutableOntologySingleRootEnforcer();
		final Result<ImmutableDirectedGraph<Term, ImmutableOntologyEdge>> enforced = enforcer
				.enforceSingleRoot(termContainer, graph);

		this.termContainer = (ImmutableTermContainer) enforced.getTermContainer();
		this.graph = enforced.getGraph();
		this.level1Terms = ImmutableList.copyOf(enforced.getLevel1Terms());
		this.rootTerm = enforced.getRoot();
	}

	@Override
	public Term getRootTerm() {
		return rootTerm;
	}

	@Override
	public Term get(TermID termId) {
		if (termId.equals(rootTerm.getID())) {
			return rootTerm;
		} else {
			return termContainer.get(termId);
		}
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
	public DirectedGraph<Term, ImmutableOntologyEdge> getGraph() {
		return graph;
	}

	@Override
	public TermContainer getTermContainer() {
		return termContainer;
	}

	@Override
	public Collection<Term> getLeafTerms() {
		List<Term> result = new ArrayList<Term>();
		for (Iterator<Term> vIt = graph.vertexIterator(); vIt.hasNext(); /* nop */) {
			result.add(vIt.next());
		}
		return result;
	}

	@Override
	public List<Term> getTermsInTopologicalOrder() {
		final List<Term> result = new ArrayList<Term>();
		new TopologicalSorting<Term, ImmutableOntologyEdge, DirectedGraph<Term, ImmutableOntologyEdge>>()
				.start(graph, new VertexVisitor<Term, ImmutableOntologyEdge>() {
					@Override
					public boolean visit(DirectedGraph<Term, ImmutableOntologyEdge> g, Term v) {
						result.add(v);
						return true;
					}
				});
		return result;
	}

	@Override
	public boolean isRootTerm(TermID termId) {
		return rootTerm.equals(termId);
	}

	@Override
	public boolean isArtificialRoot(TermID termId) {
		final Term term = get(termId);

		if (term == null) {
			return false;
		} else {
			return isRootTerm(termId) && getLevel1Terms().contains(term);
		}
	}

	@Override
	public Collection<Term> getLevel1Terms() {
		return level1Terms;
	}

	@Override
	public Collection<Term> getChildTerms(TermID termId) {
		List<Term> result = new ArrayList<Term>();
		for (Iterator<ImmutableOntologyEdge> inEdgeIt = graph.inEdgeIterator(get(termId)); inEdgeIt
				.hasNext(); /* nop */) {
			result.add(inEdgeIt.next().getSource());
		}
		return result;
	}

	@Override
	public Collection<Term> getParentTerms(TermID termId) {
		List<Term> result = new ArrayList<Term>();
		for (Iterator<ImmutableOntologyEdge> inEdgeIt = graph.outEdgeIterator(get(termId)); inEdgeIt
				.hasNext(); /* nop */) {
			result.add(inEdgeIt.next().getDest());
		}
		return result;
	}

	@Override
	public Collection<ParentTermID> getParentRelations(TermID termId) {
		final Set<ParentTermID> result = new HashSet<ParentTermID>();
		if (rootTerm.getID().equals(termId)) {
			return result;
		}

		final Term term = get(termId);
		for (Iterator<ImmutableOntologyEdge> inEdgeIt = graph.inEdgeIterator(term); inEdgeIt
				.hasNext(); /* nop */) {
			OntologyEdge e = inEdgeIt.next();
			result.add(new ParentTermID(e.getSource().getID(), e.getTermRelation()));
		}
		return result;
	}

	@Override
	public TermRelation getParentRelation(TermID termId, TermID parentId) {
		for (ParentTermID p : getParentRelations(termId)) {
			if (p.getTermID().equals(parentId)) {
				return p.getTermRelation();
			}
		}
		return null;
	}

	@Override
	public Collection<TermID> getTermSiblings(TermID termId) {
		Set<TermID> result = new HashSet<TermID>();
		for (Term term : getParentTerms(termId)) {
			for (Term childTerm : getChildTerms(term.getID())) {
				result.add(childTerm.getID());
			}
		}
		result.remove(termId);
		return result;
	}

}
