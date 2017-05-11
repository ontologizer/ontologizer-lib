package de.ontologizer.immutable.io;

import de.ontologizer.immutable.ontology.Ontology;

public interface OntologyReader {

	/**
	 * Read {@link Ontology} from a file.
	 * 
	 * @return The completely read {@link Ontology}
	 */
	public Ontology read();

}
