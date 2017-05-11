package de.ontologizer.immutable.io;

import de.ontologizer.immutable.ontology.ImmutableOntology;
import de.ontologizer.immutable.ontology.ImmutableTermContainer;
import de.ontologizer.immutable.ontology.Ontology;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import ontologizer.io.obo.IParserInput;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;

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
	public ImmutableOntology readImmutable() {
		OBOParser parser = new OBOParser(new IParserInput() {
			@Override
			public InputStream inputStream() {
				return readerInput.inputStream();
			}

			@Override
			public void close() {
				readerInput.close();
			}

			@Override
			public int getSize() {
				return readerInput.getSize();
			}

			@Override
			public int getPosition() {
				return readerInput.getPosition();
			}

			@Override
			public String getFilename() {
				return readerInput.getFilename();
			}
		}, 0);

		// Peform the parsing
		try {
			parser.doParse();
		} catch (IOException e) {
			// XXX add to interface, document
			throw new RuntimeException("Problem reading OBO file", e);
		} catch (OBOParserException e) {
			// XXX add to interface, document
			throw new RuntimeException("Problem parsing OBO file", e);
		}

		// Build TermContainer from parse results
		return ImmutableOntology.constructFromTerms(
				new ImmutableTermContainer(parser.getTermMap(), parser.getFormatVersion(), parser.getDate()));
	}

}
