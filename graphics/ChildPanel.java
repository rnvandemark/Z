package graphics;

import java.awt.Container;

import javax.swing.JPanel;

/**
 * A simple class to define common functionality between all possible panels added
 * to the main frame.
 */
public abstract class ChildPanel extends JPanel {
	
	/**
	 * A generated serial version number for this serializable object.
	 */
	private static final long serialVersionUID = -1159620028857263458L;
	
	/**
	 * Given the relationship of Java containers, get the main frame that owns
	 * this panel.
	 * @return The main frame that owns this panel.
	 */
	public MainFrame getMainFrame() {
		Container c = this.getParent();
		
		while (c != null) {
			if (c instanceof MainFrame)
				return (MainFrame)c;
			c = c.getParent();
		}
		
		return null;
	}
}