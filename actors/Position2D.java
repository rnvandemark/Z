package actors;

/*
 * A simple two-dimensional point representation with floating-point precision, with required
 * coordinates in the x and y axes.
 */
public class Position2D extends Kinematic2D {
	
	/**
	 * An distance epsilon threshold for position equality.
	 */
	private double EQUALITY_EPSILON = 0.01;
	
	/**
	 * The default constructor.
	 * Defaults the x and y position each to 0.
	 */
	public Position2D() {
		super(0.0, 0.0);
	}
	
	/**
	 * A copy constructor.
	 * Copies the given position values.
	 * @param other The other position value to populate values from.
	 */
	public Position2D(Position2D other) {
		super(other);
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
		return this.distanceBetween(other.x, other.y);
	}
	
	/**
	 * Calculate the linear distance between this point and another.
	 * @param px The other x-coordinate.
	 * @param py The other y-coordinate.
	 * @return The distance between this and the other point.
	 */
	public double distanceBetween(double px, double py) {
		double dx = px - this.x;
		double dy = py - this.y;
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
	
	/**
	 * Offset this position by a distance in the x and y axes.
	 * @param dx The displacement in the x-axis.
	 * @param dy The displacement in the y-axis.
	 */
	public void translate(double dx, double dy) {
		this.set(this.x + dx , this.y + dy);
	}
	
	/**
	 * Create a new position that is offset by this one by a distance in the x and y axes.
	 * @param dx The displacement in the x-axis.
	 * @param dy The displacement in the y-axis.
	 * @return The new offset position.
	 */
	public Position2D translated(double dx, double dy) {
		Position2D p = new Position2D(this);
		p.translate(dx, dy);
		return p;
	}
	
	/**
	 * Checks for what's considered equality between a 2D position and an x,y coordinate pair.
	 * @param px The other x-coordinate.
	 * @param py The other y-coordinate.
	 * @param eps An epsilon threshold to consider equality with.
	 * @return Whether or not the points can be considered equal.
	 */
	public boolean equals(double px, double py, double eps) {
		return this.distanceBetween(px, py) < eps;
	}
	
	/**
	 * Checks for what's considered equality between a 2D position and an x,y coordinate pair.
	 * @param px The other x-coordinate.
	 * @param py The other y-coordinate.
	 * @return Whether or not the points can be considered equal.
	 */
	public boolean equals(double px, double py) {
		return this.equals(px, py, EQUALITY_EPSILON);
	}
	
	/**
	 * Override from the {@link java.lang.Object} method.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Position2D)
			return this.equals(((Position2D) obj).x, ((Position2D) obj).y);
		else
			return false;
	}
}