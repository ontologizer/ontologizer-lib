package ontologizer.enumeration;

import static ontologizer.types.ByteString.b;
import static org.junit.Assert.assertEquals;
import static ontologizer.ontology.TermID.tid;

import java.io.IOException;

import org.junit.Test;

import ontologizer.association.Association;
import ontologizer.association.Gene2Associations;
import ontologizer.io.obo.OBOOntologyCreator;
import ontologizer.io.obo.OBOParser;
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

		OBOParser parser = parseTestComment();
		Ontology ontology = OBOOntologyCreator.create(parser);

		/* Create item 1 that is annotated to test2  */
		Gene2Associations item1Associations = new Gene2Associations(b("item1"));
		item1Associations.add(new Association(b("item1"), "GO:0000002"));

		/* Create item 2 that is annotated to test3  */
		Gene2Associations item2Associations = new Gene2Associations(b("item2"));
		item2Associations.add(new Association(b("item2"), "GO:0000003"));

		/* Push annotations to the enumerator */
		TermEnumerator te = new TermEnumerator(ontology);
		te.push(item1Associations);
		te.push(item2Associations);

		/* We will end up with three annotated terms*/
		assertEquals(3, te.getTotalNumberOfAnnotatedTerms());

		/* Term 1 will have 0 direct item, but all indirect as, by default, we propagate
		 * all annotations.
		 */
		assertEquals(0, te.getAnnotatedGenes(tid("GO:0000001")).directAnnotated.size());;
		assertEquals(2, te.getAnnotatedGenes(tid("GO:0000001")).totalAnnotated.size());

		/* Create enumerator of a different flavour */
		te = new TermEnumerator(ontology, true);
		te.push(item1Associations);
		te.push(item2Associations);

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
