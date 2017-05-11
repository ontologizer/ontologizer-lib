package de.ontologizer.immutable.io;

import de.ontologizer.immutable.ontology.Ontology;

public interface OntologyWriter {

	/**
	 * Write an {@link Ontology} to the writer.
	 * 
	 * @param ontology
	 */
	public void write(Ontology ontology);
	
}
