package gtcmtswarmbot.mobilecontrol.enums;

public enum Mapping{
    NONE(0),ANGLE (1), NEIGHBOR(2), SPEED(3),
    ANGLE_EMBELLISH(17), NEIGBHOR_EMBELLISH(18),SPEED_EMBELLISH(19), 
    PROXIMITY1(24);
    
    
    private int map;
    Mapping(int code) {
        this.setMap(code);

    }
	public void setMap(int map) {
		this.map = map;
	}
	public int getMap() {
		return map;
	}
}
