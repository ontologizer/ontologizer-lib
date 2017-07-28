package ontologizer.enumeration;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import ontologizer.association.AssociationContainer;
import ontologizer.association.ItemAssociations;
import ontologizer.ontology.TermID;
import ontologizer.types.ByteString;

public class TermEnumeratorTest
{
	private static TermAnnotations annotatedGenes(TermEnumerator e, String term)
	{
		return e.getAnnotatedGenes(new TermID(term));
	}

	private void assertEnumerator(AssociationContainer assoc, TermEnumerator e)
	{
		assertEquals(11, e.getTotalNumberOfAnnotatedTerms());
		assertEquals(500, e.getGenes().size());

		TermAnnotations ag = annotatedGenes(e, "GO:0000001");
		assertEquals(assoc.getAllAnnotatedGenes(), new HashSet<ByteString>(ag.totalAnnotated));
	}

	@Test
	public void testEnumeratorOnInternalOntology()
	{
		InternalOntology internal = new InternalOntology();
		TermEnumerator e = new TermEnumerator(internal.graph);
		for (ItemAssociations g2a : internal.assoc)
			e.push(g2a);
		assertEnumerator(internal.assoc, e);
	}

	@Test
	public void testEnumeratorOnInternalOntologyViaBuild()
	{
		InternalOntology internal = new InternalOntology();
		TermEnumerator e = TermEnumerator.ontology(internal.graph).forAll(internal.assocList).build();
		assertEnumerator(internal.assoc, e);
	}
}
