package actors;

/*
 * A simple two-dimensional point representation with floating-point precision, with required
 * coordinates in the x and y axes.
 */
public class Position {
	
	/**
	 * The current position in the x-axis.
	 */
	public double x;
	
	/**
	 * The current position in the y-axis.
	 */
	public double y;
	
	/**
	 * The default constructor.
	 * Defaults the x and y position each to 0.
	 */
	public Position() {
		this(0.0, 0.0);
	}
	
	/**
	 * Another constructor.
	 * Specifies both the x and y positions.
	 * @param x The new position in the x-axis.
	 * @param y The new position in the y-axis.
	 */
	public Position(double x, double y) {
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
	 * Override from {@link java.lang.Object} method.
	 */
	@Override
	public String toString() {
		return String.format("x: %f, y: %f", this.x, this.y);
	}
}