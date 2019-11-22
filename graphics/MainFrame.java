package graphics;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

/**
 * The class that encapsulates the main window/graphics.
 */
public class MainFrame extends JFrame {
	
	/**
	 * A generated serial version number for this serializable object.
	 */
	private static final long serialVersionUID = 8986297244578121580L;
	
	/**
	 * The panel that is responsible for rendering an active game session.
	 */
	private SessionPanel sessionPanel;

	/**
	 * The panel that is responsible for displaying the statistics for an active game
	 * session.
	 */
	private SessionInfoPanel sessionInfoPanel;

	/**
	 * The default constructor.
	 * Set up the full screen window that contains all of the user interaction through
	 * JPanel's owned by this object.
	 * For now, this also creates a sample session with the test map.
	 */
	public MainFrame() {
		MainFrameController c = new MainFrameController(this);
		
		this.setLayout(new GridBagLayout());
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setBackground(Color.WHITE);
		this.setVisible(true);
		
		c.startSession("test");
	}
	
	/**
	 * Getter for the child panel that renders the active session.
	 * This is null if there is no active session.
	 * @return The panel responsible for rendering the active session.
	 */
	public SessionPanel getSessionPanel() {
		return this.sessionPanel;
	}
	
	/**
	 * Getter for the child panel that renders statistics for the active session.
	 * This is null if there is no active session.
	 * @return The panel responsible for displaying the stats for the active session
	 */
	public SessionInfoPanel getSessionInfoPanel() {
		return this.sessionInfoPanel;
	}
	
	/**
	 * Performs the steps necessary to safely create and add a new session panel.
	 * @param dirURL The folder name containing a map's information.
	 */
	public void setSessionRelatedPanels(String dirURL) {
		this.destroySessionGraphics();
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.sessionPanel = new SessionPanel(dirURL);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth = 3;
		constraints.gridheight = 3;
		constraints.gridx = 1;
		constraints.gridy = 1;
		this.add(this.sessionPanel, constraints);
		
		this.sessionInfoPanel = new SessionInfoPanel(this.sessionPanel.getSession());
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.gridx = 0;
		constraints.gridy = 2;
		this.add(this.sessionInfoPanel, constraints);
	}
	
	/**
	 * If there is an active session, safely end it and remove the graphics associated
	 * with it.
	 */
	public void destroySessionGraphics() {
		if (this.sessionInfoPanel != null) {
			SessionInfoPanelController c = this.sessionInfoPanel.getController();
			if ((!c.removeWaveChangeListenerFromSession())
					|| (!c.removePointsChangeListenerFromSession()))
				System.err.println(
					"Failed to safely remove the info panel from the active session!"
				);
			this.sessionInfoPanel.setVisible(false);
			this.remove(this.sessionInfoPanel);
			this.sessionInfoPanel = null;
		}
		
		if (this.sessionPanel != null) {
			this.removeKeyListener(this.sessionPanel.getSessionPanelController());
			if (!this.sessionPanel.getSessionPanelController().killSafely())
				System.err.println("Failed to safely kill the active session!");
			this.sessionPanel.setVisible(false);
			this.remove(this.sessionPanel);
			this.sessionPanel = null;
		}
	}
}