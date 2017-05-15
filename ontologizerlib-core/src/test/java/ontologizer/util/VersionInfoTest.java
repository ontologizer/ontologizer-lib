package ontologizer.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ontologizer.util.VersionInfo;

public class VersionInfoTest
{
	@Test
	public void testGetVersion()
	{
		/* TODO: Test result via properties */
		assertNotNull(VersionInfo.getVersion());
	}
}
