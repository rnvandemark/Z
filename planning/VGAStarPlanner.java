package planning;

import game.MapData;
import planning.VisibilityGraph.VGNode;

/**
 * A version of a planner that utilizes a visibility graph to run
 * Dijkstra's algorithm with a straight-line distance heuristic, so
 * the algorithm favors next visiting traversable elements that are
 * spatially closer in the environment's plane. This is commonly
 * called A*. This class is not meant to be built off of, hence the
 * final modifier. Any other derivations should be built off of
 * {@link planning.AbstractVGDijkstraPlanner}, like this class was.
 */
public final class VGAStarPlanner extends AbstractVGDijkstraPlanner {
	
	/**
	 * The sole constructor.
	 * Takes the arguments necessary to build a visibility graph and
	 * initialize it with a map's data.
	 * @param discretizationRatio The visibility graph's discretization.
	 * @param cleanlinessThreshold The visibility graph's degree of
	 * redundancy removal effort.
	 * @param initialMapData The data to first build the visibility
	 * graph with.
	 */
	public VGAStarPlanner(
			int discretizationRatio,
			double cleanlinessThreshold,
			MapData initialMapData) {
		super(discretizationRatio, cleanlinessThreshold, initialMapData);
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected boolean setInitialDistances(VGNode start, VGNode goal) {
		for (VGNode e : this.getTraversalMediumCollection()) {
			this.elementMap.setTentativeDistanceFor(
				e,
				e.equals(start) ? 0.0 : Double.POSITIVE_INFINITY
			);
			
			this.elementMap.setHeuristicWeightFor(
				e,
				e.equals(start)
					? this.calcHeuristicWeightFor(e, goal)
					: Double.POSITIVE_INFINITY
			);
		}
		
		return true;
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected double calcHeuristicWeightFor(VGNode element, VGNode goal) {
		return element.getPosition().distanceBetween(goal.getPosition());
	}
}