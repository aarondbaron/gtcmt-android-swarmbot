package gtcmtswarmbot.mobilecontrol.groupings;

import java.util.List;
import java.util.Vector;

public class Traverse {
	public static List<List<String>> getGroupings(List<List<String>> registry){
		List<List<String>> groupings = new Vector<List<String>>();
		Graph graph = new Graph();
		
		// Step 1, Building connections between values in the same groupings.
		//  So 1: A B C will build three nodes with connections between them.
		
		// For each grouping
		for(int outer_index = 0; outer_index < registry.size(); outer_index++){
			
			/* First, Make sure a Node exists for every inner value. */
			for(int inner_index = 0; inner_index < registry.get(outer_index).size(); inner_index++){
				String inner_node_value = registry.get(outer_index).get(inner_index);
				
				if(!graph.hasNode(inner_node_value)){
					graph.addNode(new Node(inner_node_value));
				}
			}
			
			/* Second, connect all inner nodes. */
			for(int inner_index = 0; inner_index < registry.get(outer_index).size(); inner_index++){
				String inner_node_value = registry.get(outer_index).get(inner_index);
				Node inner_node = graph.getNode(inner_node_value);
				
				for(int other_inner_index = 0; other_inner_index < registry.get(outer_index).size(); other_inner_index++){
					if(other_inner_index == inner_index){
						continue; /* don't make a connection between the same nodes */
					}
					
					String other_inner_node_value = registry.get(outer_index).get(other_inner_index);
					graph.addConnection(inner_node, graph.getNode(other_inner_node_value));
				}
			}
			
		}
		
		/* set all unvisited to make sure we can do step 2 */
		graph.setAllUnvisited();
		
		// Step 2, now that all connections are formed, we want 1 grouping per connected sub-graph. 
		//  This can be done through an exhaustive DFS with node coloring.
		
		for(Node node : graph.getAllNodes()){
			if(node.isVisited()){ /* skip if the node is already visited */
				continue;
			}
			List<String> collectedValues = new Vector<String>();
			traverse(node, collectedValues, graph);
			groupings.add(collectedValues);
		}
		
		return groupings;
	}
	
	/* Traverse a node, adding that node's value to collectedValues. */ 
	/* Then traverse its children, recursively adding to collectedValues. */
	/* Will paint each visited node as visited */
	private static void traverse(Node node, List<String> collectedValues, Graph graph){
		collectedValues.add(node.getValue());
		node.setVisited(true);
		
		for(Node connected_node : graph.getConnectedNodes(node)){
			if(!connected_node.isVisited()){
				traverse(connected_node, collectedValues, graph);
			}
		}
	}
}
