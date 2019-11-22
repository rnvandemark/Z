package actors;

/*
 * A simple two-dimensional representation of a kinematic measurement with floating-point precision.
 */
public abstract class Kinematic2D {
	
	/**
	 * The current kinematic measurement's value in the x-axis.
	 */
	public double x;
	
	/**
	 * The current kinematic measurement's value in the y-axis.
	 */
	public double y;

	/**
	 * The default constructor.
	 * Defaults the x and y value each to 0.
	 */
	public Kinematic2D() {
		this(0.0, 0.0);
	}
	
	/**
	 * A copy constructor.
	 * Copies the given values.
	 * @param other The other value to populate values from.
	 */
	public Kinematic2D(Kinematic2D other) {
		this.set(other);
	}
	
	/**
	 * Another constructor.
	 * Specifies both the x and y values.
	 * @param x The new value in the x-axis.
	 * @param y The new value in the y-axis.
	 */
	public Kinematic2D(double x, double y) {
		this.set(x, y);
	}
	
	/**
	 * A simple set function, to set both the x and y coordinates.
	 * @param x The new x-coordinate.
	 * @param y The new y-coordinate.
	 */
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * A simple set function, to set both the x and y coordinates, based on another value.
	 * @param k The value to copy from.
	 */
	public void set(Kinematic2D k) {
		this.x = k.x;
		this.y = k.y;
	}
	
	/**
	 * Override from {@link java.lang.Object} method.
	 */
	@Override
	public String toString() {
		return String.format("{x: %f, y: %f}", this.x, this.y);
	}
}