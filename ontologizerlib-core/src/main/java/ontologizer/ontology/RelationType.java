package ontologizer.ontology;

import static ontologizer.types.ByteString.b;

import java.io.Serializable;

import ontologizer.types.ByteString;

/**
 * Instances of this class represent a single relation type (e.g., is_a).
 *
 * @author Sebastian Bauer
 */
public final class RelationType implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** A default relation type that is the most unspecific one */
	public static final RelationType UNKNOWN = new RelationType(b("unknown"), TermRelation.UNKOWN);

	private final ByteString name;
	private final TermRelation type;

	/**
	 * Constructs a new relation.
	 *
	 * @param name the name of the relation (e.g., is_a)
	 * @param type the predefined type of the relation.
	 */
	public RelationType(ByteString name, TermRelation type)
	{
		this.name = name;
		this.type = type;
	}

	public RelationType(TermRelation type)
	{
		this.name = type.relationName();
		this.type = type;

	}
	/**
	 * @return the name of the relation.
	 */
	public ByteString name()
	{
		return name;
	}

	/**
	 * @return the type of the relation.
	 */
	public TermRelation type()
	{
		return type;
	}
}
