package planning;

import java.util.LinkedList;

import actors.Position2D;
import actors.Velocity2D;

/**
 * A container for a linked list of 2D positions representing a path for the actor to follow.
 */
public class PlannedPath extends LinkedList<Position2D> {
	
	/**
	 * A generated serial version number for this serializable object.
	 */
	private static final long serialVersionUID = -6035395736846205994L;
	
	/**
	 * Given some small threshold, decide whether or not a provided 2D position is considered equivalent
	 * to the next objective position.
	 * @param current The position that should be approaching the next position in the path.
	 * @param epsilon Some small threshold, in pixels, that can be considered "at" the destination.
	 * @return Whether or not the position can be considered "at" the next position.
	 */
	public boolean atNextPosition(Position2D current, double epsilon) {
		if (this.size() > 0) {
			return this.getFirst().distanceBetween(current) <= epsilon;
		} else {
			return false;
		}
	}
	
	/**
	 * Remove the first element of this linked list. This implies the next objective position has been reached.
	 * @return The position attached to the first node of the list.
	 */
	public Position2D consumeNext() {
		return this.pollFirst();
	}
	
	/**
	 * Calculate the velocity between the current position of some actor and the next objective position
	 * of this path.
	 * @param current The current position to plan the next velocity from.
	 * @param speed The net speed of the calculated velocity.
	 * @return The 2D velocity to get to the next point.
	 */
	public Velocity2D nextMovement(Position2D current, double speed) {
		if (this.size() > 0) {
			Position2D next = this.getFirst();
			return Velocity2D.generateFromVector(
				Math.atan2(next.y - current.y, next.x - current.x),
				speed
			);
		} else {
			return new Velocity2D();
		}
	}
	
	/**
	 * Override from {@link java.lang.Object} method.
	 */
	@Override
	public String toString() {
		String s = "";
		
		if (!this.isEmpty()) {
			s = this.getFirst().toString();
			for (int i = 1; i < this.size(); i++) {
				s += (", " + this.get(i).toString());
			}
		}
		
		return String.format("[%s]", s);
	}
}