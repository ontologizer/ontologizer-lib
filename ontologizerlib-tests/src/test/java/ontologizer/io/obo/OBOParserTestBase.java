package ontologizer.io.obo;

import java.io.IOException;

import ontologizer.TestBase;
import ontologizer.io.ParserFileInput;

/**
 * A special test base that involves the OBOParser.
 *
 * @author Sebastian Bauer
 */
public class OBOParserTestBase extends TestBase
{
	/**
	 * Return an obo parser suitable to parse the comment of the current test.
	 *
	 * @param options the options submitted to the parser.
	 *
	 * @return the obo parser
	 *
	 * @throws IOException
	 */
	protected OBOParser getTestCommentAsOBOParser(int options) throws IOException
	{
		return new OBOParser(new ParserFileInput(getTestCommentAsPath(".obo")), options);
	}

	/**
	 * Returns an obo parser that has already parses the comment of the current
	 * test.
	 *
	 * @param options the obo parser options to customize the parser's behaviour.
	 * @return the obo parser
	 *
	 * @throws IOException
	 * @throws OBOParserException
	 */
	protected OBOParser parseTestComment(int options) throws IOException, OBOParserException
	{
		OBOParser oboParser = getTestCommentAsOBOParser(options);
		oboParser.doParse();
		return oboParser;
	}

	/**
	 * Returns an obo parser that has already parsed the comment of the current
	 * test.
	 *
	 * @return the obo parser
	 *
	 * @throws IOException
	 * @throws OBOParserException
	 */
	protected OBOParser parseTestComment() throws IOException, OBOParserException
	{
		return parseTestComment(0);
	}
}
