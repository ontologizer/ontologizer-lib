package ontologizer.enumeration;

import static java.util.Arrays.asList;
import static ontologizer.ontology.TermID.tid;
import static ontologizer.types.ByteString.b;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import ontologizer.association.Association;
import ontologizer.association.ItemAssociations;
import ontologizer.io.obo.OBOOntologyCreator;
import ontologizer.io.obo.OBOParserException;
import ontologizer.io.obo.OBOParserTestBase;
import ontologizer.ontology.Ontology;

/**
 * Simple tests for the TermEnumerator
 *
 * @author Sebastian Bauer
 */
public class TermEnumeratorTest extends OBOParserTestBase
{
	/**
	 * Create two associations: item1 is associated to GO:0000002 and
	 * item2 to GO:0000003.
	 *
	 * @return the list of associations
	 */
	private static List<ItemAssociations> createAssociations()
	{
		/* Create item 1 that is annotated to test2  */
		ItemAssociations item1Associations = new ItemAssociations(b("item1"));
		item1Associations.add(new Association(b("item1"), "GO:0000002"));

		/* Create item 2 that is annotated to test3  */
		ItemAssociations item2Associations = new ItemAssociations(b("item2"));
		item2Associations.add(new Association(b("item2"), "GO:0000003"));

		return asList(item1Associations, item2Associations);
	}

	/// [term]
	/// name: test
	/// id: GO:0000001
	///
	/// [term]
	/// name: test2
	/// id: GO:0000002
	///
	/// relationship: regulates GO:0000001 ! test
	///
	/// [term]
	/// name: test3
	/// id: GO:0000003
	///
	/// is_a: GO:0000001 ! test
	@Test
	public void testPropagationFlavours() throws IOException, OBOParserException
	{
		/* Here we test different propagation flavour */
		Ontology ontology = OBOOntologyCreator.create(parseTestComment());

		List<ItemAssociations> associations = createAssociations();

		/* Push annotations to the enumerator */
		TermEnumerator te = new TermEnumerator(ontology);
		associations.stream().forEach(te::push);

		/* We will end up with three annotated terms*/
		assertEquals(3, te.getTotalNumberOfAnnotatedTerms());

		/* Term 1 will have 0 direct item, but all indirect as, by default, we propagate
		 * all annotations.
		 */
		assertEquals(0, te.getAnnotatedGenes(tid("GO:0000001")).directAnnotated.size());;
		assertEquals(2, te.getAnnotatedGenes(tid("GO:0000001")).totalAnnotated.size());

		/* Create enumerator of a different flavour */
		te = new TermEnumerator(ontology, true);
		associations.stream().forEach(te::push);

		/* We will end up with three annotated terms*/
		assertEquals(3, te.getTotalNumberOfAnnotatedTerms());

		/* Term 1 will have 0 direct item and one indirect one as "regulates" is not
		 * propagating.
		 */
		assertEquals(0, te.getAnnotatedGenes(tid("GO:0000001")).directAnnotated.size());;
		assertEquals(1, te.getAnnotatedGenes(tid("GO:0000001")).totalAnnotated.size());
		assertEquals("item2", te.getAnnotatedGenes(tid("GO:0000001")).totalAnnotated.get(0).toString());
	}
}
