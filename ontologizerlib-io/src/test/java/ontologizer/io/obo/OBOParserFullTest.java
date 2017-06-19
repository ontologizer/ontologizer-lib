package ontologizer.io.obo;

import static ontologizer.types.ByteString.b;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import ontologizer.io.ParserFileInput;
import ontologizer.ontology.Term;
import ontologizer.types.ByteString;

/**
 * A test suite for testing reading complete obo files.
 *
 * @author Sebastian Bauer
 */
public class OBOParserFullTest
{
	/* internal fields */
	public static final String GOtermsOBOFile = OBOParserFullTest.class.
			getClassLoader().getResource("gene_ontology.1_2.obo.gz").getPath();
	private static final int nTermCount = 35520;
	private static final int nRelations = 63105;
	private static final ByteString formatVersion = b("1.2");
	private static final ByteString date = b("04:01:2012 11:50");
	private static final ByteString data_version = b("1.1.2476");

	@Test
	public void testTermBasics() throws IOException, OBOParserException
	{
		/* Parse OBO file */
		System.out.println("Parse OBO file");
		OBOParser oboParser = new OBOParser(new ParserFileInput(GOtermsOBOFile));
		System.out.println(oboParser.doParse());
		HashMap<String,Term> id2Term = new HashMap<String,Term>();

		int relations = 0;
		for (Term t : oboParser.getTermMap())
		{
			relations += t.getParents().length;
			id2Term.put(t.getIDAsString(),t);
		}

		assertEquals(nTermCount, oboParser.getTermMap().size());
		assertEquals(formatVersion,oboParser.getFormatVersion());
		assertEquals(date,oboParser.getDate());
		assertEquals(data_version,oboParser.getDataVersion());
		assertEquals(nRelations,relations);
		assertTrue(id2Term.containsKey("GO:0008150"));
		assertEquals(0,id2Term.get("GO:0008150").getParents().length);
	}

	@Test
	public void testIgnoreSynonyms() throws IOException, OBOParserException
	{
		OBOParser oboParser = new OBOParser(new ParserFileInput(GOtermsOBOFile),OBOParser.IGNORE_SYNONYMS);
		oboParser.doParse();
		for (Term t : oboParser.getTermMap())
			assertTrue(t.getSynonyms() == null || t.getSynonyms().length == 0);
	}
}
