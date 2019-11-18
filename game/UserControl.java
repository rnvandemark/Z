package game;

import java.awt.event.KeyEvent;

/**
 * An enum to track which key codes correspond to which intended player action.
 */
public enum UserControl {
	
	/**
	 * Move the player left.
	 */
	LEFT(KeyEvent.VK_A),
	
	/**
	 * Move the player right.
	 */
	RIGHT(KeyEvent.VK_D),
	
	/**
	 * Move the player down.
	 */
	DOWN(KeyEvent.VK_S),
	
	/**
	 * Move the player up.
	 */
	UP(KeyEvent.VK_W),
	
	/**
	 * Make the player sprint.
	 */
	SPRINT(KeyEvent.VK_SHIFT);
	
	/**
	 * The key code that triggers the intended action.
	 */
	public final Integer value;
	
	/**
	 * The sole constructor.
	 * Takes the key code.
	 * @param v The key code to trigger the intended action.
	 */
	private UserControl(Integer v) {
		this.value = v;
	}
	
	/**
	 * A static function to find the appropriate action given the key code. Returns
	 * null if the key code doesn't correspond to an action.
	 * @param k The input key code.
	 * @return The calculated control.
	 */
	public static UserControl findByKey(Integer k) {
		for (UserControl c : UserControl.values()) {
			if (c.value.equals(k)) {
				return c;
			}
		}
		return null;
	}
}