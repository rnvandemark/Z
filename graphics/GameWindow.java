package graphics;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

import actors.Actor;
import game.Session;

/**
 * The class that encapsulates the main gameplay's window/graphics.
 */
public class GameWindow extends JFrame {
	
	/**
	 * A generated serial version number for this serializable object.
	 */
	private static final long serialVersionUID = 8986297244578121580L;
	
	/**
	 * The current game session being ran. If no game is running, then this should be null.
	 */
	private Session session;

	/**
	 * The default constructor. For now, this will load a default map and automatically
	 * start the session.
	 */
	public GameWindow() {
		this.session = new Session("test");
		
		new GameWindowController(this);
		
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setBackground(Color.BLACK);
		this.setVisible(true);
	}
	
	/**
	 * Override from the {@link javax.swing.JFrame} method.
	 */
	@Override
	public void paint(Graphics g) {
		super.paintComponents(g);
		
		g.setColor(this.session.getPlayer().getColor());
		g.fillOval(
			(int)this.session.getPlayer().getPosition().x - Actor.RADIUS,
			(int)this.session.getPlayer().getPosition().y - Actor.RADIUS,
			Actor.RADIUS * 2,
			Actor.RADIUS * 2
		);
	}
	
	/**
	 * Getter for the active game session.
	 */
	public Session getSession() {
		return this.session;
	}
}