package de.ontologizer.immutable.ontology;

import de.ontologizer.immutable.graph.DirectedGraph;
import java.util.Collection;
import ontologizer.ontology.Term;

/**
 * Adjust a graph representing an {@link Ontology} by introducing root terms.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
interface OntologySingleRootEnforcer<GraphType extends DirectedGraph<Term, OntologyEdge>> {

	/**
	 * Process the given term container and graph for ontologies.
	 * 
	 * <p>
	 * Note that a new root term is not added to the result's term container but
	 * only to the graph.
	 * </p>
	 * 
	 * @param termContainer
	 *            {@link TermContainer} with the terms.
	 * @param graph
	 *            {@link DirectedGraph} representing the ontology.
	 * @return <code>Result</code> of enforcing a single root container.
	 */
	public Result<GraphType> enforceSingleRoot(TermContainer termContainer, GraphType graph);

	/**
	 * Interface for the type of the result of the root term adjustment.
	 * 
	 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel
	 *         Holtgrewe</a>
	 */
	public interface Result<GraphType extends DirectedGraph<Term, OntologyEdge>> {

		/**
		 * Query for root of possibly updated ontology.
		 * 
		 * @return Root {@link Term} of enforcing single root.
		 */
		public Term getRoot();

		/**
		 * Query for possibly updated graph.
		 * 
		 * @return Possibly modified {@link DirectedGraph}
		 */
		public GraphType getGraph();

		/**
		 * Query for terms on level 1.
		 * 
		 * @return {@link Collection} of terms on level1.
		 */
		public Collection<Term> getLevel1Terms();

		/**
		 * Query for term container, never changed.
		 * 
		 * @return Unchanged term container.
		 */
		public TermContainer getTermContainer();

	}

}
