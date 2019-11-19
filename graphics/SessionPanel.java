package graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import actors.Actor;
import game.MapData;
import game.Session;

/**
 * The graphical element responsible for rendering the active game's visuals.
 */
public class SessionPanel extends ChildPanel {
	
	/**
	 * A generated serial version number for this serializable object.
	 */
	private static final long serialVersionUID = 1788913909671853530L;
	
	/**
	 * The current game session being ran.
	 */
	private Session session;
	
	/**
	 * The controller for this UI element.
	 */
	private SessionPanelController controller;
	
	/**
	 * The sole constructor.
	 * Given a map's name, create a session for it. Also, create the controller and
	 * set the proper size of this panel.
	 */
	public SessionPanel(String dirURL) {
		this.session    = new Session(dirURL);
		this.controller = new SessionPanelController(this);
		
		this.setSize(this.getPreferredSize());
	}
	
	/**
	 * Getter for the active game session.
	 * @return The active game session.
	 */
	public Session getSession() {
		return this.session;
	}
	
	/**
	 * Getter for this UI element's controller.
	 * @return This element's controller.
	 */
	public SessionPanelController getSessionPanelController() {
		return this.controller;
	}

	/**
	 * Override from the {@link javax.swing.JComponent} method.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(MapData.MAP_WIDTH, MapData.MAP_HEIGHT);
	}
	
	/**
	 * Override from the {@link javax.swing.JComponent} method.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g.create();
		
		g2d.drawImage(this.session.getMapData().getImage(), 0, 0, null);
		g2d.setColor(this.session.getPlayer().getColor());
		g2d.fillOval(
			(int)this.session.getPlayer().getPosition().x - Actor.RADIUS,
			(int)this.session.getPlayer().getPosition().y - Actor.RADIUS,
			Actor.RADIUS * 2,
			Actor.RADIUS * 2
		);
	}
}