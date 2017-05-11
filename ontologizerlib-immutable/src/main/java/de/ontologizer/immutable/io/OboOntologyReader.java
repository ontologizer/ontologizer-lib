package de.ontologizer.immutable.io;

import de.ontologizer.immutable.ontology.Ontology;
import java.io.File;

/**
 * Read {@link Ontology} from an OBO file.
 * 
 * <pre>
 * Ontology ontology = OboOntologyReader.fromPath("input.obo.gz");
 * </pre>
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class OboOntologyReader implements OntologyReader {

	/** The {@link ReaderInput} to read from. */
	private final ReaderInput readerInput;

	/**
	 * Construct with {@link String} path to output file.
	 */
	public static OboOntologyReader fromPath(String outputPath) {
		return fromFile(new File(outputPath));
	}

	/**
	 * Construct with output {@link File}.
	 */
	public static OboOntologyReader fromFile(File outputFile) {
		return new OboOntologyReader(new FileReaderInput(outputFile));
	}

	/**
	 * Construct with {@link ReaderInput}.
	 */
	public OboOntologyReader(ReaderInput readerInput) {
		this.readerInput = readerInput;
	}

	@Override
	public Ontology read() {
		throw new RuntimeException();
	}

}
