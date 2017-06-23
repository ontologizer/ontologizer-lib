package ontologizer.io.obo;

import java.io.File;
import java.io.IOException;

import ontologizer.io.ParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermContainer;

/**
 * This class provides easy to use methods to create an
 * ontology from an obo file. No fancy interactions are supported.
 *
 * @author Sebastian Bauer
 */
public class OBOOntologyCreator
{
	/**
	 * Create an ontology from obo file.
	 *
	 * @param oboFile the file object describing attributes of the file to be parsed
	 * @return the ontology object
	 *
	 * @throws IOException
	 * @throws OBOParserException
	 */
	public static Ontology create(File oboFile) throws IOException, OBOParserException
	{
		return create(oboFile.getAbsolutePath());
	}

	/**
	 * Create an ontology from a file that is accessible via a local path.
	 *
	 * @param oboPath the local path of the ontology.
	 * @return the ontology object
	 *
	 * @throws IOException
	 * @throws OBOParserException
	 */
	public static Ontology create(String oboPath) throws IOException, OBOParserException
	{
		return create(new OBOParser(new ParserFileInput(oboPath)));
	}

	public static Ontology create(OBOParser parser) throws IOException, OBOParserException
	{
		parser.doParse();
		TermContainer termContainer = new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser.getDate());
		return Ontology.create(termContainer);
	}
}
