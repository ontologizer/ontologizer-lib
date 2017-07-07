package ontologizer.enumeration;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Test;

import ontologizer.association.ItemAssociations;
import ontologizer.ontology.TermID;
import ontologizer.types.ByteString;

public class TermEnumeratorTest
{
	private static TermAnnotations annotatedGenes(TermEnumerator e, String term)
	{
		return e.getAnnotatedGenes(new TermID(term));
	}

	@Test
	public void testEnumeratorOnInternalOntology()
	{
		InternalOntology internal = new InternalOntology();
		TermEnumerator e = new TermEnumerator(internal.graph);
		for (ItemAssociations g2a : internal.assoc)
			e.push(g2a);

		assertEquals(11, e.getTotalNumberOfAnnotatedTerms());
		assertEquals(500, e.getGenes().size());

		TermAnnotations ag = annotatedGenes(e, "GO:0000001");
		assertEquals(internal.assoc.getAllAnnotatedGenes(), new HashSet<ByteString>(ag.totalAnnotated));
	}
}
