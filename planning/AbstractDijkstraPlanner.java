package planning;

import java.util.Collection;
import java.util.HashMap;

import actors.Position2D;
import game.MapData;
import util.Couple;

/**
 * An abstract class that describes the general procedure for Dijkstra's
 * algorithm, generalized so that it can be ran on different types of map
 * representations and with different heuristics, both of which are meant
 * to be interchangeable for optimal efficiency for the scenario it's being
 * ran in.
 * @param <TraversalMediumType> The data type that the algorithm will use
 * to represent traversable nodes that connect the start and goal positions
 * and can be visited.
 */
public abstract class AbstractDijkstraPlanner<TraversalMediumType> extends Planner {
	
	/**
	 * A container for the maps that are used while undergoing the planning
	 * routine.
	 */
	protected DijkstraHashMap elementMap;
	
	/**
	 * The sole constructor.
	 * Takes the map representation and initial map data that the Planner
	 * class requires. The type of map representation is meant to be flexible,
	 * hence the parameterization that this class has.
	 * @param mapRepresenation The map representation to use.
	 * @param initialMapData The map data to first build the map
	 * representation with.
	 */
	protected AbstractDijkstraPlanner(
			MapRepresentation mapRepresenation, MapData initialMapData) {
		super(mapRepresenation, initialMapData);
		this.elementMap = null;
	}
	
	/**
	 * Given an instance of the traversable medium for this derived instance
	 * of the planner, get its position relative to the environment.
	 * @param element An instance of a traversable element in the environment.
	 * @return This element's position.
	 */
	protected abstract Position2D getPositionOf(TraversalMediumType element);
	
	/**
	 * Get a collection of all the traversable nodes available for
	 * the algorithm to visit.
	 * @return A collection of traversable elements.
	 */
	protected abstract Collection<TraversalMediumType> getTraversalMediumCollection();
	
	/**
	 * Perform any necessary setup procedure before the main part of the path
	 * planning routine starts. Furthermore, create and return a tuple of the
	 * start and goal positions described as traversable nodes.
	 * @param start The starting position for path planning.
	 * @param goal The goal position for path planning.
	 * @return A tuple containing the start and goal nodes.
	 */
	protected abstract Couple<TraversalMediumType, TraversalMediumType>
		prepareGeneration(Position2D start, Position2D goal);
	
	/**
	 * Given the start and goal positions for the path planning routine, set
	 * the initial distances for all of the traversable nodes.
	 * @param start The starting position for path planning.
	 * @param goal The goal position for path planning.
	 * @return Whether or not the distances were set without error.
	 */
	protected abstract boolean setInitialDistances(
		TraversalMediumType start, TraversalMediumType goal);
	
	/**
	 * Get a collection of the elements that are some source element's
	 * neighbors, relative to whatever traversable environment they reside in.
	 * @param element The element to get the neighbors of.
	 * @return A collection of the input element's neighbor.
	 */
	protected abstract Collection<TraversalMediumType> getNeighborsFor(
		TraversalMediumType element);
	
	/**
	 * Get the distance between two traversable elements that are
	 * neighbors, which can also be represented as the cost to traverse from
	 * the first element to the other.
	 * @param source The first element.
	 * @param destination The second element.
	 * @return The distance between the first and second element.
	 */
	protected abstract double distanceBetweenNeighbors(
		TraversalMediumType source, TraversalMediumType destination);
	
	/**
	 * Calculate the weight between the goal and some other specified element
	 * according to the applied heuristic.
	 * @param element The element to get the objective heuristic weight for.
	 * @param goal The traversable element representing the goal.
	 * @return The calculated heuristic weight for the element.
	 */
	protected abstract double calcHeuristicWeightFor(
		TraversalMediumType element, TraversalMediumType goal);
	
	/**
	 * Perform any necessary actions for after the path planning routine has
	 * finished.
	 * @param start The starting position for the path planning that just
	 * occurred.
	 * @param goal The goal position for the path planning that just occurred.
	 * @return Whether or not the closing process was successful.
	 */
	protected abstract boolean closeGeneration(
		TraversalMediumType start, TraversalMediumType goal);
	
