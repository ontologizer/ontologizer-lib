package ontologizer.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ontologizer.association.Association;
import ontologizer.association.AssociationContainer;
import ontologizer.association.ItemAssociations;
import ontologizer.ontology.Ontology;
import ontologizer.ontology.Ontology.ITermIDVisitor;
import ontologizer.ontology.TermID;
import ontologizer.types.ByteString;
import sonumina.math.graph.Algorithms;
import sonumina.math.graph.Grabbers;
import sonumina.math.graph.INeighbourGrabber;

/**
 * This class encapsulates the enumeration of explicit and implicit
 * annotations for an set of genes. You can iterate conveniently over
 * all terms where items have been annotated to.
 *
 * @author Sebastian Bauer
 */
public class TermEnumerator implements Iterable<TermID>
{
	/** The GO graph */
	private Ontology graph;

	private HashMap<TermID,TermAnnotations> map;

	/** The grabber that is used to propagate annotations */
	private INeighbourGrabber<TermID> grabber;

	/** Holds the number of suspicious annotations */
//	private int suspiciousCount;

	/**
	 * Construct the enumerator with propagating annotation through all relations.
	 *
	 * @param ont the ontology to work on
	 */
	public TermEnumerator(Ontology ont)
	{
		this(ont, false);
	}

	/**
	 * Construct the enumerator.
	 *
	 * @param ont the ontology to work on
	 * @param respectAnnotationPropagationRules if set to true, annotations are only
	 *  propagated via relation types that have the propagating property set to true
	 */
	public TermEnumerator(Ontology ont, boolean respectAnnotationPropagationRules)
	{
		this.graph = ont;

		map = new HashMap<TermID,TermAnnotations>();

		/* Construct different grabber depending whether the propagation
		 * property shall be respected or not.
		 */

		if (!respectAnnotationPropagationRules)
		{
			grabber = Grabbers.inGrabber(graph);
		} else
		{
			/* Java 8 would be handy here, but not yet */
			grabber = new INeighbourGrabber<TermID>()
			{
				@Override
				public Iterator<TermID> grabNeighbours(TermID v)
				{
					List<TermID> parents = new ArrayList<TermID>();

					for (TermID p : graph.getParentNodes(v))
					{
						if (graph.getDirectRelation(p,v).isPropagating())
						{
							parents.add(p);
						}
					}
					return parents.iterator();
				}
			};
		}

	}


	/**
	 *
	 * @param geneAssociations
	 */
	public void push(ItemAssociations geneAssociations)
	{
		push(geneAssociations,null);
	}

