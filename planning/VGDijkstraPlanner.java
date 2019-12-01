package planning;

import java.util.Collection;

import game.MapData;
import planning.VisibilityGraph.VGNode;

/**
 * A version of a planner that utilizes a visibility graph to run
 * Dijkstra's algorithm with no heuristic (to be more specific, with
 * no weights guiding where the algorithm visits next in the traversable
 * medium). This class is not meant to be built off of, hence the final
 * modifier. Any other derivations should be built off of 
 * {@link planning.AbstractVGDijkstraPlanner}, like this class was.
 */
public final class VGDijkstraPlanner extends AbstractVGDijkstraPlanner {
	
	/**
	 * The sole constructor.
	 * Takes the arguments necessary to build a visibility graph and
	 * initialize it with a map's data.
	 * @param discretizationRatio The visibility graph's discretization.
	 * @param cleanlinessThreshold The visibility graph's degree of
	 * redundancy removal effort.
	 * @param initialMapData The data to first build the visibility
	 * graph with.
	 * @param salvageThreshold The threshold used for the salvaging routine.
	 */
	public VGDijkstraPlanner(
			int discretizationRatio,
			double cleanlinessThreshold,
			MapData initialMapData,
			double salvageThreshold) {
		super(
			discretizationRatio,
			cleanlinessThreshold,
			initialMapData,
			salvageThreshold
		);
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected boolean setInitialDistances(
			VGNode start, VGNode goal, Collection<VGNode> elements) {
		for (VGNode e : elements) {
			this.elementMap.setTentativeDistanceFor(
				e,
				e.equals(start) ? 0.0 : Double.POSITIVE_INFINITY
			);
			
			this.elementMap.setHeuristicWeightFor(
				e,
				e.equals(start) ? 0.0 : Double.POSITIVE_INFINITY
			);
		}
		
		return true;
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected double calcHeuristicWeightFor(VGNode element, VGNode goal) {
		return 0.0;
	}
}