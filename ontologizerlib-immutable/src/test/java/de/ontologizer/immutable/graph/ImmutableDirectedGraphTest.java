package de.ontologizer.immutable.graph;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for class {@link ImmutableDirectedGraph}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableDirectedGraphTest {

	/** Sample graph to use for testing. */
	static ImmutableDirectedGraph<Integer> g;

	/** Sample vertex list to use for testing. */
	static ImmutableList<Integer> vertices;

	/** Sample edge list to use for testing. */
	static ImmutableList<ImmutableEdge<Integer>> edges;

	@BeforeClass
	public static void setUp() throws Exception {
		vertices = ImmutableList.of(0, 1, 2, 3, 4, 5, 6, 7);

		edges = ImmutableList.of(ImmutableEdge.construct(0, 1),
				ImmutableEdge.construct(0, 2), ImmutableEdge.construct(0, 3),
				ImmutableEdge.construct(1, 4), ImmutableEdge.construct(2, 5),
				ImmutableEdge.construct(3, 6), ImmutableEdge.construct(4, 7),
				ImmutableEdge.construct(5, 7), ImmutableEdge.construct(6, 7));

		g = ImmutableDirectedGraph.construct(edges);
	}

	@Test
	public void testConstructFromVerticesAndEdges() {
		ImmutableDirectedGraph<Integer> graph = ImmutableDirectedGraph
				.construct(vertices, edges);
		Assert.assertEquals(
				"ImmutableDirectedGraph [edgeLists={0=ImmutableVertexEdgeList "
						+ "[inEdges=[], outEdges=[ImmutableEdge [source=0, dest=1], "
						+ "ImmutableEdge [source=0, dest=2], ImmutableEdge [source=0, "
						+ "dest=3]]], 1=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
						+ "[source=0, dest=1]], outEdges=[ImmutableEdge [source=1, dest=4]]], "
						+ "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=0, dest=2]], "
						+ "outEdges=[ImmutableEdge [source=2, dest=5]]], 3=ImmutableVertexEdgeList "
						+ "[inEdges=[ImmutableEdge [source=0, dest=3]], outEdges=[ImmutableEdge "
						+ "[source=3, dest=6]]], 4=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
						+ "[source=1, dest=4]], outEdges=[ImmutableEdge [source=4, dest=7]]], "
						+ "5=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=2, dest=5]], "
						+ "outEdges=[ImmutableEdge [source=5, dest=7]]], 6=ImmutableVertexEdgeList "
						+ "[inEdges=[ImmutableEdge [source=3, dest=6]], outEdges=[ImmutableEdge "
						+ "[source=6, dest=7]]], 7=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
						+ "[source=4, dest=7], ImmutableEdge [source=5, dest=7], ImmutableEdge "
						+ "[source=6, dest=7]], outEdges=[]]}]",
				graph.toString());
	}

	@Test(expected = VerticesAndEdgesIncompatibleException.class)
	public void testConstructFromVerticesAndEdgesCheckFails() {
		ImmutableList.Builder<ImmutableEdge<Integer>> builder = ImmutableList
				.builder();
		builder.addAll(edges);
		builder.add(ImmutableEdge.construct(7, 8));

		ImmutableDirectedGraph.construct(vertices, builder.build(), true);
	}

	@Test
	public void testConstructFromEdges() {
		ImmutableDirectedGraph<Integer> graph = ImmutableDirectedGraph
				.construct(edges);
		Assert.assertEquals(
				"ImmutableDirectedGraph [edgeLists={0=ImmutableVertexEdgeList "
						+ "[inEdges=[], outEdges=[ImmutableEdge [source=0, dest=1], "
						+ "ImmutableEdge [source=0, dest=2], ImmutableEdge [source=0, "
						+ "dest=3]]], 1=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
						+ "[source=0, dest=1]], outEdges=[ImmutableEdge [source=1, dest=4]]], "
						+ "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=0, dest=2]], "
						+ "outEdges=[ImmutableEdge [source=2, dest=5]]], 3=ImmutableVertexEdgeList "
						+ "[inEdges=[ImmutableEdge [source=0, dest=3]], outEdges=[ImmutableEdge "
						+ "[source=3, dest=6]]], 4=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
						+ "[source=1, dest=4]], outEdges=[ImmutableEdge [source=4, dest=7]]], "
						+ "5=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=2, dest=5]], "
						+ "outEdges=[ImmutableEdge [source=5, dest=7]]], 6=ImmutableVertexEdgeList "
						+ "[inEdges=[ImmutableEdge [source=3, dest=6]], outEdges=[ImmutableEdge "
						+ "[source=6, dest=7]]], 7=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
						+ "[source=4, dest=7], ImmutableEdge [source=5, dest=7], ImmutableEdge "
						+ "[source=6, dest=7]], outEdges=[]]}]",
				graph.toString());
	}

	@Test
	public void testShallowCopy() {
		DirectedGraph<Integer> copy = g.shallowCopy();

		Assert.assertNotSame(g, copy);
		// TODO: make internal maps sorted so we can compare string values
	}

	@Test
	public void testContainsVertex() {
		Assert.assertTrue(g.containsVertex(0));
		Assert.assertFalse(g.containsVertex(8));
	}

	@Test
	public void testCountVertices() {
		Assert.assertEquals(g.countVertices(), 8);
	}

	@Test
	public void testVertexIterator() {
		final List<Integer> vList = new ArrayList<Integer>();

		final Iterator<Integer> vIt = g.vertexIterator();
		while (vIt.hasNext()) {
			vList.add(vIt.next());
		}

		Assert.assertEquals("[0, 1, 2, 3, 4, 5, 6, 7]", vList.toString());
	}

	@Test
	public void testGetVertices() {
		Collection<Integer> vs = g.getVertices();
		Assert.assertEquals("[0, 1, 2, 3, 4, 5, 6, 7]", vs.toString());
	}

	@Test
	public void testContainsEdge() {
		Assert.assertTrue(g.containsEdge(0, 1));
		Assert.assertFalse(g.containsEdge(1, 0));
	}

	@Test
	public void testGetEdge() {
		Assert.assertEquals("ImmutableEdge [source=0, dest=1]",
				g.getEdge(0, 1).toString());
		Assert.assertNull(g.getEdge(1, 0));
	}

	@Test
	public void testCountEdges() {
		Assert.assertEquals(9, g.countEdges());
	}

	@Test
	public void testEdgeIterator() {
		final List<Edge<Integer>> eList = new ArrayList<Edge<Integer>>();

		final Iterator<? extends Edge<Integer>> eIt = g.edgeIterator();
		while (eIt.hasNext()) {
			eList.add(eIt.next());
		}

		Assert.assertEquals(
				"[ImmutableEdge [source=0, dest=1], ImmutableEdge [source=0, dest=2], "
						+ "ImmutableEdge [source=0, dest=3], ImmutableEdge [source=1, dest=4], "
						+ "ImmutableEdge [source=2, dest=5], ImmutableEdge [source=3, dest=6], "
						+ "ImmutableEdge [source=4, dest=7], ImmutableEdge [source=5, dest=7], "
						+ "ImmutableEdge [source=6, dest=7]]",
				eList.toString());
	}

	@Test
	public void testGetInDegree() {
		Assert.assertEquals(g.getInDegree(0), 0);
		Assert.assertEquals(g.getInDegree(2), 1);
		Assert.assertEquals(g.getInDegree(7), 3);
	}

	@Test
	public void testInEdgeIterator() {
		final List<Edge<Integer>> eList = new ArrayList<Edge<Integer>>();

		Iterator<? extends Edge<Integer>> eIt = g.inEdgeIterator(7);
		while (eIt.hasNext()) {
			eList.add(eIt.next());
		}

		Assert.assertEquals(
				"[ImmutableEdge [source=4, dest=7], ImmutableEdge [source=5, dest=7], "
						+ "ImmutableEdge [source=6, dest=7]]",
				eList.toString());
	}

	@Test
	public void testGetOutDegree() {
		Assert.assertEquals(g.getOutDegree(0), 3);
		Assert.assertEquals(g.getOutDegree(2), 1);
		Assert.assertEquals(g.getOutDegree(7), 0);
	}

	@Test
	public void testOutEdgeIterator() {
		final List<Edge<Integer>> eList = new ArrayList<Edge<Integer>>();

		Iterator<? extends Edge<Integer>> eIt = g.outEdgeIterator(0);
		while (eIt.hasNext()) {
			eList.add(eIt.next());
		}

		Assert.assertEquals(
				"[ImmutableEdge [source=0, dest=1], ImmutableEdge [source=0, dest=2], "
						+ "ImmutableEdge [source=0, dest=3]]",
				eList.toString());
	}

	@Test
	public void testParentVertexIterator() {
		final List<Integer> vList = new ArrayList<Integer>();

		Iterator<Integer> pvIt = g.parentVertexIterator(0);
		while (pvIt.hasNext()) {
			vList.add(pvIt.next());
		}

		Assert.assertEquals("[1, 2, 3]", vList.toString());
	}

	@Test
	public void testChildVertexIterator() {
		final List<Integer> vList = new ArrayList<Integer>();

		Iterator<Integer> pvIt = g.childVertexIterator(7);
		while (pvIt.hasNext()) {
			vList.add(pvIt.next());
		}

		Assert.assertEquals("[4, 5, 6]", vList.toString());
	}

	@Test
	public void testSubGraph() {
		DirectedGraph<Integer> subG = g.subGraph(ImmutableList.of(0, 1, 2, 3));
		Assert.assertEquals(
				"ImmutableDirectedGraph [edgeLists={0=ImmutableVertexEdgeList [inEdges=[], "
						+ "outEdges=[ImmutableEdge [source=0, dest=1], ImmutableEdge [source=0, dest=3], "
						+ "ImmutableEdge [source=0, dest=2]]], 1=ImmutableVertexEdgeList "
						+ "[inEdges=[ImmutableEdge [source=0, dest=1]], outEdges=[]], "
						+ "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=0, dest=2]], outEdges=[]], "
						+ "3=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=0, dest=3]], outEdges=[]]}]",
				subG.toString());
	}

}
