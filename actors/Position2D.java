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
}