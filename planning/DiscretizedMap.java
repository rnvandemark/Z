package planning;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.AbstractMap.SimpleEntry;

import actors.Position2D;
import game.MapData;

/**
 * A type of map representation that takes the black and white data of a map and creates a
 * discretized version of it, given a ratio of discretization. The result is broken out into
 * a doubly-scripted array of cells simply labelled as "occupied" (not available for an actor
 * to reside in) or otherwise.
 */
public class DiscretizedMap extends MapRepresentation {
	
	/**
	 * When checking for a straight line path between two points, it's necessary to create
	 * discretized segments of the line and check if those points are valid positions for
	 * actors to inhabit. This value if the length of those discretized lines.
	 */
	private final static double DEFAULT_DISC_DISTANCE_RATIO = 0.5;
	
	/**
	 * The ratio of discretization. Given an input image of A pixels wide and B pixels high,
	 * if the discretization ratio D helps to create a new image that is A/D pixels wide and
	 * B/D pixels high. A D by D area of pixels is compressed into one discretized cell, and
	 * if a single cell is the original image is labeled as uninhabitable, then the resulting
	 * discretized cell is also uninhabitable.
	 */
	private int discretizationRatio;
	
	/**
	 * The generated cells for the discretized representation of an input map. A value of "true"
	 * means that the cell is occupied and cannot be inhabited by an actor.
	 */
	private boolean[][] cells;
	
	/**
	 * The sole constructor.
	 * Simply takes a discretization ratio and ensures the cells grid is null for now.
	 * @param discretizationRatio The ratio of discretization.
	 */
	public DiscretizedMap(int discretizationRatio) {
		this.discretizationRatio = discretizationRatio;
		this.cells               = null;
	}
	
	/**
	 * Checks whether or not the straight line path between the start and goal position is
	 * completely clear of obstacles.
	 * @param start The start position of the map.
	 * @param goal The goal position of the map.
	 * @param exclusionThreshold The distance away from the start and goal positions that are
	 * ignored when checking for obstacle collisions.
	 * @param stepDistance The distance to interpolate by for each check.
	 * @return An effective tuple of size two, the first value being whether or not the entire
	 * path is clear, the second being the furthest valid position. If the path is completely
	 * clear, the second position is equivalent to the goal position. If not, the last valid
	 * point is returned.
	 */
	public SimpleEntry<Boolean, Position2D> pathIsClear(
			Position2D start, Position2D goal, double exclusionThreshold, double stepDistance
	) {
		double stepAngle     = start.angleBetween(goal);
		double totalDistance = start.distanceBetween(goal);
		
		double displacement     = 0.0;
		int exitStatus          = 0;
		Position2D stepPosition = new Position2D(-1, -1);
		
		int pxi, pyi;
		Position2D furthestValid = null;
		
		while (exitStatus == 0) {
			if (displacement >= totalDistance) {
				displacement = totalDistance;
				exitStatus   = 2;
			}
			
			stepPosition.set(
				start.x + (displacement * Math.cos(stepAngle)),
				start.y + (displacement * Math.sin(stepAngle))
			);
			
			if ((exclusionThreshold <= 0)
				|| ((!start.equals(stepPosition, exclusionThreshold))
					&& (!goal.equals(stepPosition, exclusionThreshold)))
			) {
				pxi = (int)Math.round(stepPosition.x);
				pyi = (int)Math.round(stepPosition.y);
				
				if (!this.openAt(pxi, pyi)) {
					exitStatus = 1;
				} else {
					if (furthestValid == null)
						furthestValid = new Position2D(pxi, pyi);
					else
						furthestValid.set(pxi, pyi);
				}
			}
			
			displacement += stepDistance;
		}
		
		if (exitStatus == 2) {
			if (furthestValid == null)
				furthestValid = new Position2D(goal);
			else
				furthestValid.set(goal);
		}
		
		return new SimpleEntry<Boolean, Position2D>(exitStatus == 2, furthestValid);
	}
	
