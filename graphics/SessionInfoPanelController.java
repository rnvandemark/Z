package graphics;

import game.PointsChangeEvent;
import game.PointsChangeListener;
import game.WaveChangeEvent;
import game.WaveChangeListener;

/**
 * A controller for a session info panel, which handles incoming events and updates the
 * text for the session info panel.
 */
public class SessionInfoPanelController
	implements WaveChangeListener, PointsChangeListener {
	
	/**
	 * The session info panel that this controller updates.
	 */
	private SessionInfoPanel parent;
	
	/**
	 * The sole constructor.
	 * Takes a reference to the session info panel that this controller will update.
	 * @param parent The session info panel that this controller will be responsible for.
	 */
	public SessionInfoPanelController(SessionInfoPanel parent) {
		this.parent = parent;
	}
	
	/**
	 * Override from the {@link game.WaveChangeListener} method.
	 */
	@Override
	public void waveNumberChanged(WaveChangeEvent e) {
		this.parent.setWaveNumberLabelText(Integer.toString(e.getWaveNumber()));
	}
	
	/**
	 * Override from the {@link game.WaveChangeListener} method.
	 */
	@Override
	public boolean removeWaveChangeListenerFromSession() {
		return this.parent.getSession().removeWaveChangeListener(this);
	}
	
	/**
	 * Override from the {@link game.PointsChangeListener} method.
	 */
	@Override
	public void pointCountChanged(PointsChangeEvent e) {
		this.parent.setPointCountLabelText(Integer.toString(e.getPointCount()));
	}

	/**
	 * Override from the {@link game.PointsChangeListener} method.
	 */
	@Override
	public boolean removePointsChangeListenerFromSession() {
		return this.parent.getSession().removePointsChangeListener(this);
	}
}