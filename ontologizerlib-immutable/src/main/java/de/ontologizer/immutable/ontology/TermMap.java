package de.ontologizer.immutable.ontology;

import java.io.Serializable;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;

// TODO: could be merged with TermContainer

/**
 * Interface for mappings from {@link TermID} to {@link Term}.
 *
 * @author Sebastian Bauer
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermMap extends Iterable<Term>, Serializable {

	/**
	 * Return the full term reference to the given term id.
	 *
	 * @param termId
	 *            the term id for which to get the term.
	 * @return the {@link Term} or <code>null</code> if non could be found
	 */
	public Term get(TermID termId);

	/**
	 * Return number of terms in map.
	 * 
	 * @return number of terms in map
	 */
	public int countTerms();

	/**
	 * Return largest term ID.
	 * 
	 * @return largest term ID.
	 */
	public int maximumTermID();

}
