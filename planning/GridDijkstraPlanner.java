package planning;

import actors.Position2D;
import game.MapData;

public final class GridDijkstraPlanner extends AbstractGridDijkstraPlanner {
	
	public GridDijkstraPlanner(int discretizationRatio, MapData initialMapData) {
		super(discretizationRatio, initialMapData);
	}

	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected boolean setInitialDistances(Position2D start, Position2D goal) {
		for (Position2D p : this.getTraversalMediumCollection()) {
			this.elementMap.setTentativeDistanceFor(
				p,
				p.equals(start) ? 0.0 : Double.POSITIVE_INFINITY
			);
			
			this.elementMap.setHeuristicWeightFor(
				p,
				p.equals(start) ? 0.0 : Double.POSITIVE_INFINITY
			);
		}
		
		return true;
	}

	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected double calcHeuristicWeightFor(Position2D element, Position2D goal) {
		return 0.0;
	}
}