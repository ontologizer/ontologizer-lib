package sonumina.math.graph;

import java.util.HashSet;
import java.util.Iterator;

/**
 * This class encompasses certain algorithms.
 *
 * @author Sebastian Köhler, Sebastian Bauer
 */
public class Algorithms
{
	/**
	 * Returns clustering coefficient as described in the paper
	 * by Watts and Strogatz (1998 Nature).
	 *
	 * @param v
	 * @return The clustering coefficient of the vertex.
	 */
	public static <V> double getClusteringCoefficient(IDirectedGraph<V> g, V v)
	{
		/*
		 * Determine the neighborhood of provided vertex.
		 */
		HashSet<V> neighborhood  = new HashSet<V>();

		Iterator<V> neighborsIt = g.getChildNodes(v).iterator();
		while (neighborsIt.hasNext())
			neighborhood.add( neighborsIt.next() );

		int numberNeighbors = neighborhood.size();

		/*
		 * For isolated nodes we set the CC to 0. This is kind of standard,
		 * even though in Brandes & Erlebach (Network Analysis) 2005 these
		 * are set to one. A discussion of this can be found in:
		 * Marcus Kaiser; New Journal of Physics (2008); "Mean clustering coefficients:
		 * the role of isolated nodes and leafs on clustering measures for small-world
		 * networks"
		 */
		if (numberNeighbors < 2)
			return 0;
		/*
		 * Now determine the number of links
		 * inside the neighborhood.
		 */
		int numEdgesNeighborhood = 0;
		for (V neighbor : neighborhood)
		{
			/*
			 * Get all nodes reachable from this node
			 */
			Iterator<V> neighborsNeighborsIt = g.getChildNodes(neighbor).iterator();
			while (neighborsNeighborsIt.hasNext())
			{
				V neighborsNeighbor = neighborsNeighborsIt.next();

				if (neighborsNeighbor == v)
					continue;
				if (neighborsNeighbor == neighbor)
					continue;

				if (neighborhood.contains(neighborsNeighbor))
					++numEdgesNeighborhood;
			}
		}

		/*
		 * Calculate clustering coefficient
		 */
		double denominator 	= (double)(numberNeighbors*(numberNeighbors-1));
		double C 			= (double)numEdgesNeighborhood / denominator;
		return C;
	}
}
