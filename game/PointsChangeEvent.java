package game;

/**
 * A simple class to encapsulate an event created for when a player's point count updates.
 */
public class PointsChangeEvent {
	
	/**
	 * The updated point count.
	 */
	private int pointCount;
	
	/**
	 * The sole constructor.
	 * Takes the value that the point count has been updated to.
	 * @param pointCount
	 */
	public PointsChangeEvent(int pointCount) {
		this.pointCount = pointCount;
	}
	
	/**
	 * Getter for the point count;
	 * @return The wave point count.
	 */
	public int getPointCount() {
		return this.pointCount;
	}
}