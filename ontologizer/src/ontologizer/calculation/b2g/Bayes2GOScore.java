package ontologizer.calculation.b2g;

import java.util.Random;

import ontologizer.enumeration.TermEnumerator;
import ontologizer.ontology.TermID;
import ontologizer.types.ByteString;
import sonumina.collections.IntMapper;

/**
 * The base class of bayes2go Score.
 *
 * For efficiency reasons terms and genes are represented by own ids.
 *
 * @author Sebastian Bauer
 */
abstract public class Bayes2GOScore extends Bayes2GOScoreBase
{
	/** Source of randomness */
	protected Random rnd;

	/** Array holding the observed values for each gene */
	protected double [] observedValueOfGene;

	protected int numRecords;
	protected int [] termActivationCounts;

	protected boolean usePrior = true;
	protected double p = Double.NaN;

	/**
	 * This is a simple interface that provides values to the genes.
	 */
	public static interface IGeneValueProvider
	{
		/**
		 * Return the value that is associated with the given gene.
		 *
		 * @param gid id of the gene whose value should be returned
		 * @return the value
		 */
		double getGeneValue(int gid);

		/**
		 * Returns the threshold that specifies when a gene is considered as observed or not.
		 * For instance, if the associated values are p values, then one would return the desired
		 * significance level.
		 *
		 * @return the threshold
		 */
		double getThreshold();

		/**
		 * Returns whether genes whose associated values that are smaller than others are considered
		 * as better candidates than the larger ones. For instance, if the associated values are p
		 * values, one would return true here.
		 *
		 * @return whether small numeric values are better than large ones.
		 */
		boolean smallerIsBetter();
	}

	public static int [][] makeTermLinks(TermEnumerator populationEnumerator, IntMapper<TermID> termMapper, IntMapper<ByteString> geneMapper)
	{
		int [][] termLinks = new int[termMapper.getSize()][];

		for (int i = 0; i < termMapper.getSize(); i++)
		{
			TermID tid = termMapper.get(i);

			/* Fill in the links */
			termLinks[i] = new int[populationEnumerator.getAnnotatedGenes(tid).totalAnnotated.size()];
			int j=0;
			for (ByteString gene : populationEnumerator.getAnnotatedGenes(tid).totalAnnotated)
			{
				int gid = geneMapper.getIndex(gene);
				if (gid == -1)
				{
					throw new IllegalArgumentException("Gene " + gene.toString() + " is not contained within the gene mapper");
				}
				termLinks[i][j] = gid;
				j++;
			}
		}

		return termLinks;
	}

	public Bayes2GOScore(Random rnd, int [][] termLinks, int numGenes, IGeneValueProvider geneValueProvider)
	{
		super(termLinks, numGenes);

		this.rnd = rnd;

		double threshold = geneValueProvider.getThreshold();
		boolean smallerIsBetter = geneValueProvider.smallerIsBetter();

		/* Initialize basics of genes */
		observedValueOfGene = new double[numGenes];

		for (int i = 0; i < numGenes; i++)
		{
			observedValueOfGene[i] = geneValueProvider.getGeneValue(i);
			if (smallerIsBetter) observedGenes[i] = observedValueOfGene[i] <= threshold;
			else observedGenes[i] = observedValueOfGene[i] >= threshold;
		}

		termActivationCounts = new int[numTerms];
	}

	/**
	 * Constructs a class for calculating the Bayes2GO score suitable for an MCMC algorithm.
	 *
	 * @param rnd Random source for proposing states.
	 * @param termLinks terms to genes.
	 * @param observedGenes state of each gene whether it is observed or not.
	 */
	public Bayes2GOScore(Random rnd, int [][] termLinks, final boolean [] observedGenes)
	{
		/* Here a gene value provider is constructed that maps the boolean observed state back
		 * to values some values. A gene, that is observed gets a -1, a gene that is not observed
		 * gets a 1. Applied with a threshold of one, this gives back the same set of observed genes.
		 */
		this(rnd, termLinks, observedGenes.length, new IGeneValueProvider() {
			@Override
			public boolean smallerIsBetter() {
				return true;
			}
			@Override
			public double getThreshold() {
				return 0;
			}
			@Override
			public double getGeneValue(int gid) {
				if (observedGenes[gid]) return -1;
				return 1;
			}
		});
	}

	public void setUsePrior(boolean usePrior)
	{
		this.usePrior = usePrior;
	}

	public boolean getUsePrior()
	{
		return usePrior;
	}

	public void setExpectedNumberOfTerms(double terms)
	{
		p = (double)terms / numTerms;
	}

	/**
	 * Returns the score of the setting if the given terms
	 * are active and all others are inactive.
	 *
	 * @param activeTerms defines which terms are considered as active
	 * @return the score
	 */
	public double score(int [] activeTerms)
	{
		int [] oldTerms = new int[numTerms - numInactiveTerms];
		for (int i=numInactiveTerms,j=0;i<numTerms;i++,j++)
			oldTerms[j] = termPartition[i];

		/* Deactivate old terms */
		for (int i=0;i<oldTerms.length;i++)
			switchState(oldTerms[i]);

		/* Enable new terms */
		for (int idx : activeTerms)
		{
			switchState(idx);
		}

		double score = getScore();

		/* Disable new terms */
		for (int idx : activeTerms)
		{
			switchState(idx);
		}

		/* Enable old terms again */
		for (int i=0;i<oldTerms.length;i++)
			switchState(oldTerms[i]);

		return score;
	}

	/**
	 * @return the score of the current state.
	 */
	public abstract double getScore();

	public abstract void proposeNewState(long rand);
	public void proposeNewState()
	{
		proposeNewState(rnd.nextLong());
	}

	public void exchange(int t1, int t2)
	{
		switchState(t1);
		switchState(t2);
	}

	public abstract void undoProposal();

	public abstract long getNeighborhoodSize();

	/**
	 * Records the current settings.
	 */
	public void record()
	{
		for (int i = numInactiveTerms; i < numTerms; i++)
			termActivationCounts[termPartition[i]]++;

		numRecords++;
	}

	/**
	 * @return the terms that are currently activated
	 */
	public int [] getActiveTerms()
	{
		int [] list = new int[numTerms - numInactiveTerms];
		for (int i = numInactiveTerms; i < numTerms; i++)
			list[i-numInactiveTerms] = termPartition[i];
		return list;
	}
}
