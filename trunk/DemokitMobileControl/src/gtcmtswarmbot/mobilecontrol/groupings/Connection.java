package gtcmtswarmbot.mobilecontrol.groupings;


public class Connection {
	protected Node nodeA, nodeB;
	
	public Connection(Node nodeA, Node nodeB){
		this.nodeA = nodeA;
		this.nodeB = nodeB;
	}

	public Node getNodeA() {
		return nodeA;
	}

	public void setNodeA(Node nodeA) {
		this.nodeA = nodeA;
	}

	public Node getNodeB() {
		return nodeB;
	}

	public void setNodeB(Node nodeB) {
		this.nodeB = nodeB;
	}
	
	/* if you want to use in a HashMap, you have to implement this for efficiency */
	@Override
	public int hashCode(){
		return nodeA.hashCode() + nodeB.hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Connection){
			Connection otherConn = (Connection)other;
			/* two true cases, since we don't care about order */
			if( (nodeA.equals(otherConn.getNodeA()) && nodeB.equals(otherConn.getNodeB())) || 
				(nodeA.equals(otherConn.getNodeB()) && nodeB.equals(otherConn.getNodeA())) ){
				return true;
			}
		}
		return false;
	}
}
