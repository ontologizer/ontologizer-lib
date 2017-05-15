package de.ontologizer.immutable.io;

import de.ontologizer.immutable.ontology.Ontology;
import de.ontologizer.immutable.ontology.OntologyEdge;

public interface OntologyWriter<EdgeType extends OntologyEdge> {

	/**
	 * Write an {@link Ontology} to the writer.
	 * 
	 * @param ontology
	 */
	public void write(Ontology<EdgeType> ontology);
	
}
