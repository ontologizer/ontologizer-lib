package de.ontologizer.immutable.graph;

/**
 * Interface for constructing edges of the correct type.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface EdgeFactory<VertexType, EdgeType extends Edge<VertexType>> {

	/**
	 * Construct from given source and target vertex, other edge attributes are
	 * set to the default value.
	 * 
	 * @param u
	 *            source vertex
	 * @param v
	 *            target vertex
	 * @return constructed {@link Edge}
	 */
	public EdgeType construct(VertexType u, VertexType v);

}
