package planning;

import actors.Position2D;
import game.MapData;

public abstract class Planner {
	
	protected MapRepresentation mapRepresentation;
	
	public Planner(MapRepresentation mapRepresentation, MapData initalMapData) {
		this.mapRepresentation = mapRepresentation;
		if (!this.buildRepresentation(initalMapData)) {
			throw new RuntimeException("Error building initial map representation.");
		}
	}
	
	public boolean buildRepresentation(MapData mapData) {
		return this.mapRepresentation.build(mapData);
	}
	
	/**
	 * Given a start position and an objective position, attempt to generate a path between the
	 * two in the available map representation.
	 * @param start The starting position (x, y coordinate-pixel-pair) in the map.
	 * @param goal The objective position (x, y coordinate-pixel-pair) in the map.
	 * @return The generated path of linked positions, or null if the path was deemed impossible.
	 */
	public abstract PlannedPath generatePath(Position2D start, Position2D goal);
	
	/**
	 * Given a path that was planned for something that closely resembled a new pair of start
	 * and objective positions, try to salvage the existing path (meaning perhaps only the
	 * start and goal positions are modified if simple enough, or perhaps some points are added
	 * to validate the path).
	 * @param old The previously generated planned path, which will be modified if salvaging is
	 * successful.
	 * @param newStart The new starting position (x, y coordinate-pixel-pair) in the map.
	 * @param newGoal The new objective position (x, y coordinate-pixel-pair) in the map.
	 * @return Whether or not the path could be salvaged and modified. If true, the old planned
	 * path will have been modified. If false, then the old path will not have changed, and a new
	 * path should try and be generated.
	 */
	public abstract boolean salvagePath(PlannedPath old, Position2D newStart, Position2D newGoal);
}