	/**
	 * Override from the {@link planning.Planner} method.
	 */
	@Override
	public PlannedPath generatePath(Position2D start, Position2D goal) {
		// Generate the start and goal elements.
		Couple<TraversalMediumType, TraversalMediumType> startAndGoal =
			this.prepareGeneration(start, goal);
		
		// Initialize the container for the maps that this routine uses.
		// TODO: Remove this from here and have this only built when necessary.
		this.elementMap = new DijkstraHashMap(this.getTraversalMediumCollection());
		
		// Initialize the tentative distances and heuristic weights to use.
		if (!this.setInitialDistances(startAndGoal.first, startAndGoal.second))
			return null;
		
		// Declare some reusable variables, and initialize the current
		// element to start the main routine at.
		TraversalMediumType nextElement, currElement = startAndGoal.first;
		double nextDist, currDist, altDist;
		
		// Perform this task until the goal node is marked as visited.
		while (!this.elementMap.isVisited(startAndGoal.second)) {
			// Get the shortest distance required to travel to this element
			// as of now.
			currDist = this.elementMap.getTentativeDistanceFor(currElement);
			
			// Loop through and update the tentative distances for each of
			// this element's neighbor.
			for (TraversalMediumType neighbor : this.getNeighborsFor(currElement)) {
				altDist = currDist + this.distanceBetweenNeighbors(currElement, neighbor);
				if (this.elementMap.getTentativeDistanceFor(neighbor) > altDist) {
					// A shorter path to this neighboring element was found
					// when this current element is the source. Update the maps.
					this.elementMap.setSourceElementFor(neighbor, currElement);
					this.elementMap.setTentativeDistanceFor(neighbor, altDist);
					this.elementMap.setHeuristicWeightFor(
						neighbor,
						altDist + this.calcHeuristicWeightFor(neighbor, startAndGoal.second)
					);
				}
			}
			
			// Mark this element as visited. If this wasn't the goal node, them
			// figure out which element to visit next.
			this.elementMap.markVisited(currElement);
			if (!currElement.equals(startAndGoal.second)) {
				nextDist    = Double.POSITIVE_INFINITY;
				nextElement = null;
				
				// Loop through each node to find the next element in a greedy
				// fashion, by finding the shortest tentative distance for a
				// node that hasn't been visited yet.
				for (TraversalMediumType e : this.getTraversalMediumCollection()) {
					if (!this.elementMap.isVisited(e)) {
						altDist = this.elementMap.getHeuristicWeightFor(e);
						if (altDist < nextDist) {
							nextElement = e;
							nextDist    = altDist;
						}
					}
				}
				
				// Finalize the next element to visit.
				currElement = nextElement;
			}
		}
		
		// Start building a path.
		PlannedPath path = new PlannedPath();
		
		// Given that we have the final element and a map describing each
		// elements' source element, build the map backwards.
		while (currElement != null) {
			path.addFirst(this.getPositionOf(currElement));
			currElement = this.elementMap.getSourceElementFor(currElement);
		}
		
		// Include the original start and goal positions.
		path.setOriginalStartAndGoal(
			this.getPositionOf(startAndGoal.first),
			this.getPositionOf(startAndGoal.second)
		);
		
		// Ensure any closing routine is performed successfully.
		if (!this.closeGeneration(startAndGoal.first, startAndGoal.second))
			throw new RuntimeException("Failed to properly close path generation.");
		
		// Finished, return the path.
		return path;
	}
	
	/**
	 * Override from the {@link planning.Planner} method.
	 */
	@Override
	public boolean salvagePath(PlannedPath old, Position2D newStart, Position2D newGoal) {
		return false;
	}
	
	/**
	 * A container to manage all of the data structures needed for the main
	 * planning routine. These include data structures to describe the
	 * tentative distances, whether or not an element has been marked as
	 * visited, and the best source element, for each element.
	 */
	protected class DijkstraHashMap {
		
