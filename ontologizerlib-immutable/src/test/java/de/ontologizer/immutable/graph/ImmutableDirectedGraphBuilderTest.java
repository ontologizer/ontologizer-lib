package de.ontologizer.immutable.graph;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for class {@link ImmutableDirectedGraph.Builder}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableDirectedGraphBuilderTest {

	ImmutableDirectedGraph.Builder<Integer> builder;

	@Before
	public void setUp() throws Exception {
		builder = ImmutableDirectedGraph.builder();
	}

	@Test
	public void testBuild() {
		builder.addEdge(new ImmutableEdge<Integer>(0, 1));
		builder.addEdge(1, 2);
		builder.addVertex(0);
		builder.addVertices(ImmutableList.of(1, 2));

		ImmutableDirectedGraph<Integer> g = builder.build(true);
		Assert.assertEquals(
				"ImmutableDirectedGraph [edgeLists={0=ImmutableVertexEdgeList "
						+ "[inEdges=[], outEdges=[ImmutableEdge [source=0, dest=1]]], "
						+ "1=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=0, "
						+ "dest=1]], outEdges=[ImmutableEdge [source=1, dest=2]]], "
						+ "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=1, "
						+ "dest=2]], outEdges=[]]}]",
				g.toString());
	}

}
