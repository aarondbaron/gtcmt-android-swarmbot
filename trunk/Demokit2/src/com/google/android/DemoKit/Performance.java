package com.google.android.DemoKit;

import java.util.Vector;

public class Performance implements Runnable{
	
	Vector scenes;
	
	int sceneCounter;
	
	String performanceType;
	
	Performance()
	{
		performanceType = "";
	}
	
	
	public void run()
	{
		update();
	}
	
	public void addScene(Scene s)
	{
		
		scenes.add(s);
	}
	
	
	
	
	public class Scene
	{
		
		Scene()
		{
			
		}
	
	}
	
	
	void update()
	{
		
	}

}
