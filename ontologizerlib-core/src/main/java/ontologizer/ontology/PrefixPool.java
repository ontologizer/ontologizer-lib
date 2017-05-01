package ontologizer.ontology;

import java.io.Serializable;
import sonumina.collections.ReferencePool;

/**
 * A common pool for Prefix instances.
 *
 * @author Sebastian Bauer
 */
public class PrefixPool implements Serializable
{
	private static final long serialVersionUID = 1L;

	private ReferencePool<Prefix> prefixPool = new ReferencePool<Prefix>();

	public Prefix map(Prefix ref)
	{
		return prefixPool.map(ref);
	}
}

