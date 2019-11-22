package actors;

import java.awt.Color;

/**
 * A type of actor that uses path planning techniques to find a path to the player. The
 * main objective for the player is to kill these continuously while these zombies try to
 * kill the player once they're close enough.
 */
public class Zombie extends Actor {
	
	/**
	 * The slowest speed for a zombie.
	 */
	public final static int MIN_SPEED = 10;
	
	/**
	 * The fastest speed for a zombie.
	 */
	public final static int MAX_SPEED = Player.WALK_SPEED;
	
	/**
	 * The color for the player when they full health.
	 */
	public final static Color FULL_HEALTH_COLOR = new Color(0, 200, 0);
	
	/**
	 * The color for the player when they're dead.
	 */
	public final static Color LOW_HEALTH_COLOR = Color.LIGHT_GRAY;
	
	/**
	 * The health that this zombie started with. This is only tracked to make the color
	 * change routine possible the way that it is.
	 */
	private int initialHealth;
	
	/**
	 * The sole constructor.
	 * Takes the initial x and y position coordinates, as well as the health that it will
	 * spawn with.
	 * @param px The initial x-axis position.
	 * @param py The initial y-axis position.
	 * @param initialHealth The initial health for this zombie.
	 */
	public Zombie(double px, double py, int initialHealth) {
		super(Color.GREEN, px, py, 0.0, 0.0, initialHealth);
		this.initialHealth = initialHealth;
	}
	
	/**
	 * Override from the {@link actors.Actor} method.
	 */
	@Override
	public void updateColor() {
		double dr = (FULL_HEALTH_COLOR.getRed()   - LOW_HEALTH_COLOR.getRed())   / (double)this.initialHealth;
		double dg = (FULL_HEALTH_COLOR.getGreen() - LOW_HEALTH_COLOR.getGreen()) / (double)this.initialHealth;
		double db = (FULL_HEALTH_COLOR.getBlue()  - LOW_HEALTH_COLOR.getBlue())  / (double)this.initialHealth;
		
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