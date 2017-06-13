package sonumina.math.graph;

import java.util.List;

/**
 * Functional interface that is used for merging edge data.
 *
 * @author Sebastian Bauer
 */
public interface IEdgeDataMerger<ED>
{
	public ED merge(List<ED> data);
}