	/**
	 * Pushes the given gene association into the enumerator. I.e.
	 * add the gene in question to all terms annotating that gene.
	 *
	 * @param geneAssociations the gene associations
	 * @param evidences consider only annotation entries that correspond to
	 *  the given evidence codes.
	 */
	public void push(ItemAssociations geneAssociations, Set<ByteString> evidences)
	{
		ByteString geneName = geneAssociations.name();

		/* Check for suspicious annotations. An annotation i is suspicious
		 * if there exists a more specialized annotation orgininating from
		 * i. If an annotation isn't suspicious it is valid and placed in
		 * validAssocs */
/*		LinkedList<Association> validAssocs = new LinkedList<Association>();
		for (Association i : geneAssociations)
		{
			boolean existsPath = false;

			for (Association j : geneAssociations)
			{
				if (i.getGoID().equals(j.getGoID())) continue;

				if (graph.existsPath(i.getGoID(),j.getGoID()))
				{
					suspiciousCount++;
					existsPath = true;
				}
			}

			if (!existsPath)
			{
*/				/* No direct path from i to another assoc exists hence the association is
				 * valid */
/*				validAssocs.add(i);
			}
		}
*/
		/* Here we ignore the association qualifier (e.g. colocalized_with)
		 * completely.
		 */

		HashSet<TermID> termIDSet = new HashSet<TermID>();

		/* At first add the direct counts and remember the terms */
		for (Association association : geneAssociations)
		{
			TermID termID = association.getTermID();

			if (!graph.termExists(termID))
				continue;

			if (evidences != null)
			{
				if (!evidences.contains(association.getEvidence()))
					continue;
			}

			TermAnnotations termGenes = map.get(termID);

			/* Create an entry if it doesn't exist */
			if (termGenes == null)
			{
				termGenes = new TermAnnotations();
				map.put(termID,termGenes);
			}

			termGenes.directAnnotated.add(geneName);

			/* This term is annotated */
			termIDSet.add(association.getTermID());
		}

		/* Then add the total counts */

		/**
		 * The term visitor: To all visited terms (which here
		 * all terms up from the goTerms of the set) add the
		 * given gene.
		 *
		 * @author Sebastian Bauer
		 */
		class VisitingGOVertex implements ITermIDVisitor
		{
			private ByteString geneName;

			public VisitingGOVertex(ByteString geneName)
			{
				this.geneName = geneName;
			}

			public boolean visited(TermID tid)
			{
				TermAnnotations termGenes = map.get(tid);

				if (termGenes == null)
				{
					termGenes = new TermAnnotations();
					map.put(tid,termGenes);
				}
				termGenes.totalAnnotated.add(geneName);
				return true;
			}
		};

		/* Create the visting */
		VisitingGOVertex vistingGOVertex = new VisitingGOVertex(geneName);

		/* Start propagation */
		Algorithms.bfs(termIDSet, grabber, vistingGOVertex);
	}

	/**
	 * Return genes directly or indirectly annotated to the given
	 * goTermID.
	 *
	 * @param goTermID
	 * @return the annotated genes
	 */
	public TermAnnotations getAnnotatedGenes(TermID goTermID)
	{
		if (map.containsKey(goTermID))
			return map.get(goTermID);
		else
			return new TermAnnotations();
	}


	/**
	 *
	 * @author Sebastian Bauer
	 */
	public class GOTermOftenAnnotatedCount implements Comparable<GOTermOftenAnnotatedCount>
	{
		public TermID term;
		public int counts;

		public int compareTo(GOTermOftenAnnotatedCount o)
		{
			/* We sort reversely */
			return o.counts - counts;
		}
	};

	/**
	 * Returns the terms which shares genes with the given term and neither an ascendant nor
	 * descendant.
	 *
	 * @param goTermID
	 * @return terms that share items with goTermID
	 */
	public GOTermOftenAnnotatedCount [] getTermsOftenAnnotatedWithAndNotOnPath(TermID goTermID)
	{
		ArrayList<GOTermOftenAnnotatedCount> list = new ArrayList<GOTermOftenAnnotatedCount>();

		TermAnnotations goTermIDAnnotated = map.get(goTermID);
		if (goTermIDAnnotated == null) return null;

		/* For every term genes are annotated to */
		for (TermID curTerm : map.keySet())
		{
			/* Ignore terms on the same path */
			if (graph.isRootTerm(curTerm)) continue;
			if (curTerm.equals(goTermID)) continue;
			if (graph.existsPath(curTerm,goTermID) || graph.existsPath(goTermID,curTerm))
				continue;

			/* Find out the number of genes which are annotated to both terms */
			int count = 0;
			TermAnnotations curTermAnnotated = map.get(curTerm);
			for (ByteString gene : curTermAnnotated.totalAnnotated)
			{
				if (goTermIDAnnotated.totalAnnotated.contains(gene))
					count++;
			}

			if (count != 0)
			{
				GOTermOftenAnnotatedCount tc = new GOTermOftenAnnotatedCount();
				tc.term = curTerm;
				tc.counts = count;
				list.add(tc);
			}
		}

		GOTermOftenAnnotatedCount [] termArray = new GOTermOftenAnnotatedCount[list.size()];
		list.toArray(termArray);
		Arrays.sort(termArray);

		return termArray;
	}

	public Iterator<TermID> iterator()
	{
		return map.keySet().iterator();
	}

