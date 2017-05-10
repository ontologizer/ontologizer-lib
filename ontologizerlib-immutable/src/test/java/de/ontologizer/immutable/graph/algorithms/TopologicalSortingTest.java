/**
 * 
 */
package de.ontologizer.immutable.graph.algorithms;

import com.google.common.collect.ImmutableList;
import de.ontologizer.immutable.graph.ImmutableDirectedGraph;
import de.ontologizer.immutable.graph.ImmutableEdge;
import de.ontologizer.immutable.graph.algorithms.GraphNotDagException;
import de.ontologizer.immutable.graph.algorithms.TopologicalSorting;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link TopologicalSorting} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TopologicalSortingTest {

	/** Acyclic directed graph for testing. */
	ImmutableDirectedGraph<Integer> dag;

	/** Cyclic graph for testing. */
	ImmutableDirectedGraph<Integer> cyclic;

	/** Helper for visiting vertices. */
	GraphVisitor visitor;

	@Before
	public void setUp() throws Exception {
		dag = ImmutableDirectedGraph
				.construct(ImmutableList.of(new ImmutableEdge<Integer>(0, 1),
						new ImmutableEdge<Integer>(0, 2),
						new ImmutableEdge<Integer>(0, 3),
						new ImmutableEdge<Integer>(1, 4),
						new ImmutableEdge<Integer>(2, 4),
						new ImmutableEdge<Integer>(3, 4)));

		cyclic = ImmutableDirectedGraph
				.construct(ImmutableList.of(new ImmutableEdge<Integer>(0, 1),
						new ImmutableEdge<Integer>(1, 2),
						new ImmutableEdge<Integer>(2, 3),
						new ImmutableEdge<Integer>(3, 4),
						new ImmutableEdge<Integer>(4, 0)));

		visitor = new GraphVisitor();;
	}

	@Test
	public void testStartFromDag() {
		new TopologicalSorting<Integer, ImmutableDirectedGraph<Integer>>()
				.start(dag, visitor);

		Assert.assertEquals("[0, 1, 2, 3, 4]",
				visitor.getVisitedVertices().toString());
	}

	@Test(expected = GraphNotDagException.class)
	public void testStartFromCyclic() {
		new TopologicalSorting<Integer, ImmutableDirectedGraph<Integer>>()
				.start(cyclic, visitor);
	}

}
