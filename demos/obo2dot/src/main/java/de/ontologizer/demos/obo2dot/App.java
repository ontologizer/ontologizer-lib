package de.ontologizer.demos.obo2dot;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import ontologizer.io.dot.AbstractDotAttributesProvider;
import ontologizer.io.dot.GODOTWriter;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserFileInput;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermContainer;
import ontologizer.ontology.TermID;

public class App {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Error: incorrect number of arguments");
			System.err.println("Usage: java -jar obo2dot.jar IN.obo OUT.dot");
			System.exit(1);
		}

		try {
			// Load Ontology from file
			Ontology ontology = parseObo(args[0]);

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

	private static Ontology parseObo(String pathObo) throws IOException, OBOParserException {
		System.err.println("Reading ontology from OBO file " + pathObo + " ...");
		OBOParser parser = new OBOParser(new OBOParserFileInput(pathObo));
		String parseResult = parser.doParse();

		System.err.println("Information about parse result:");
		System.err.println(parseResult);
		TermContainer termContainer =
				new TermContainer(parser.getTermMap(), parser.getFormatVersion(), parser.getDate());
		final Ontology ontology = Ontology.create(termContainer);
		System.err.println("=> done reading OBO file");
		return ontology;
	}

	private static void writeObo(Ontology ontology, String pathDot) {
		System.err.println("Writing ontology as DOT file to " + pathDot + " ...");
		final Set<TermID> terms = new HashSet<TermID>();
		for (Term t : ontology.getGraph().getVertices())
			terms.add(t.getID());
		GODOTWriter.writeDOT(ontology, new File(pathDot), ontology.getRootTerm().getID(), terms,
				new AbstractDotAttributesProvider());
		System.err.println("=> done writing DOT file");
	}

}
