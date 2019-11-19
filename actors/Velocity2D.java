package actors;

/*
 * A simple two-dimensional velocity representation, with required velocities in
 * the x and y axes.
 */
public class Velocity2D extends Kinematic2D {
	
	/**
	 * The default constructor.
	 * Defaults the x and y velocity each to 0.
	 */
	public Velocity2D() {
		super(0.0, 0.0);
	}
	
	/**
	 * Another constructor.
	 * Specifies both the x and y velocities.
	 * @param x The new velocity in the x-axis.
	 * @param y The new velocity in the y-axis.
	 */
	public Velocity2D(double x, double y) {
		super(x, y);
	}
	
	/**
	 * Set the x and y from a vector representation.
	 * @param direction The direction of the vector, in radians.
	 * @param magnitude The intensity of the velocity (the net speed).
	 */
	public void setFromVector(double direction, double magnitude) {
		this.x = Math.cos(direction) * magnitude;
		this.y = Math.sin(direction) * magnitude;
	}
	
	/**
	 * A static factory function to generate a velocity from a vector representation.
	 * @param direction The direction of the vector, in radians.
	 * @param magnitude The intensity of the velocity (the net speed).
	 */
	public static Velocity2D generateFromVector(double direction, double magnitude) {
		Velocity2D v = new Velocity2D();
		v.setFromVector(direction, magnitude);
		return v;
	}
}