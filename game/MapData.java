package game;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import actors.Position2D;

/**
 * Contains the data for the appearance and functionality of a map, including the image rendered
 * on the screen for the map, spawn points, etc.
 */
public class MapData {
	
	/**
	 * The required width of the map PNG file.
	 */
	public final static int MAP_WIDTH  = 600;
	
	/**
	 * The required height of the map PNG file.
	 */
	public final static int MAP_HEIGHT = 400;
	
	/**
	 * The RGB pixel data for the image of the map.
	 */
	private BufferedImage image;
	
	/**
	 * The spawn point for the player, when the session using this map starts.
	 */
	private Position2D playerSpawn;
	
	/**
	 * A list of all of the possible zombie spawn positions.
	 */
	private ArrayList<Position2D> zombieSpawns;
	
	/**
	 * A list of all of the station positions that a robot can recharge at.
	 */
	private ArrayList<Position2D> robotStations;
	
	/**
	 * The sole constructor.
	 * Takes a folder URL, which contains at least the map's image and a text file describing
	 * the player's spawn point, and a nonzero positive number of zombie spawns and robot
	 * recharge stations.
	 * @param dirURL The map's directory name.
	 */
	public MapData(String dirURL) {
		this.image         = null;
		this.playerSpawn   = new Position2D(-1, -1);
		this.zombieSpawns  = new ArrayList<Position2D>();
		this.robotStations = new ArrayList<Position2D>();
		
		try {
			BufferedImage originalImage = ImageIO.read(
				MapData.class.getResource("resources/maps/" + dirURL + "/map.png")
			);
			
			if ((originalImage.getWidth() != MAP_WIDTH) || (originalImage.getHeight() != MAP_HEIGHT)) {
				throw new RuntimeException(String.format(
					"Map must be %dx%d pixels.",
					MAP_WIDTH,
					MAP_HEIGHT
				));
			}

			int whiteRGB = Color.WHITE.getRGB();
			int blackRGB = Color.BLACK.getRGB();
			this.image = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x < MAP_WIDTH; x++) {
				for (int y = 0; y < MAP_HEIGHT; y++) {
					if (originalImage.getRGB(x, y) == whiteRGB) {
						this.image.setRGB(x, y, whiteRGB);
					} else {
						this.image.setRGB(x, y, blackRGB);
					}
				}
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					MapData.class.getResource("resources/maps/" + dirURL + "/data.txt").getPath()
			)));
			
			String line;
			String[] values;
			ArrayList<Position2D> lastDeclaration = null;
			while ((line = reader.readLine()) != null) {
				if (line.charAt(0) == '\t') {
					values = line.trim().split(",");
					lastDeclaration.add(new Position2D(
						Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim())
					));
				} else if (line.startsWith("playerSpawn")) {
					values = line.trim().split(":");
					values = values[1].trim().split(",");
					this.playerSpawn.set(
						Integer.parseInt(values[0].trim()), Integer.parseInt(values[1].trim())
					);
				} else if (!line.trim().equals("")) {
					if (line.equals("zombieSpawns")) {
						lastDeclaration = this.zombieSpawns;
					} else if (line.equals("robotStations")) {
						lastDeclaration = this.robotStations;
					}
				}
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Getter for the map image.
	 * @return The map image.
	 */
	public BufferedImage getImage() {
		return this.image;
	}
	
	/**
	 * Getter for the player's spawn point.
	 * @return The player's spawn point.
	 */
	public Position2D getPlayerSpawn() {
		return this.playerSpawn;
	}
	
	/**
	 * Getter for the list of possible zombie spawn positions.
	 * @return The list of possible zombie spawn positions.
	 */
	public ArrayList<Position2D> getZombieSpawns() {
		return this.zombieSpawns;
	}
	
	/**
	 * Getter for the list of all robot recharge station positions.
	 * @return The list of all robot recharge station positions.
	 */
	public ArrayList<Position2D> getRobotStations() {
		return this.robotStations;
	}
}