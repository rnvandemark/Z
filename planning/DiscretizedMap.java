package planning;

import java.awt.Color;
import java.awt.image.BufferedImage;

import game.MapData;

public class DiscretizedMap extends MapRepresentation {
	
	private int discretizationRatio;
	
	private boolean[][] cells;
	
	public DiscretizedMap(int discretizationRatio) {
		this.discretizationRatio = discretizationRatio;
		this.cells               = null;
	}
	
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
	
	public boolean openAt(int x, int y) {
		return !cells[x][y];
	}
	
	public boolean openAtOriginal(int x, int y) {
		return this.openAt(x / this.discretizationRatio, y / this.discretizationRatio);
	}
	
	public int getWidth() {
		return this.cells.length;
	}
	
	public int getHeight() {
		if (this.getWidth() == 0) {
			return 0;
		} else {
			return this.cells[0].length;
		}
	}
	
	public int getDiscretizationRatio() {
		return this.discretizationRatio;
	}
}