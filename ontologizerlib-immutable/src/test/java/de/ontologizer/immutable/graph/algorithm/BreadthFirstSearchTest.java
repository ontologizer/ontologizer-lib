/**
 * 
 */
package de.ontologizer.immutable.graph.algorithm;

import com.google.common.collect.ImmutableList;
import de.ontologizer.immutable.graph.ImmutableDirectedGraph;
import de.ontologizer.immutable.graph.ImmutableEdge;
import de.ontologizer.immutable.graph.algorithms.BreadthFirstSearch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link BreadthFirstSearch} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class BreadthFirstSearchTest {

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
		new BreadthFirstSearch<Integer, ImmutableDirectedGraph<Integer>>()
				.startFrom(dag, 4, visitor);

		Assert.assertEquals("[4, 1, 2, 3, 0]",
				visitor.getVisitedVertices().toString());
	}

	@Test
	public void testStartFromCyclic() {
		new BreadthFirstSearch<Integer, ImmutableDirectedGraph<Integer>>()
				.startFrom(cyclic, 4, visitor);

		Assert.assertEquals("[4, 3, 2, 1, 0]",
				visitor.getVisitedVertices().toString());
	}

}
