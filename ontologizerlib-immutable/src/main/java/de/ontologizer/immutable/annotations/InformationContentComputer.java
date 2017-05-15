package de.ontologizer.immutable.annotations;

import de.ontologizer.immutable.ontology.Ontology;
import de.ontologizer.immutable.ontology.OntologyEdge;
import java.util.Collection;

/**
 * Given a list {@link TermAnnotation}s or {@link AnnotationTerms} and an {@link Ontology}, compute
 * {@link InformationContentMap}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class InformationContentComputer<AnnotationType extends Annotation, VertexType,
		EdgeType extends OntologyEdge, OntologyType extends Ontology<EdgeType>> {

	public static <AnnotationType extends Annotation, VertexType, EdgeType extends OntologyEdge,
			OntologyType extends Ontology<EdgeType>> InformationContentMap fromAnnotationTerms(
					Collection<AnnotationTerms<AnnotationType>> annotationTerms) {
		return null;
	}

	public static <AnnotationType extends Annotation, VertexType, EdgeType extends OntologyEdge,
			OntologyType extends Ontology<EdgeType>> InformationContentMap fromTermAnnotations(
					Collection<TermAnnotations<AnnotationType>> termAnnotations) {
		return null;
	}

}
