package game;

import java.util.concurrent.locks.ReentrantLock;

import actors.Player;

/**
 * A collection of objects describing the elements involved in a game session, such as all of
 * the actors, the environment map, and more.
 */
public class Session {
	
	/**
	 * The data for the environment that the actors reside in.
	 */
	private MapData mapData;
	
	/**
	 * The player that the user has control over for this session.
	 */
	private Player player;
	
	/**
	 * The wave/round currently active in the game, which describes the active zombies and
	 * their characteristics.
	 */
	private Wave currentWave;
	
	/**
	 * The locking mechanism used to ensure thread-safe access to the array of zombie objects
	 * owned by the current wave, as well as the player.
	 */
	private ReentrantLock actorsMutex;
	
	/**
	 * The sole constructor.
	 * Given a file URL to a directory, get the proper files to build the environment.
	 * @param dirURL The file URL to the directory containing the info for the desired map.
	 */
	public Session(String dirURL) {
		this.mapData     = new MapData(dirURL);
		this.player      = new Player(this.mapData.getPlayerSpawn());
		this.currentWave = null;
		this.actorsMutex = new ReentrantLock(true);
	}
	
	/**
	 * Queue this thread to hold the actor locking mechanism, blocking until the execution
	 * thread has the hold.
	 */
	public void acquireActorLock() {
		this.actorsMutex.lock();
	}
	
	/**
	 * Attempt to hold the actor locking mechanism.
	 * @return Whether or not the lock was successfully made.
	 */
	public boolean attemptActorLock() {
		return this.actorsMutex.tryLock();
	}
	
	/**
	 * For a certain amount of time, try to acquire the actor lock (without queuing to wait
	 * and subsequently blocking).
	 * @param timeoutMS The number of milliseconds to attempt to hold the lock.
	 * @return Whether or not the lock was successfully acquired.
	 */
	public boolean waitForActorLock(long timeoutMS) {
		long startTime = System.currentTimeMillis();
		boolean result = attemptActorLock();
		
		while ((!result) && (System.currentTimeMillis() - startTime < timeoutMS)) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) { e.printStackTrace(); }
			result = attemptActorLock();
		}
		
		return result;
	}
	
	/**
	 * Release the actor lock, ensuring first that the thread of execution does in fact have
	 * ownership of the lock at this point in time.
	 * @return Whether or not the thread of execution was able to release the lock.
	 */
	public boolean releaseActorLock() {
		if (this.actorsMutex.isHeldByCurrentThread()) {
			this.actorsMutex.unlock();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * A helper function to start (instantiate) the next wave, given the current wave's
	 * wave number.
	 */
	public void startNextWave() {
		this.currentWave = new Wave(
			this.currentWave == null ? 1 : this.currentWave.getWaveNumber() + 1
		);
	}
	
	/**
	 * Getter for the session's map's data.
	 * @return The session's map's data.
	 */
	public MapData getMapData() {
		return this.mapData;
	}
	
	/**
	 * Getter for the current player.
	 * @return The current player.
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Getter for the current wave running.
	 * @return The current wave running.
	 */
	public Wave getCurrentWave() {
		return this.currentWave;
	}
}