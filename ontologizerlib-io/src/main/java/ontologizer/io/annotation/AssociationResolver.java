package ontologizer.io.annotation;

import java.util.logging.Level;
import java.util.logging.Logger;

import ontologizer.association.Association;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermMap;
import ontologizer.ontology.TermPropertyMap;

/**
 * Resolve associations.
 *
 * @author Sebastian Bauer
 */
public class AssociationResolver
{
	private static Logger logger = Logger.getLogger(AssociationResolver.class.getName());

	private int unknown;
	private int obsolete;

	/** All known terms */
	private TermMap terms;

	private TermPropertyMap<TermID> altTermIDMap = null;

	public AssociationResolver(TermMap terms)
	{
		this.terms = terms;
	}

	/**
	 * Resolve the given association, i.e., try to find an alternative if the target doesn't
	 * map to a primary term id.
	 *
	 * @param assoc the association to be resolved.
	 * @return the term id to which the target of the association was resolved.
	 */
	public TermID resolveAssociation(Association assoc)
	{
		TermID currentTermID = assoc.getTermID();

		Term currentTerm = terms.get(currentTermID);
		if (currentTerm == null)
		{
			TermID altID;

			if (altTermIDMap == null)
			{
				altTermIDMap = new TermPropertyMap<TermID>(terms, TermPropertyMap.term2AltIdMap);
			}

			/* Try to find the term among the alternative terms before giving up. */
			altID = altTermIDMap.get(currentTermID);
			if (altID != null)
				currentTerm = terms.get(altID);
			if (altID == null || currentTerm == null)
			{
				logger.log(Level.WARNING, "Skipping association of the item \"{}\" to {} because the term was not found! "
						+ "Are the OBO file and the association file both up-to-date?",
						new Object[] { assoc.getObjectSymbol(), currentTermID });
				unknown++;
				return null;
			} else
			{
				/* Okay, found, so set the new attributes */
				currentTermID = altID;
				assoc.setTermID(currentTermID);
			}
		} else
		{
			/* Reset the term id so a unique id is used */
			currentTermID = currentTerm.getID();
			assoc.setTermID(currentTermID);
		}

		if (currentTerm.isObsolete())
		{
			logger.log(Level.WARNING, "Skipping association of the item \"{}\" to {} because the term is obsolete! "
					+ "Are the OBO file and the association file both up-to-date?",
					new Object[] { assoc.getObjectSymbol(), currentTermID });
			obsolete++;
			return null;
		}
		return currentTermID;
	}

	/**
	 * @return number rejected annotations due to their targets being unknown.
	 */
	public int getUnknown()
	{
		return unknown;
	}

	/**
	 * @return number of rejected annotations due to their targets being obsolete.
	 */
	public int getObsolete()
	{
		return obsolete;
	}
}
