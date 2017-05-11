package de.ontologizer.immutable.ontology;

import com.google.common.base.Joiner;
import de.ontologizer.immutable.graph.ImmutableDirectedGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Subset;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermRelation;

/**
 * Enforcement of single roots for {@link ImmutableOntology}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ImmutableOntologySingleRootEnforcer
		implements OntologySingleRootEnforcer<ImmutableDirectedGraph<Term, ImmutableOntologyEdge>> {

	private static Logger LOGGER = Logger.getLogger(Ontology.class.getName());

	private final String artificialRootName;

	/**
	 * Use the given name for artificial root when introducing one.
	 */
	public ImmutableOntologySingleRootEnforcer(String artificialRootName) {
		this.artificialRootName = artificialRootName;
	}

	/**
	 * Simply a forward to
	 * <code>ImmutableOntologySingleRootEnforcer("root")</code>
	 */
	public ImmutableOntologySingleRootEnforcer() {
		this("root");
	}

	@Override
	public Result<ImmutableDirectedGraph<Term, ImmutableOntologyEdge>> enforceSingleRoot(TermContainer termContainer,
			ImmutableDirectedGraph<Term, ImmutableOntologyEdge> graph) {
		final List<Term> level1Terms = collectLevel1Terms(graph);
		if (level1Terms.size() > 1) {
			return constructFixedRootResult(termContainer, graph, level1Terms);
		} else {
			return constructSingleRootResult(termContainer, graph, level1Terms);
		}
	}

	/**
	 * Construct result object when introducing artificial root.
	 */
	private ResultImpl constructFixedRootResult(TermContainer termContainer,
			ImmutableDirectedGraph<Term, ImmutableOntologyEdge> graph, List<Term> level1Terms) {
		// Build printable list of level1 term names
		final List<String> level1TermNames = new ArrayList<String>();
		for (Term term : level1Terms) {
			level1TermNames.add(term.getName().toString());
		}
		final String jointNames = "\"" + Joiner.on("\", \"").join(level1TermNames) + "\"";

		// Construct artificial root term
		final Term rootTerm =
				new Term(level1Terms.get(0).getID().getPrefix().toString() + ":0000000", this.artificialRootName);

		LOGGER.log(Level.INFO, "Ontology contains multiple level-one terms: {}. Adding artificial root term \"{}\".",
				new Object[] { jointNames, rootTerm.getID().toString() });

		// Collect available subset and add artificial root terms to all
		final Set<Subset> availableSubsets = new HashSet<Subset>();
		for (Iterator<Term> it = graph.vertexIterator(); it.hasNext(); /* nop */) {
			final Term term = it.next();
			if (term.getSubsets() != null) {
				for (Subset subset : term.getSubsets()) {
					availableSubsets.add(subset);
				}
			}
		}

		// Rebuild graph with new artificial root term and edge list.
		final List<Term> extendedVertices = new ArrayList<Term>(graph.getVertices());
		extendedVertices.add(rootTerm);
		final List<ImmutableOntologyEdge> extendedEdges = new ArrayList<ImmutableOntologyEdge>();
		for (Iterator<ImmutableOntologyEdge> it = graph.edgeIterator(); it.hasNext(); /* nop */) {
			extendedEdges.add(it.next());
		}
		for (Term term : level1Terms) {
			extendedEdges.add(new ImmutableOntologyEdge(rootTerm, term, TermRelation.UNKOWN));
		}

		return new ResultImpl(rootTerm, ImmutableDirectedGraph.construct(extendedVertices, extendedEdges, true),
				termContainer, level1Terms);
	}

	/**
	 * Construct result object for one level 1 term.
	 */
	private ResultImpl constructSingleRootResult(TermContainer termContainer,
			ImmutableDirectedGraph<Term, ImmutableOntologyEdge> graph, List<Term> level1Terms) {
		ResultImpl result = new ResultImpl(level1Terms.get(0), graph, termContainer, level1Terms);
		LOGGER.log(Level.INFO, "Ontology contains a single level-one term ({})",
				new Object[] { result.getRoot().toString() });
		return result;
	}

	/**
	 * Collect terms on level 1 (have no outgoing edges).
	 * 
	 * @param graph
	 * @return
	 */
	private List<Term> collectLevel1Terms(ImmutableDirectedGraph<Term, ImmutableOntologyEdge> graph) {
		ArrayList<Term> result = new ArrayList<Term>();
		for (Iterator<Term> it = graph.vertexIterator(); it.hasNext(); /* nop */) {
			final Term term = it.next();
			if (graph.getInDegree(term) == 0 && !term.isObsolete()) {
				result.add(term);
			}
		}
		return result;
	}

	class ResultImpl implements de.ontologizer.immutable.ontology.OntologySingleRootEnforcer.Result<
			ImmutableDirectedGraph<Term, ImmutableOntologyEdge>> {

		private final Term root;
		private final ImmutableDirectedGraph<Term, ImmutableOntologyEdge> graph;
		private final TermContainer termContainer;
		private final List<Term> level1Terms;

		public ResultImpl(Term root, ImmutableDirectedGraph<Term, ImmutableOntologyEdge> graph,
				TermContainer termContainer, List<Term> level1Terms) {
			this.root = root;
			this.graph = graph;
			this.termContainer = termContainer;
			this.level1Terms = level1Terms;
		}

		@Override
		public Term getRoot() {
			return root;
		}

		@Override
		public ImmutableDirectedGraph<Term, ImmutableOntologyEdge> getGraph() {
			return graph;
		}

		@Override
		public TermContainer getTermContainer() {
			return termContainer;
		}

		@Override
		public Collection<Term> getLevel1Terms() {
			return level1Terms;
		}

	}

}
