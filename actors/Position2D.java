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
}