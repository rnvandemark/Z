package planning;

import java.util.ArrayList;
import java.util.Collection;

import actors.Position2D;
import game.MapData;
import util.Couple;

/**
 * An abstract implementation of a path planner that uses a grid with cells to
 * perform Dijkstra's algorithm. This level of abstraction exists so that code
 * to specify how a discretized map can be used does not have to be rewritten.
 * The only functionality not specified here is for calculating the heuristic
 * weight for an element, as well as initializing the tentative distance and
 * heuristic weight maps.
 */
public abstract class AbstractGridDijkstraPlanner
	extends AbstractDijkstraPlanner<HashablePosition2D> {
	
	/**
	 * The x and y translations that describe the difference in x and y for
	 * neighboring cells. The first eight describe the change in the x
	 * coordinate from top-left to bottom-right (left to right), and the last
	 * eight describe the change in the y coordinate from top-left to bottom-
	 * right (left to right).
	 */
	private final static int[] NEIGHBOR_TRANSLATIONS = {
		-1, +0, +1, -1, +1, -1, +0, +1,
		-1, -1, -1, +0, +0, +1, +1, +1
	};
	
	/**
	 * A grid of two-dimensional positions with specific constraints for
	 * their hashing. Each cell in the underlying grid has a position in this
	 * array, occupied or not. A position that cannot be traversable will still
	 * have a position in this array with a normal hash code, the planning
	 * algorithm is responsible for knowing which positions are valid.
	 */
	protected HashablePosition2D[][] positionsArray;
	
	/**
	 * The sole constructor.
	 * Takes the discretization ratio to build the underlying discretized map,
	 * the map data to build the discretized map, and the displacement
	 * threshold that describes when to stop trying to salvage a path. This also
	 * builds the positions array from the underlying discretized map.
	 * @param discretizationRatio The ratio of discretization.
	 * @param initialMapData The data to use to build the discretized map.
	 * @param salvageThreshold The threshold used for the salvaging routine.
	 */
	public AbstractGridDijkstraPlanner(
			int discretizationRatio,
			MapData initialMapData,
			double salvageThreshold) {
		super(new DiscretizedMap(discretizationRatio), initialMapData, salvageThreshold);
		
		DiscretizedMap discMap = (DiscretizedMap)this.mapRepresentation;
		int mh = discMap.getHeight(), mw = discMap.getWidth();
		this.positionsArray = new HashablePosition2D[mw][mh];
		for (int x = 0; x < mw; x++)
			for (int y = 0; y < mh; y++)
				this.positionsArray[x][y] = new HashablePosition2D(mw, x, y);
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected boolean pathIsClear(Position2D start, Position2D goal) {
		double rdr = 1.0 / ((DiscretizedMap)this.mapRepresentation).getDiscretizationRatio();
		return ((DiscretizedMap)this.mapRepresentation).pathIsClear(
			start.scaled(rdr, rdr), goal.scaled(rdr, rdr)
		).first;
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected Position2D getPositionOf(HashablePosition2D element) {
		int dr = ((DiscretizedMap)this.mapRepresentation).getDiscretizationRatio();
		return element.position.scaled(dr, dr);
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected Collection<HashablePosition2D> getTraversalMediumCollection() {
		DiscretizedMap discMap = (DiscretizedMap)this.mapRepresentation;
		ArrayList<HashablePosition2D> collection
			= new ArrayList<HashablePosition2D>(discMap.getWidth() * discMap.getHeight());
		
		for (HashablePosition2D[] arr : positionsArray)
			for (HashablePosition2D p : arr)
				collection.add(p);
		
		return collection;
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected Couple<HashablePosition2D, HashablePosition2D>
			prepareGeneration(Position2D start, Position2D goal) {
		double rdr = 1.0 / ((DiscretizedMap)this.mapRepresentation).getDiscretizationRatio();
		Position2D s = start.scaled(rdr, rdr), g = goal.scaled(rdr, rdr);
		return new Couple<HashablePosition2D, HashablePosition2D>(
			this.positionsArray[(int)s.x][(int)s.y],
			this.positionsArray[(int)g.x][(int)g.y]
		);
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected Collection<HashablePosition2D> getNeighborsFor(HashablePosition2D element) {
		DiscretizedMap discMap = (DiscretizedMap)this.mapRepresentation;
		
		ArrayList<HashablePosition2D> neighbors = new ArrayList<HashablePosition2D>();
		
		int sx = (int)element.position.x, sy = (int)element.position.y, cx, cy;
		for (int i = 0; i < 8; i++) {
			cx = sx + NEIGHBOR_TRANSLATIONS[i];
			cy = sy + NEIGHBOR_TRANSLATIONS[i + 8];
			if (discMap.isInBounds(cx, cy)) {
				if (discMap.openAt(cx, cy)) {
					neighbors.add(this.positionsArray[cx][cy]);
				}
			}
		}
		
		return neighbors;
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected double distanceBetweenNeighbors(HashablePosition2D source, HashablePosition2D destination) {
		return source.position.distanceBetween(destination.position);
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected boolean closeGeneration(HashablePosition2D start, HashablePosition2D goal) {
		return true;
	}
}