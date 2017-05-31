package ontologizer.ontology;

import java.io.Serializable;

/**
 * This class is used to specify a parent accompanied with
 * the kind of relationship. Note that the term "parent" is
 * indeed a misnomer. The usage of this term has historical
 * reasons when there were only a limited set of relations.
 *
 * @author Sebastian Bauer
 */
public final class ParentTermID implements Serializable
{
	private static final long serialVersionUID = 2L;

	/** The id of the related term */
	private TermID termid;

	/** the type of the relation */
	private RelationType relation;

	public ParentTermID(TermID parent, RelationType relation)
	{
		this.termid = parent;
		this.relation = relation;
	}

	/**
	 * @return the id of the related (parent) term
	 */
	public TermID getRelated()
	{
		return termid;
	}

	/**
	 * @return the relation
	 */
	public RelationType getRelation()
	{
		return relation;
	}
}
