package main;

import graphics.FontManager;
import graphics.MainFrame;

/**
 * A basic class, solely responsible for housing the program's main function.
 */
public class Runner {
	
	/**
	 * The main function for the program.
	 * @param args A list of input arguments.
	 */
	public static void main(String[] args) {
		if (!FontManager.registerFont("zombified/Zombified.ttf"))
			System.err.println("Failed to register Zombified font.");
		new MainFrame();
	}
}