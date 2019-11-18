package game;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import actors.Position;

/**
 * Contains the data for the appearance and functionality of a map, including the image rendered
 * on the screen for the map, spawn points, etc.
 */
public class MapData {
	
	/**
	 * The RGB pixel data for the image of the map.
	 */
	private BufferedImage image;
	
	/**
	 * The spawn point for the player, when the session using this map starts.
	 */
	private Position playerSpawn;
	
	/**
	 * A list of all of the possible zombie spawns.
	 */
	private ArrayList<Point> zombieSpawns;
	
	/**
	 * A list of all of the stations that a robot can recharge at.
	 */
	private ArrayList<Point> robotStations;
	
	/**
	 * The sole constructor.
	 * Takes a folder URL, which contains at least the map's image and a text file describing
	 * the player's spawn point, and a nonzero positive number of zombie spawns and robot
	 * recharge stations.
	 * @param dirURL
	 */
	public MapData(String dirURL) {
		this.image         = null;
		this.playerSpawn   = new Position(-1, -1);
		this.zombieSpawns  = new ArrayList<Point>();
		this.robotStations = new ArrayList<Point>();
		
		try {
			this.image = ImageIO.read(
				Session.class.getResource("resources/maps/" + dirURL + "/map.png")
			);
			
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					Session.class.getResource("resources/maps/" + dirURL + "/data.txt").getPath()
			)));
			
			String line;
			String[] values;
			ArrayList<Point> lastDeclaration = null;
			while ((line = reader.readLine()) != null) {
				if (line.charAt(0) == '\t') {
					values = line.trim().split(",");
					lastDeclaration.add(new Point(
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
	public Position getPlayerSpawn() {
		return this.playerSpawn;
	}
	
	/**
	 * Getter for the list of possible zombie spawns.
	 * @return The list of possible zombie spawns.
	 */
	public ArrayList<Point> getZombieSpawns() {
		return this.zombieSpawns;
	}
	
	/**
	 * Getter for the list of all robot recharge stations.
	 * @return The list of all robot recharge stations.
	 */
	public ArrayList<Point> getRobotStations() {
		return this.robotStations;
	}
}