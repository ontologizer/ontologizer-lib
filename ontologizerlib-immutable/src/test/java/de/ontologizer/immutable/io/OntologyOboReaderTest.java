package de.ontologizer.immutable.io;

import de.ontologizer.immutable.ontology.ImmutableOntology;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for the {@link OntologyOboReader} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class OntologyOboReaderTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	private String pathObo;

	private OntologyOboReader oboReader;

	@Before
	public void setUp() throws Exception {
		pathObo = OntologyOboReaderTest.class.getClassLoader()
				.getResource("gene_ontology.1_2.obo.gz").getPath();

		oboReader = new OntologyOboReader(new FileReaderInput(pathObo));
	}

	@Test
	public void test() {
		ImmutableOntology immutableOntology = oboReader.readImmutable();
		Assert.assertEquals(35520, immutableOntology.countTerms());
		Assert.assertEquals(35521, immutableOntology.getGraph().countVertices());
		Assert.assertEquals(63108, immutableOntology.getGraph().countEdges());

		Assert.assertEquals("GO:0000000", immutableOntology.getRootTerm().getID().toString());
	}

}
