package de.ontologizer.immutable.similarity;

import de.ontologizer.immutable.annotations.InformationContentMap;
import de.ontologizer.immutable.ontology.ImmutableOntology;
import de.ontologizer.immutable.ontology.Ontology;
import ontologizer.ontology.Term;

/**
 * Implementation of Resnik's similarity measure between two terms.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class ResnikOneToOneSimilarityComputation implements OneToOneSimilarityComputation {

	/** The ontology to base the similarity computation on. */
	final ImmutableOntology ontology;

	/**
	 * Information content for each term in the {@link #ontology} as required by Resnik's similarity
	 * measure.
	 */
	final InformationContentMap informationContent;

	/**
	 * Construct computation object for Resnik's similarity measure.
	 *
	 * @param ontology
	 *            The {@link Ontology} to base the computation on
	 * @param ontologyFast
	 *            The slimmed-down version to use for faster access
	 * @param informationContent
	 *            Label for each {@link Term} in <code>ontology</code> with the information content
	 */
	public ResnikOneToOneSimilarityComputation(ImmutableOntology ontology,
			InformationContentMap informationContent) {
		this.ontology = ontology;
		this.informationContent = informationContent;
	}

	@Override
	public double computeScore(Term t1, Term t2) {
		throw new RuntimeException("Implement me!");
	}

}
