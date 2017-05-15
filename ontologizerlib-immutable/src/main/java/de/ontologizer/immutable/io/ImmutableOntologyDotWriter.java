package de.ontologizer.immutable.io;

import de.ontologizer.immutable.ontology.ImmutableOntology;
import de.ontologizer.immutable.ontology.ImmutableOntologyEdge;

/**
 * Shortcut for writing out {@link ImmutableOntology} objects to DOT files.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableOntologyDotWriter extends DotOntologyWriter<ImmutableOntologyEdge> {

	public ImmutableOntologyDotWriter(WriterOutput writerOutput) {
		super(writerOutput);
	}

}
