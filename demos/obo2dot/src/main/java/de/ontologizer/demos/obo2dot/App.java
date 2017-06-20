package de.ontologizer.demos.obo2dot;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ontologizer.io.dot.AbstractDotAttributesProvider;
import ontologizer.io.dot.GODOTWriter;
import ontologizer.io.obo.OBOOntologyCreator;
import ontologizer.io.obo.OBOParserException;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.TermID;

/**
 * Demo application <code>obo2dot</code> for OntologizerLib
 *
 * <p>
 * Loads an OBO file and write out the graph representation to DOT.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author Sebastian Bauer
 */
public class App {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Error: incorrect number of arguments");
			System.err.println("Usage: java -jar obo2dot.jar IN.obo OUT.dot");
			System.exit(1);
		}

		try {
			// Load Ontology from file
			Ontology ontology = OBOOntologyCreator.create(args[0]);

			// Write Ontology as DOT
			writeObo(ontology, args[1]);
		} catch (IOException e) {
			System.err.println(
					"ERROR: Problem reading OBO file. See below for technical information\n\n");
			e.printStackTrace();
			System.exit(1);
		} catch (OBOParserException e) {
			System.err.println(
					"ERROR: Problem parsing OBO file. See below for technical information\n\n");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void writeObo(Ontology ontology, String pathDot) {
		System.err.println("Writing ontology as DOT file to " + pathDot + " ...");

		Set<TermID> termIDs = new HashSet<TermID>();
		ontology.forEach(t -> termIDs.add(t.getID()));

		GODOTWriter.writeDOT(ontology, new File(pathDot), ontology.getRootTerm().getID(), termIDs,
				new AbstractDotAttributesProvider());
		System.err.println("=> done writing DOT file");
	}

}
