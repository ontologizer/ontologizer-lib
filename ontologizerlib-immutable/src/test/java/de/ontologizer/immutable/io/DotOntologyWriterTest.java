package de.ontologizer.immutable.io;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableList;
import de.ontologizer.immutable.ontology.ImmutableOntology;
import de.ontologizer.immutable.ontology.ImmutableTermContainer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ontologizer.ontology.ParentTermID;
import ontologizer.ontology.Prefix;
import ontologizer.ontology.Term;
import ontologizer.ontology.TermID;
import ontologizer.ontology.TermRelation;
import ontologizer.types.ByteString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for the {@link OboOntologyReader} class.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DotOntologyWriterTest {

	ImmutableOntology ontology;

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		final Prefix prefix = new Prefix("XY");

		final List<Term> terms = new ArrayList<Term>();
		terms.add(new Term("XY:001", "root term", null,
				ImmutableList.<ParentTermID>of()));
		terms.add(new Term("XY:002", "child 1", null,
				ImmutableList.<ParentTermID>of(new ParentTermID(
						new TermID(prefix, 1), TermRelation.IS_A))));
		terms.add(new Term("XY:003", "root term", null,
				ImmutableList.<ParentTermID>of(
						new ParentTermID(new TermID(prefix, 1),
								TermRelation.IS_A),
						new ParentTermID(new TermID(prefix, 2),
								TermRelation.IS_A))));
		final ImmutableTermContainer termContainer = new ImmutableTermContainer(
				terms, new ByteString("format version"),
				new ByteString("2000-01-01"));

		ontology = ImmutableOntology.constructFromTerms(termContainer);
	}

	@Test
	public void test() throws IOException {
		final File tmpFile = tmpFolder.newFile();
		final DotOntologyWriter dotWriter = new DotOntologyWriter(
				new FileWriterOutput(tmpFile));
		dotWriter.write(ontology);

		BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
		List<String> lines = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			lines.add(line);
			line = reader.readLine();
		}
		reader.close();

		Assert.assertEquals(4, lines.size());
		Assert.assertEquals("", lines.get(0));
		Assert.assertEquals("", lines.get(1));
		Assert.assertEquals("", lines.get(2));
		Assert.assertEquals("", lines.get(3));
		Assert.assertEquals("", lines.get(4));
		Assert.assertEquals("", lines.get(5));
	}

}
