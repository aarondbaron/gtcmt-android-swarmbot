package gtcmtswarmbot.mobilecontrol.enums;

public enum Mapping{
    NONE(0),ANGLE (1), NEIGHBOR(2), SPEED(3),
    ANGLE_EMBELLISH(17), NEIGHBOR_EMBELLISH(18),SPEED_EMBELLISH(19), 
    PROXIMITY1(24), FIGHTSONG_ANGLE(124), FIGHTSONG_NEIGHBOR(111), FIGHTSONG_SPEED(125);
    
    
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
