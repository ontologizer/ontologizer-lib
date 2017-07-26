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
	private final ByteString fancyName;
	private final RelationMeaning meaning;

	/** The type of relation propagates along its destination/object */
	private final boolean propagating;

	/**
	 * Constructs a new relation. Depending on the relation meaning it
	 * is supposed to propagate annotations or not.
	 *
	 * @param name the name of the relation (e.g., is_a)
	 * @param meaning the predefined meaning of the relation.
	 */
	public RelationType(ByteString name, RelationMeaning meaning)
	{
		this.name = name;
		this.fancyName = name.replace('_',' ');
		this.meaning = meaning;
		switch (meaning)
		{
		case	IS_A:
		case	PART_OF_A:
				propagating = true;
				break;

		default:
				propagating = false;
				break;
		}
	}

	public RelationType(RelationMeaning type)
	{
		this(type.relationName(), type);

	}
	/**
	 * @return the name of the relation.
	 */
	public ByteString name()
	{
		return name;
	}

	/**
	 * @return the fancy, i.e., user-showable name of the relation.
	 */
	public ByteString fancyName()
	{
		return fancyName;
	}

	/**
	 * @return the meaning of the relation.
	 */
	public RelationMeaning meaning()
	{
		return meaning;
	}

	/**
	 * @return whether the relation propagates annotation relations.
	 */
	public boolean isPropagating()
	{
		return propagating;
	}
}
