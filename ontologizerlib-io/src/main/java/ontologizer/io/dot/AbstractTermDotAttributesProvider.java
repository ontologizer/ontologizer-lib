/*
 * Created on 16.02.2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ontologizer.io.dot;

import ontologizer.ontology.TermID;

/**
 * Default implementation of the interface providing attributes for dot graph.
 *
 * @author Sebastian Bauer
 */
public class AbstractTermDotAttributesProvider implements ITermDotAttributesProvider
{
	/** Returns the dot attributes for the given term. */
	public  String getDotNodeAttributes(TermID id)
	{
		return null;
	}

	public String getDotEdgeAttributes(TermID id1, TermID id2)
	{
		return null;
	}
}
