package game;

/**
 * A simple interface to raise an event when a player's point count is updated.
 */
public interface PointsChangeListener {
	
	/**
	 * The function to call when the initiator wants to pass on an event.
	 * @param e The points change event.
	 */
	void pointCountChanged(PointsChangeEvent e);
	
	/**
	 * The function to call when this object should be removed from the event iniator's
	 * scope of listeners.
	 * @return Whether or not this listener was successfully removed.
	 */
	public boolean removePointsChangeListenerFromSession();
}