	/**
	 * @return the total number of terms to which at least a single gene has been annotated.
	 */
	public int getTotalNumberOfAnnotatedTerms()
	{
		return map.size();
	}


	/**
	 * @return the currently annotated terms as a set.
	 */
	public Set<TermID> getAllAnnotatedTermsAsSet()
	{
		LinkedHashSet<TermID> at = new LinkedHashSet<TermID>();
		for (TermID t : this)
			at.add(t);
		return at;
	}

	/**
	 * @return the currently annotated terms as a list.
	 */
	public List<TermID> getAllAnnotatedTermsAsList()
	{
		ArrayList<TermID> at = new ArrayList<TermID>();
		for (TermID t : this)
			at.add(t);
		return at;
	}

	/**
	 * @return all genes contained within this set.
	 */
	public Set<ByteString> getGenes()
	{
		return new LinkedHashSet<ByteString>(getGenesAsList());
	}

	/**
	 * @return all items as a list
	 */
	public List<ByteString> getGenesAsList()
	{
		return getAnnotatedGenes(graph.getRootTerm().getID()).totalAnnotated;
	}


	/** Callback to decide whether an term should be removed */
	public static interface IRemover
	{
		/**
		 * Returns whether the given term should be removed.
		 *
		 * @param tag
		 * @return true if the term shall be removed
		 */
		public boolean remove(TermID tid, TermAnnotations tag);
	}

	/**
	 * Removes existing terms from the enumerator according to
	 * the remover.
	 * @param remove
	 */
	public void removeTerms(IRemover remove)
	{
		ArrayList<TermID> toBeRemoved = new ArrayList<TermID>();
		for (Entry<TermID, TermAnnotations> entry : map.entrySet())
		{
			if (remove.remove(entry.getKey(),entry.getValue()))
					toBeRemoved.add(entry.getKey());
		}
		for (TermID tid : toBeRemoved)
			map.remove(tid);
	}

	public static interface Optional
	{
		Optional forAll(AssociationContainer container);

		Optional forAll(List<Association> associations);

		TermEnumerator build();
	}

	public static interface RequiresOntology
	{
		Optional ontology(Ontology ontology);
	}

	public static class TermEnumeratorBuilder implements RequiresOntology, Optional
	{
		private Ontology ontology;
		private AssociationContainer assocs;
		private List<Association> associationList;

		@Override
		public Optional forAll(AssociationContainer assocs)
		{
			this.assocs = assocs;
			return this;
		}

		@Override
		public Optional ontology(Ontology ontology)
		{
			this.ontology = ontology;
			return this;
		}

		@Override
		public Optional forAll(List<Association> associations)
		{
			associationList = associations;
			return this;
		}

		@Override
		public TermEnumerator build()
		{
			TermEnumerator te = new TermEnumerator(ontology);
			if (assocs != null)
			{
				for (ItemAssociations itemAssociations : assocs)
					te.push(itemAssociations);
			}
			if (associationList != null)
			{
				/* TODO: GAFLineScanner does a similar thing (and more). Extract this from there
				 * and make it reusable.
				 */
				Map<ByteString, ItemAssociations> map = new LinkedHashMap<ByteString, ItemAssociations>();
				for (Association a : associationList)
				{
					ByteString name = a.getObjectSymbol();
					ItemAssociations itemAssociations = map.get(name);
					if (itemAssociations == null)
					{
						itemAssociations = new ItemAssociations(name);
						map.put(name, itemAssociations);
					}
					itemAssociations.add(a);
				}
				for (ItemAssociations itemAssociations : map.values())
					te.push(itemAssociations);
			}
			return te;
		}
	}

	/**
	 * Entry method for a construct that builds a TermEnumerator.
	 *
	 * @param ontology the ontology for which the TermEnumerator shall be constructed.
	 * @return an object to put optional parameters.
	 */
	public static Optional ontology(Ontology ontology)
	{
		return new TermEnumeratorBuilder().ontology(ontology);
	}
}
