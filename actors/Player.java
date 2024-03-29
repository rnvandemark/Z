package actors;

import java.awt.Color;

/**
 * The player, an actor that the application's user has control over.
 */
public class Player extends Actor {
	
	/**
	 * The normal walking speed for the player.
	 */
	public final static int WALK_SPEED = 65;
	
	/**
	 * The sprinting speed for the player.
	 */
	public final static int RUN_SPEED = 100;
	
	/**
	 * The maximum amount of health a player can have without any kind of enhancements.
	 */
	public final static int NORMAL_MAX_HEALTH = 250;
	
	/**
	 * The color for the player when they full health.
	 */
	public final static Color FULL_HEALTH_COLOR = Color.CYAN;
	
	/**
	 * The color for the player when they're dead.
	 */
	public final static Color LOW_HEALTH_COLOR = Color.RED;
	
	/**
	 * The number of points that this player has available to spend.
	 */
	private int pointCount;
	
	/**
	 * The sole constructor.
	 * Assumes default values for the super class' color, velocity, and health.
	 * @param px The initial position in the x-axis.
	 * @param py The initial position in the y-axis.
	 * @param initialPoints The initial number of points that the player will have.
	 */
	public Player(Position2D p, int initialPoints) {
		super(FULL_HEALTH_COLOR, p.x, p.y, 0.0, 0.0, NORMAL_MAX_HEALTH);
		this.pointCount = initialPoints;
	}
	
	/**
	 * Given the player and any additional enhancements, calculate the maximum health the player can have.
	 * @return The maximum amount of fixed health points.
	 */
	public int getMaxHealth() {
		return NORMAL_MAX_HEALTH;
	}
	
	/**
	 * Getter for this player's current point count.
	 * @return The player's current point count.
	 */
	public int getPointCount() {
		return this.pointCount;
	}
	
	/**
	 * Update the player's total amount of available points.
	 * @param pointsGained The number of points to add.
	 */
	public void changePoints(int pointsGained) {
		this.pointCount += pointsGained;
	}
	
	/**
	 * Override from the {@link actors.Actor} method, to ensure that max health isn't surpassed.
	 */
	@Override
	public void changeHealth(int healthGained) {
		this.health += healthGained;
		if (this.health > this.getMaxHealth()) {
			this.health = this.getMaxHealth();
		}
		this.updateColor();
	}
	
	/**
	 * Override from the {@link actors.Actor} method.
	 */
	@Override
	public void updateColor() {
		double maxHealth = this.getMaxHealth();
		
		double dr = (FULL_HEALTH_COLOR.getRed()   - LOW_HEALTH_COLOR.getRed())   / maxHealth;
		double dg = (FULL_HEALTH_COLOR.getGreen() - LOW_HEALTH_COLOR.getGreen()) / maxHealth;
		double db = (FULL_HEALTH_COLOR.getBlue()  - LOW_HEALTH_COLOR.getBlue())  / maxHealth;
		
		int nr = (int)(LOW_HEALTH_COLOR.getRed()   + (dr * this.health));
		if (nr < 0) {
			nr = 0;
		} else if (nr > 255) {
			nr = 255;
		}
		
		int ng = (int)(LOW_HEALTH_COLOR.getGreen() + (dg * this.health));
		if (ng < 0) {
			ng = 0;
		} else if (ng > 255) {
			ng = 255;
		}
		
		int nb = (int)(LOW_HEALTH_COLOR.getBlue( ) + (db * this.health));
		if (nb < 0) {
			nb = 0;
		} else if (nb > 255) {
			nb = 255;
		}
		
		this.color = new Color(nr, ng, nb);
	}
}