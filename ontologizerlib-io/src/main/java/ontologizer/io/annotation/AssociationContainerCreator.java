package ontologizer.io.annotation;

import java.io.IOException;

import ontologizer.association.AssociationContainer;
import ontologizer.io.ParserFileInput;
import ontologizer.ontology.Ontology;

/**
 * This class provides easy to use methods to create an association
 * container from a local file. No fancy interactions are supported.
 *
 * @author Sebastian Bauer
 */
public class AssociationContainerCreator
{
	/**
	 * Create the association container from a simple file for the given ontology.
	 * Associations for which no term is within the ontology are ignored.
	 *
	 * @param gafPath the path of the gaf file
	 * @param ontology the ontology that defines the terms to be parsed
	 * @return the association container
	 * @throws IOException
	 */
	public static AssociationContainer create(String gafPath, Ontology ontology) throws IOException
	{
		AssociationParser parser = new AssociationParser(new ParserFileInput(gafPath), ontology.getTermMap());
		while (!parser.parse());
		return new AssociationContainer(parser.getAssociations(), parser.getAnnotationMapping());
	}
}
