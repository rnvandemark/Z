package planning;

import java.util.ArrayList;
import java.util.Collection;

import actors.Position2D;
import game.MapData;
import util.Couple;

public abstract class AbstractGridDijkstraPlanner
	extends AbstractDijkstraPlanner<Position2D> {
	
	protected ArrayList<Position2D> positionsArray;
	
	public AbstractGridDijkstraPlanner(int discretizationRatio, MapData initialMapData) {
		super(new DiscretizedMap(discretizationRatio), initialMapData);
		
		DiscretizedMap discMap = (DiscretizedMap)this.mapRepresentation;
		int mh = discMap.getHeight(), mw = discMap.getWidth();
		this.positionsArray = new ArrayList<Position2D>(mw * mh);
		for (int y = 0; y < mh; y++)
			for (int x = 0; x < mw; x++)
				this.positionsArray.add(new Position2D(x, y));
	}
	
	@Override
	protected Position2D getPositionOf(Position2D element) {
		return element;
	}
	
	@Override
	protected Collection<Position2D> getTraversalMediumCollection() {
		return this.positionsArray;
	}
	
	@Override
	protected Couple<Position2D, Position2D> prepareGeneration(Position2D start, Position2D goal) {
		DiscretizedMap discMap = (DiscretizedMap)this.mapRepresentation;
		int dr = discMap.getDiscretizationRatio(), mw = discMap.getWidth();
		int sx = (int)start.x / dr, sy = (int)start.y / dr;
		int gx = (int)goal.x / dr, gy = (int)goal.y / dr;
		return new Couple<Position2D, Position2D>(
			this.positionsArray.get((sy * mw) + sx),
			this.positionsArray.get((gy * mw) + gx)
		);
	}
	
	@Override
	protected Collection<Position2D> getNeighborsFor(Position2D element) {
		DiscretizedMap discMap = (DiscretizedMap)this.mapRepresentation;
		int dr = discMap.getDiscretizationRatio(), mw = discMap.getWidth();
		
		ArrayList<Position2D> neighbors = new ArrayList<Position2D>();
		
		int[] arrDX = new int[] {
			-1, +0, +1, -1,
			+1, -1, +0, +1
		};
		int[] arrDY = new int[] {
			-1, -1, -1, +0,
			+0, +1, +1, +1
		};

		int sx = (int)element.x / dr, sy = (int)element.y / dr, cx, cy;
		for (int x : arrDX) {
			for (int y : arrDY) {
				cx = sx + x;
				cy = sy + y;
				if (discMap.isInBounds(cx, cy)) {
					if (discMap.openAt(cx, cy)) {
						neighbors.add(positionsArray.get((cy * mw) + cx));
					}
				}
			}
		}
		
		return neighbors;
	}
	
	@Override
	protected double distanceBetweenNeighbors(Position2D source, Position2D destination) {
		return source.distanceBetween(destination);
	}
	
	@Override
	protected boolean closeGeneration(Position2D start, Position2D goal) {
		return true;
	}
}