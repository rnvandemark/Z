package planning;

import java.awt.Color;
import java.awt.image.BufferedImage;

import game.MapData;

/**
 * A type of map representation that takes the black and white data of a map and creates a
 * discretized version of it, given a ratio of discretization. The result is broken out into
 * a doubly-scripted array of cells simply labelled as "occupied" (not available for an actor
 * to reside in) or otherwise.
 */
public class DiscretizedMap extends MapRepresentation {
	
	/**
	 * The ratio of discretization. Given an input image of A pixels wide and B pixels high,
	 * if the discretization ratio D helps to create a new image that is A/D pixels wide and
	 * B/D pixels high. A D by D area of pixels is compressed into one discretized cell, and
	 * if a single cell is the original image is labeled as uninhabitable, then the resulting
	 * discretized cell is also uninhabitable.
	 */
	private int discretizationRatio;
	
	/**
	 * The generated cells for the discretized representation of an input map. A value of "true"
	 * means that the cell is occupied and cannot be inhabited by an actor.
	 */
	private boolean[][] cells;
	
	/**
	 * The sole constructor.
	 * Simply takes a discretization ratio and ensures the cells grid is null for now.
	 * @param discretizationRatio The ratio of discretization.
	 */
	public DiscretizedMap(int discretizationRatio) {
		this.discretizationRatio = discretizationRatio;
		this.cells               = null;
	}
	
	/**
	 * Override from the {@link planning.MapRepresentation} method.
	 */
	@Override
	public boolean build(MapData mapData) {
		BufferedImage initialImage = mapData.getImage();
		int cw = (int)Math.ceil(initialImage.getWidth()  / (double)this.discretizationRatio);
		int ch = (int)Math.ceil(initialImage.getHeight() / (double)this.discretizationRatio);
		this.cells = new boolean[cw][ch];
		
		int blackRGB = Color.BLACK.getRGB();
		for (int x = 0; x < cw; x++) {
			for (int y = 0; y < ch; y++) {
				boolean occupied = false;
				
				for (int i = 0; i < this.discretizationRatio; i++) {
					if (occupied)
						break;
					for (int j = 0; j < this.discretizationRatio; j++) {
						occupied = initialImage.getRGB(x + i, y + j) == blackRGB;
						if (occupied)
							break;
					}
				}
				
				this.cells[x][y] = occupied;
			}
		}
		
		return true;
	}
	
	/**
	 * Getter for whether or not a cell at the discretized coordinate pair x and y is habitable
	 * by an actor.
	 * @param x The x coordinate of the discretized map.
	 * @param y The y coordinate of the discretized map.
	 * @return True if the cell at this position can be inhabited by an actor, false otherwise.
	 */
	public boolean openAt(int x, int y) {
		return !cells[x][y];
	}
	
	/**
	 * Getter for whether or not a cell at the coordinate pair x and y of the original map image
	 * is habitable by an actor.
	 * @param x The x coordinate of the original map.
	 * @param y The y coordinate of the original map.
	 * @return True if the cell at this position can be inhabited by an actor, false otherwise.
	 */
	public boolean openAtOriginal(int x, int y) {
		return this.openAt(x / this.discretizationRatio, y / this.discretizationRatio);
	}
	
	/**
	 * Getter for the width of the discretized map.
	 * @return The width of the discretized map.
	 */
	public int getWidth() {
		return this.cells.length;
	}

	/**
	 * Getter for the height of the discretized map.
	 * @return The height of the discretized map.
	 */
	public int getHeight() {
		if (this.getWidth() == 0) {
			return 0;
		} else {
			return this.cells[0].length;
		}
	}
	
	/**
	 * Getter for the discretization ratio.
	 * @return The discretization ratio.
	 */
	public int getDiscretizationRatio() {
		return this.discretizationRatio;
	}
}