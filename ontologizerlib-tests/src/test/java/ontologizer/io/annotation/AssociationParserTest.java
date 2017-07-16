package ontologizer.io.annotation;

import static ontologizer.types.ByteString.EMPTY;
import static ontologizer.types.ByteString.b;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ontologizer.TestBase;
import ontologizer.TestSourceUtils;
import ontologizer.association.Association;
import ontologizer.association.AssociationContainer;
import ontologizer.association.AssociationResolver;
import ontologizer.io.ParserFileInput;
import ontologizer.io.obo.OBOParser;
import ontologizer.io.obo.OBOParserException;
import ontologizer.ontology.TermContainer;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermPropertyMap;
import ontologizer.types.ByteString;

public class AssociationParserTest extends TestBase
{
	private static final String OBO_FILE = AssociationParserTest.class.
			getClassLoader().getResource("gene_ontology.1_2.obo.gz").getPath();
	private static final String ASSOCIATION_FILE = AssociationParserTest.class.
			getClassLoader().getResource("gene_association.sgd.gz").getPath();

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Test
	public void testSimple() throws IOException, OBOParserException
	{
		int nAnnotatedGenes = 6359;
		int nAssociations = 87599;
		int nSynonyms = 9251;
		int nDBObjects = 6359;

		String[] someGenes = {"SRL1", "DDR2", "UFO1"};
		int[] someGeneTermCounts = {11, 4, 8};

		OBOParser oboParser = new OBOParser(new ParserFileInput(OBO_FILE));
		oboParser.doParse();
		AssociationParser ap = new AssociationParser(new ParserFileInput(ASSOCIATION_FILE), new TermContainer(oboParser.getTermMap(), EMPTY, EMPTY));
		assertEquals(ap.getFileType(),AssociationParser.Type.GAF);
		assertEquals(87599, ap.getAssociations().size());

		Association a = ap.getAssociations().get(0);
		assertEquals("S000007287",a.getDB_Object().toString());

		/* Note that this excludes NOT annotations */
		a = ap.getAssociations().get(49088);
		assertEquals("S000004009",a.getDB_Object().toString());

		AssociationContainer ac = new AssociationContainer(ap.getAssociations(), ap.getAnnotationMapping());
		assertEquals("number of parsed associations", nAssociations, ap.getAssociations().size());
		assertEquals("number of parsed synonyms", nSynonyms,ap.getAnnotationMapping().getNumberOfSynonyms());
		assertEquals("number of parsed DB objects", nDBObjects,ap.getAnnotationMapping().getSymbols().length);
		assertEquals("number of annotated genes", nAnnotatedGenes,ac.getAllAnnotatedGenes().size());

		for (int i=0; i<someGenes.length; i++) {
			Assert.assertEquals(ac.get(b(someGenes[i])).getAssociations().size(), someGeneTermCounts[i]);
		}
	}

	@Test
	public void testWithoutTermMap() throws IOException
	{
		AssociationParser ap = new AssociationParser(new ParserFileInput(ASSOCIATION_FILE));
		assertEquals(ap.getFileType(),AssociationParser.Type.GAF);
		assertEquals(87599, ap.getAssociations().size());

		Association a = ap.getAssociations().get(0);
		assertEquals("S000007287",a.getDB_Object().toString());
	}

	@Test
	public void testUncompressed() throws IOException, OBOParserException
	{
		/* As testSimple() but bypasses auto decompression by manually decompressing
		 * the association file
		 */
		File assocFile = tmpFolder.newFile();
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(ASSOCIATION_FILE));
		FileOutputStream out = new FileOutputStream(assocFile);
		byte [] buf = new byte[4096];
		int read;
		while ((read = in.read(buf)) > 0)
			out.write(buf, 0,  read);
		in.close();
		out.close();

		OBOParser oboParser = new OBOParser(new ParserFileInput(OBO_FILE));
		oboParser.doParse();
		AssociationParser ap = new AssociationParser(new ParserFileInput(assocFile.getAbsolutePath()), new TermContainer(oboParser.getTermMap(), EMPTY, EMPTY));
		assertEquals(ap.getFileType(),AssociationParser.Type.GAF);
		assertEquals(87599, ap.getAssociations().size());

