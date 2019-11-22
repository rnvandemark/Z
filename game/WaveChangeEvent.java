package game;

/**
 * A simple class to encapsulate an event created for when a session's wave number changes.
 */
public class WaveChangeEvent {
	
	/**
	 * The wave number changed to.
	 */
	private int waveNumber;
	
	/**
	 * The sole constructor.
	 * Takes the value that the wave number changed to.
	 * @param waveNumber The wave number changed to.
	 */
	public WaveChangeEvent(int waveNumber) {
		this.waveNumber = waveNumber;
	}
	
	/**
	 * Getter for the wave number;
	 * @return The wave number.
	 */
	public int getWaveNumber() {
		return this.waveNumber;
	}
}