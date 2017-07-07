package ontologizer.ontology;

import static ontologizer.types.ByteString.b;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class TermContainerTest
{
	private TermContainer container;
	private Term root;
	private Term bioproc;
	private Term cellcomp;
	private Term molfunc;


	@Before
	public void setUp() throws Exception
	{
		root = new Term("GO:0000000", "root", (ParentTermID)null);
		ArrayList<ParentTermID> rootlist = new ArrayList<ParentTermID>();
		rootlist.add(new ParentTermID(root.getID(), RelationType.UNKNOWN));
		bioproc = new Term("GO:0008150", "biological process", new Namespace("B"), rootlist);
		cellcomp = new Term("GO:0005575", "cellular component", new Namespace("C"), rootlist);
		molfunc = new Term("GO:0003674", "molecular function", new Namespace("F"), rootlist);

		HashSet<Term> termsConstructed = new HashSet<Term>();
		termsConstructed.add(root);
		termsConstructed.add(bioproc);
		termsConstructed.add(cellcomp);
		termsConstructed.add(molfunc);

		container = new TermContainer(termsConstructed, b("noformat"), b("nodate"));
	}

	@Test
	public void testBasicStructure()
	{
		assertEquals(4, container.termCount());
		assertEquals("noformat", container.getFormatVersion().toString());
		assertEquals("nodate", container.getDate().toString());

		assertEquals("root", container.getName("GO:0000000"));
		assertEquals("root", container.getName(root.getID()));
		assertEquals("biological process", container.getName("GO:0008150"));
		assertEquals("biological process", container.getName(bioproc.getID()));
		assertEquals("cellular component", container.getName("GO:0005575"));
		assertEquals("cellular component", container.getName(cellcomp.getID()));
		assertEquals("molecular function", container.getName("GO:0003674"));
		assertEquals("molecular function", container.getName(molfunc.getID()));

		assertTrue(container.get("GO:0000000").equals(root));
		assertTrue(container.get(root.getID()).equals(root));
		assertTrue(container.get("GO:0008150").equals(bioproc));
		assertTrue(container.get(bioproc.getID()).equals(bioproc));
		assertTrue(container.get("GO:0005575").equals(cellcomp));
		assertTrue(container.get(cellcomp.getID()).equals(cellcomp));
		assertTrue(container.get("GO:0003674").equals(molfunc));
		assertTrue(container.get(molfunc.getID()).equals(molfunc));

		assertNull(container.get("GO:0000815"));
		Term anotherTerm = new Term("GO:0000815", "dummy", (ParentTermID)null);
		assertTrue(container.get(anotherTerm.getID()) == null);
	}
}