		Association a = ap.getAssociations().get(0);
		assertEquals("S000007287",a.getDB_Object().toString());

		/* Note that this excludes NOT annotations */
		a = ap.getAssociations().get(49088);
		assertEquals("S000004009",a.getDB_Object().toString());
	}

	/// # Comment1
	/// DB\tDBOBJID2\tSYMBOL\t\tGO:0005760\tPMID:00000\tEVIDENCE\t\tC\t\tgene\ttaxon:4932\t20121212\tSBA
	@Test
	public void testSkipHeader() throws IOException, OBOParserException
	{
		String gafFile = getTestCommentAsPath(".gaf", TestSourceUtils.DECODE_TABS);

		OBOParser oboParser = new OBOParser(new ParserFileInput(OBO_FILE));
		oboParser.doParse();

		AssociationParser ap = new AssociationParser(new ParserFileInput(gafFile), new TermContainer(oboParser.getTermMap(), EMPTY, EMPTY));
		AssociationContainer assoc = new AssociationContainer(ap.getAssociations(), ap.getAnnotationMapping());

		assertEquals(1, assoc.getAllAnnotatedGenes().size());
	}

	/// DB\tDBOBJID2\tSYMBOL\t\tGO:0005760\tPMID:00000\tEVIDENCE\t\tC\t\tgene\ttaxon:4932\t20121212\tSBA
	@Test
	public void testReadFromCompressedFile() throws IOException, OBOParserException
	{
		String gafFile = getTestCommentAsPath(".gaf", TestSourceUtils.DECODE_TABS);

		OBOParser oboParser = new OBOParser(new ParserFileInput(OBO_FILE));
		oboParser.doParse();

		AssociationParser ap = new AssociationParser(new ParserFileInput(gafFile), new TermContainer(oboParser.getTermMap(), EMPTY, EMPTY));
		AssociationContainer assoc = new AssociationContainer(ap.getAssociations(), ap.getAnnotationMapping());

		assertEquals(1, assoc.getAllAnnotatedGenes().size());
	}

	/// DB\tDBOBJID1\tSYMBOL\t\tGO:0005763\tPMID:00000\tEVIDENCE\t\tC\tSYNONYM1|SYNONYM2\tgene\ttaxon:4932\t20121212\tSBA
	/// DB\tDBOBJID2\tSYMBOL\t\tGO:0005760\tPMID:00000\tEVIDENCE\t\tC\t\tgene\ttaxon:4932\t20121212\tSBA
	@Test
	public void testAmbiguousGAFCaseA() throws IOException, OBOParserException
	{
		String gafFile = getTestCommentAsPath(".gaf", TestSourceUtils.DECODE_TABS);

		OBOParser oboParser = new OBOParser(new ParserFileInput(OBO_FILE));
		oboParser.doParse();

		WarningCapture warningCapture = new WarningCapture();
		AssociationParser ap = new AssociationParser(new ParserFileInput(gafFile), new TermContainer(oboParser.getTermMap(), EMPTY, EMPTY), null, warningCapture);
		AssociationContainer assoc = new AssociationContainer(ap.getAssociations(), ap.getAnnotationMapping());

		/* We expect only one annotated object as DBOBJID1 is the same as DBOBJID2 due to the same symbol */
		assertEquals(1,assoc.getAllAnnotatedGenes().size());
		assertEquals("SYMBOL",assoc.getAllAnnotatedGenes().iterator().next().toString());
		assertEquals(1, warningCapture.warnings.size());
		/* DBOBJID2 becomes a synonym */
		assertEquals(true, assoc.isSynonym(new ByteString("DBOBJID2")));
	}

	///
	///
	/// DB\tDBOBJID\tSYMBOL1\t\tGO:0005763\tPMID:00000\tEVIDENCE\t\tC\tSYNONYM1|SYNONYM2\tgene\ttaxon:4932\t20121212\tSBA
	/// DB\tDBOBJID2\tSYMBOL2\t\tGO:0005760\tPMID:00000\tEVIDENCE\t\tC\t\tgene\ttaxon:4932\t20121212\tSBA
	@Test
	public void testTwoEntries() throws IOException, OBOParserException
	{
		String gafFile = getTestCommentAsPath(".gaf", TestSourceUtils.DECODE_TABS);

		OBOParser oboParser = new OBOParser(new ParserFileInput(OBO_FILE));
		oboParser.doParse();

		AssociationParser ap = new AssociationParser(new ParserFileInput(gafFile), new TermContainer(oboParser.getTermMap(), EMPTY, EMPTY));
		AssociationContainer assoc = new AssociationContainer(ap.getAssociations(), ap.getAnnotationMapping());

		assertEquals(2,assoc.getAllAnnotatedGenes().size());
	}

	/**
	 * A progress that just captures the warnings.
	 */
	public static class WarningCapture implements IAssociationParserProgress
	{
		public List<String> warnings = new ArrayList<String>();

		@Override
		public void init(int max)
		{
		}

		@Override
		public void update(int current)
		{
		}

		@Override
		public void warning(String message)
		{
			warnings.add(message);
		}
	}

	///
	/// DB\tDBOBJID\tSYMBOL1\t\tGO:0005763\tPMID:00000\tEVIDENCE\t\tC\tSYNONYM1|SYNONYM2\tgene\ttaxon:4932\t20121212\tSBA
	/// DB\tDBOBJID\tSYMBOL2\t\tGO:0005760\tPMID:00000\tEVIDENCE\t\tC\t\tgene\ttaxon:4932\t20121212\tSBA
	///
	@Test
	public void testAmbiguousGAFCaseB() throws IOException, OBOParserException
	{
		String gafFile = getTestCommentAsPath(".gaf", TestSourceUtils.DECODE_TABS);

		OBOParser oboParser = new OBOParser(new ParserFileInput(OBO_FILE));
		oboParser.doParse();

		WarningCapture warningCapture = new WarningCapture();
		AssociationParser ap = new AssociationParser(new ParserFileInput(gafFile), new TermContainer(oboParser.getTermMap(), EMPTY, EMPTY), null, warningCapture);
		AssociationContainer assoc = new AssociationContainer(ap.getAssociations(), ap.getAnnotationMapping());

		assertEquals(2, assoc.getAllAnnotatedGenes().size());
		assertEquals(1, warningCapture.warnings.size());
	}

	/// DB\tDBOBJID1\tSYMBOL1\t\tGO:0000104\tPMID:00000\tEVIDENCE\t\tC\t\tgene\ttaxon:4932\t20121212\tSBA
	/// DB\tDBOBJID2\tSYMBOL2\t\tGO:0019739\tPMID:00000\tEVIDENCE\t\tC\t\tgene\ttaxon:4932\t20121212\tSBA
	@Test
	public void testAltId() throws IOException, OBOParserException
	{
		/* Here we test if the annotations are properly resolved.
		 * In particular, GO:0019739 is an alternative id for GO:0000104
		 * so both annotations should refer the same term.
		 */

		String gafFile = getTestCommentAsPath(".gaf", TestSourceUtils.DECODE_TABS);

		TermContainer tc = createTermContainer();
		TermPropertyMap<TermID> altIdMap = new TermPropertyMap<TermID>(tc, TermPropertyMap.term2AltIdMap);

		/* The precondition: check if the ids really refer to the same term */
		assertEquals(new TermID("GO:0000104"), altIdMap.get(new TermID("GO:0019739")));

		/* Now parse associations, we expect that both refer to the same term */
		AssociationParser ap = new AssociationParser(new ParserFileInput(gafFile), tc);
		assertEquals(2, ap.getAssociations().size());

		Association a0 = ap.getAssociations().get(0);
		Association a1 = ap.getAssociations().get(1);

		assertEquals("DBOBJID1", a0.getDB_Object().toString());
		assertEquals("DBOBJID2", a1.getDB_Object().toString());

		assertEquals(new TermID("GO:0000104"), a0.getTermID());
		assertEquals(new TermID("GO:0000104"), a1.getTermID());
	}

	/// DB\tDBOBJID1\tSYMBOL1\t\tGO:0000104\tPMID:00000\tEVIDENCE\t\tC\t\tgene\ttaxon:4932\t20121212\tSBA
	/// DB\tDBOBJID2\tSYMBOL2\t\tGO:0019739\tPMID:00000\tEVIDENCE\t\tC\t\tgene\ttaxon:4932\t20121212\tSBA
	@Test
	public void testAltIdLaterResolving() throws IOException, OBOParserException
	{
		/* Here we test if the annotations are properly resolved after
		 * all of them have read in.
		 *
		 * In particular, GO:0019739 is an alternative id for GO:0000104
		 * so both annotations should refer the same term.
		 *
		 * In this test, the resolving will happen explicitly after the
		 * annotations have been loaded.
		 */

		String gafFile = getTestCommentAsPath(".gaf", TestSourceUtils.DECODE_TABS);

		TermContainer tc = createTermContainer();
		TermPropertyMap<TermID> altIdMap = new TermPropertyMap<TermID>(tc, TermPropertyMap.term2AltIdMap);

		/* The precondition: check if the ids really refer to the same term */
		assertEquals(new TermID("GO:0000104"), altIdMap.get(new TermID("GO:0019739")));

		/* Now parse associations, we expect that both refer to the same term */
		AssociationParser ap = new AssociationParser(new ParserFileInput(gafFile));
		assertEquals(2, ap.getAssociations().size());

		Association a0 = ap.getAssociations().get(0);
		Association a1 = ap.getAssociations().get(1);

		assertEquals("DBOBJID1", a0.getDB_Object().toString());
		assertEquals("DBOBJID2", a1.getDB_Object().toString());

		assertEquals(new TermID("GO:0000104"), a0.getTermID());
		assertEquals(new TermID("GO:0019739"), a1.getTermID());

		/* Now resolve the annotations */
		AssociationResolver resolver = new AssociationResolver(tc);
		List<Association> associations = resolver.resolveAndModify(ap.getAssociations());

		a0 = associations.get(0);
		a1 = associations.get(1);

		assertEquals(new TermID("GO:0000104"), a0.getTermID());
		assertEquals(new TermID("GO:0000104"), a1.getTermID());
	}


	/**
	 * Create a term container for the test obo file.
	 *
	 * @return the term container
	 * @throws IOException
	 * @throws OBOParserException
	 */
	private TermContainer createTermContainer() throws IOException, OBOParserException
	{
		OBOParser oboParser = new OBOParser(new ParserFileInput(OBO_FILE));
		oboParser.doParse();

		TermContainer tc = new TermContainer(oboParser.getTermMap(), EMPTY, EMPTY);
		return tc;
	}

	///
	/// S000007287\tGO:0005763,GO:0032543,GO:0042255,GO:0003735,GO:0032543,GO:0005762,GO:0003735,GO:0003735,GO:0042255
	/// S000004660\tGO:0005739,GO:0006810,GO:0005743,GO:0016020,GO:0055085,GO:0005488
	/// S000004660\tGO:0006810,GO:0005471,GO:0016021,GO:0006783,GO:0005743,GO:0005743
	///
	@Test
	public void testIDS() throws IOException, OBOParserException
	{
		String idsFile = getTestCommentAsPath(".ids", TestSourceUtils.DECODE_TABS);

		OBOParser oboParser = new OBOParser(new ParserFileInput(OBO_FILE));
		oboParser.doParse();

		AssociationParser ap = new AssociationParser(new ParserFileInput(idsFile),new TermContainer(oboParser.getTermMap(), EMPTY, EMPTY));
		assertEquals(21,ap.getAssociations().size());
	}
}
