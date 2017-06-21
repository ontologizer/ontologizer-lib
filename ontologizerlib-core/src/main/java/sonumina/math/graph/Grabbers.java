package sonumina.math.graph;

import java.util.Iterator;

/**
 * This class contains methods to construct grabbers
 * that are most useful.
 *
 * @author Sebastian Bauer
 */
public class Grabbers
{
	public static <V> INeighbourGrabber<V> outGrabber(final IDirectedGraph<V> g)
	{
		return new INeighbourGrabber<V>()
		{
			@Override
			public Iterator<V> grabNeighbours(V v)
			{
				return g.getChildNodes(v).iterator();
			}
		};
	}

	public static <V> INeighbourGrabber<V> inGrabber(final IDirectedGraph<V> g)
	{
		return new INeighbourGrabber<V>()
		{
			@Override
			public Iterator<V> grabNeighbours(V v)
			{
				return g.getParentNodes(v).iterator();
			}
		};
	}
}
