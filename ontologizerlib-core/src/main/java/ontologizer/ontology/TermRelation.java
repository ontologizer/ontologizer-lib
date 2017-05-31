package ontologizer.ontology;

import static ontologizer.types.ByteString.b;

import ontologizer.types.ByteString;

/**
 * The relation a term can have to another.
 *
 * @author Sebastian Bauer
 *
 * TODO: Rename
 */
public enum TermRelation
{
	IS_A(b("is_a")),
	PART_OF_A(b("part_of")),
	REGULATES(b("regulates")),
	NEGATIVELY_REGULATES(b("negatively_regulates")),
	POSITIVELY_REGULATES(b("positively_regulates")),
	/* This should always be the last one */
	UNKOWN(b("unknown"));

	private final ByteString relationName;

	private TermRelation(ByteString relationName)
	{
		this.relationName = relationName;
	}

	public ByteString relationName()
	{
		return relationName;
	}
}
