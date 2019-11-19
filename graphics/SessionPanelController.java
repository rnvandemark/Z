package graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import actors.Player;
import game.UserControl;

/**
 * The controller responsible for handling functionality for and input into the panel
 * that renders active game sessions.
 */
public class SessionPanelController
	implements MouseListener, MouseMotionListener, KeyListener {
	
	/**
	 * The number of frames to attempt to render every second.
	 */
	private final static int FPS = 30;
	
	/**
	 * The number of milliseconds each frame is ideally displayed for.
	 */
	private final static int FRAME_PERIOD_MS = 1000 / FPS;
	
	/**
	 * The session panel that this controller is responsible for handling input from.
	 */
	private SessionPanel parent;
	
	/**
	 * The atomic boolean that describes when the operation threads should be stopped.
	 */
	private AtomicBoolean keepThreadsAlive;
	
	/**
	 * The thread responsible for updating the graphics coinciding with frame rate.
	 */
	private Thread graphicsUpdateThread;
	
	/**
	 * The thread-safe map tracking the keys that are being pressed to manipulate the
	 * player.
	 */
	private ConcurrentHashMap<UserControl, Boolean> keysPressed;
	
	/**
	 * The sole constructor.
	 * Initializes the variable members.
	 * @param p The session panel that this controller is to be responsible for.
	 */
	public SessionPanelController(SessionPanel p) {
		this.parent = p;
		
		this.keepThreadsAlive = new AtomicBoolean();
		
		this.graphicsUpdateThread = new Thread(new Runnable() {			
			
			private int vx = 0, vy = 0;
			private boolean sprinting = false;
			
			@Override
			public void run() {
				while (keepThreadsAlive.get()) {
					vx = 0;
					vy = 0;
					
					if (keysPressed.get(UserControl.LEFT).booleanValue())
						vx -= 1;
					
					if (keysPressed.get(UserControl.RIGHT).booleanValue())
						vx += 1;
					
					if (keysPressed.get(UserControl.UP).booleanValue())
						vy -= 1;
					
					if (keysPressed.get(UserControl.DOWN).booleanValue())
						vy += 1;
					
					sprinting = keysPressed.get(UserControl.SPRINT).booleanValue();
					
					parent.getSession().getPlayer().updatePosition(FRAME_PERIOD_MS / 1000.0);
					parent.getSession().getPlayer().setVelocity(
						Math.atan2(vy, vx),
						Math.sqrt((vx * vx) + (vy * vy)) * (sprinting ? Player.RUN_SPEED : Player.WALK_SPEED)
					);
					
					parent.repaint();
					
					try {
						Thread.sleep(FRAME_PERIOD_MS);
					} catch (InterruptedException e) { e.printStackTrace(); }
				}
			}
		});
		
		this.keysPressed = new ConcurrentHashMap<UserControl, Boolean>();
	}
	
	/**
	 * Override from KeyListener interface.
	 * Handles keys being pressed on the parent {@link graphics.SessionPanel}.
	 * @param e The key event to handle.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		UserControl c = UserControl.findByKey(e.getKeyCode());
		if (c != null)
			this.keysPressed.put(c, true);
	}

	/**
	 * Override from KeyListener interface.
	 * Handles keys being released on the parent {@link graphics.SessionPanel}.
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
	 * Try to start the main thread(s).
	 */
	public void start() {
		if (this.keepThreadsAlive.compareAndSet(false, true)) {
			this.keysPressed.put(UserControl.LEFT, false);
			this.keysPressed.put(UserControl.RIGHT, false);
			this.keysPressed.put(UserControl.UP, false);
			this.keysPressed.put(UserControl.DOWN, false);
			this.keysPressed.put(UserControl.SPRINT, false);
			this.graphicsUpdateThread.start();
		} else {
			throw new RuntimeException("Atomic flag failure.");
		}
	}
	
	/**
	 * Try to safely (thread-safe) stop the main thread(s).
	 * @return Whether or not the proper elements could be safely brought down.
	 */
	public boolean killSafely() {
		if (this.keepThreadsAlive.compareAndSet(true, false)) {
			try {
				this.graphicsUpdateThread.join();
				return true;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			throw new RuntimeException("Atomic flag failure.");
		}
	}
}