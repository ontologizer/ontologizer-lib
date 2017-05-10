package de.ontologizer.immutable.graph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link ImmutableEdge} class.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableEdgeTest {

	/* Three {@link ImmutableEdge} options to use for exercising code. */
	ImmutableEdge<Integer> e1;
	ImmutableEdge<Integer> e2;
	ImmutableEdge<Integer> e3;

	@Before
	public void setUp() throws Exception {
		e1 = ImmutableEdge.construct(0, 1);
		e2 = ImmutableEdge.construct(0, 1);
		e3 = ImmutableEdge.construct(1, 2);
	}

	@Test
	public void testGetters() {
		Assert.assertEquals(e1.getSource(), Integer.valueOf(0));
		Assert.assertEquals(e1.getDest(), Integer.valueOf(1));
		Assert.assertEquals(e1.getWeight(), 1);
	}

	@Test
	public void testEquals() {
		Assert.assertTrue(e1.equals(e2));
		Assert.assertFalse(e1.equals(e3));
	}

	@Test
	public void testHashCode() {
		Assert.assertEquals(e1.hashCode(), e2.hashCode());
		Assert.assertNotEquals(e1.hashCode(), e3.hashCode());
	}

	@Test
	public void testToString() {
		Assert.assertEquals("ImmutableEdge [source=0, dest=1]", e1.toString());
		Assert.assertEquals("ImmutableEdge [source=0, dest=1]", e2.toString());
		Assert.assertEquals("ImmutableEdge [source=1, dest=2]", e3.toString());
	}

}
