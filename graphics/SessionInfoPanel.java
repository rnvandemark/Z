package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import game.Session;

/**
 * The graphical element that displays statistics for the session, the most important
 * of which is likely such as the current wave number and the number of points that
 * the player has to spend.
 */
public class SessionInfoPanel extends ChildPanel {
	
	/**
	 * A generated serial version number for this serializable object.
	 */
	private static final long serialVersionUID = 7670124364171762371L;
	
	/**
	 * The width that this panel should have.
	 */
	private final static int WIDTH = 300;
	
	/**
	 * The height that this panel should have.
	 */
	private final static int HEIGHT = 400;
	
	/**
	 * The session that this panel gets its info from.
	 */
	private Session parent;
	
	/**
	 * The controller to handle this graphical element's incoming events.
	 */
	private SessionInfoPanelController controller;
	
	/**
	 * The label that displays the wave number.
	 */
	private JLabel waveNumberLabel;
	
	/**
	 * The label that displays the player's available points.
	 */
	private JLabel pointCountLabel;
	
	/**
	 * The sole constructor.
	 * Simply takes the session to listen for events from and adds this as a listener.
	 * @param parent The session to get info from.
	 */
	public SessionInfoPanel(Session parent) {
		this.parent = parent;
		
		this.controller = new SessionInfoPanelController(this);
		if (!this.parent.addWaveChangeListener(this.controller)) {
			throw new RuntimeException(
				"Failed to add session info panel controller as a wave change listener."
			);
		}
		if (!this.parent.addPointsChangeListener(this.controller)) {
			throw new RuntimeException(
				"Failed to add session info panel controller as a points change listener."
			);
		}
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.waveNumberLabel = new JLabel();
		this.waveNumberLabel.setForeground(Color.RED);
		this.waveNumberLabel.setFont(new Font("Zombified", Font.BOLD, 64));
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 3;
		this.add(this.waveNumberLabel, constraints);
		
		this.pointCountLabel = new JLabel(
			Integer.toString(this.parent.getPlayer().getPointCount())
		);
		this.pointCountLabel.setForeground(Color.RED);
		this.pointCountLabel.setFont(new Font("Zombified", Font.PLAIN, 32));
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		this.add(this.pointCountLabel, constraints);
		
		this.setSize(this.getPreferredSize());
	}
	
	/**
	 * Helper function to set the text for the wave number label.
	 * @param s The new text for the wave number label.
	 */
	public void setWaveNumberLabelText(String s) {
		this.waveNumberLabel.setText(s);
		this.waveNumberLabel.setSize(this.waveNumberLabel.getPreferredSize());
	}

	/**
	 * Helper function to set the text for the point count label.
	 * @param s The new text for the point count label.
	 */
	public void setPointCountLabelText(String s) {
		this.pointCountLabel.setText(s);
		this.pointCountLabel.setSize(this.pointCountLabel.getPreferredSize());
	}
	
	/**
	 * Getter for the session that this panel displays info for.
	 * @return The session.
	 */
	public Session getSession() {
		return this.parent;
	}
	
	/**
	 * Getter for the controller of this panel.
	 * @return The controller.
	 */
	public SessionInfoPanelController getController() {
		return this.controller;
	}
	
	/**
	 * Override from the {@link javax.swing.JComponent} method.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}
}