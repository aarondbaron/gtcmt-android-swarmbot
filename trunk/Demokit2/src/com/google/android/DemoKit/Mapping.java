package com.google.android.DemoKit;

public enum Mapping{
    NONE(0),ANGLE (1), NEIGHBOR(2), SPEED(3),
    ANGLE_EMBELLISH(17), NEIGBHOR_EMBELLISH(18),SPEED_EMBELLISH(19);
    
    
    int map;
    Mapping(int code) {
        this.map = code;

    }
}
