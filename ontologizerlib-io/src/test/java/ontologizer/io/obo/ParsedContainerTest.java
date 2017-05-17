package ontologizer.io.obo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ontologizer.io.ParserFileInput;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermContainer;

public class ParsedContainerTest
{
	// reusable fields for dependent tests
	public TermContainer container;

	// internal fields
	private OBOParser oboParser;

	private Term bioproc = new Term("GO:0008150", "biological_process");
	private Term cellcomp = new Term("GO:0005575", "cellular_component");
	private Term molfunc = new Term("GO:0003674", "molecular_function");

	@Before
	public void setUp() throws Exception
	{
		oboParser = new OBOParser(new ParserFileInput(OBOParserTest.GOtermsOBOFile));
		oboParser.doParse();
		container = new TermContainer(oboParser.getTermMap(), oboParser.getFormatVersion(), oboParser.getDate());
	}


	@Test
	public void testBasicStructure()
	{

		Assert.assertTrue(container.termCount() == oboParser.getTermMap().size());
		Assert.assertTrue(container.getFormatVersion().equals(oboParser.getFormatVersion()));
		Assert.assertTrue(container.getDate().equals(oboParser.getDate()));

		Assert.assertTrue(container.getName("GO:0008150").equals("biological_process"));
		Assert.assertTrue(container.getName(bioproc.getID()).equals("biological_process"));
		Assert.assertTrue(container.getName("GO:0005575").equals("cellular_component"));
		Assert.assertTrue(container.getName(cellcomp.getID()).equals("cellular_component"));
		Assert.assertTrue(container.getName("GO:0003674").equals("molecular_function"));
		Assert.assertTrue(container.getName(molfunc.getID()).equals("molecular_function"));

		Assert.assertTrue(container.get("GO:0008150").equals(bioproc));
		Assert.assertTrue(container.get(bioproc.getID()).equals(bioproc));
		Assert.assertTrue(container.get("GO:0005575").equals(cellcomp));
		Assert.assertTrue(container.get(cellcomp.getID()).equals(cellcomp));
		Assert.assertTrue(container.get("GO:0003674").equals(molfunc));
		Assert.assertTrue(container.get(molfunc.getID()).equals(molfunc));

		Assert.assertTrue(container.get("GO:9999999") == null);
		Term anotherTerm = new Term("GO:9999999", "dummy");
		Assert.assertTrue(container.get(anotherTerm.getID()) == null);
	}


}
