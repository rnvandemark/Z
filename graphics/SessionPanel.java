package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import actors.Actor;
import actors.Position2D;
import actors.Zombie;
import game.MapData;
import game.Session;
import game.Wave;
import planning.PlannedPath;

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
	 * The last read position of the player, so the player can be drawn. This is kept
	 * separately in an effort to free up as much time as possible for the actor lock.
	 */
	private Position2D playerPositionToDraw;
	
	/**
	 * The last read color of the player, so the player can be drawn. This is kept
	 * separately in an effort to free up as much time as possible for the actor lock.
	 */
	private Color playerColorToDraw;
	
	/**
	 * A list of the last read positions for the active zombies. If a value in this
	 * array is null, it means there's no zombie active at that index.This is kept
	 * separately in an effort to free up as much time as possible for the actor lock.
	 */
	private Position2D[] zombiePositionsToDraw;
	
	/**
	 * A list of the last read colors for the active zombies. If a value in this
	 * array is null, it means there's no zombie active at that index.This is kept
	 * separately in an effort to free up as much time as possible for the actor lock.
	 */
	private Color[] zombieColorsToDraw;
	
	/**
	 * A list of the last read paths for the active zombies. If a value in this
	 * array is null, it means there's no zombie active at that index, or the active
	 * zombie does not have a path to follow.This is kept separately in an effort to
	 * free up as much time as possible for the actor lock. This is temporary, to show
	 * the paths being calculated and executed... This might become a toggleable setting.
	 */
	private ArrayList<LinkedList<Position2D>> zombiePathsToDraw;
	
	/**
	 * The sole constructor.
	 * Given a map's name, create a session for it. Also, create the controller and
	 * set the proper size of this panel.
	 */
	public SessionPanel(String dirURL) {
		this.session    = new Session(dirURL);
		this.controller = new SessionPanelController(this);
		
		this.playerPositionToDraw  = null;
		this.playerColorToDraw     = null;
		this.zombiePositionsToDraw = new Position2D[Wave.MAX_ZOMBIES_AT_ONCE];
		this.zombieColorsToDraw    = new Color[Wave.MAX_ZOMBIES_AT_ONCE];
		this.zombiePathsToDraw     = new ArrayList<LinkedList<Position2D>>(Wave.MAX_ZOMBIES_AT_ONCE);
		
		for (int i = 0; i < Wave.MAX_ZOMBIES_AT_ONCE; i++)
			this.zombiePathsToDraw.add(null);
		
		this.setSize(this.getPreferredSize());
	}
	
	/**
	 * A helper function to acquire the session's actor lock and capture the player's
	 * and active zombies' positions, colors, and paths.
	 */
	private void captureActorData() {
		this.session.acquireActorLock();
		try {
			this.playerPositionToDraw = this.session.getPlayer().getPosition();
			this.playerColorToDraw    = this.session.getPlayer().getColor();
			
			Wave w = this.session.getCurrentWave();
			
			Zombie z;
			PlannedPath p;
			Iterator<Position2D> t;
			LinkedList<Position2D> l;
			for (int i = 0; i < Wave.MAX_ZOMBIES_AT_ONCE; i++) {
				z = null;
				if (w != null)
					z = w.getZombieAt(i);
				this.zombiePositionsToDraw[i] = z == null ? null : z.getPosition();
				this.zombieColorsToDraw[i]    = z == null ? null : z.getColor();
				
				p = w == null ? null : w.getZombiePathAt(i);
				l = null;
				if (p != null) {
					if (p.size() >= 2) {
						l = new LinkedList<Position2D>();
						t = p.iterator();
						
						l.add(this.zombiePositionsToDraw[i]);
						while (t.hasNext())
							l.addLast(t.next());
					}
				}
				this.zombiePathsToDraw.set(i, l);
			}
		} finally {
			if (!this.session.releaseActorLock()) {
				throw new RuntimeException("Unorganized thread ownership.");
			}
		}
	}

	/**
	 * A helper function to draw the data captured from the player and zombie actors.
	 */
	private void drawActorData(Graphics2D g2d) {
		g2d.setColor(this.playerColorToDraw);
		g2d.fillOval(
			(int)this.playerPositionToDraw.x - Actor.RADIUS,
			(int)this.playerPositionToDraw.y - Actor.RADIUS,
			Actor.RADIUS * 2,
			Actor.RADIUS * 2
		);
		
		Position2D p, n;
		Color c;
		LinkedList<Position2D> l;
		Iterator<Position2D> t;
		for (int i = 0; i < Wave.MAX_ZOMBIES_AT_ONCE; i++) {
			p = this.zombiePositionsToDraw[i];
			c = this.zombieColorsToDraw[i];
			if ((p != null) && (c != null)) {
				g2d.setColor(c);
				g2d.fillOval(
					(int)p.x - Actor.RADIUS,
					(int)p.y - Actor.RADIUS,
					Actor.RADIUS * 2,
					Actor.RADIUS * 2
				);
				
				l = this.zombiePathsToDraw.get(i);
				if (l != null) {
					g2d.setColor(Color.RED);
					t = l.iterator();
					p = t.next();
					while (t.hasNext()) {
						n = t.next();
						g2d.drawLine((int)p.x, (int)p.y, (int)n.x, (int)n.y);
						p = n;
					}
				}
			}
		}
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
		this.captureActorData();
		this.drawActorData(g2d);
	}
}