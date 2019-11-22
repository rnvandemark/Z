package planning;

import game.MapData;

/**
 * An abstract class that aims to somehow represent an RGB image of a map in a
 * way that is computationally efficient for the planner that uses it, such as
 * a discretized version or a graph of some sort.
 */
public abstract class MapRepresentation {
	
	/**
	 * Given a map's data, build this other representation.
	 * @param mapData The data for the map to represent differently.
	 * @return Whether or not the new representation was successfully built.
	 */
	public abstract boolean build(MapData mapData);
}