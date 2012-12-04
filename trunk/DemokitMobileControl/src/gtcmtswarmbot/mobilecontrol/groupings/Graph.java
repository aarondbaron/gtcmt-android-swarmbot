package gtcmtswarmbot.mobilecontrol.groupings;


import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class Graph {
	/* list of all connections */
	protected List<Connection> connections;
	/* list of all registered nodes */
	protected List<Node> nodes;
	/* list of how many connections are associated with a node, for efficiency */
	protected HashMap<Node, Integer> numConnections;

	public Graph(){
		connections = new Vector<Connection>();
		nodes = new Vector<Node>();
		numConnections = new HashMap<Node, Integer>();
	}
	
	public List<Node> getAllNodes() {
		return nodes;
	}

	/* does this graph contain a node with the given value? */
	public boolean hasNode(String value){
		if(getNode(value) != null){
			return true;
		}
		return false;
	}
	
	/* return a node by its value */
	public Node getNode(String value){
		for(Node n : nodes){
			if(n.getValue().equals(value)){
				return n;
			}
		}
		return null;
	}
	
	public void setAllUnvisited(){
		for(Node n : nodes){
			n.setVisited(false);
		}
	}
	
	public void addNode(Node n){
		if(!nodes.contains(n)){
			nodes.add(n);
			numConnections.put(n, Integer.valueOf(0));
		}
	}
	
	public void removeNode(Node n){
		if(!nodes.contains(n)){
			return; /* nothing to remove */
		}
				
		/* if there are any connections to remove... */
		if(numConnections.get(n).intValue() > 0){
			/* remove all connections associated with this node */
			List<Connection> relevantConnections = getConnections(n);
			for(Connection conn : relevantConnections){
				removeConnection(conn);
			}
		}
		
		/* remove from the HashMap */
		numConnections.remove(n);
		
		/* remove the node from the list */
		nodes.remove(n);
	}
	
	/* Add a connection between two nodes with implied "if not exists." 
	 * Order doesn't matter.  All connections are assumed bi-directional.
	 */
	public void addConnection(Node nodeA, Node nodeB){
		/* check for existence by seeing if one node is connected
		 * to another.  Then add a connection.
		 */
		
		/* first, make sure both nodes are registered */
		if(!nodes.contains(nodeA) || !nodes.contains(nodeB)){
			return;
		}
		
		/* We arbitrarily choose to check for existence from nodeA's perspective.
		 * We could increase efficiency by checking the node with the least connections,
		 * but that makes for more complicated code.
		 */
		
		if(!getConnectedNodes(nodeA).contains(nodeB)){
			/* no previous connection exists, so make a new one */
			connections.add(new Connection(nodeA, nodeB));
			/* and increment numConnections for each */
			
			int numConnectionsA = numConnections.get(nodeA);
			int numConnectionsB = numConnections.get(nodeB);
			
			numConnections.put(nodeA, Integer.valueOf(numConnectionsA + 1));
			numConnections.put(nodeB, Integer.valueOf(numConnectionsB + 1));
		}
	}
	
	/* Remove a particular connection given the nodes */
	public void removeConnection(Node nodeA, Node nodeB){
		for(Connection conn : connections){
			if(conn.getNodeA().equals(nodeA) && conn.getNodeB().equals(nodeB)){
				removeConnection(conn);
				break;
			} else if(conn.getNodeA().equals(nodeB) && conn.getNodeB().equals(nodeA)){
				removeConnection(conn);
				break;
			}
		}
	}
	
	/* Remove a particular connection given a Connection object */
	public void removeConnection(Connection conn){
		int numConnectionsA = numConnections.get(conn.getNodeA()).intValue();
		int numConnectionsB = numConnections.get(conn.getNodeB()).intValue();
		
		numConnections.put(conn.getNodeA(), Integer.valueOf(numConnectionsA - 1));
		numConnections.put(conn.getNodeB(), Integer.valueOf(numConnectionsB - 1));
		
		connections.remove(conn);
	}
	
	/* Get all connections associated with the input node.
	 * This method mainly exists for efficient connection
	 * removal. */
	public List<Connection> getConnections(Node node){
		List<Connection> relevantConns = new Vector<Connection>();
		int foundConnections = 0;
		
		/* make sure the node is registered */
		if(!nodes.contains(node)){
			return relevantConns;
		}
		
		int maxConnections = numConnections.get(node);
		
		if(foundConnections == maxConnections){
			return relevantConns; /* stop early if there are NO connections to be found */
		}
		
		for(Connection conn : connections){
			if(conn.getNodeA().equals(node) || conn.getNodeB().equals(node)){
				relevantConns.add(conn);
				if(foundConnections == maxConnections){
					break; /* stop early if we've found all the connections already */
				}
			}
		}
		
		return relevantConns;
	}
	
	/* get all nodes connected to the input node.
	 * This method is preferable to getAllConnections because
	 * you don't have to deal with Connection objects. */
	public List<Node> getConnectedNodes(Node node){
		List<Connection> relevantConnections = getConnections(node);
		List<Node> connectedNodes = new Vector<Node>();
		
		/* for each connection, add the OTHER node in the connection
		 * to connectedNodes.
		 */
		for(Connection conn : relevantConnections){
			if(conn.getNodeA().equals(node)){
				connectedNodes.add(conn.getNodeB());
			} else {
				connectedNodes.add(conn.getNodeA());
			}
		}
		
		return connectedNodes;
	}
}
