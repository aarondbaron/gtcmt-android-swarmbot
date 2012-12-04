package gtcmtswarmbot.mobilecontrol.groupings;

public class Node {
	protected String value;
	protected boolean visited;
	
	public Node(String value){
		this.value = value;
		this.visited = false;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	/* if you want to place Node in a HashMap, you must implement this for efficiency */
	@Override
	public int hashCode(){
		return value.hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Node){
			Node otherNode = (Node)other;
			/* I require that the visited flags also match. */
			if(value.equals(otherNode.getValue()) && visited == otherNode.isVisited()){
				return true;
			}
		}
		return false;
	}
}
