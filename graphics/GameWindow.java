package graphics;

import java.awt.Color;

import javax.swing.JFrame;

/**
 * The class that encapsulates the main gameplay's window/graphics.
 */
public class GameWindow extends JFrame {
	
	/**
	 * A generated serial version number for this serializable object.
	 */
	private static final long serialVersionUID = 8986297244578121580L;

	/**
	 * The default constructor. For now, this will load a default map and automatically
	 * start the session.
	 */
	public GameWindow() {
		new GameWindowController(this);
		
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setBackground(Color.BLACK);
		this.setVisible(true);
	}
}