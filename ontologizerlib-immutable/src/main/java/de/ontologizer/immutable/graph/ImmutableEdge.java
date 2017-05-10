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
	 * Static constructor method so <code>Vertex</code> does not have to be
	 * explicit.
	 * 
	 * @param source
	 *            <code>Vertex</code> to use for the edge foot vertex
	 * @param dest
	 *            <code>Vertex</code> to use for the edge tip vertex
	 */
	public static <Vertex> ImmutableEdge<Vertex> construct(Vertex source,
			Vertex dest) {
		return new ImmutableEdge<Vertex>(source, dest);
	}

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

	@Override
	public String toString() {
		return "ImmutableEdge [source=" + source + ", dest=" + dest + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dest == null) ? 0 : dest.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		ImmutableEdge<Vertex> other = (ImmutableEdge<Vertex>) obj;
		if (dest == null) {
			if (other.dest != null)
				return false;
		} else if (!dest.equals(other.dest)) {
			return false;
		}
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source)) {
			return false;
		}
		return true;
	}

}
