package planning;

import java.util.Collection;

import game.MapData;

/**
 * A version of a planner that utilizes a discretized map to run Dijkstra's
 * algorithm with a straight-line distance heuristic, so the algorithm favors
 * next visiting traversable elements that are spatially closer in the
 * environment's plane. This is commonly called A*. This class is not meant to
 * be built off of, hence the final modifier. Any other derivations should be
 * built off of {@link planning.AbstractGridDijkstraPlanner}, like this class was.
 */
public final class GridAStarPlanner extends AbstractGridDijkstraPlanner {
	
	/**
	 * The sole constructor.
	 * Takes the discretization ratio to build the underlying discretized map,
	 * the map data to build the discretized map, and the displacement
	 * threshold that describes when to stop trying to salvage a path, which are
	 * all passed onto the super constructor.
	 * @param discretizationRatio The ratio of discretization.
	 * @param initialMapData The data to use to build the discretized map.
	 * @param salvageThreshold The threshold used for the salvaging routine.
	 */
	public GridAStarPlanner(
			int discretizationRatio, MapData initialMapData, double salvageThreshold) {
		super(discretizationRatio, initialMapData, salvageThreshold);
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected boolean setInitialDistances(
			HashablePosition2D start,
			HashablePosition2D goal,
			Collection<HashablePosition2D> elements) {
		for (HashablePosition2D p : elements) {
			this.elementMap.setTentativeDistanceFor(
				p,
				p.equals(start) ? 0.0 : Double.POSITIVE_INFINITY
			);
			
			this.elementMap.setHeuristicWeightFor(
				p,
				p.equals(start)
					? this.calcHeuristicWeightFor(p, goal)
					: Double.POSITIVE_INFINITY
			);
		}
		
		return true;
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected double calcHeuristicWeightFor(HashablePosition2D element, HashablePosition2D goal) {
		return element.position.distanceBetween(goal.position);
	}
}