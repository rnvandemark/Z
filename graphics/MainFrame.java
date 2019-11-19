package graphics;

import java.awt.Color;
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
	 * The default constructor.
	 * Set up the full screen window that contains all of the user interaction through
	 * JPanel's owned by this object.
	 * For now, this also creates a sample session with the test map.
	 */
	public MainFrame() {
		new MainFrameController(this).startSession("test");
		
		this.setLayout(new GridBagLayout());
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setBackground(Color.WHITE);
		this.setVisible(true);
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
	 * Performs the steps necessary to safely add a started session panel.
	 * @param s The new session panel to add to the main frame.
	 */
	public void setSessionPanel(SessionPanel s) {
		this.destroySessionPanel();
		this.sessionPanel = s;
		this.add(this.sessionPanel);
	}
	
	/**
	 * If there is an active session, safely end it and remove the graphics associated
	 * with it.
	 */
	public void destroySessionPanel() {
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