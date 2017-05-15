package de.ontologizer.immutable.annotations;

import java.util.Collection;
import ontologizer.ontology.Term;

/**
 * Store the {@link Annotation}s for a given {@link Term}
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermAnnotations<AnnotationType extends Annotation> {

	/**
	 * Query for {@link Term} for which the {@link Annotation}s are stored.
	 * 
	 * @return {@link Term} for which the {@link Annotation}s are stored.
	 */
	public Term getTerm();

	/**
	 * Query for {@link Annotations}s that the given {@link Term} is associated with.
	 * 
	 * @return {@link Collection} of {@link Annotation}s related to {@link Term}.
	 */
	public Collection<Annotation> getAnnotations();
	
}
