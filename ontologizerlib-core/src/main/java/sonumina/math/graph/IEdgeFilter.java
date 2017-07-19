package sonumina.math.graph;

public interface IEdgeFilter<ED>
{
	boolean leaveOut(ED ed);
}
