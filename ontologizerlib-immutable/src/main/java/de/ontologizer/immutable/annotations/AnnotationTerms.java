package de.ontologizer.immutable.annotations;

import java.util.Collection;
import ontologizer.ontology.Term;

/**
 * Store the {@link Term}s for a given {@link Annotation}
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface AnnotationTerms<AnnotationType extends Annotation> {

	/**
	 * Query for {@link Annotation} for which the {@link Term}s are stored.
	 * 
	 * @return {@link Annotation} for which the {@link Term}s are stored.
	 */
	public Annotation getAnnotation();

	/**
	 * Query for {@link Term}s that the given {@link Annotation} relates to.
	 * 
	 * @return {@link Collection} of {@link Term}s related to {@link Annotation}.
	 */
	public Collection<Term> getTerms();

}
