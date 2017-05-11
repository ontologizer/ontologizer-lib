package de.ontologizer.immutable.ontology;

/**
 * Interface that all {@link Ontology} decorators should inherit from.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface OntologyDecorator<EdgeType extends OntologyEdge> extends Ontology<EdgeType> {

}
