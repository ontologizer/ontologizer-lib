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

	/** A default relation meaning that is the most unspecific one */
	public static final RelationType UNKNOWN = new RelationType(b("unknown"), RelationMeaning.UNKOWN);

	private final ByteString name;
	private final RelationMeaning meaning;

	/**
	 * Constructs a new relation.
	 *
	 * @param name the name of the relation (e.g., is_a)
	 * @param meaning the predefined meaning of the relation.
	 */
	public RelationType(ByteString name, RelationMeaning meaning)
	{
		this.name = name;
		this.meaning = meaning;
	}

	public RelationType(RelationMeaning type)
	{
		this.name = type.relationName();
		this.meaning = type;

	}
	/**
	 * @return the name of the relation.
	 */
	public ByteString name()
	{
		return name;
	}

	/**
	 * @return the meaning of the relation.
	 */
	public RelationMeaning meaning()
	{
		return meaning;
	}
}
