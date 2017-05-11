package ontologizer.ontology;

import java.io.Serializable;

// TODO: rename to ParentRelation

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
	private static final long serialVersionUID = 1L;

	/** The id of the related term */
	private TermID termid;

	/** the type of the relation */
	private TermRelation relation;

	public ParentTermID(TermID parent, TermRelation relation)
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
	public TermRelation getRelation()
	{
		return relation;
	}
}
