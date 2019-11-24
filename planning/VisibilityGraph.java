package planning;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

import actors.Position2D;
import game.MapData;

/**
 * A representation of an environment that uses a graph to describe what significant
 * points in the environment (in this case, vertices of obstacles) are visible by
 * others significant points. The underlying map is created with a discretized map so
 * took much detail doesn't drastically hurt the performance of the building routine.
 * This class uses a generic "vertex detection" routine to be applicable to any
 * quality discretized map, while also removing redundancy in curved slanted surfaces
 * as best as possible.
 */
public class VisibilityGraph extends MapRepresentation {
	
	/**
	 * The discretized map that this graph is built based on.
	 */
	private DiscretizedMap discMap;
	
	/**
	 * The data that creates the actual graph representation. Each node key in this
	 * has a list of nodes, where each node is both visible by the key node, and the
	 * key node is visible to each node.
	 */
	private HashMap<VGNode, VGEdgeList> nodeMap;
	
	/**
	 * Whether or not this graph should try to be more minimalistic. In other words,
	 * whether or not this graph should attempt to realize that there are multiple
	 * vertices near each other, and that some should be removed because the quality
	 * of the graph can still be maintained. If this value is less than or equal to
	 * zero, the cleanliness routine will not be initiated. Otherwise, this value
	 * describes a distance on the discretized map representation that the graph is
	 * built from.
	 */
	private double cleanlinessThreshold;
	
	/**
	 * The sole constructor.
	 * Takes a discretization ratio to build the discretized map with.
	 * @param discretizationRatio The magnitude of discretization to perform on the
	 * map.
	 * @param cleanlinessThreshold The threshold that defines the degree to which
	 * the clean up procedure should look for redundant nodes.
	 */
	public VisibilityGraph(int discretizationRatio, double cleanlinessThreshold) {
		this.discMap              = new DiscretizedMap(discretizationRatio);
		this.nodeMap              = null;
		this.cleanlinessThreshold = cleanlinessThreshold;
	}
	
	/**
	 * A helper function to get the type of status that a cell in the discretized
	 * map has, considering elements such as the bounds of the map and the nodes
	 * that have already been found.
	 * @param x The x position in the discretized map.
	 * @param y The y position in the discretized map.
	 * @param foundNodes A list of the nodes that have already been confirmed as
	 * significant positions.
	 * @return An int describing the status. Those values can be:<br>
	 * 		-1 if the [x,y] position is outside the bounds of the map,<br>
	 * 		0 if the cell at [x,y] is unoccupied and therefore habitable,<br>
	 * 		1 if the cell at [x,y] is occupied, cannot be habitable, and
	 * 			corresponds to a position that has not already been described by
	 * 			an existing node,<br>
	 * 		2 if the cell at [x,y] is already represented by a node.
	 */
	private int getStatusOf(int x, int y, ArrayList<VGNode> foundNodes) {
		// Check to see if the position is in the bounds of the map.
		if ((x >= 0) && (x < this.discMap.getWidth())
				&& (y >= 0) && (y < this.discMap.getHeight())) {
			
			// The cell is in bounds, check to see if it's occupied.
			if (this.discMap.openAt(x, y)) {
				// It's unoccupied.
				return 0;
			} else {
				// It's occupied, check to see if it represents an existing node.
				Position2D p = new Position2D(x, y);
				
				for (VGNode v : foundNodes) {
					if (v.nearEqualTo(p)) {
						return 2;
					}
				}
				
				// There's no node for this cell, either because it's not a
				// vertex or it hasn't been found yet.
				return 1;
			}
		} else {
			// The cell is out of bounds.
			return -1;
		}
	}
	
