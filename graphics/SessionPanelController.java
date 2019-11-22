package graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import actors.Player;
import actors.Position2D;
import actors.Zombie;
import game.Session;
import game.UserControl;
import game.Wave;
import planning.IPlanning;
import planning.PlannedPath;

/**
 * The controller responsible for handling functionality for and input into the panel
 * that renders active game sessions.
 */
public class SessionPanelController
	implements MouseListener, MouseMotionListener, KeyListener {
	
	/**
	 * The number of frames to attempt to render every second.
	 */
	private final static int FPS = 40;
	
	/**
	 * The number of milliseconds each frame is ideally displayed for.
	 */
	private final static int FRAME_PERIOD_MS = 1000 / FPS;
	
	/**
	 * The session panel that this controller is responsible for handling input from.
	 */
	private SessionPanel parent;
	
	/**
	 * The thread-safe boolean that describes when the operation threads should be stopped.
	 */
	private AtomicBoolean keepThreadsAlive;
	
	/**
	 * The thread responsible for updating the graphics coinciding with frame rate.
	 */
	private Thread graphicsUpdateThread;
	
	/**
	 * The thread responsible for updating the planned paths for zombies to follow.
	 */
	private Thread zombiesPathPlanThread;
	
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
		
		this.keepThreadsAlive      = new AtomicBoolean();
		this.graphicsUpdateThread  = new Thread(new GraphicsRunnable(this));
		this.zombiesPathPlanThread = new Thread(new ZombiePathPlanningRunnable(this));
		
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
//		System.out.println(e.getX() - this.parent.getBounds().x);
//		System.out.println(e.getY() - this.parent.getBounds().y);
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
	 * Try to start the main threads.
	 */
	public void start() {
		if (this.keepThreadsAlive.compareAndSet(false, true)) {
			this.keysPressed.put(UserControl.LEFT, false);
			this.keysPressed.put(UserControl.RIGHT, false);
			this.keysPressed.put(UserControl.UP, false);
			this.keysPressed.put(UserControl.DOWN, false);
			this.keysPressed.put(UserControl.SPRINT, false);
			this.graphicsUpdateThread.start();
			
			this.parent.getSession().startNextWave();
			for (int i = 0; i < 12; i++) {
				this.parent.getSession().getCurrentWave().spawnZombie(
					this.parent.getSession().getMapData().getRandomZombieSpawnPoint()
				);
			}
			this.zombiesPathPlanThread.start();
		} else {
			throw new RuntimeException("Atomic flag failure.");
		}
	}
	
	/**
	 * Try to safely (thread-safe) stop the main threads.
	 * @return Whether or not the proper elements could be safely brought down.
	 */
	public boolean killSafely() {
		if (this.keepThreadsAlive.compareAndSet(true, false)) {
			try {
				this.graphicsUpdateThread.join();
				this.zombiesPathPlanThread.join();
				return true;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			throw new RuntimeException("Atomic flag failure.");
		}
	}
	
	/**
	 * A simple class made to organize functionality, so a large chunk of code wasn't
	 * intrusive. This class implements the Runnable interface and is meant to maintain
	 * a session panel controller's graphics.
	 */
	private static class GraphicsRunnable implements Runnable {
		
		/**
		 * The controller that is using this instance for its graphics.
		 */
		private SessionPanelController controller;
		
		/**
		 * The sole constructor.
		 * Simply take and save a reference to the controller that owns this instance.
		 * @param controller
		 */
		public GraphicsRunnable(SessionPanelController controller) {
			this.controller = controller;
		}
		
		/**
		 * Override from the {@link java.lang.Runnable} method.
		 */
		@Override
		public void run() {
			final double dt = FRAME_PERIOD_MS / 1000.0;
			
			int vx = 0, vy = 0;
			boolean sprinting = false;
			long startTime, waitTime;
			
			while (controller.keepThreadsAlive.get()) {
				vx = 0;
				vy = 0;
				
				if (controller.keysPressed.get(UserControl.LEFT).booleanValue())
					vx -= 1;
				
				if (controller.keysPressed.get(UserControl.RIGHT).booleanValue())
					vx += 1;
				
				if (controller.keysPressed.get(UserControl.UP).booleanValue())
					vy -= 1;
				
				if (controller.keysPressed.get(UserControl.DOWN).booleanValue())
					vy += 1;
				
				sprinting = controller.keysPressed.get(UserControl.SPRINT).booleanValue();
				
				if (controller.parent.getSession().waitForActorLock(FRAME_PERIOD_MS / 2)) {
					startTime = System.currentTimeMillis();
					try {
						controller.parent.getSession().getPlayer().attemptTranslationIn(
							controller.parent.getSession().getPlayer().getVelocity().x * dt,
							controller.parent.getSession().getPlayer().getVelocity().y * dt,
							controller.parent.getSession().getMapData()
						);
						controller.parent.getSession().getPlayer().setVelocity(
							Math.atan2(vy, vx),
							Math.sqrt((vx * vx) + (vy * vy)) * (sprinting ? Player.RUN_SPEED : Player.WALK_SPEED)
						);
						
						if (controller.parent.getSession().getCurrentWave() != null) {
							for (int i = 0; i < Wave.MAX_ZOMBIES_AT_ONCE; i++) {
								Zombie z = controller.parent.getSession().getCurrentWave().getZombieAt(i);
								if (z != null) {
									z.attemptTranslationIn(
										z.getVelocity().x * dt,
										z.getVelocity().y * dt,
										controller.parent.getSession().getMapData()
									);
								}
							}
						}
					} finally {
						if (controller.parent.getSession().releaseActorLock()) {
							waitTime = FRAME_PERIOD_MS - (System.currentTimeMillis() - startTime);
							if (waitTime < 0)
								waitTime = 0;
						} else {
							throw new RuntimeException("Unorganized thread ownership.");
						}
					}
				} else {
					waitTime = FRAME_PERIOD_MS / 2;
				}
				
				controller.parent.repaint();
				
				try {
					Thread.sleep(FRAME_PERIOD_MS);
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
	}
	
	/**
	 * A simple class made to organize functionality, so a large chunk of code wasn't
	 * intrusive. This class implements the Runnable interface and is meant to handle
	 * (re)generating a session's active zombies' path.
	 */
	private static class ZombiePathPlanningRunnable implements Runnable {
		
		/**
		 * The controller that is using this instance for its graphics.
		 */
		private SessionPanelController controller;
		
		/**
		 * The sole constructor.
		 * Simply take and save a reference to the controller that owns this instance.
		 * @param controller
		 */
		public ZombiePathPlanningRunnable(SessionPanelController controller) {
			this.controller = controller;
		}
		
		/**
		 * Override from the {@link java.lang.Runnable} method.
		 */
		@Override
		public void run() {
			final long FULL_LOOP_TIME = 100;
			
			long startTime, waitTime;
			
			Position2D goal  = null;
			Session session  = controller.parent.getSession();
			Wave currentWave = session.getCurrentWave();
			
			Zombie z;
			PlannedPath p;
			
			boolean setValues;
			Position2D[] currentPositions = new Position2D[Wave.MAX_ZOMBIES_AT_ONCE];
			PlannedPath[] currentPaths    = new PlannedPath[Wave.MAX_ZOMBIES_AT_ONCE];
			boolean[] recalculatedPaths   = new boolean[Wave.MAX_ZOMBIES_AT_ONCE];
			
			while (controller.keepThreadsAlive.get()) {
				setValues = false;
				startTime = System.currentTimeMillis();
				
				session.acquireActorLock();
				try {
					goal = session.getPlayer().getPosition();
					for (int i = 0; i < Wave.MAX_ZOMBIES_AT_ONCE; i++) {
						z = currentWave.getZombieAt(i);
						p = currentWave.getZombiePathAt(i);
						currentPositions[i]  = z == null ? null : z.getPosition();
						currentPaths[i]      = p == null ? null : p;
						recalculatedPaths[i] = false;
					}
					setValues = true;
				} finally {
					if (!session.releaseActorLock())
						throw new RuntimeException("Unorganized thread ownership.");
				}
				
				if (setValues) {
					for (int i = 0; i < Wave.MAX_ZOMBIES_AT_ONCE; i++) {
						if (currentPositions[i] != null) {
							Position2D start = currentPositions[i];
							if (!IPlanning.getZombiesPlanner().salvagePath(currentPaths[i], start, goal)) {
								currentPaths[i]      = IPlanning.getZombiesPlanner().generatePath(start, goal);
								recalculatedPaths[i] = true;
							}
						}
					}
					
					session.acquireActorLock();
					try {
						for (int i = 0; i < Wave.MAX_ZOMBIES_AT_ONCE; i++) {
							if (recalculatedPaths[i])
								currentWave.setZombiePathAt(i, currentPaths[i]);
							
							z = currentWave.getZombieAt(i);
							if (z != null) {
								p = currentWave.getZombiePathAt(i);
								
								if (p == null) {
									currentWave.respawnZombie(
										i,
										session.getMapData().getRandomZombieSpawnPoint()
									);
								} else {
									if (p.atNextPosition(currentPositions[i], 2))
										p.consumeNext();
									z.setVelocity(p.nextMovement(currentPositions[i], Zombie.MIN_SPEED));
								}
							}
						}
					} finally {
						if (!session.releaseActorLock())
							throw new RuntimeException("Unorganized thread ownership.");
					}
				}
				
				waitTime = FULL_LOOP_TIME - (System.currentTimeMillis() - startTime);
				if (waitTime < 0)
					waitTime = 0;
				
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
	}
}