	/**
	 * Checks whether or not the straight line path between the start and goal position is
	 * completely clear of obstacles, with a default discretization distance.
	 * @param start The start position of the map.
	 * @param goal The goal position of the map.
	 * @param exclusionThreshold The distance away from the start and goal positions that are
	 * ignored when checking for obstacle collisions.
	 * @return An effective tuple of size two, the first value being whether or not the entire
	 * path is clear, the second being the furthest valid position. If the path is completely
	 * clear, the second position is equivalent to the goal position. If not, the last valid
	 * point is returned.
	 */
	public SimpleEntry<Boolean, Position2D> pathIsClear(
			Position2D start, Position2D goal, double exclusionThreshold) {
		return this.pathIsClear(
			start,
			goal,
			exclusionThreshold,
			DEFAULT_DISC_DISTANCE_RATIO * this.discretizationRatio
		);
	}
	
	/**
	 * Checks whether or not the straight line path between the start and goal position is
	 * completely clear of obstacles, with a default discretization distance, and ignoring
	 * an exclusion threshold.
	 * @param start The start position of the map.
	 * @param goal The goal position of the map.
	 * @return An effective tuple of size two, the first value being whether or not the entire
	 * path is clear, the second being the furthest valid position. If the path is completely
	 * clear, the second position is equivalent to the goal position. If not, the last valid
	 * point is returned.
	 */
	public SimpleEntry<Boolean, Position2D> pathIsClear(Position2D start, Position2D goal) {
		return this.pathIsClear(start, goal, -1);
	}
	
	/**
	 * Getter for whether or not a cell at the discretized coordinate pair x and y is habitable
	 * by an actor.
	 * @param x The x coordinate of the discretized map.
	 * @param y The y coordinate of the discretized map.
	 * @return True if the cell at this position can be inhabited by an actor, false otherwise.
	 */
	public boolean openAt(int x, int y) {
		return !cells[x][y];
	}
	
	/**
	 * Getter for whether or not a cell at the coordinate pair x and y of the original map image
	 * is habitable by an actor.
	 * @param x The x coordinate of the original map.
	 * @param y The y coordinate of the original map.
	 * @return True if the cell at this position can be inhabited by an actor, false otherwise.
	 */
	public boolean openAtOriginal(int x, int y) {
		return this.openAt(x / this.discretizationRatio, y / this.discretizationRatio);
	}
	
	/**
	 * Getter for the width of the discretized map.
	 * @return The width of the discretized map.
	 */
	public int getWidth() {
		return this.cells.length;
	}

	/**
	 * Getter for the height of the discretized map.
	 * @return The height of the discretized map.
	 */
	public int getHeight() {
		return this.getWidth() == 0 ? 0 : this.cells[0].length;
	}
	
	/**
	 * Getter for the discretization ratio.
	 * @return The discretization ratio.
	 */
	public int getDiscretizationRatio() {
		return this.discretizationRatio;
	}
	
	/**
	 * Override from the {@link planning.MapRepresentation} method.
	 */
	@Override
	public boolean build(MapData mapData) {
		BufferedImage initialImage = mapData.getImage();
		int dr = this.discretizationRatio;
		int cw = (int)Math.ceil(initialImage.getWidth()  / (double)dr);
		int ch = (int)Math.ceil(initialImage.getHeight() / (double)dr);
		this.cells = new boolean[cw][ch];
		
		int blackRGB = Color.BLACK.getRGB();
		for (int x = 0; x < cw; x++) {
			for (int y = 0; y < ch; y++) {
				boolean occupied = false;
				
				for (int i = 0; (i < this.discretizationRatio) && (!occupied); i++)
					for (int j = 0; (j < this.discretizationRatio) && (!occupied); j++)
						occupied = initialImage.getRGB((x * dr) + i, (y * dr) + j) == blackRGB;
				
				this.cells[x][y] = occupied;
			}
		}
		
		return true;
	}
}