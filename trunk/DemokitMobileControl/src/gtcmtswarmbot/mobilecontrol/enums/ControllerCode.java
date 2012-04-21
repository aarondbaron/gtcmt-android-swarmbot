package gtcmtswarmbot.mobilecontrol.enums;

public enum ControllerCode{
    SYNC(1000),STOPALL (999), MOVE(998), FORMATION(997), ORBIT(996), TUGMOVE(9988),
    MAPPING(800);
    
    
    int code;
    ControllerCode(int code) {
        this.code = code;

    }
}