package graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import game.MapData;
import planning.IPlanning;
import planning.RRTPlanner;

/**
 * The controller class for the MainFrame graphics class. This contains the code for all
 * of the events coming through the main GUI.
 */
public class MainFrameController implements KeyListener {
	
	/**
	 * The GUI that this class is handling the event handling for.
	 */
	private MainFrame parent;
	
	/**
	 * The sole constructor. Takes a {@link graphics.MainFrame} instance to own the
	 * event handling for.
	 * @param parent The {@link graphics.MainFrame} instance.
	 */
	public MainFrameController(MainFrame parent) {
		this.parent = parent;
		this.parent.addKeyListener(this);
	}
	
	/**
	 * Override from KeyListener interface.
	 * Handles keys being pressed on the parent {@link graphics.MainFrame}.
	 * @param e The key event to handle.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			this.killSafely();
	}

	/**
	 * Override from KeyListener interface.
	 * Nothing to do.
	 * @param e The key event to handle.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// Nothing to do
	}

	/**
	 * Override from KeyListener interface.
	 * Nothing to do.
	 * @param e The key event to handle.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// Nothing to do
	}
	
	/**
	 * Given a map's name, create a session for it, as well as the graphics for it, and
	 * pass ownership of the graphics over to the parent main frame. Then, start the
	 * session.
	 * @param dirURL The map's name.
	 */
	public void startSession(String dirURL) {
		this.parent.setSessionRelatedPanels(dirURL);
		
		IPlanning.setZombiesPlannerType(RRTPlanner.class);
		if (!IPlanning.renewZombiesPlanner(
				new Class<?>[]{MapData.class, boolean.class, double.class},
				this.parent.getSessionPanel().getSession().getMapData(),
				true,
				30.0
		)) {
			throw new RuntimeException("Failed to set the Zombies path planner.");
		}
		
		this.parent.addKeyListener(this.parent.getSessionPanel().getSessionPanelController());
		this.parent.addMouseListener(this.parent.getSessionPanel().getSessionPanelController());
		this.parent.addMouseMotionListener(this.parent.getSessionPanel().getSessionPanelController());
		
		this.parent.getSessionPanel().getSessionPanelController().start();
	}
	
	/**
	 * Safely bring down active components of the application and close the parent
	 * {@link graphics.MainFrame}.
	 */
	public void killSafely() {
		if (this.parent.getSessionPanel() != null)
			if (!this.parent.getSessionPanel().getSessionPanelController().killSafely())
				System.err.println("Failed to kill session!");
		
		this.parent.dispatchEvent(new WindowEvent(this.parent, WindowEvent.WINDOW_CLOSING));
	}
}