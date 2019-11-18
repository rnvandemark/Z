package actors;

/*
 * A simple two-dimensional velocity representation, with required velocities in
 * the x and y axes.
 */
public class Velocity {
	
	/**
	 * The current velocity in the x-axis.
	 */
	public double x;
	
	/**
	 * The current velocity in the y-axis.
	 */
	public double y;
	
	/**
	 * The default constructor.
	 * Defaults the x and y velocity each to 0.
	 */
	public Velocity() {
		this(0.0, 0.0);
	}
	
	/**
	 * Another constructor.
	 * Specifies both the x and y velocities.
	 * @param x The new velocity in the x-axis.
	 * @param y The new velocity in the y-axis.
	 */
	public Velocity(double x, double y) {
		this.set(x, y);
	}
	
	/**
	 * A simple set function, to set both the x and y velocities.
	 * @param x The new x velocity.
	 * @param y The new y velocity.
	 */
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
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
	public static Velocity generateFromVector(double direction, double magnitude) {
		Velocity v = new Velocity();
		v.setFromVector(direction, magnitude);
		return v;
	}
	
	/**
	 * Override from {@link java.lang.Object} method.
	 */
	@Override
	public String toString() {
		return String.format("x: %f, y: %f", this.x, this.y);
	}
}