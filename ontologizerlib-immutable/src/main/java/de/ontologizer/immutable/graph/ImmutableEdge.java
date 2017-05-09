package de.ontologizer.immutable.graph;

/**
 * Implementation of an immutable edge with default weight of 1.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ImmutableEdge<Vertex> implements Edge<Vertex> {

	private static final long serialVersionUID = 1L;

	private final Vertex source;
	private final Vertex dest;

	/**
	 * Construct <code>ImmutableEdge</code> with the given source and
	 * destination vertices.
	 *
	 * @param source
	 *            <code>Vertex</code> to use for the edge foot vertex
	 * @param dest
	 *            <code>Vertex</code> to use for the edge tip vertex
	 */
	public ImmutableEdge(Vertex source, Vertex dest) {
		this.source = source;
		this.dest = dest;
	}

	@Override
	public Vertex getSource() {
		return source;
	}

	@Override
	public Vertex getDest() {
		return dest;
	}

	@Override
	public int getWeight() {
		return 1;
	}

}
