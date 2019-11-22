package actors;

import java.awt.Color;

import game.MapData;

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
	 * Set the current x and y velocity.
	 * @param direction The direction of the vector, in radians.
	 * @param magnitude The intensity of the velocity (the net speed).
	 */
	public void setVelocity(double direction, double magnitude) {
		this.velocity.setFromVector(direction, magnitude);
	}
	
	/**
	 * Set the current velocity.
	 * @param velocity The velocity to copy.
	 */
	public void setVelocity(Velocity2D velocity) {
		this.velocity.set(velocity);
	}
	
	/**
	 * Set the x and y position.
	 * @param position The new position to copy.
	 */
	public void setPosition(Position2D position) {
		this.position.set(position);
	}
	
	/**
	 * Given a desired translation in the x and y axes, attempt to move the desired
	 * amounts if the provided map allows it. If not, try to move in the individual axes.
	 * @param dx The translation in the x-axis.
	 * @param dy The translation in the y-axis.
	 * @param mapData The data for the map that this actor is trying to move in.
	 */
	public void attemptTranslationIn(double dx, double dy, MapData mapData) {
		Position2D newPosition = this.position.translated(dx, dy);
		if (mapData.positionIsValid(newPosition)) {
			this.position.set(newPosition);
		} else {
			newPosition = this.position.translated(dx, 0);
			if (mapData.positionIsValid(newPosition)) {
				this.position.set(newPosition);
			} else {
				newPosition = this.position.translated(0, dy);
				if (mapData.positionIsValid(newPosition)) {
					this.position.set(newPosition);
				}
			}
		}
	}
	
	/**
	 * Based on the actor's characteristics (such as health), set the color.
	 */
	public abstract void updateColor();
}