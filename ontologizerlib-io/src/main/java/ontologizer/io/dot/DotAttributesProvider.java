package ontologizer.io.dot;

/**
 * The provider class for node and edge attributes that are used in the
 * dot file.
 *
 * @author Sebastian Bauer
 *
 * @param <V>
 */
public class DotAttributesProvider<V>
{
	public String getDotNodeName(V vt)
	{
		return null;
	}

	public String getDotNodeAttributes(V vt)
	{
		return null;
	}

	public String getDotEdgeAttributes(V src, V dest)
	{
		return null;
	}

	public String getDotGraphAttributes()
	{
		return "nodesep=0.4; ranksep=0.4;";
	}

	public String getDotHeader()
	{
		return null;
	}
}

