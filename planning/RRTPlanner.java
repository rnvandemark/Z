package planning;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Random;

import actors.Position2D;
import game.MapData;

public class RRTPlanner extends Planner {
	
	private final static double INTERP_DISTANCE = 5;
	
	private final static double DISC_DISTANCE_RATIO = 0.5;
	
	private boolean useBestEffort;
	
	private Random random;
	
	public RRTPlanner(MapData initialMapData, boolean useBestEffort) {
		super(new DiscretizedMap(1), initialMapData);
		this.useBestEffort = useBestEffort;
		this.random = new Random();
	}
	
	public SimpleEntry<Boolean, Position2D> pathIsClear(Position2D start, Position2D goal) {
		DiscretizedMap mapRep = (DiscretizedMap)this.mapRepresentation;
		
		double stepDistance  = DISC_DISTANCE_RATIO * mapRep.getDiscretizationRatio();
		double stepAngle     = start.angleBetween(goal);
		double totalDistance = start.distanceBetween(goal);
		
		double displacement     = 0.0;
		int exitStatus          = 0;
		Position2D stepPosition = new Position2D(-1, -1);
		
		int px, py;
		Position2D furthestValid = null;
		
		while (exitStatus == 0) {
			if (displacement >= totalDistance) {
				displacement = totalDistance;
				exitStatus   = 2;
			}
			
			stepPosition.set(
				start.x + (displacement * Math.cos(stepAngle)),
				start.y + (displacement * Math.sin(stepAngle))
			);
			
			px = (int)Math.round(stepPosition.x);
			py = (int)Math.round(stepPosition.y);
			if (!mapRep.openAtOriginal(px, py)) {
				exitStatus = 1;
			} else {
				if (furthestValid == null)
					furthestValid = new Position2D();
				furthestValid.set(px, py);
			}
			
			displacement += stepDistance;
		}
		
		if (exitStatus == 2) {
			furthestValid.set(goal.x, goal.y);
		}
		
		return new SimpleEntry<Boolean, Position2D>(exitStatus == 2, furthestValid);
	}

	@Override
	public PlannedPath generatePath(Position2D start, Position2D goal) {
		RRTNode startNode = new RRTNode(null, start);
		RRTNode finalNode = null;
		
		if (this.pathIsClear(start, goal).getKey().booleanValue()) {
			finalNode = new RRTNode(startNode, goal);
		} else {
			Position2D randomPosition = new Position2D(-1, -1);
			SimpleEntry<RRTNode, Double> closestPair;
			RRTNode closestNode;
			double closestDistance, closestAngle;
			SimpleEntry<Boolean, Position2D> interpolationPair;
			Position2D interpolatedPosition, bestEffortPosition;
			boolean interpolationValid;
			
			ArrayList<RRTNode> nodes = new ArrayList<RRTNode>();
			nodes.add(startNode);
			
			while ((finalNode == null) && (nodes.size() < 10000)) {
				randomPosition.set(
					random.nextDouble() * MapData.MAP_WIDTH,
					random.nextDouble() * MapData.MAP_HEIGHT
				);
				
				closestPair     = RRTNode.closestTo(nodes, randomPosition);
				closestNode     = closestPair.getKey();
				closestDistance = closestPair.getValue();
				closestAngle    = closestNode.angleBetween(randomPosition);
				
				if (closestDistance < INTERP_DISTANCE)
					interpolatedPosition = randomPosition;
				else
					interpolatedPosition = new Position2D(
						closestNode.position.x + (INTERP_DISTANCE * Math.cos(closestAngle)),
						closestNode.position.y + (INTERP_DISTANCE * Math.sin(closestAngle))
					);
				
				interpolationPair  = this.pathIsClear(closestNode.position, interpolatedPosition);
				interpolationValid = interpolationPair.getKey().booleanValue();
				bestEffortPosition = interpolationPair.getValue();
				
				if (interpolationValid || (this.useBestEffort && (bestEffortPosition != null))) {
					RRTNode newNode = new RRTNode(closestNode, bestEffortPosition);
					nodes.add(newNode);
					
					if (this.pathIsClear(newNode.position, goal).getKey().booleanValue()) {
						finalNode = new RRTNode(newNode, goal);
					}
				}
			}
		}
		
		if (finalNode == null) {
			return null;
		} else {
			PlannedPath path = new PlannedPath();
			
			RRTNode earliestConnection, toAdd = finalNode;
			Position2D lastAddedPosition;
			while (toAdd != null) {
				lastAddedPosition = toAdd.position;
				path.addFirst(lastAddedPosition);
				
				earliestConnection = toAdd.parent;
				if (earliestConnection != null) {
					earliestConnection = earliestConnection.parent;
					toAdd = earliestConnection;
					
					while (toAdd != null) {
						if (this.pathIsClear(toAdd.position, lastAddedPosition).getKey().booleanValue()) {
							earliestConnection = toAdd;
						}
						
						toAdd = toAdd.parent;
					}
				}
				
				toAdd = earliestConnection;
			}
			
			return path;
		}
	}

	@Override
	public boolean salvagePath(PlannedPath old, Position2D newStart, Position2D newGoal) {
		return false;
	}
	
	private static class RRTNode {
		
		private RRTNode parent;
		
		private Position2D position;
		
		public RRTNode(RRTNode parent, Position2D position) {
			this.parent   = parent;
			this.position = position;
		}
		
		public double distanceTo(Position2D other) {
			return position.distanceBetween(other);
		}
		
		public double angleBetween(Position2D other) {
			return position.angleBetween(other);
		}
		
		public static SimpleEntry<RRTNode, Double> closestTo(ArrayList<RRTNode> nodes, Position2D other) {
			if (nodes.isEmpty()) {
				return null;
			} else {
				RRTNode closest = nodes.get(0);
				double distance = closest.distanceTo(other);
				
				RRTNode n;
				double d;
				for (int i = 1; i < nodes.size(); i++) {
					n = nodes.get(i);
					d = n.distanceTo(other);
					if (d < distance) {
						closest  = n;
						distance = d;
					}
				}
				
				return new SimpleEntry<RRTNode, Double>(closest, distance);
			}
		}
	}
}