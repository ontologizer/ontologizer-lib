package ontologizer.io.annotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ontologizer.association.AnnotationContext;
import ontologizer.association.AnnotationMapBuilder;
import ontologizer.association.Association;
import ontologizer.io.IParserInput;
import ontologizer.io.linescanner.AbstractByteLineScanner;
import ontologizer.ontology.PrefixPool;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermMap;
import ontologizer.ontology.TermPropertyMap;
import ontologizer.types.ByteString;

/**
 * A GAF Line scanner.
 *
 * @author Sebastian Bauer
 */
class GAFByteLineScanner extends AbstractByteLineScanner
{
	private static Logger logger = Logger.getLogger(GAFByteLineScanner.class.getName());

	private static final byte PIPE = (byte)'|';

	/** The wrapped input */
	private IParserInput input;

	/** Contains all items whose associations should gathered or null if all should be gathered */
	private Set<ByteString> names;

	/** All known terms */
	private TermMap terms;

	/** Set of evidences that shall be considered or null if all should be considered */
	private Set<ByteString> evidences;

	/** Monitor progress */
	private IAssociationParserProgress progress;

	private int lineno = 0;
	private long millis = 0;
	public int good = 0;
	public int bad = 0;
	public int skipped = 0;
	public int nots = 0;
	public int evidenceMismatch = 0;
	public int kept = 0;
	public int obsolete = 0;

	/** Mapping from gene (or gene product) names to Association objects */
	private ArrayList<Association> associations = new ArrayList<Association>();

	/** Our prefix pool */
	private PrefixPool prefixPool = new PrefixPool();

	private TermPropertyMap<TermID> altTermIDMap = null;

	private HashSet<TermID> usedTermIDs = new HashSet<TermID>();

	/**********************************************************************/

	private AnnotationMapBuilder mapBuilder;

	private AnnotationMapBuilder.WarningCallback warningCallback;

	public GAFByteLineScanner(IParserInput input, byte [] head, Set<ByteString> names, TermMap terms, Set<ByteString> evidences, final IAssociationParserProgress progress)
	{
		super(input.inputStream());

		push(head);

		this.input = input;
		this.names = names;
		this.terms = terms;
		this.evidences = evidences;
		this.progress = progress;

		if (progress != null)
		{
			warningCallback = new AnnotationMapBuilder.WarningCallback()
			{
				@Override
				public void warning(String warning)
				{
					progress.warning(warning);
				}
			};
		}
		this.mapBuilder = new AnnotationMapBuilder(warningCallback);
	}

	@Override
	public boolean newLine(byte[] buf, int start, int len)
	{
		/* Progress stuff */
		if (progress != null)
		{
			long newMillis = System.currentTimeMillis();
			if (newMillis - millis > 250)
			{
				progress.update(input.getPosition());
				millis = newMillis;
			}
		}

		lineno++;

		/* Ignore comments */
		if (len < 1 || buf[start]=='!')
			return true;

		Association assoc = Association.createFromGAFLine(buf,start,len,prefixPool);

		TermID currentTermID = assoc.getTermID();

		good++;

		if (assoc.hasNotQualifier())
		{
			skipped++;
			nots++;
			return true;
		}

		if (evidences != null)
		{
			/*
			 * Skip if evidence of the annotation was not supplied as
			 * argument
			 */
			if (!evidences.contains(assoc.getEvidence()))
			{
				skipped++;
				evidenceMismatch++;
				return true;
			}
		}

		if (terms != null)
		{
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
							+ "Are the OBO file and the association file both up-to-date?",
							new Object[] { assoc.getObjectSymbol(), currentTermID });
					skipped++;
					return true;
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
				logger.log(Level.WARNING, "Skipping association of the item \"{}\" t {} because the term was not found! "
						+ "Are the OBO file and the association file both up-to-date?",
						new Object[] { assoc.getObjectSymbol(), currentTermID });
				skipped++;
				obsolete++;
				return true;
			}
		}

		usedTermIDs.add(currentTermID);

		ByteString[] synonyms;
		/* populate synonym string field */
		if (assoc.getSynonym() != null && assoc.getSynonym().length() > 2)
		{
			/* Note that there can be multiple synonyms, separated by a pipe */
			synonyms = assoc.getSynonym().split(PIPE);
		} else
			synonyms = null;

		if (names != null)
		{
			/* We are only interested in associations to given genes */
			boolean keep = false;

			/* Check if synonyms are contained */
			if (synonyms != null)
			{
				for (int i = 0; i < synonyms.length; i++)
				{
					if (names.contains(synonyms[i]))
					{
						keep = true;
						break;
					}
				}
			}

			if (keep || names.contains(assoc.getObjectSymbol()) || names.contains(assoc.getDB_Object()))
			{
				kept++;
			} else
			{
				skipped++;
				return true;
			}
		} else
		{
			kept++;
		}

		/* Add the Association to ArrayList */
		associations.add(assoc);

		mapBuilder.add(assoc, synonyms, lineno);

		return true;
	}

	/**
	 * @return the number of terms used by the import.
	 */
	public int getNumberOfUsedTerms()
	{
		return usedTermIDs.size();
	}

	public ArrayList<Association> getAssociations()
	{
		return associations;
	}

	/**
	 * @return the annotation context.
	 */
	public AnnotationContext getAnnotationContext()
	{
		return mapBuilder.build();
	}
};

