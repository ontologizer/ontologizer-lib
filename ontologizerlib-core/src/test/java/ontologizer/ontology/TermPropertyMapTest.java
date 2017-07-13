package ontologizer.ontology;

import static ontologizer.ontology.TermID.tid;
import static ontologizer.types.ByteString.b;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TermPropertyMapTest
{
	private Term t0;
	private Term t1;
	private Term t2;
	private TermContainer container;

	@Before
	public void setUp() throws Exception
	{
		t0 = Term.name("root").id("T:0000000").altId(tid("T:1000000")).build();
		t1 = Term.name("root").id("T:0000001").altId(tid("T:1000001")).build();
		t2 = Term.name("root").id("T:0000002").altId(tid("T:1000002")).build();

		HashSet<Term> termsConstructed = new HashSet<Term>(Arrays.asList(t0, t1, t2));
		container = new TermContainer(termsConstructed, b("noformat"), b("nodate"));
	}

	@Test
	public void testTermPropertyMap()
	{
		TermPropertyMap<TermID> altIdMap = new TermPropertyMap<TermID>(container, TermPropertyMap.term2AltIdMap);
		Assert.assertEquals(tid("T:0000000"),altIdMap.get(tid("T:1000000")));
		Assert.assertEquals(tid("T:0000001"),altIdMap.get(tid("T:1000001")));
		Assert.assertEquals(tid("T:0000002"),altIdMap.get(tid("T:1000002")));
	}
}
