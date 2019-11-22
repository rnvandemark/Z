package graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

/**
 * A simple effectively static class that handles adding available fonts to the system.
 */
public final class FontManager {
	
	/**
	 * A reference to the local graphics environment.
	 */
	private final static GraphicsEnvironment GE
		= GraphicsEnvironment.getLocalGraphicsEnvironment();
	
	/**
	 * The sole constructor.
	 * Set to private so no instances can be made.
	 */
	private FontManager() {}
	
	/**
	 * Given the URL to a new font, register it in the graphics environment.
	 * @param ttfURL The relative URL to the game/resources/fonts folder.
	 * @return Whether or not the font was successfully registered.
	 */
	public static boolean registerFont(String ttfURL) {
		try {
			return GE.registerFont(Font.createFont(
				Font.TRUETYPE_FONT,
				FontManager.class.getResourceAsStream("resources/fonts/" + ttfURL)
			));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}