package game;

import actors.Player;

/**
 * A collection of objects describing the elements involved in a game session, such as all of
 * the actors, the environment map, and more.
 */
public class Session {
	
	/**
	 * The data for the environment that the actors reside in.
	 */
	private MapData mapData;
	
	/**
	 * The player that the user has control over for this session.
	 */
	private Player player;
	
	/**
	 * The sole constructor.
	 * Given a file URL to a directory, get the proper files to build the environment.
	 * @param dirURL The file URL to the directory containing the info for the desired map.
	 */
	public Session(String dirURL) {
		this.mapData = new MapData(dirURL);
		this.player  = new Player(this.mapData.getPlayerSpawn());
	}
	
	/**
	 * Getter for the session's map's data.
	 * @return The session's map's data.
	 */
	public MapData getMapData() {
		return this.mapData;
	}
	
	/**
	 * Getter for the current player.
	 * @return The current player.
	 */
	public Player getPlayer() {
		return this.player;
	}
}