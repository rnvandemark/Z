package graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import actors.Player;
import game.UserControl;

/**
 * The controller class for the GameWindow graphics class. This contains the code for all
 * of the events coming through the main GUI.
 */
public class GameWindowController
	implements MouseListener, MouseMotionListener, KeyListener {
	
	/**
	 * The GUI that this class is handling the event handling for.
	 */
	private GameWindow parent;
	
	/**
	 * The atomic boolean that describes when the operation threads should be stopped.
	 */
	private AtomicBoolean keepThreadsAlive;
	
	/**
	 * The thread responsible for updating the graphics coinciding with frame rate.
	 */
	private Thread graphicsUpdateThread;
	
	/**
	 * The directional keys that are being pressed at the moment.
	 */
	private ConcurrentHashMap<UserControl, Boolean> keysPressed;
	
	/**
	 * The sole constructor. Takes a {@link graphics.GameWindow} instance to own the
	 * event handling for.
	 * @param parent The {@link graphics.GameWindow} instance.
	 */
	public GameWindowController(GameWindow parent) {
		this.parent = parent;
		
		this.keepThreadsAlive = new AtomicBoolean();
		
		this.graphicsUpdateThread = new Thread(new Runnable() {			
			
			private int vx = 0, vy = 0;
			private boolean sprinting = false;
			
			@Override
			public void run() {
				while (keepThreadsAlive.get()) {
					vx = 0;
					vy = 0;
					try {
						if (keysPressed.get(UserControl.LEFT).booleanValue())
							vx -= 1;
						
						if (keysPressed.get(UserControl.RIGHT).booleanValue())
							vx += 1;
						
						if (keysPressed.get(UserControl.UP).booleanValue())
							vy -= 1;
						
						if (keysPressed.get(UserControl.DOWN).booleanValue())
							vy += 1;
						
						sprinting = keysPressed.get(UserControl.SPRINT).booleanValue();
						
						parent.getSession().getPlayer().updatePosition(0.017);
						parent.getSession().getPlayer().setVelocity(
							Math.atan2(vy, vx),
							Math.sqrt((vx * vx) + (vy * vy)) * (sprinting ? Player.RUN_SPEED : Player.WALK_SPEED)
						);
						
						parent.repaint();
						
						// Operate under ~60FPS
						Thread.sleep(17);
					} catch (InterruptedException e) { e.printStackTrace(); }
				}
			}
		});
		
		this.keysPressed = new ConcurrentHashMap<UserControl, Boolean>();
		this.keysPressed.put(UserControl.LEFT, false);
		this.keysPressed.put(UserControl.RIGHT, false);
		this.keysPressed.put(UserControl.UP, false);
		this.keysPressed.put(UserControl.DOWN, false);
		this.keysPressed.put(UserControl.SPRINT, false);
		
		this.parent.addMouseListener(this);
		this.parent.addMouseMotionListener(this);
		this.parent.addKeyListener(this);
		
		if (this.keepThreadsAlive.compareAndSet(false, true)) {
			this.graphicsUpdateThread.start();
		} else {
			throw new RuntimeException("Atomic flag failure.");
		}
	}
	
	/**
	 * Override from KeyListener interface.
	 * Handles keys being pressed on the parent {@link graphics.GameWindow}.
	 * @param e The key event to handle.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {
			case KeyEvent.VK_ESCAPE:
				this.killSafely();
				break;
			
			default:
			{
				UserControl c = UserControl.findByKey(code);
				if (c != null)
					this.keysPressed.put(c, true);
			}
		}
	}

	/**
	 * Override from KeyListener interface.
	 * Handles keys being released on the parent {@link graphics.GameWindow}.
	 * @param e The key event to handle.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		UserControl c = UserControl.findByKey(e.getKeyCode());
		if (c != null)
			this.keysPressed.put(c, false);
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
	 * Override from MouseMotionListener interface.
	 * Nothing to do.
	 * @param e The mouse motion event to handle.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// Nothing to do
	}

	/**
	 * Override from MouseMotionListener interface.
	 * Nothing to do.
	 * @param e The mouse motion event to handle.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// Nothing to do
	}

	/**
	 * Override from MouseListener interface.
	 * Nothing to do.
	 * @param e The mouse event to handle.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// Nothing to do
	}

	/**
	 * Override from MouseListener interface.
	 * Nothing to do.
	 * @param e The mouse event to handle.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// Nothing to do
	}

	/**
	 * Override from MouseListener interface.
	 * Nothing to do.
	 * @param e The mouse event to handle.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// Nothing to do
	}

	/**
	 * Override from MouseListener interface.
	 * Nothing to do.
	 * @param e The mouse event to handle.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// Nothing to do
	}

	/**
	 * Override from MouseListener interface.
	 * Nothing to do.
	 * @param e The mouse event to handle.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// Nothing to do
	}
	
	/**
	 * Safely bring down active components of the application and close the parent {@link graphics.GameWindow}.
	 */
	public void killSafely() {
		if (this.keepThreadsAlive.compareAndSet(true, false)) {
			try {
				this.graphicsUpdateThread.join();
			} catch (InterruptedException e) { e.printStackTrace(); }
			this.parent.dispatchEvent(new WindowEvent(this.parent, WindowEvent.WINDOW_CLOSING));
		} else {
			throw new RuntimeException("Atomic flag failure.");
		}
	}
}