package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import actors.Actor;
import actors.Position2D;

/**
 * Contains the data for the appearance and functionality of a map, including the image rendered
 * on the screen for the map, spawn points, etc.
 */
public class MapData {
	
	/**
	 * The required width, in pixels, of the map's PNG file.
	 */
	public final static int MAP_WIDTH  = 600;
	
	/**
	 * The required height, in pixels, of the map's PNG file.
	 */
	public final static int MAP_HEIGHT = 400;
	
	/**
	 * The size, in pixels, of a cell in the discretized map. The total number of pixels in the
	 * area of a single cell then becomes the square of this value.
	 */
	public final static int DISCRETIZATION_GRID_SIZE = 3;
	
	/**
	 * The RGB pixel data for the image of the map that is rendered to the screen.
	 */
	private BufferedImage displayedImage;
	
	/**
	 * The RGB pixel data for the image of the map that is inflated to account for actor
	 * dimensionality, so path planning does not produce impossible paths and movement does not
	 * allow any actor to traverse an occupied cell/pixel.
	 */
	private BufferedImage inflatedImage;
	
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
	 * A pseudo-random number generator to use for generating random spawn points.
	 */
	private final Random random;
	
	/**
	 * The sole constructor.
	 * Takes a folder URL, which contains at least the map's image and a text file describing
	 * the player's spawn point, and a nonzero positive number of zombie spawns and robot
	 * recharge stations.
	 * @param dirURL The map's directory name.
	 */
	public MapData(String dirURL) {
		this.displayedImage = null;
		this.inflatedImage  = null;
		this.playerSpawn    = new Position2D(-1, -1);
		this.zombieSpawns   = new ArrayList<Position2D>();
		this.robotStations  = new ArrayList<Position2D>();
		this.random         = new Random();
		
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
			this.displayedImage = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x < MAP_WIDTH; x++)
				for (int y = 0; y < MAP_HEIGHT; y++)
					this.displayedImage.setRGB(
						x,
						y,
						originalImage.getRGB(x, y) == whiteRGB ? whiteRGB : blackRGB
					);
			
			this.inflatedImage = new BufferedImage(
				this.displayedImage.getWidth(),
				this.displayedImage.getHeight(),
				this.displayedImage.getType()
			);
			
			Graphics2D g2D = this.inflatedImage.createGraphics();
			g2D.setColor(Color.WHITE);
			g2D.fillRect(0, 0, this.inflatedImage.getWidth(), this.inflatedImage.getHeight());

			g2D.setColor(Color.BLACK);
			for (int x = 0; x < this.displayedImage.getWidth(); x++)
				for (int y = 0; y < this.displayedImage.getHeight(); y++)
					if (this.displayedImage.getRGB(x, y) == blackRGB)
						g2D.fillOval(
							x - Actor.RADIUS, y - Actor.RADIUS,
							Actor.RADIUS * 2, Actor.RADIUS * 2
						);
			
			g2D.dispose();
			
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
	 * Get whether or not a position is valid on the map.
	 * @param position The position in question.
	 * @return Whether or not the position is valid.
	 */
	public boolean positionIsValid(Position2D position) {
		return this.inflatedImage.getRGB((int)position.x, (int)position.y) == Color.WHITE.getRGB();
	}
	
	/**
	 * Get the position for a random spawn point for a zombie.
	 * @return The random spawn point.
	 */
	public Position2D getRandomZombieSpawnPoint() {
		if (this.zombieSpawns.isEmpty()) {
			throw new RuntimeException("No zombie spawn points to select from.");
		}
		
		return this.zombieSpawns.get(this.random.nextInt(this.zombieSpawns.size()));
	}
	
	/**
	 * Getter for the image of the map that is to be rendered onto the screen.
	 * @return The rendered map image.
	 */
	public BufferedImage getDisplayedImage() {
		return this.displayedImage;
	}
	
	/**
	 * Getter for the image of the map that is representative of a traversable environment.
	 * @return The traversable map image.
	 */
	public BufferedImage getInflatedImage() {
		return this.inflatedImage;
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