package ontologizer.enumeration;

import java.util.ArrayList;
import java.util.List;

import ontologizer.types.ByteString;

/**
 * Instanced of this class represent items that are annotated to
 * the same term without any reference to that term.
 *
 * @author Sebastian Bauer
 */
public class TermAnnotations
{
	/** List of directly annotated genes TODO: Make private */
	public List<ByteString> directAnnotated = new ArrayList<ByteString>();

	/** List of genes annotated at whole TODO: Make private */
	public List<ByteString> totalAnnotated = new ArrayList<ByteString>();

	public int directAnnotatedCount()
	{
		return directAnnotated.size();
	}

	public int totalAnnotatedCount()
	{
		return totalAnnotated.size();
	}
}
