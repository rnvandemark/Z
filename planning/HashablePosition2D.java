package planning;

import actors.Position2D;

/**
 * A version of a two-dimensional position where the hash code is generated
 * from the x and y coordinate values, relative to the parent coordinate frame
 * that this position resides in. The hash value is 0 for a position of
 * (0, 0), and it increases by one as the x coordinate value increases by one,
 * and it increases by the width of the parent coordinate frame as the y
 * coordinate value increases by one.
 */
public class HashablePosition2D {
	
	/**
	 * The width of the two-dimensional coordinate frame that this position
	 * resides in. This is made to be final so that the coordinate frame in
	 * context is made constant.
	 */
	private final int parentWidth;
	
	/**
	 * A position in the coordinate frame. This is made public to be
	 * modifiable, but the coordinate frame for the position should be in
	 * the same context for the lifetime of this object.
	 */
	public Position2D position;
	
	/**
	 * The sole constructor.
	 * Accepts the width of the parent coordinate frame, as well as an
	 * initial x and y coordinate value pair to initialize the position.
	 * @param parentWidth The width of the two-dimensional coordinate frame
	 * that this position resides in.
	 * @param px The x value for the position.
	 * @param py The y value for the position.
	 */
	public HashablePosition2D(int parentWidth, double px, double py) {
		this.parentWidth  = parentWidth;
		this.position     = new Position2D(px, py);
	}
	
	/**
	 * Override from the {@link java.lang.Object} method.
	 */
	@Override
	public int hashCode() {
		return (int)((this.position.y * this.parentWidth) + this.position.x);
	}

	/**
	 * Override from the {@link java.lang.Object} method.
	 */
	@Override
	public String toString() {
		return String.format("{%d: %s}", this.hashCode(), this.position.toString());
	}
}