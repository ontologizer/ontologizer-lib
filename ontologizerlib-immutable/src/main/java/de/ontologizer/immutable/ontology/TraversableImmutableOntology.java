package de.ontologizer.immutable.ontology;

/**
 * Decorator class for traversing {@link ImmutableOntology} objects.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class TraversableImmutableOntology extends TraversableOntologyImpl<ImmutableOntologyEdge> {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct decorator from {@link ImmutableOntology}
	 * 
	 * @param ontology
	 *            {@link ImmutableOntology} to decorate
	 */
	public TraversableImmutableOntology(ImmutableOntology ontology) {
		super(ontology);
	}

}