	/**
	 * Given a cell, get a list of the neighboring cells that are occupied. In
	 * this case, a position that's out of bounds is considered "occupied".
	 * @param x The x position in the discretized map.
	 * @param y The y position in the discretized map.
	 * @param foundNodes A list of the nodes that have already been confirmed as
	 * significant positions.
	 * @return A list of indices, where the list can have a size between 0 and 8
	 * inclusive, and can contain values describing the index of a cell relative
	 * to the one described by x and y. If the cell at [x,y] is "P", then the
	 * returned indices can be understood as:<br>
	 * <br>
	 * 0  |  1  |  2  <br>
	 * -------------  <br>
	 * 3  |  P  |  4  <br>
	 * -------------  <br>
	 * 5  |  6  |  7  <br>
	 */
	private ArrayList<Integer> getOccupiedNeighbors(int x, int y, ArrayList<VGNode> foundNodes) {
		// Populate an array with the statuses of a cell's neighboring cells.
		int[] n = new int[8];
		n[0] = this.getStatusOf(x - 1, y - 1, foundNodes);
		n[1] = this.getStatusOf(x,     y - 1, foundNodes);
		n[2] = this.getStatusOf(x + 1, y - 1, foundNodes);
		n[3] = this.getStatusOf(x - 1, y    , foundNodes);
		n[4] = this.getStatusOf(x + 1, y    , foundNodes);
		n[5] = this.getStatusOf(x - 1, y + 1, foundNodes);
		n[6] = this.getStatusOf(x,     y + 1, foundNodes);
		n[7] = this.getStatusOf(x + 1, y + 1, foundNodes);
		
		// Create a list of the significant indices.
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < n.length; i++)
			if (n[i] != 0)
				indices.add((Integer)i);
		
