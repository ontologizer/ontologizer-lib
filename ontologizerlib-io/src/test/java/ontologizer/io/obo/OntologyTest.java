package ontologizer.io.obo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ontologizer.io.ParserFileInput;
import ontologizer.ontology.Namespace;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Subset;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermContainer;
import ontologizer.ontology.TermID;
import ontologizer.types.ByteString;
import ontologizer.ontology.Ontology.TermLevels;
import ontologizer.ontology.Ontology.ITermIDVisitor;

public class OntologyTest
{
	private TermContainer goTerms;

	private Ontology graph;

	@Before
	public void setUp() throws Exception
	{
		String GOtermsOBOFile = OntologyTest.class.
				getClassLoader().getResource("gene_ontology.1_2.obo.gz").getPath();

		/* Parse file and create term container */
		System.out.println("Parse OBO file");
		OBOParser oboParser = new OBOParser(new ParserFileInput(GOtermsOBOFile));
		System.out.println(oboParser.doParse());
		goTerms = new TermContainer(oboParser.getTermMap(), oboParser.getFormatVersion(), oboParser.getDate());

		/* Build graph */
		System.out.println("Build graph");
		graph = Ontology.create(goTerms);
	}

	@Test
	public void testRoot()
	{
		Set<String> terms;

		terms = graph.getTermChildrenAsStrings("GO:0000000");
		assertTrue("Test we get some Set<String> object back", terms != null);
		assertEquals("Root has three descendants",3, terms.size());

		terms = graph.getTermParentsAsStrings("GO:0000000");
		assertTrue("Test we gat some Set<String> object back", terms != null);
		assertTrue("Root has no ancestors", terms.size() == 0);
	}

	@Test
	public void testExistsPath()
	{
		assertTrue(graph.existsPath(new TermID("GO:0009987"),
				new TermID("GO:0006281")));
		assertFalse(graph.existsPath(new TermID("GO:0006281"),
				new TermID("GO:0009987")));

		assertTrue(graph.existsPath(new TermID("GO:0008150"),
				new TermID("GO:0006281")));
		assertFalse(graph.existsPath(new TermID("GO:0006281"),
				new TermID("GO:0008150")));

		assertFalse(graph.existsPath(new TermID("GO:0006139"),
				new TermID("GO:0009719")));
		assertFalse(graph.existsPath(new TermID("GO:0009719"),
				new TermID("GO:0006139")));
	}

	@Test
	public void testWalkToRoot()
	{
		/**
		 * A basic visitor: It simply counts up the number of visited terms.
		 *
		 * @author Sebastian Bauer
		 */
		class TermIDVisitor implements ITermIDVisitor
		{
			public int count = 0;

			public boolean visited(TermID term)
			{
				count++;
				System.out.println(term + " " + count);
				return true;
			}

			public void resetCount()
			{
				count = 0;
			};

			public int getCount()
			{
				return count;
			};
		}
		;

		TermIDVisitor visitor = new TermIDVisitor();

		/*
		 * Note, if GO changes these values are no longer correct. But you can
		 * verify them then via www.godatabase.org.
		 */
		graph.walkToSource(new TermID("GO:0008152"), visitor);
		assertEquals(3,visitor.getCount());
		visitor.resetCount();

		graph.walkToSource(new TermID("GO:0044237"), visitor);
		assertEquals(5,visitor.getCount());
		visitor.resetCount();

		graph.walkToSource(new TermID("GO:0006281"), visitor);
		assertEquals(19,visitor.getCount());
	}

	/**
	 * Return a termid collection from strings.
	 *
	 * @param ids
	 * @return
	 */
	private static Collection<TermID> newTermIDCollection(String...ids)
	{
		HashSet<TermID> hs = new HashSet<TermID>();
		for (String id : ids)
			hs.add(new TermID(id));
		return hs;
	}

	@Test
	public void testGetSharedParentsSimple()
	{
		Term t = graph.getTerm("GO:0008152");
		Collection<TermID> parents = graph.getSharedParents(t.getID(), t.getID());
		assertEquals(3,  parents.size());
		assertTrue(parents.containsAll(newTermIDCollection("GO:0008152", "GO:0008150", "GO:0000000")));
	}

	@Test
	public void testGetSharedParents()
	{
		Term t1 = graph.getTerm("GO:0008152");
		Term t2 = graph.getTerm("GO:0008150");
		Collection<TermID> parents = graph.getSharedParents(t1.getID(), t2.getID());
		assertEquals(2,  parents.size());
		assertTrue(parents.containsAll(newTermIDCollection("GO:0008150", "GO:0000000")));
	}

	@Test
	public void testGetSharedParentsTwoLeaves()
	{
		Term t1 = graph.getTerm("GO:0034641");
		Term t2 = graph.getTerm("GO:0019326");
		Collection<TermID> actual = graph.getSharedParents(t1.getID(), t2.getID());

		Collection<TermID> expected = graph.getTermsOfInducedGraph(null, t1.getID());
		expected.retainAll(graph.getTermsOfInducedGraph(null, t2.getID()));
		assertEquals(expected.size(),  actual.size());
		assertTrue(actual.containsAll(expected));
	}

	@Test
	public void testRelevantSubontology()
	{
		graph.setRelevantSubontology("biological_process");

		Ontology biologicalProcess = graph.getOntlogyOfRelevantTerms();
		assertEquals(21763, biologicalProcess.getNumberOfTerms());
		assertEquals("biological_process", biologicalProcess.getRootTerm().getName().toString());

		Namespace n = biologicalProcess.getRootTerm().getNamespace();
		ByteString nn = n.getName();
		assertEquals("biological_process", nn.toString());

		for (Term t : biologicalProcess)
			assertEquals(nn, t.getNamespace().getName());
	}

