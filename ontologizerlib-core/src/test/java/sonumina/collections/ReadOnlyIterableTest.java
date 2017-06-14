package sonumina.collections;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class ReadOnlyIterableTest
{
	@Test
	public void testWhetherReadOnlyIterableThrowsException()
	{
		/* Arrays.asList() will result in unmodifiable iterators
		 * so we construct a proper ArrayList from the result.
		 */
		List<Integer> list = new ArrayList<>(Arrays.asList(1,2,3,4));

		/* Remove first one */
		Iterator<Integer> iterator = list.iterator();
		iterator.next();
		iterator.remove();

		iterator = new ReadOnlyIterable<>(list).iterator();
		iterator.next();
		try
		{
			iterator.remove();
			fail("Exception UnsupportedOperationException not thrown!");
		} catch(UnsupportedOperationException ex)
		{
		}
	}
}
