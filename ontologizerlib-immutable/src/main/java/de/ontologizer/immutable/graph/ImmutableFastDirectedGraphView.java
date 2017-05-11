package de.ontologizer.immutable.graph;

import com.google.common.collect.ImmutableList;
import de.ontologizer.immutable.graph.algorithms.BreadthFirstSearch;
import de.ontologizer.immutable.graph.algorithms.VertexVisitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import sonumina.collections.IntMapper;

/**
 * Implementation of {@link FastDirectedGraphView} for {@link DirectedGraph}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author Sebastian Bauer
 * @author Sebastian Koehler
 */
public final class ImmutableFastDirectedGraphView<VertexType,
		EdgeType extends Edge<VertexType> & ShallowCopyable<EdgeType>>
		implements FastDirectedGraphView<VertexType, EdgeType> {

	private static final long serialVersionUID = 1L;

	/**
	 * Underlying {@link DirectedGraph}.
	 */
	private final ImmutableDirectedGraph<VertexType, EdgeType> graph;

	/**
	 * {@link IntMapper} used for mapping from <code>Vertex</code> to
	 * <code>int</code>
	 */
	private final IntMapper<VertexType> mapper;

	/**
	 * Contains all the ancestors of the terms (and the terms itself). Note that
	 * the array of ancestors is sorted.
	 */
	private final ImmutableList<ImmutableList<Integer>> vertexAncestors;

	/** Contains the parents of the terms */
	private final ImmutableList<ImmutableList<Integer>> vertexParents;

	/** Contains the children of the term */
	private final ImmutableList<ImmutableList<Integer>> vertexChildren;

	/**
	 * Contains the descendants of the (i.e., children, grand-children, etc. and
	 * the term itself). Note that the array of descendants is sorted.
	 */
	private final ImmutableList<ImmutableList<Integer>> vertexDescendants;

	/**
	 * Construct new {@link ImmutableFastDirectedGraphView} from
	 * {@link DirectedGraph}.
	 *
	 * @return {@link ImmutableFastDirectedGraphView} after construction
	 */
	public static <VertexType, EdgeType extends Edge<VertexType> & ShallowCopyable<EdgeType>>
			ImmutableFastDirectedGraphView<VertexType, EdgeType>
			construct(ImmutableDirectedGraph<VertexType, EdgeType> g) {
		final IntMapper<VertexType> mapper = IntMapper.create(g.getVertices(), g.countVertices());

		// Collect parents for each vertex
		final ArrayList<List<Integer>> vertexParents = new ArrayList<List<Integer>>();
		for (int i = 0; i < mapper.getSize(); i++) {
			vertexParents.add(createIndicesFromIter(mapper, g.parentVertexIterator(mapper.get(i))));
		}

		// Collect ancestors for each vertex
		final ArrayList<List<Integer>> vertexAncestors = new ArrayList<List<Integer>>();
		for (int i = 0; i < mapper.getSize(); i++) {
			VertexType v = mapper.get(i);
			final List<VertexType> ancestors = new ArrayList<VertexType>();
			final BreadthFirstSearch<VertexType, EdgeType, DirectedGraph<VertexType, EdgeType>> bfs =
					new BreadthFirstSearch<VertexType, EdgeType, DirectedGraph<VertexType, EdgeType>>();
			bfs.startFrom(g, v, new VertexVisitor<VertexType, EdgeType>() {
				@Override
				public boolean visit(DirectedGraph<VertexType, EdgeType> g, VertexType v) {
					ancestors.add(v);
					return true;
				}
			});
			vertexAncestors.add(createIndicesFromIter(mapper, ancestors.iterator()));

			if (!vertexAncestors.isEmpty()) {
				Collections.sort(vertexAncestors.get(vertexAncestors.size() - 1));
			}
		}

		// Collect children for each vertex
		final ArrayList<List<Integer>> vertexChildren = new ArrayList<List<Integer>>();
		for (int i = 0; i < mapper.getSize(); i++) {
			vertexChildren.add(createIndicesFromIter(mapper, g.childVertexIterator(mapper.get(i))));
		}

		/* Term descendants stuff */
		final ArrayList<List<Integer>> vertexDescendants = new ArrayList<List<Integer>>();
		for (int i = 0; i < mapper.getSize(); i++) {
			VertexType v = mapper.get(i);
			final List<VertexType> descendants = new ArrayList<VertexType>();
			final BreadthFirstSearch<VertexType, EdgeType, DirectedGraph<VertexType, EdgeType>> bfs =
					new BreadthFirstSearch<VertexType, EdgeType, DirectedGraph<VertexType, EdgeType>>();
			bfs.startFromReverse(g, v, new VertexVisitor<VertexType, EdgeType>() {
				@Override
				public boolean visit(DirectedGraph<VertexType, EdgeType> g, VertexType v) {
					descendants.add(v);
					return true;
				}
			});
			vertexDescendants.add(createIndicesFromIter(mapper, descendants.iterator()));

			if (!vertexDescendants.isEmpty()) {
				Collections.sort(vertexDescendants.get(vertexDescendants.size() - 1));
			}
		}

		return new ImmutableFastDirectedGraphView<VertexType, EdgeType>(g, mapper, vertexParents, vertexAncestors,
				vertexChildren, vertexDescendants);
	}

	private static <Vertex> List<Integer> createIndicesFromIter(IntMapper<Vertex> mapper, Iterator<Vertex> iterator) {
		final List<Integer> result = new ArrayList<Integer>();

		while (iterator.hasNext()) {
			final Vertex p = iterator.next();
			final int idx = mapper.getIndex(p);
			if (idx != -1) {
				result.add(idx);
			}
		}

		return result;
	}

	private ImmutableFastDirectedGraphView(ImmutableDirectedGraph<VertexType, EdgeType> graph,
			IntMapper<VertexType> mapper, Collection<? extends Collection<Integer>> vertexParents,
			Collection<? extends Collection<Integer>> vertexAncestors,
			Collection<? extends Collection<Integer>> vertexChildren,
			Collection<? extends Collection<Integer>> vertexDescendants) {
		this.graph = graph;
		this.mapper = mapper;

		ImmutableList.Builder<ImmutableList<Integer>> vertexAncestorBuilder = ImmutableList.builder();
		for (Collection<Integer> c : vertexAncestors) {
			vertexAncestorBuilder.add(ImmutableList.copyOf(c));
		}
		this.vertexAncestors = vertexAncestorBuilder.build();

		ImmutableList.Builder<ImmutableList<Integer>> vertexParentBuilder = ImmutableList.builder();
		for (Collection<Integer> c : vertexParents) {
			vertexParentBuilder.add(ImmutableList.copyOf(c));
		}
		this.vertexParents = vertexParentBuilder.build();

		ImmutableList.Builder<ImmutableList<Integer>> vertexChildBuilder = ImmutableList.builder();
		for (Collection<Integer> c : vertexChildren) {
			vertexChildBuilder.add(ImmutableList.copyOf(c));
		}
		this.vertexChildren = vertexChildBuilder.build();

		ImmutableList.Builder<ImmutableList<Integer>> vertexDescendantBuilder = ImmutableList.builder();
		for (Collection<Integer> c : vertexDescendants) {
			vertexDescendantBuilder.add(ImmutableList.copyOf(c));
		}
		this.vertexDescendants = vertexDescendantBuilder.build();
	}

	@Override
	public DirectedGraph<VertexType, EdgeType> getGraph() {
		return graph;
	}

	@Override
	public int countVertices() {
		return graph.countVertices();
	}

	@Override
	public int countEdges() {
		return graph.countEdges();
	}

	@Override
	public VertexType getVertex(int index) {
		return mapper.get(index);
	}

	@Override
	public int getVertexIndex(VertexType v) {
		return mapper.getIndex(v);
	}

	@Override
	public List<Integer> getVertexIndices(Collection<VertexType> vertices) {
		ImmutableList.Builder<Integer> builder = ImmutableList.builder();
		for (VertexType v : vertices) {
			builder.add(mapper.getIndex(v));
		}
		return builder.build();
	}

	@Override
	public boolean isAncestor(VertexType u, VertexType v) {
		if (!containsVertex(u) || !containsVertex(v)) {
			return false;
		}
		ImmutableList<Integer> descs = vertexAncestors.get(getVertexIndex(v));
		return descs.contains(getVertexIndex(u));
	}

	@Override
	public boolean isDescendant(VertexType u, VertexType v) {
		if (!containsVertex(u) || !containsVertex(v)) {
			return false;
		}
		ImmutableList<Integer> descs = vertexDescendants.get(getVertexIndex(v));
		return descs.contains(getVertexIndex(u));
	}

	@Override
	public List<VertexType> getDescendants(VertexType v) {
		ImmutableList.Builder<VertexType> builder = ImmutableList.builder();
		for (int childIndex : vertexDescendants.get(getVertexIndex(v))) {
			builder.add(getVertex(childIndex));
		}
		return builder.build();
	}

	@Override
	public List<VertexType> getAncestors(VertexType v) {
		ImmutableList.Builder<VertexType> builder = ImmutableList.builder();
		for (int parentIndex : vertexAncestors.get(getVertexIndex(v))) {
			builder.add(getVertex(parentIndex));
		}
		return builder.build();
	}

	@Override
	public boolean containsVertex(VertexType v) {
		return graph.containsVertex(v);
	}

	@Override
	public List<VertexType> getParents(VertexType v) {
		getVertexIndex(v);
		ImmutableList.Builder<VertexType> builder = ImmutableList.builder();
		for (int childIndex : vertexParents.get(getVertexIndex(v))) {
			builder.add(getVertex(childIndex));
		}
		return builder.build();
	}

	@Override
	public List<VertexType> getChildren(VertexType v) {
		ImmutableList.Builder<VertexType> builder = ImmutableList.builder();
		for (int childIndex : vertexChildren.get(getVertexIndex(v))) {
			builder.add(getVertex(childIndex));
		}
		return builder.build();
	}

}
