package de.ontologizer.immutable.annotations;

/**
 * Interface for the annotation of a {@link Term}.
 * 
 * <p>
 * Note that the relation between {@link Term} and {@link Annotation} generally is <code>n:m</code>.
 * </p>
 * 
 * <p>
 * Note that you have to override the {@link #hashCode()} and {@link #equals(Object)} for the
 * annotations to work properly.
 * </p>
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @see TermAnnotations
 * @see AnnotationTerms
 */
public interface Annotation {

}