		return indices;
	}
	
	/**
	 * A helper function to find the next vertex, given cell position [x,y].
	 * This will either return a position value if a vertex is found, or null if
	 * no new vertex is found. In most cases, if a new vertex is returned, this
	 * will be at [x,y]. If the vertex is on a diagonal, then the vertex returned
	 * could not be at [x,y] but rather at some point on the same diagonal,
	 * because each diagonal should only have one significant point.
	 * @param x The x position in the discretized map.
	 * @param y The y position in the discretized map.
	 * @param foundNodes A list of the nodes that have already been confirmed as
	 * significant positions.
	 * @return The position of a new vertex if one is found, or null if no
	 * vertex is found.
	 */
	private Position2D findVertex(int x, int y, ArrayList<VGNode> foundNodes) {
		Position2D newVertex = null;
		
		// Perform logic based on the status of this cell.
		int status = this.getStatusOf(x, y, foundNodes);
		switch (status) {
			case 0:
			case 2:
				// The cell is either unoccupied or already confirmed to be a
				// vertex, no need to look further.
				break;
			
			case 1: {
				// The cell is occupied, it could be a vertex.
				// Get the neighboring cells that are occupied and perform logic
				// on the number of them that are occupied.
				ArrayList<Integer> indices = this.getOccupiedNeighbors(x, y, foundNodes);
				switch(indices.size()) {
					case 0:
					case 1:
						// A cell with zero or one occupied neighbor is guaranteed
						// to be a vertex.
						newVertex = new Position2D(x, y);
						break;
					
					case 2:
						// A cell with two neighbors is a vertex if it's the two
						// neighbors don't form a straight line with this cell in
						// the middle. When any of those four scenarios occur, the
						// sum of those indices is seven, so use that logic to
						// test for it.
						if ((indices.get(0) + indices.get(1)) != 7)
							newVertex = new Position2D(x, y);
						break;
					
					case 3: {
						// A cell with three neighbors is considered a vertex if
						// a square is formed, including the source cell. In other
						// words, if a right angle is formed around the source
						// cell, this is a vertex. When any of those four scenarios
						// occur, the difference in their positions is always one
						// and two, but the order of them might change.
						int d1 = indices.get(1).intValue() - indices.get(0).intValue();
						int d2 = indices.get(2).intValue() - indices.get(1).intValue();
						if (((d1 == 1) && (d2 == 2)) || ((d1 == 2) && (d2 == 1)))
							newVertex = new Position2D(x, y);
						break;
					}
					
					case 4: {
						// A cell with four neighbors is considered a vertex if
						// a the four create an L shape around the source cell. When
						// any of those eight scenarios occur, the difference in their
						// positions is always a combination of ones, twos, and threes.
						int d1 = indices.get(1).intValue() - indices.get(0).intValue();
						int d2 = indices.get(2).intValue() - indices.get(1).intValue();
						int d3 = indices.get(3).intValue() - indices.get(2).intValue();
						if (((d1 == 1) && (d2 == 1) && (d3 == 1))
								|| ((d1 == 1) && (d2 == 1) && (d3 == 2))
								|| ((d1 == 1) && (d2 == 2) && (d3 == 2))
								|| ((d1 == 2) && (d2 == 1) && (d3 == 1))
								|| ((d1 == 2) && (d2 == 2) && (d3 == 1))
								|| ((d1 == 1) && (d2 == 2) && (d3 == 3))
								|| ((d1 == 3) && (d2 == 2) && (d3 == 1)))
							newVertex = new Position2D(x, y);
						break;
					}
					
					case 5: {
						// A cell with five neighbors could be a part of a diagonal if
						// the shape described by a case with three neighbors is
						// formed, but instead with the neighboring unoccupied cells.
						// Get a list of the indices that are unoccupied.
						ArrayList<Integer> notIndices = new ArrayList<Integer>();
						for (int i = 0; i < 8; i++)
							if (!indices.contains((Integer)i))
									notIndices.add(i);
						
						// Determine how the possible diagonal is pointed.
						int dx, dy = 1;
						if (notIndices.containsAll(Arrays.asList(0, 1, 3))
								|| notIndices.containsAll(Arrays.asList(4, 6, 7))) {
							// The diagonal points down to the left.
							dx = -1;
						} else if (notIndices.containsAll(Arrays.asList(1, 2, 4))
								|| notIndices.containsAll(Arrays.asList(3, 5, 6))) {
							// The diagonal points down to the right.
							dx = 1;
						} else {
							// No diagonal is formed, stop searching.
							break;
						}
						
						// Start searching for the start and end of the diagonal,
						// and stop the search if a vertex describing this diagonal
						// was already found.
						int newX = x, newY = y;
						int itersBackward = 0;
						
						boolean stillDiagonal = true;
						boolean foundExistingVertex = false;
						
						// Search backwards first. Because the vertex search routine
						// with the cell [0,0] and increases in x and y, if a vertex
						// was already found, it would be above it, so save some
						// time by searching there first.
						while (stillDiagonal && (!foundExistingVertex)) {
							newX -= dx;
							newY -= dy;
							
							if (this.getStatusOf(newX, newY, foundNodes) == 2) {
								// A previously existing node was found.
								foundExistingVertex = true;
							} else if (this.getOccupiedNeighbors(newX, newY, foundNodes).size() != 5) {
								// A cell that isn't a part of the diagonal was found.
								stillDiagonal = false;
							} else {
								// Keep searching.
								itersBackward++;
							}
						}
						
						// If an existing vertex was found, exit.
						if (foundExistingVertex)
							break;
						
						int itersForward = 0;
						
						stillDiagonal = true;
						newX = x;
						newY = y;

						// Search forwards now, since an existing vertex wasn't found.
						while (stillDiagonal && (!foundExistingVertex)) {
							newX += dx;
							newY += dy;
							
							if (this.getStatusOf(newX, newY, foundNodes) == 2) {
								// A previously existing node was found.
								foundExistingVertex = true;
							} else if (this.getOccupiedNeighbors(newX, newY, foundNodes).size() != 5) {
								// A cell that isn't a part of the diagonal was found.
								stillDiagonal = false;
							} else {
								// Keep searching.
								itersForward++;
							}
						}
						
						// If an existing vertex was found, exit.
						if (foundExistingVertex)
							break;
						
						
						// Calculate the center point of the diagonal and make a new
						// vertex out of it.
						int minX = x + (itersBackward * -dx);
						int minY = y + (itersBackward * -dy);
						int maxX = x + (itersForward * dx);
						int maxY = y + (itersForward * dy);
						
						newVertex = new Position2D(
							minX + ((maxX - minX) / 2),
							minY + ((maxY - minY) / 2)
						);
					}
				}
				
				break;
			}
			
			// Any other status for this cell is invalid.
			default:
				throw new RuntimeException(
					String.format("Illegal cell position: [%d,%d] -> %d", x, y, status)
				);
		}
		
		return newVertex;
	}
	
	/**
	 * A helper function to add edges to the graph for each confirmed clear line
	 * of sight between vertices.
	 * @param n The node to find edges for.
	 * @param allNodes A collection of all the confirmed nodes in the graph.
	 */
	public void addEdgesFor(VGNode n, Collection<VGNode> allNodes) {
		ArrayList<VGNode> existingDestinations = new ArrayList<VGNode>();
		for (VGEdge e : this.nodeMap.get(n))
			existingDestinations.add(e.destination);
		
		// Loop through all the nodes to look for possible connections.
		for (VGNode o : allNodes) {
			// Don't try to connect a node with itself.
			if (!n.equals(o)) {
				
				// Don't try to connect nodes that are already connected.
				if (!existingDestinations.contains(o)) {
					
					// If the path is clear from one node to another, add two
					// edges between them in the graph, effectively making a
					// bidirected graph.
					if (this.discMap.pathIsClear(
						n.position, o.position, 0.75 * this.discMap.getDiscretizationRatio()
					).getKey().booleanValue()) {
						double distance = n.distanceBetween(o);
						this.nodeMap.get(n).add(new VGEdge(distance, o));
						this.nodeMap.get(o).add(new VGEdge(distance, n));
					}
				}
			}
		}
	}
	
	/**
	 * Remove all the edges in a graph that are from or lead to a specified node,
	 * removing it from the graph.
	 * @param n The node to remove from the graph.
	 * @return Whether or not all values were removed as expected.
	 */
	public boolean removeEdgesFor(VGNode n) {
		boolean result = true;
		
		// Remove all the edges leading to this node.
		for (VGEdge e : this.nodeMap.get(n))
			if (!this.nodeMap.get(e.destination).remove(n))
				result = false;
		
		// Remove this node from the graph's underlying map.
		if (this.nodeMap.remove(n) == null)
			result = false;
		
		return result;
	}
	
	/**
	 * Get a set of the nodes in this graph by getting all of the keys.
	 * @return A set of the nodes in the graph.
	 */
	public Set<VGNode> getNodeSet() {
		return this.nodeMap.keySet();
	}
	
	/**
	 * Given a node in the graph, get the list of edges that it has. If the node
	 * is not in the graph, this will return null.
	 * @param n The node in the graph to get all of its edges for.
	 * @return The list of edges that the provided node has in the graph.
	 */
	public VGEdgeList getEdgesFor(VGNode n) {
		return this.nodeMap.get(n);
	}
	
	/**
	 * A debugging utility to save a sketch of the visibility graph over the
	 * discretized map.
	 * @param outputURL The file URL to save the image to.
	 */
	public void writeImageTo(String outputURL) {
		int dr = this.discMap.getDiscretizationRatio();
		
		BufferedImage img = new BufferedImage(
			dr * this.discMap.getWidth(),
			dr * this.discMap.getHeight(),
			BufferedImage.TYPE_INT_RGB
		);
		
		Graphics2D g2d = (Graphics2D)img.getGraphics();
		
		boolean found;
		for (int i = 0; i < this.discMap.getWidth(); i++) {
			for (int j = 0; j < this.discMap.getHeight(); j++) {
				found = false;
				
				for (VGNode n : this.nodeMap.keySet()) {
					if (n.nearEqualTo(new Position2D(i, j))) {
						found = true;
						break;
					}
				}
				
				if (found)
					g2d.setColor(Color.RED);
				else
					g2d.setColor(this.discMap.openAt(i, j) ? Color.WHITE : Color.DARK_GRAY);
				
				g2d.fillRect(i * dr, j * dr, dr, dr);
			}
		}
		
		g2d.setColor(Color.CYAN);
		for (VGNode n : nodeMap.keySet())
			for (VGEdge e : nodeMap.get(n))
				g2d.drawLine(
					(int)n.position.x * dr,
					(int)n.position.y * dr,
					(int)e.destination.position.x * dr,
					(int)e.destination.position.y * dr
				);
		
		try {
			ImageIO.write(
				img,
				outputURL.substring(outputURL.indexOf(".") + 1), 
				new File(outputURL)
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Override from the {@link planning.DiscretizedMap} method.
	 */
	@Override
	public boolean build(MapData mapData) {
		// Stop tying to build of the discretized map cannot be built.
		if (!this.discMap.build(mapData))
			return false;
		
		// Declare a list of nodes already found.
		ArrayList<VGNode> foundNodes = new ArrayList<VGNode>();
		
		// Go through each cell in the map and search for vertices.
		Position2D v;
		for (int x = 0; x < this.discMap.getWidth(); x++) {
			for (int y = 0; y < this.discMap.getHeight(); y++) {
				if ((v = this.findVertex(x, y, foundNodes)) != null) {
					foundNodes.add(new VGNode(v));
				}
			}
		}
		
		// If desired, clean up the visibility graph.
		if (this.cleanlinessThreshold > 0) {
			HashMap<VGNode, ArrayList<VGNode>> nodesToCheck = new HashMap<VGNode, ArrayList<VGNode>>();
			
			for (VGNode n : foundNodes) {
				nodesToCheck.put(n, new ArrayList<VGNode>());
				for (VGNode o : foundNodes) {
					if ((!n.equals(o)) && (n.distanceBetween(o) <= this.cleanlinessThreshold)) {
						nodesToCheck.get(n).add(o);
					}
				}
			}

			ArrayList<VGNode> l, toRemove;
			int maxSize;
			VGNode maxObj;
			
			boolean continueLoop = true;
			while (continueLoop) {
				toRemove = new ArrayList<VGNode>();
				maxSize  = 0;
				maxObj   = null;
				
				for (VGNode k : nodesToCheck.keySet()) {
					l = nodesToCheck.get(k);
					
					if (l.isEmpty() || (!foundNodes.contains(k))) {
						toRemove.add(k);
					} else if (l.size() > maxSize) {
						maxSize = l.size();
						maxObj  = k;
					}
				}
				
				if (maxObj == null) {
					continueLoop = false;
				} else {
					for (VGNode n : nodesToCheck.get(maxObj))
						foundNodes.remove(n);
					nodesToCheck.get(maxObj).clear();
				}
				
				for (VGNode n : toRemove)
					nodesToCheck.remove(n);
			}
		}
		
		// Declare the node map.
		this.nodeMap = new HashMap<VGNode, VGEdgeList>();
		
		// Give each node a list of empty connections.
		for (VGNode n : foundNodes)
			this.nodeMap.put(n, new VGEdgeList());
		
		// Try and connect each vertex in the list of vertices.
		for (VGNode n : foundNodes)
			this.addEdgesFor(n, foundNodes);
		
		return true;
	}
	
	/**
	 * A simple helper class to represent a node in the graph.
	 */
	public class VGNode {
		
		/**
		 * The position that this node has in the graph.
		 */
		private Position2D position;
		
		/**
		 * The sole constructor.
		 * Simply takes a position.
		 * @param position This node's position.
		 */
		private VGNode(Position2D position) {
			this.position = position;
		}
		
		/**
		 * Get the distance between two nodes' positions.
		 * @param n The other node.
		 * @return The distance between this and the other node.
		 */
		private double distanceBetween(VGNode n) {
			return this.position.distanceBetween(n.position);
		}
		
		/**
		 * Check for close-enough-equality to a provided position.
		 * @param p The position to check for equality with.
		 * @return Whether or not this node's position and the other can be
		 * considered equal.
		 */
		private boolean nearEqualTo(Position2D p) {
			return this.position.equals(p);
		}

		/**
		 * Override from the {@link java.lang.Object} method.
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof VGNode)
				return this.nearEqualTo(((VGNode)obj).position);
			else
				return false;
		}

		/**
		 * Override from the {@link java.lang.Object} method.
		 */
		@Override
		public String toString() {
			return this.position.toString();
		}
	}
	
	/**
	 * A simple helper class to objectify the edges between two nodes, including
	 * the destination node and the weight between them.
	 */
	public class VGEdge {
		
		/**
		 * The "cost" for traversing the connection between this node and the
		 * destination node.
		 */
		private double weight;
		
		/**
		 * The node that this edge leads to.
		 */
		private VGNode destination;
		
		/**
		 * The sole constructor.
		 * Takes all the arguments necessary to build and edge, the traversal weight
		 * and the destination node.
		 * @param weight The weight for traversal.
		 * @param destination The node that traversing this edge leads to.
		 */
		private VGEdge(double weight, VGNode destination) {
			this.weight      = weight;
			this.destination = destination;
		}
		
		/**
		 * Getter for the weight of this edge.
		 * @return The weight of this edge.
		 */
		public double getWeight() {
			return this.weight;
		}
		
		/**
		 * Getter for the destination node of this edge.
		 * @return The destination node of this edge.
		 */
		public VGNode getDestination() {
			return this.destination;
		}
	}
	
	/*
	 * A simple helper class to represent an array list of graph edges.
	 */
	public class VGEdgeList extends ArrayList<VGEdge> {
		
		/**
		 * A generated serial version number for this serializable object.
		 */
		private static final long serialVersionUID = -8606906925166626247L;
		
		/**
		 * A helper function to remove an edge given its destination node.
		 * @param n The destination node of the edge to remove.
		 * @return Whether or not the destination node's edge was
		 * successfully removed.
		 */
		private boolean remove(VGNode n) {
			Iterator<VGEdge> iter = this.iterator();
			
			VGEdge e;
			while (iter.hasNext()) {
				e = iter.next();
				if (e.destination.equals(n)) {
					this.remove(e);
					return true;
				}
			}
			
			return false;
		}
	}
}