package planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import actors.Position2D;
import game.MapData;
import planning.VisibilityGraph.VGEdge;
import planning.VisibilityGraph.VGNode;
import util.Couple;

/**
 * An implementation of a path planner that uses a visibility graph to perform
 * Dijkstra's algorithm.
 */
public class VGDijkstraPlanner extends AbstractDijkstraPlanner<VGNode> {
	
	/**
	 * The sole constructor.
	 * Takes the arguments necessary to create a visibility graph to use for
	 * planning, as well the map data used for initializing the planner.
	 * @param discretizationRatio The magnitude of discretization to perform on
	 * the map.
	 * @param cleanlinessThreshold The threshold that defines the degree to which
	 * the clean up procedure should look for redundant nodes.
	 * @param initialMapData The map data used for initializing the planner.
	 */
	public VGDijkstraPlanner(
			int discretizationRatio, double cleanlinessThreshold, MapData initialMapData) {
		super(
			new VisibilityGraph(discretizationRatio, cleanlinessThreshold),
			initialMapData
		);
	}
	
	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected Position2D getPositionOf(VGNode element) {
		return element.getPosition();
	}

	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected Collection<VGNode> getTraversalMediumCollection() {
		return ((VisibilityGraph)this.mapRepresentation).getNodeSet();
	}

	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected Couple<VGNode, VGNode>prepareGeneration(Position2D start, Position2D goal) {
		VisibilityGraph vg   = (VisibilityGraph)this.mapRepresentation;
		Set<VGNode> allNodes = vg.getNodeSet();
		
		VGNode startNode = new VGNode(start);
		VGNode goalNode  = new VGNode(goal);
		
		vg.addEdgesFor(startNode, allNodes);
		vg.addEdgesFor(goalNode,  allNodes);
		
		return new Couple<VGNode, VGNode>(startNode, goalNode);
	}

	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected boolean setInitialDistances(VGNode start, VGNode goal) {
		for (VGNode n : this.getTraversalMediumCollection())
			this.elementMap.setTentativeDistanceFor(
				n,
				n.equals(start) ? 0.0 : Double.POSITIVE_INFINITY
			);
		
		return true;
	}

	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected Collection<VGNode> getNeighborsFor(VGNode element) {
		ArrayList<VGNode> nodes = new ArrayList<VGNode>();
		for (VGEdge e : ((VisibilityGraph)this.mapRepresentation).getEdgesFor(element))
			nodes.add(e.getDestination());
		return nodes;
	}

	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected double distanceBetween(VGNode source, VGNode destination) {
		for (VGEdge e : ((VisibilityGraph)this.mapRepresentation).getEdgesFor(source))
			if (e.getDestination().equals(destination))
				return e.getWeight();
		
		throw new RuntimeException(
			"Unable to find expected visibility graph connection."
		);
	}

	/**
	 * Override from the {@link planning.AbstractDijkstraPlanner} method.
	 */
	@Override
	protected boolean closeGeneration(VGNode start, VGNode goal) {
		VisibilityGraph vg = (VisibilityGraph)this.mapRepresentation;
		return vg.removeEdgesFor(start) && vg.removeEdgesFor(goal);
	}
}