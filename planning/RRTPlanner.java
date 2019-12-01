package planning;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import actors.Position2D;
import game.MapData;
import util.Couple;

/**
 * A rapidly-exploring random tree planning implementation. This is a stochastic
 * approach to planning, which continuously adds points to a growing tree of possible
 * paths, until a path to the objective is found. At this point, the path can be
 * shortened through shortcuts. This is not guaranteed, nor likely, to be the most
 * efficient path from start to goal positions. This is more likely to be used with
 * high-dimensional robotics, like six-dimensional or redundant kinematic chains, but
 * it's included in this for fun and completion.
 */
public class RRTPlanner extends Planner {
	
	/**
	 * While building the tree, when a randomly generated point and the straight line
	 * path between it and the position of the closest node in the tree is unobstructed,
	 * a point this far away in the direction to the new point from the existing node will
	 * be added (unless the distance between the two is less than this distance, then it
	 * will be exactly that distance long instead).
	 */
	private final static double INTERP_DISTANCE = 2.5;
	
	/**
	 * The amount of milliseconds to attempt building a path for before timing out / failing.
	 */
	private final static int TIMEOUT_MS = 1500;
	
	/**
	 * While checking if a path is clear, if a discretized point corresponds to an uninhabitable
	 * position on the map, but any point between the start and goal positions was habitable,
	 * then that last successful position can be used as the ending point and added to the tree
	 * if this is true. Otherwise, if this is false, then the entire path must be habitable.
	 */
	private boolean useBestEffort;
	
	/**
	 * The maximum allowable distance between possibly interchangable points when trying to
	 * salvage an existing path generating by this class' planning algorithm.
	 */
	private double salvageThreshold;
	
	/**
	 * The pseudo-random number generator to use to generate the random points.
	 */
	private Random random;
	
	/**
	 * The sole constructor.
	 * Builds the super constructor with the provided map's data and a 1:1 discretized map (in
	 * other words, not discretized), as well as whether or not to use the best effort builder.
	 * @param initialMapData The initial map data to use to build the representation.
	 * @param useBestEffort Whether or not to use the best effort planning.
	 * @param salvageThreshold The threshold used for the salvaging routine.
	 */
	public RRTPlanner(MapData initialMapData, boolean useBestEffort, double salvageThreshold) {
		super(new DiscretizedMap(1), initialMapData);
		
		this.useBestEffort    = useBestEffort;
		this.salvageThreshold = salvageThreshold;
		this.random           = new Random();
	}
	
	/**
	 * Override from the {@link planning.Planner} method.
	 */
	@Override
	public PlannedPath generatePath(Position2D start, Position2D goal) {
		DiscretizedMap mapRep = (DiscretizedMap)this.mapRepresentation;
		
		RRTNode startNode = new RRTNode(null, start);
		RRTNode finalNode = null;
		
		if (mapRep.pathIsClear(start, goal).first) {
			// The path from the start to goal positions is clear, finished.
			finalNode = new RRTNode(startNode, goal);
		} else {
			// There's at least one obstruction between the start and goal positions.
			
			// Declare a lot of variables that will be used often in the routine.
			Position2D randomPosition = new Position2D(-1, -1);
			Couple<RRTNode, Double> closestPair;
			RRTNode closestNode, newNode;
			double closestDistance, closestAngle;
			Couple<Boolean, Position2D> interpolationPair;
			Position2D interpolatedPosition, bestEffortPosition;
			boolean interpolationValid;
			
			// Initialize a list of nodes that connect to build the tree.
			LinkedList<RRTNode> nodes = new LinkedList<RRTNode>();
			nodes.add(startNode);
			
			// Start the build routine, until either a node containing the goal position
			// is added, or the routine times out.
			long startTime = System.currentTimeMillis();
			while ((finalNode == null) && (System.currentTimeMillis() - startTime < TIMEOUT_MS)) {
				// Get a random 2D position in the map.
				randomPosition.set(
					random.nextDouble() * MapData.MAP_WIDTH,
					random.nextDouble() * MapData.MAP_HEIGHT
				);
				
				// Get the node closest to this random point and the angle between those
				// two points.
				closestPair     = RRTNode.closestTo(nodes, randomPosition);
				closestNode     = closestPair.first;
				closestDistance = closestPair.second;
				closestAngle    = closestNode.angleBetween(randomPosition);
				
				// Get the resulting position to check for a valid path between.
				if (closestDistance < INTERP_DISTANCE)
					interpolatedPosition = randomPosition;
				else
					interpolatedPosition = closestNode.position.translated(
						INTERP_DISTANCE * Math.cos(closestAngle),
						INTERP_DISTANCE * Math.sin(closestAngle)
					);
				
				// Check if the path is clear and the last habitable position.
				interpolationPair  = mapRep.pathIsClear(closestNode.position, interpolatedPosition);
				interpolationValid = interpolationPair.first;
				bestEffortPosition = interpolationPair.second;
				
				// If the last valid position can be used, add a new node to the tree.
				if (interpolationValid || (this.useBestEffort && (bestEffortPosition != null))) {
					newNode = new RRTNode(closestNode, bestEffortPosition);
					nodes.add(newNode);
					
					// If the path between this new node and the goal position is clear,
					// finish the search by creating the final connection.
					if (mapRep.pathIsClear(newNode.position, goal).first)
						finalNode = new RRTNode(newNode, goal);
				}
			}
		}
		
		// Initialize the path to be null, in case the timeout occurred.
		PlannedPath path = null;
		
		// Build the path if the final node was created, and also take the time for
		// shortcuts.
		if (finalNode != null) {
			path = new PlannedPath();
			
			RRTNode earliestConnection, toAdd = finalNode;
			Position2D lastAddedPosition;
			
			// Work backwards to look for shortcuts from one node in the path to another.
			while (toAdd != null) {
				lastAddedPosition = toAdd.position;
				path.addFirst(lastAddedPosition);
				
				// This node's parent is obviously able to connect to its parent, so start
				// the search at its grandparent.
				earliestConnection = toAdd.parent;
				if (earliestConnection != null) {
					earliestConnection = earliestConnection.parent;
					toAdd = earliestConnection;
					
					// Work backwards in the tree until the start node is reached and passed.
					while (toAdd != null) {
						if (mapRep.pathIsClear(
								toAdd.position, lastAddedPosition
							).first) {
							// The path between a node and some other node before its parent
							// is clear, keep a reference to it to make a shortcut later
							// (unless an earlier connection is found).
							earliestConnection = toAdd;
						}
						
						// Continue the loop.
						toAdd = toAdd.parent;
					}
				}
				
				// The earliest connection is found and will be added during the next loop, or
				// it's null and the loop exits.
				toAdd = earliestConnection;
			}
			
			// Set the original start and goal positions.
			path.setOriginalStartAndGoal(
				new Position2D(startNode.position),
				new Position2D(finalNode.position)
			);
		}
		
		// Return the built and shortened path.
		return path;
	}
	
