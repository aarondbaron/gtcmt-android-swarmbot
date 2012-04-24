package gtcmtswarmbot.mobilecontrol.enums;

public enum ControllerCode{
    SYNC(1000),STOPALL (999), MOVE(998), FORMATION(997), ORBIT(996), TUGMOVE(9988), AVATARMOVE(9987), SETSEQUENCE(801),
    MAPPING(800);
    
    
    int code;
    ControllerCode(int code) {
        this.code = code;

    }
    
    
    
    public void setCode(int code) {
		this.code = code;
	}
	public int getCode() {
		return code;
	}
}