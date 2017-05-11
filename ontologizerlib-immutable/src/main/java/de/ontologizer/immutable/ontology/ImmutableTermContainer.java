package de.ontologizer.immutable.ontology;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Collection;
import java.util.Iterator;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.types.ByteString;

/**
 * Immutable {@link TermContainer} implementation.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ImmutableTermContainer implements TermContainer {

	private static final long serialVersionUID = 1L;

	/** Mapping from {@link TermID} to {@link Term}. */
	private ImmutableMap<TermID, Term> mapping;

	/** Immutable list of terms in container. */
	private final ImmutableList<Term> terms;

	/** Ontology file format version. */
	private final ByteString formatVersion;

	/** Ontolog file date. */
	private final ByteString date;

	/**
	 * Construct object with {@link Collection} of {@link Term}s, format version
	 * and date.
	 * 
	 * @param terms
	 *            {@link Collection} of {@link Term}s to use for construction
	 * @param formatVersion
	 *            Ontology file format version
	 * @param date
	 *            Ontology date
	 */
	public ImmutableTermContainer(Collection<Term> terms,
			ByteString formatVersion, ByteString date) {
		Builder<TermID, Term> builder = ImmutableMap.<TermID, Term>builder();
		for (Term term : terms) {
			builder.put(term.getID(), term);
		}
		this.mapping = builder.build();

		this.terms = ImmutableList.copyOf(terms);
		this.formatVersion = formatVersion;
		this.date = date;
	}

	@Override
	public Term get(TermID termId) {
		return mapping.get(termId);
	}

	@Override
	public Iterator<Term> iterator() {
		return terms.iterator();
	}

	@Override
	public ByteString getFormatVersion() {
		return formatVersion;
	}

	@Override
	public ByteString getDate() {
		return date;
	}

	@Override
	public int countTerms() {
		return mapping.size();
	}

	@Override
	public int maximumTermID() {
		if (mapping.isEmpty()) {
			return -1;
		} else {
			int result = mapping.keySet().iterator().next().id;
			for (TermID termId : mapping.keySet()) {
				if (result < termId.id) {
					result = termId.id;
				}
			}
			return result;
		}
	}

}
