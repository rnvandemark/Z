package game;

import java.util.Random;

import actors.Position2D;
import actors.Zombie;
import planning.PlannedPath;

/**
 * A collection of values that creates a level of difficulty for a specific number of
 * zombies that are to spawn. Most values tracked are related to characteristics of
 * the zombies for this round, including an array that tracks those that are currently
 * active and their generated paths.
 */
public class Wave {
	
	/**
	 * The maximum number of zombies that can be active at a single time in any session.
	 */
	public final static int MAX_ZOMBIES_AT_ONCE = 25;
	
	/**
	 * The number of this wave. The difficulty increases as this number increases.
	 */
	private int waveNumber;
	
	/**
	 * The amount of health that zombies will spawn with during this wave.
	 */
	private int zombieHealth;
	
	/**
	 * The number of zombies that will be spawned throughout this wave.
	 */
	private int zombieSpawnsLeft;
	
	/**
	 * An array that describes the zombies currently active in a session. If the value at
	 * an index in this array is null, that means that there are less than the maximum
	 * number of possible zombies active at the moment. Otherwise, a non-null value describes
	 * one of the zombies currently active. When spawning in a new zombie, the first null
	 * pointer in this array will be populated with a newly instantiated zombie.
	 */
	private Zombie[] activeZombies;
	
	/**
	 * An array that describes the path for each of the active zombies. A zombie at some
	 * index i in {@link activeZombies} will have its path described at index i in this array.
	 * A null value at index i means that there is no zombie active in the {@link activeZombies}
	 * array, so naturally there is no path to follow either. If a value in this array is not
	 * null, then there must be an active zombie at the corresponding index. However, if a value
	 * in this array is null, there can still be an active zombie at the same index, it just
	 * does not have a path to follow yet.
	 */
	private PlannedPath[] activeZombiePaths;
	
	/**
	 * A pseudo-random number generator to help generate the random zombie speeds.
	 */
	private Random random;
	
	/**
	 * The sole constructor.
	 * Given the number that this wave represents, generate the zombie characteristics for this
	 * wave, as well as initialize the arrays for the active zombies and their paths.
	 * @param waveNumber The number for this wave.
	 */
	public Wave(int waveNumber) {
		this.waveNumber        = waveNumber;
		this.zombieHealth      = this.waveNumber * 125;
		this.zombieSpawnsLeft  = (int)(5 * Math.pow(1.2, this.waveNumber));
		this.activeZombies     = new Zombie[MAX_ZOMBIES_AT_ONCE];
		this.activeZombiePaths = new PlannedPath[MAX_ZOMBIES_AT_ONCE];
		this.random            = new Random();
	}
	
	/**
	 * Generate a random speed through a standard normal distribution, with skew based on the
	 * wave number.
	 * @return A randomly generated speed.
	 */
	private double getRandomZombieSpeed() {
		double g = this.random.nextGaussian() * 10;
		int b = Math.min(60, this.waveNumber) + 15;
		double p = Math.max(1.0, Math.min(100.0, b + g)) / 100;
		return Zombie.MIN_SPEED + (Zombie.DIFF_IN_SPEEDS * p);
	}
	
	/**
	 * Getter for this wave's number.
	 * @return This wave's number.
	 */
	public int getWaveNumber() {
		return this.waveNumber;
	}
	
	/**
	 * Get the active zombie at the specified index.
	 * @param i The index in the active zombie array.
	 * @return The active zombie at the specified index.
	 */
	public Zombie getZombieAt(int i) {
		return this.activeZombies[i];
	}
	
	/**
	 * Get the current path for the active zombie at the specified index. This value can be null
	 * if there is not active zombie at this index, or if this corresponding active zombie does
	 * not have a path to follow yet.
	 * @param i The index in the active zombie path array.
	 * @return The corresponding active zombie's path currently being followed.
	 */
	public PlannedPath getZombiePathAt(int i) {
		return this.activeZombiePaths[i];
	}
	
	/**
	 * Helper function to acknowledge a zombie being killed. This ensures the active zombie is
	 * nullified, as well as its path.
	 * @param i The index for the recently killed zombie in the active zombie array.
	 * @return True if there was an active zombie at the specified array index and it was
	 * properly "marked dead", false otherwise.
	 */
	public boolean killedZombieAt(int i) {
		if (this.activeZombies[i] != null) {
			this.activeZombies[i]     = null;
			this.activeZombiePaths[i] = null;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Helper function to spawn in a new zombie at a specified spawn point. This ensures that
	 * more zombies can spawn, marks a new spawn, and populates the active zombie array with a
	 * new zombie at the earliest possible index.
	 * @param spawnPoint The position to spawn the zombie at.
	 * @return True if a zombie can be spawned in and was, false otherwise.
	 */
	public boolean spawnZombie(Position2D spawnPoint) {
		if (this.zombieSpawnsLeft > 0) {
			for (int i = 0; i < MAX_ZOMBIES_AT_ONCE; i++) {
				if (this.activeZombies[i] == null) {
					this.activeZombies[i] = new Zombie(
						spawnPoint.x,
						spawnPoint.y,
						this.zombieHealth,
						this.getRandomZombieSpeed()
					);
					this.activeZombiePaths[i] = null;
					this.zombieSpawnsLeft--;
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Take an existing zombie at some specified index and effectively respawn it. Its velocity
	 * is set to 0, its path is nullified, and its position is changed. Other characteristics,
	 * such as its health, remain the same.
	 * @param i The index of the zombie to respawn to the active zombie array.
	 * @param respawnPoint The position to respawn the zombie at.
	 * @return True if the specified index corresponds to an active zombie and could be respawned,
	 * false otherwise.
	 */
	public boolean respawnZombie(int i, Position2D respawnPoint) {
		if (this.activeZombies[i] != null) {
			this.activeZombies[i].setVelocity(0, 0);
			this.activeZombies[i].setPosition(respawnPoint);
			this.activeZombiePaths[i] = null;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Given some path and a specified index, set an active zombie's path.
	 * @param i The index of the zombie in the active zombie array to set the path for.
	 * @param path The path to set.
	 */
	public void setZombiePathAt(int i, PlannedPath path) {
		this.activeZombiePaths[i] = path;
	}
	
	/**
	 * Helper function to decide if more zombies are supposed to spawn this wave or not.
	 * @return Whether or not more zombies are to spawn for this wave.
	 */
	public boolean isDoneSpawning() {
		return this.zombieSpawnsLeft == 0;
	}
	
	/**
	 * Helper function to decide if if this wave is finished, meaning that no more zombies are to
	 * spawn, and there are no zombies active at the moment either.
	 * @return Whether or not the wave is treated as finished.
	 */
	public boolean isFinished() {
		if (this.isDoneSpawning()) {
			for (int i = 0; i < MAX_ZOMBIES_AT_ONCE; i++) {
				if (this.activeZombies[i] != null) {
					return false;
				}
			}
		}
		
		return false;
	}
}