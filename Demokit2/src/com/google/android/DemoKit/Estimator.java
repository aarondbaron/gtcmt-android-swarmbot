package com.google.android.DemoKit;

public class Estimator {
	
	PVector v;
	float ang;
	
	int n;
	
	int[] px,py;
	long[] times;
	long timer;
	
	int iter=0;
	
	long interval;
	
	BoeBotController bbc;
	
	Estimator(BoeBotController bbc)
	{
		this.bbc=bbc;
		interval = System.currentTimeMillis();
		v = new PVector();
		
		n=10;
		
		px= new int[n];
		py= new int[n];
		times = new long[n];
		timer=System.currentTimeMillis();
		
		
	}
	
	float getAngle()
	{
		
		//source 1 -- camera position
		// source 2 -- camera angle
		// source 3 --  compass
		float source1=v.heading2D()+(float)Math.PI;
		float source2=source1;
		float source3=source1;
		
		ang=.5f*(source1) + .5f*(source2) + .5f*source3;
		
		return ang;
	}
	
	
	

}