	/**
	 * Override from the {@link planning.Planner} method.
	 */
	@Override
	public boolean salvagePath(PlannedPath old, Position2D newStart, Position2D newGoal) {
		// If the old path is unable to be saved, don't try.
		if (old != null) {
			if (old.size() > 1) {
				// A path can be salvaged if it makes sense for it to be (in other words, if
				// there isn't a direct line of sight between the new start and goal positions)
				// and if the positions are within some predefined threshold.
				DiscretizedMap mapRep = (DiscretizedMap)this.mapRepresentation;
				if (!mapRep.pathIsClear(newStart, newGoal).first) {
					if (newStart.equals(old.getOriginalStart(), this.salvageThreshold)
							&& newGoal.equals(old.getOriginalGoal(), this.salvageThreshold)) {
						old.removeLast();
						old.addLast(newGoal);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * A class to create a simple reverse linked list of 2D positions. Each node has a position
	 * and a reference to its parent node.
	 */
	private static class RRTNode {
		
		/**
		 * The node that this node branches off of.
		 */
		private RRTNode parent;
		
		/**
		 * The position that this node represents in the tree.
		 */
		private Position2D position;
		
		/**
		 * The sole constructor.
		 * Completely builds the node with a parent and position.
		 * @param parent The parent node to this node.
		 * @param position The position corresponding to this node.
		 */
		public RRTNode(RRTNode parent, Position2D position) {
			this.parent   = parent;
			this.position = position;
		}
		
		/**
		 * Calculates the distance between this node and a 2D position.
		 * @param other The point to get the distance between.
		 * @return The distance between this node and the position.
		 */
		public double distanceTo(Position2D other) {
			return this.position.distanceBetween(other);
		}

		/**
		 * Calculates the angle between this node and a 2D position.
		 * @param other The point to get the angle between.
		 * @return The angle between this node and the position.
		 */
		public double angleBetween(Position2D other) {
			return this.position.angleBetween(other);
		}
		
		/**
		 * Given a list of existing nodes, get the node closest to a 2D position.
		 * @param nodes The list of nodes to search through. This is assumed to have a
		 * size of at least one.
		 * @param other The position to find a nearby node for.
		 * @return An effective tuple of size two, the first value being the the closest
		 * node, the second one being the distance between the node and the point.
		 */
		public static Couple<RRTNode, Double> closestTo(
			LinkedList<RRTNode> nodes, Position2D other) {
			if (nodes.isEmpty()) {
				return null;
			} else {
				Iterator<RRTNode> iter = nodes.iterator();
				
				RRTNode closest = iter.next();
				double distance = closest.distanceTo(other);
				
				RRTNode n;
				double d;
				while (iter.hasNext()) {
					n = iter.next();
					d = n.distanceTo(other);
					if (d < distance) {
						closest  = n;
						distance = d;
					}
				}
				
				return new Couple<RRTNode, Double>(closest, distance);
			}
		}
		
		/**
		 * Override from the {@link java.lang.Object} method.
		 */
		@Override
		public String toString() {
			String s = this.position.toString();
			
			if (this.parent != null)
				s += (" <- " + this.parent.toString());
			
			return s;
		}
	}
}