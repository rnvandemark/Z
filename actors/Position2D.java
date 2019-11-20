package actors;

/*
 * A simple two-dimensional point representation with floating-point precision, with required
 * coordinates in the x and y axes.
 */
public class Position2D extends Kinematic2D {
	
	/**
	 * The default constructor.
	 * Defaults the x and y position each to 0.
	 */
	public Position2D() {
		super(0.0, 0.0);
	}
	
	/**
	 * Another constructor.
	 * Specifies both the x and y positions.
	 * @param x The new position in the x-axis.
	 * @param y The new position in the y-axis.
	 */
	public Position2D(double x, double y) {
		super(x, y);
	}
	
	/**
	 * Calculate the linear distance between this point and another.
	 * @param other The other 2D position.
	 * @return The distance between this and the other point.
	 */
	public double distanceBetween(Position2D other) {
		double dx = other.x - this.x;
		double dy = other.y - this.y;
		return Math.sqrt((dx * dx) + (dy * dy));
	}
	
	/**
	 * Calculate the angle between this and another position, relative to this point.
	 * @param other The other 2D position.
	 * @return The angle between this and the other position, in radians.
	 */
	public double angleBetween(Position2D other) {
		return Math.atan2(other.y - this.y, other.x - this.x);
	}
}