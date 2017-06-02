package ontologizer.ontology;

import static ontologizer.types.ByteString.b;

import ontologizer.types.ByteString;

/**
 * The meaning that a relation type can have.
 *
 * @author Sebastian Bauer
 */
public enum RelationMeaning
{
	IS_A(b("is_a")),
	PART_OF_A(b("part_of")),
	REGULATES(b("regulates")),
	NEGATIVELY_REGULATES(b("negatively_regulates")),
	POSITIVELY_REGULATES(b("positively_regulates")),
	/* This should always be the last one */
	UNKOWN(b("unknown"));

	private final ByteString relationName;

	private RelationMeaning(ByteString relationName)
	{
		this.relationName = relationName;
	}

	public ByteString relationName()
	{
		return relationName;
	}
}
