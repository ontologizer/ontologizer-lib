package de.ontologizer.immutable.ontology;

import de.ontologizer.immutable.graph.Edge;
import de.ontologizer.immutable.graph.ShallowCopyable;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermRelation;

/**
 * An edge in an {@link Ontology} graph, stores {@link TermRelation} as label.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface OntologyEdge extends Edge<Term>, ShallowCopyable<OntologyEdge> {

	/**
	 * Query for term relation of ontology edge.
	 * 
	 * @return {@link TermRelation} that is to be used for the edge label.
	 */
	public TermRelation getTermRelation();

}
