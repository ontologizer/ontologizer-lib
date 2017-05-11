package de.ontologizer.immutable.io;

import de.ontologizer.immutable.ontology.Ontology;
import java.io.File;

/**
 * Writing of an ontology to the DOT format.
 * 
 * <pre>
 * Ontology ontology = // ...
 * 		DotOntologyWriter.fromPath("ontology.dot.gz").write(ontology);
 * </pre>
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DotOntologyWriter implements OntologyWriter {

	/** Where the data is written to. */
	private final WriterOutput writerOutput;

	/**
	 * Construct with {@link String} path to output file.
	 */
	public static DotOntologyWriter fromPath(String outputPath) {
		return fromFile(new File(outputPath));
	}

	/**
	 * Construct with output {@link File}.
	 */
	public static DotOntologyWriter fromFile(File outputFile) {
		return new DotOntologyWriter(new FileWriterOutput(outputFile));
	}

	/**
	 * Construct with {@link WriterOutput}.
	 */
	public DotOntologyWriter(WriterOutput writerOutput) {
		this.writerOutput = writerOutput;
	}

	@Override
	public void write(Ontology ontology) {
		throw new RuntimeException();
	}

}
