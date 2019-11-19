package actors;

import java.awt.Color;

/**
 * The base concept for an actor that can interact with the world's environment. Basic ideas like
 * movement and appearance can start to be described here.
 */
public abstract class Actor {
	
	/**
	 * The radius of the actors when drawn.
	 */
	public final static int RADIUS = 6;
	
	/**
	 * The current color for this actor's graphical appearance.
	 */
	protected Color color;
	
	/**
	 * The current position of this actor, in the x and y axes. Because these actors appear as
	 * circles, this describes the center of the circle.
	 */
	private Position2D position;
	
	/**
	 * The current velocity of this actor, in the x and y axes.
	 */
	private Velocity2D velocity;
	
	/**
	 * The current health for this actor. This should be positive when it's "alive", and anything
	 * less than or equal to zero should be considered "dead".
	 */
	protected int health;
	
	/**
	 * The sole constructor.
	 * Requires the information to construct the initial state of this actor.
	 * @param c The initial color.
	 * @param px The x-coordinate for the initial position.
	 * @param py The y-coordinate for the initial position.
	 * @param vx The x portion of the initial velocity.
	 * @param vy The y portion of the initial velocity.
	 * @param h The initial health.
	 */
	public Actor(Color c, double px, double py, double vx, double vy, int h) {
		this.color    = c;
		this.position = new Position2D(px, py);
		this.velocity = new Velocity2D(vx, vy);
		this.health   = h;
	}
	
	/**
	 * Getter for the color.
	 * @return The current color of this actor.
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Getter for the position.
	 * @return The current position of this actor.
	 */
	public Position2D getPosition() {
		return this.position;
	}
	
	/**
	 * Getter for the velocity.
	 * @return The current velocity of this actor.
	 */
	public Velocity2D getVelocity() {
		return this.velocity;
	}
	
	/**
	 * Getter for the health.
	 * @return The current health of this actor.
	 */
	public int getHealth() {
		return this.health;
	}
	
	/**
	 * A helper function to check whether or not this actor has lost enough health to be
	 * considered dead.
	 * @return Whether or not this actor is considered dead.
	 */
	public boolean hasDied() {
		return this.getHealth() <= 0;
	}
	
	/**
	 * Update an actor's health and subsequent color.
	 * @param healthGained The number of health points to add to this actor.
	 */
	public void changeHealth(int healthGained) {
		this.health += healthGained;
		this.updateColor();
	}
	
	/**
	 * Given an amount of time that has passed, change this actor's position based on its
	 * current velocity.
	 * @param dt The amount of time that has passed, in seconds.
	 */
	public void updatePosition(double dt) {
		this.position.x += (this.velocity.x * dt);
		this.position.y += (this.velocity.y * dt);
	}
	
	/**
	 * Set the current x and y velocity.
	 * @param direction The direction of the vector, in radians.
	 * @param magnitude The intensity of the velocity (the net speed).
	 */
	public void setVelocity(double direction, double magnitude) {
		this.velocity.setFromVector(direction, magnitude);
	}
	
	/**
	 * Based on the actor's characteristics (such as health), set the color.
	 */
	public abstract void updateColor();
}