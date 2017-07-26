package ontologizer.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

public class ByteStringTest
{
	private static final byte PIPE = (byte)'|';

	@Test
	public void testToString()
	{
		byte [] txt = new byte[]{'h', 'e', 'l', 'l', 'o'};
		assertEquals("hello", ByteString.toString(new ByteString(txt)));
	}

	@Test
	public void testToStringUTF8()
	{
		byte [] txt = new byte[]{'h', 'a', 'l', 'l', (byte)0xc3, (byte)0xb6};
		assertEquals("hall\u00f6", ByteString.toString(new ByteString(txt)));
	}

	@Test
	public void testIdempotency()
	{
		String txt = "hall\u00f6";
		assertEquals(txt, new ByteString(txt).toString(), txt);
	}

	@Test
	public void testParseInteger()
	{
		assertEquals(1234, ByteString.parseFirstInt(new ByteString("1234")));
		assertEquals(1234, ByteString.parseFirstInt(new ByteString("001234")));
		assertEquals(4500, ByteString.parseFirstInt(new ByteString("4500")));
		assertEquals(4500, ByteString.parseFirstInt(new ByteString("0000000004500")));
		assertEquals(1234, ByteString.parseFirstInt(new ByteString("ssss1234ssss")));
		assertEquals(1234, ByteString.parseFirstInt(new ByteString("ssss001234ssss")));

		try
		{
			ByteString.parseFirstInt(new ByteString("sswwscs"));
			assertTrue(false);
		} catch (NumberFormatException ex) {}
	}

	@Test
	public void testByteParseInteger()
	{
		byte [] buf = "xx1234xx".getBytes();
		assertEquals(1234, ByteString.parseFirstInt(buf, 0, buf.length));
		assertEquals(123, ByteString.parseFirstInt(buf, 0, 5));
		assertEquals(23, ByteString.parseFirstInt(buf, 3, 2));
	}

	@Test
	public void testSubstring()
	{
		assertEquals("TEst",new ByteString("TestTEstTest").substring(4,8).toString());
	}

	@Test
	public void testSplit()
	{
		ByteString [] split = new ByteString("str1|str2|str3").split(PIPE);
		Assert.assertEquals(3,split.length);
		Assert.assertEquals("str1", split[0].toString());
		Assert.assertEquals("str2", split[1].toString());
		Assert.assertEquals("str3", split[2].toString());

		split = new ByteString("str1|str2|str3|").split(PIPE);
		Assert.assertEquals(4,split.length);
		Assert.assertEquals("str1", split[0].toString());
		Assert.assertEquals("str2", split[1].toString());
		Assert.assertEquals("str3", split[2].toString());
		Assert.assertEquals("", split[3].toString());

		split = new ByteString("str1").split(PIPE);
		Assert.assertEquals(1,split.length);
		Assert.assertEquals("str1", split[0].toString());

		split = new ByteString("str1||str3").split(PIPE);
		Assert.assertEquals(3,split.length);
		Assert.assertEquals("str1", split[0].toString());
		Assert.assertEquals("", split[1].toString());
		Assert.assertEquals("str3", split[2].toString());
	}

	@Test
	public void testReplace()
	{
		ByteString oldStr = new ByteString("positively_regulates");
		ByteString newStr = oldStr.replace('_', ' ');
		Assert.assertEquals("positively regulates", newStr.toString());
	}
}