		/**
		 * A map to maintain the source element that describes the previous
		 * step of the shortest path back towards the start for each traversable
		 * element.
		 */
		private HashMap<TraversalMediumType, TraversalMediumType> sourceElement;
		
		/**
		 * A map to maintain whether or not each traversable element has been
		 * marked as visited.
		 */
		private HashMap<TraversalMediumType, Boolean> visited;
		
		/**
		 * A map to maintain the tentative distance for each traversable element.
		 */
		private HashMap<TraversalMediumType, Double> tentativeDistances;
		
		/**
		 * A map to maintain the current weighted values for the heuristic.
		 */
		private HashMap<TraversalMediumType, Double> heuristicWeight;
		
		/**
		 * The sole constructor.
		 * Given a collection of traversable elements, initialize all of the
		 * maps to initial states.
		 * @param traversalMediumElements The collection of traversable elements.
		 */
		protected DijkstraHashMap(Collection<TraversalMediumType> traversalMediumElements) {
			this.sourceElement      = new HashMap<TraversalMediumType, TraversalMediumType>();
			this.visited            = new HashMap<TraversalMediumType, Boolean>();
			this.tentativeDistances = new HashMap<TraversalMediumType, Double>();
			this.heuristicWeight    = new HashMap<TraversalMediumType, Double>();
			
			for (TraversalMediumType e : traversalMediumElements) {
				this.sourceElement.put(e, null);
				this.visited.put(e, false);
				this.tentativeDistances.put(e, 0.0);
				this.heuristicWeight.put(e, 0.0);
			}
		}
		
		/**
		 * Getter for the tentative distance of an element.
		 * @param element The element to get the tentative distance for,
		 * @return The tentative distance of the element.
		 */
		protected double getTentativeDistanceFor(TraversalMediumType element) {
			return this.tentativeDistances.get(element).doubleValue();
		}
		
		/**
		 * Getter for whether or not a specified traverasble element has been
		 * marked as visited,
		 * @param element The element to check the visitation status of.
		 * @return Whether or not the element has been visited.
		 */
		protected boolean isVisited(TraversalMediumType element) {
			return this.visited.get(element).booleanValue();
		}
		
		/**
		 * Getter for the source element of a specified traversable element.
		 * @param element The element to get the source element of.
		 * @return The source element for the element.
		 */
		protected TraversalMediumType getSourceElementFor(TraversalMediumType element) {
			return this.sourceElement.get(element);
		}
		
		/**
		 * Getter for the current heurisic weight of a specified traversable
		 * element.
		 * @param element The element to get current heuristic weight for.
		 * @return The heuristic weight of the element.
		 */
		protected double getHeuristicWeightFor(TraversalMediumType element) {
			return this.heuristicWeight.get(element).doubleValue();
		}
		
		/**
		 * Set the tentative distance for a traversable element.
		 * @param element The element to set the tentative distance for.
		 * @param distance The new tentative distance for some element.
		 */
		protected void setTentativeDistanceFor(TraversalMediumType element, double distance) {
			this.tentativeDistances.put(element, (Double)distance);
		}
		
		/**
		 * Mark a traversable element as visited.
		 * @param element The element to mark as visited.
		 */
		protected void markVisited(TraversalMediumType element) {
			this.visited.put(element, true);
		}
		
		/**
		 * Set the source element of some other traversable element.
		 * @param element The element to the source of.
		 * @param source The new source element for the first element.
		 */
		protected void setSourceElementFor(TraversalMediumType element, TraversalMediumType source) {
			this.sourceElement.put(element, source);
		}
		
		/**
		 * Set the heuristic weight of a traversable element.
		 * @param element The element to set the weight for.
		 * @param weight The new weight for the specified element.
		 */
		protected void setHeuristicWeightFor(TraversalMediumType element, double weight) {
			this.heuristicWeight.put(element, weight);
		}
	}
}