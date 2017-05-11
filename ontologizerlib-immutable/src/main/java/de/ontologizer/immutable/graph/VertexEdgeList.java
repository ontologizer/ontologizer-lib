package de.ontologizer.immutable.graph;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation detail for storing in- and out-edge lists.
 * 
 * <p>
 * This class is not part of the public API of ontologizerlib and can change
 * without notices.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class VertexEdgeList<VertexType, EdgeType extends Edge<VertexType>> {

	private final ImmutableList<EdgeType> inEdges;
	private final ImmutableList<EdgeType> outEdges;

	public static <VertexType, EdgeType extends Edge<VertexType>> Builder<VertexType, EdgeType> builder() {
		return new Builder<VertexType, EdgeType>();
	}

	public VertexEdgeList(Collection<EdgeType> inEdges, Collection<EdgeType> outEdges) {
		this.inEdges = ImmutableList.copyOf(inEdges);
		this.outEdges = ImmutableList.copyOf(outEdges);
	}

	public ImmutableList<EdgeType> getInEdges() {
		return inEdges;
	}

	public ImmutableList<EdgeType> getOutEdges() {
		return outEdges;
	}

	/**
	 * Internal helper for construction of VertexEdgeList objects.
	 *
	 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel
	 *         Holtgrewe</a>
	 */
	public static final class Builder<VertexType, EdgeType extends Edge<VertexType>> {
		private final List<EdgeType> inEdges = new ArrayList<EdgeType>();
		private final List<EdgeType> outEdges = new ArrayList<EdgeType>();

		public void addInEdge(EdgeType edge) {
			inEdges.add(edge);
		}

		public void addOutEdge(EdgeType edge) {
			outEdges.add(edge);
		}

		public VertexEdgeList<VertexType, EdgeType> build() {
			return new VertexEdgeList<VertexType, EdgeType>(inEdges, outEdges);
		}
	}

	@Override
	public String toString() {
		return "ImmutableVertexEdgeList [inEdges=" + inEdges + ", outEdges=" + outEdges + "]";
	}

}
