package ontologizer.ontology;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ontologizer.types.ByteString;
import sonumina.collections.ReferencePool;

/**
 * This is a pool about
 *
 * @author Sebastian Bauer
 */
public class RelationTypePool implements Serializable
{
	private static final long serialVersionUID = 1L;

	private ReferencePool<RelationType> pool;
	private Map<ByteString,RelationType> name2Type;

	public RelationTypePool()
	{
		pool = new ReferencePool<RelationType>();
		name2Type = new HashMap<ByteString,RelationType>();
	}

	/**
	 * Map the given relation type to a unique one. If the relation type
	 * is not known, then the given one will become the representative.
	 *
	 * @param ref
	 * @return the unique relation type
	 */
	public RelationType map(RelationType ref)
	{
		RelationType type = pool.map(ref);
		if (type == ref)
		{
			name2Type.put(ref.name(), ref);
		}
		return type;
	}

	/**
	 * Find the relation type by the given name.
	 *
	 * @param name the name of the relation type (e.g., is_a)
	 * @return the relation type or null if none exists.
	 */
	public RelationType map(ByteString name)
	{
		return name2Type.get(name);
	}

	/**
	 * Find the relation type by name contained in the given byte array.
	 *
	 * @param buf the buffer that contains the name
	 * @param start the offset the first byte of the name
	 * @param end the end of the name (exclusive).
	 * @return the relation type or null if none exists.
	 */
	public RelationType map(byte [] buf, int start, int end)
	{
		return map(new ByteString(buf, start, end));
	}
}