	private static Set<String> subsetNames(Collection<Subset> subsets)
	{
		HashSet<String> names = new HashSet<String>();
		for (Subset s : subsets)
			names.add(s.getName().toString());
		return names;
	}

	private static Set<String> subsetNames(Subset [] subsets)
	{
		HashSet<String> names = new HashSet<String>();
		for (Subset s : subsets)
			names.add(s.getName().toString());
		return names;
	}

	private static Set<String> termNames(Collection<TermID> terms)
	{
		Set<String> names = new HashSet<String>();
		for (TermID t : terms)
			names.add(t.toString());
		return names;
	}

	@Test
	public void testSubsetOntology()
	{
		Set<String> expectedSubsets = new HashSet<String>(
			Arrays.asList(
				"goslim_aspergillus",
				"goslim_candida",
				"goslim_generic",
				"goslim_pir",
				"goslim_plant",
				"goslim_pombe",
				"goslim_yeast",
				"gosubset_prok",
				"high_level_annotation_qc",
				"mf_needs_review"
				));
		Assert.assertEquals(expectedSubsets, subsetNames(graph.getAvailableSubsets()));

		graph.setRelevantSubset("goslim_generic");
		Ontology goslim = graph.getOntlogyOfRelevantTerms();

		int expectedGoSlimTerms = 0;
		for (Term t : graph)
		{
			if (subsetNames(t.getSubsets()).contains("goslim_generic"))
				expectedGoSlimTerms++;
		}
		assertEquals(expectedGoSlimTerms, goslim.getNumberOfTerms());
	}

	@Test
	public void testSlimSubsetInternalOntology()
	{
		Ontology o = new InternalOntology().graph;
		Set<String> expectedSubsets = new HashSet<String>(Arrays.asList("slim"));
		Assert.assertEquals(expectedSubsets, subsetNames(o.getAvailableSubsets()));

		o.setRelevantSubset("slim");
		Ontology slim = o.getOntlogyOfRelevantTerms();
		assertEquals(5, slim.getNumberOfTerms());

		Set<String> expectedTerms = new HashSet<String>(Arrays.asList("GO:0000002","GO:0000003"));
		assertEquals(expectedTerms, termNames(slim.getTermChildren(new TermID("GO:0000001"))));

		expectedTerms = new HashSet<String>(Arrays.asList("GO:0000007"));
		assertEquals(expectedTerms, termNames(slim.getTermChildren(new TermID("GO:0000002"))));

		expectedTerms = new HashSet<String>(Arrays.asList("GO:0000007"));
		assertEquals(expectedTerms, termNames(slim.getTermChildren(new TermID("GO:0000003"))));

		expectedTerms = new HashSet<String>(Arrays.asList("GO:0000010"));
		assertEquals(expectedTerms, termNames(slim.getTermChildren(new TermID("GO:0000007"))));
	}

	private static <T> Set<T> set(Iterable<T> iterable)
	{
		Set<T> set = new HashSet<T>();
		for (T i : iterable)
		{
			set.add(i);
		}
		return set;
	}

	@Test
	public void testGOLevelsEmpty()
	{
		TermLevels noLevels = graph.getTermLevels(new HashSet<TermID>());
		assertEquals(-1, noLevels.getMaxLevel());
		assertEquals(-1, noLevels.getTermLevel(graph.getLeafTermIDs().iterator().next()));

		/* Level 0 and 1 */
		TermLevels twoLevels = graph.getTermLevels(new HashSet<TermID>(graph.getTermChildren(graph.getRootTerm().getID())));
		assertEquals(1, twoLevels.getMaxLevel());

		TermLevels allLevels = graph.getTermLevels(set(graph.getGraph().getVertices()));
		assertEquals(20, allLevels.getMaxLevel());
	}

	@Test
	public void testIsArtificialRootTerm()
	{
		Ontology o = new InternalOntology().graph;
		assertFalse(o.isArtificialRootTerm(new TermID("GO:0000001")));
		assertFalse(o.isArtificialRootTerm(new TermID("GO:0000002")));
		assertFalse(o.isArtificialRootTerm(new TermID("GO:0000003")));
	}

	@Test
	public void testGOLevelInternalOntology()
	{
		Ontology o = new InternalOntology().graph;
		TermLevels levels = o.getTermLevels(set(o.getGraph().getVertices()));
		assertEquals(5, levels.getMaxLevel());

		assertEquals(0, levels.getTermLevel(new TermID("GO:0000001")));
		assertEquals(1, levels.getTermLevel(new TermID("GO:0000002")));
		assertEquals(1, levels.getTermLevel(new TermID("GO:0000003")));
		assertEquals(2, levels.getTermLevel(new TermID("GO:0000004")));
		assertEquals(2, levels.getTermLevel(new TermID("GO:0000005")));
		assertEquals(2, levels.getTermLevel(new TermID("GO:0000006")));
		assertEquals(3, levels.getTermLevel(new TermID("GO:0000007")));
		assertEquals(4, levels.getTermLevel(new TermID("GO:0000008")));
		assertEquals(4, levels.getTermLevel(new TermID("GO:0000009")));
		assertEquals(5, levels.getTermLevel(new TermID("GO:0000010")));
		assertEquals(5, levels.getTermLevel(new TermID("GO:0000011")));
	}
}
