package ontologizer.ontology;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import sonumina.collections.IntMap;
import sonumina.collections.IntMapper;

/**
 * A simple class mapping term ids to actual terms.
 *
 * @author Sebastian Bauer
 */
public class TermMap implements Iterable<Term>, Serializable
{
	private static final long serialVersionUID = 1L;

	private IntMapper<TermID> termIDMapper;
	private Term [] terms;

	private TermMap()
	{
	}

	/**
	 * Initialize the map with the given terms. Required for subclasses.
	 *
	 * @param terms
	 */
	protected TermMap(Iterable<Term> terms, int size)
	{
		init(terms, size);
	}

	/**
	 * Initialize the map with the given terms.
	 *
	 * @param terms
	 */
	private void init(Iterable<Term> terms, int size)
	{
		this.terms = new Term[size];
		termIDMapper = IntMapper.create(terms, size, new IntMap<Term, TermID>()
		{
			@Override
			public TermID map(Term key, int index)
			{
				TermMap.this.terms[index] = key;
				return key.getID();
			}
		});
	}

	/**
	 * Return the full term reference to the given term id.
	 *
	 * @param tid the term id for which to get the term.
	 * @return the term.
	 */
	public Term get(TermID tid)
	{
		int idx = termIDMapper.getIndex(tid);
		if (idx == -1)
		{
			return null;
		}
		return terms[idx];
	}

	/**
	 * Create a term id map.
	 *
	 * @param terms
	 * @return the term map
	 */
	public static TermMap create(Iterable<Term> terms, int size)
	{
		TermMap map = new TermMap();
		map.init(terms, size);
		return map;
	}

	public static TermMap create(Collection<Term> terms)
	{
		return create(terms, terms.size());
	}

	@Override
	public Iterator<Term> iterator()
	{
		return new Iterator<Term>()
		{
			int index;

			@Override
			public boolean hasNext()
			{
				return index < terms.length;
			}

			@Override
			public Term next()
			{
				return terms[index++];
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * @return the number of terms in this map.
	 */
	public int size()
	{
		return terms.length;
	}
}
