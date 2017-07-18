package ontologizer.io.annotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ontologizer.association.AnnotationContext;
import ontologizer.association.AnnotationMapBuilder;
import ontologizer.association.Association;
import ontologizer.association.AssociationResolver;
import ontologizer.io.IParserInput;
import ontologizer.io.linescanner.AbstractByteLineScanner;
import ontologizer.ontology.PrefixPool;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermMap;
import ontologizer.types.ByteString;

/**
 * A GAF Line scanner.
 *
 * @author Sebastian Bauer
 */
class GAFByteLineScanner extends AbstractByteLineScanner
{
	/** The wrapped input */
	private IParserInput input;

	/** Contains all items whose associations should gathered or null if all should be gathered */
	private Set<ByteString> names;

	/** Monitor progress */
	private IAssociationParserProgress progress;

	private int lineno = 0;
	private long millis = 0;
	public int good = 0;
	public int bad = 0;
	public int skipped = 0;
	public int nots = 0;
	public int kept = 0;

	/** Mapping from gene (or gene product) names to Association objects */
	private ArrayList<Association> associations = new ArrayList<Association>();

	/** Our prefix pool */
	private PrefixPool prefixPool = new PrefixPool();

	private HashSet<TermID> usedTermIDs = new HashSet<TermID>();

	/**********************************************************************/

	private AnnotationMapBuilder mapBuilder;

	private AnnotationMapBuilder.WarningCallback warningCallback;

	private AssociationResolver resolver;

	public GAFByteLineScanner(IParserInput input, byte [] head, Set<ByteString> names, TermMap terms, Set<ByteString> evidences, final IAssociationParserProgress progress)
	{
		super(input.inputStream());

		push(head);

		if (terms != null)
		{
			resolver = new AssociationResolver(terms, evidences);
		}

		this.input = input;
		this.names = names;
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

		if (resolver != null)
		{
			currentTermID = resolver.resolveAssociation(assoc);
			if (currentTermID == null)
			{
				return true;
			}
		}

		usedTermIDs.add(currentTermID);

		ByteString[] synonyms = assoc.getSynonyms();;

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

	/**
	 * @return total number of entries that didn't match the specified
	 *  evidences.
	 */
	public int getEvidenceMismatchCount()
	{
		if (resolver != null)
		{
			return resolver.getEvidenceMismatch();
		}
		return 0;
	}

	/**
	 * @return total number of entries that referred to obsolete terms.
	 */
	public int getObsoleteCount()
	{
		if (resolver != null)
		{
			return resolver.getObsolete();
		}
		return 0;
	}
};

