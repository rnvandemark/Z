package planning;

import actors.Position2D;
import game.MapData;

public final class GridAStarPlanner extends AbstractGridDijkstraPlanner {
	
	public GridAStarPlanner(int discretizationRatio, MapData initialMapData) {
		super(discretizationRatio, initialMapData);
	}
	
	@Override
	protected boolean setInitialDistances(Position2D start, Position2D goal) {
		for (Position2D p : this.getTraversalMediumCollection()) {
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
	
	@Override
	protected double calcHeuristicWeightFor(Position2D element, Position2D goal) {
		return element.distanceBetween(goal);
	}
}