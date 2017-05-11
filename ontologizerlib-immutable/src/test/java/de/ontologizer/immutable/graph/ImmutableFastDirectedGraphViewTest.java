package de.ontologizer.immutable.graph;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for class {@link ImmutableFastDirectedGraphView}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableFastDirectedGraphViewTest {

	/** Sample edge list to use for testing. */
	static ImmutableList<ImmutableEdge<Integer>> edges;

	/** Graph to use for testing. */
	static ImmutableDirectedGraph<Integer, ImmutableEdge<Integer>> graph;

	/** Graph view under test */
	static ImmutableFastDirectedGraphView<Integer, ImmutableEdge<Integer>> gView;

	@BeforeClass
	public static void setUp() throws Exception {
		edges = ImmutableList.of(ImmutableEdge.construct(0, 1), ImmutableEdge.construct(0, 2),
				ImmutableEdge.construct(0, 3), ImmutableEdge.construct(1, 4), ImmutableEdge.construct(2, 5),
				ImmutableEdge.construct(3, 6), ImmutableEdge.construct(4, 7), ImmutableEdge.construct(5, 7),
				ImmutableEdge.construct(6, 7));

		graph = ImmutableDirectedGraph.construct(edges);

		gView = ImmutableFastDirectedGraphView.construct(graph);
	}

	@Test
	public void testGetGraph() {
		Assert.assertSame(graph, gView.getGraph());
	}

	@Test
	public void testCountVertices() {
		Assert.assertEquals(8, gView.countVertices());
	}

	@Test
	public void testCountEdges() {
		Assert.assertEquals(9, gView.countEdges());
	}

	@Test
	public void testGetVertex() {
		Assert.assertEquals("1", gView.getVertex(1).toString());
	}

	@Test
	public void testGetVertexIndex() {
		Assert.assertEquals(1, gView.getVertexIndex(1));
	}

	@Test
	public void testGetVertexIndices() {
		Assert.assertEquals("[0, 3]", gView.getVertexIndices(ImmutableList.of(0, 3)).toString());
	}

	@Test
	public void testIsAncestor() {
		Assert.assertTrue(gView.isAncestor(1, 0));
		Assert.assertFalse(gView.isAncestor(0, 1));
	}

	@Test
	public void testIsDescendant() {
		Assert.assertTrue(gView.isDescendant(0, 1));
		Assert.assertFalse(gView.isDescendant(1, 0));
	}

	@Test
	public void testGetDescendans() {
		Assert.assertEquals("[0]", gView.getDescendants(0).toString());
		Assert.assertEquals("[0, 1]", gView.getDescendants(1).toString());
	}

	@Test
	public void testGetAncestors() {
		Assert.assertEquals("[0, 1, 2, 3, 4, 5, 6, 7]", gView.getAncestors(0).toString());
		Assert.assertEquals("[1, 4, 7]", gView.getAncestors(1).toString());
	}

	@Test
	public void testContainsVertex() {
		Assert.assertTrue(gView.containsVertex(0));
		Assert.assertFalse(gView.containsVertex(8));
	}

	@Test
	public void testGetParents() {
		Assert.assertEquals("[1, 2, 3]", gView.getParents(0).toString());
		Assert.assertEquals("[4]", gView.getParents(1).toString());
	}

	@Test
	public void testGetChildren() {
		Assert.assertEquals("[]", gView.getChildren(0).toString());
		Assert.assertEquals("[0]", gView.getChildren(1).toString());
	}

}
