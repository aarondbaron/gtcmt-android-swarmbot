package gtcmtswarmbot.mobilecontrol.enums;

public enum Formation{
    CIRCLE(0), SQUARE(1), HORIZONTAL(2), VERTICAL(3);
    
    
    int formation;
    Formation(int code) {
        this.formation = code;

    }
}