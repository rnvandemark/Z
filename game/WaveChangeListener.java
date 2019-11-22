package game;

/**
 * A simple interface to raise an event when a session's wave number changes.
 */
public interface WaveChangeListener {
	
	/**
	 * The function to call when the initiator wants to pass on an event.
	 * @param e The wave change event.
	 */
	void waveNumberChanged(WaveChangeEvent e);
	
	/**
	 * The function to call when this object should be removed from the event iniator's
	 * scope of listeners.
	 * @return Whether or not this listener was successfully removed.
	 */
	public boolean removeWaveChangeListenerFromSession();
}