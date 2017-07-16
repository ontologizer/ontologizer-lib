package ontologizer.association;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermMap;
import ontologizer.ontology.TermPropertyMap;
import ontologizer.types.ByteString;

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
	private int evidenceMismatch;

	/** All known terms */
	private TermMap terms;

	/** The evidences that shall be considered, null means to take them all */
	private Set<ByteString> evidences;

	private TermPropertyMap<TermID> altTermIDMap = null;

	public AssociationResolver(TermMap terms, Set<ByteString> evidences)
	{
		this.terms = terms;
		this.evidences = evidences;
	}

	public AssociationResolver(TermMap terms)
	{
		this(terms, null);
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

		if (evidences != null)
		{
			/*
			 * Skip if evidence of the annotation was not supplied as
			 * argument
			 */
			if (!evidences.contains(assoc.getEvidence()))
			{
				evidenceMismatch++;
				return null;
			}
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

	/**
	 * @return number of rejected annotations due to their evidences not matching
	 *  the requested ones.
	 */
	public int getEvidenceMismatch()
	{
		return evidenceMismatch;
	}

	/**
	 * Resolve all annotations of a given list using the given resolver and
	 * apply the resolving.
	 *
	 * This is a in-place operation. The associations will be mutated by
	 * this method.
	 *
	 * @param associations the association to be resolved
	 * @return a list of possible
	 */
	public List<Association> resolveAndModify(List<Association> associations)
	{
		List<Association> modifiedAssociations = new ArrayList<Association>(associations.size());
		for (Association a : associations)
		{
			TermID tid = resolveAssociation(a);
			if (tid == null)
			{
				continue;
			}
			a.setTermID(tid);
			modifiedAssociations.add(a);
		}
		return modifiedAssociations;
	}
}
