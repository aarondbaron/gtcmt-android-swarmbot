package com.google.android.DemoKit;

import java.util.Vector;

public class Performance implements Runnable{
	
	Vector<Scene> scenes;
	
	Song song;
	
	int sceneCounter;
	Scene currentScene;
	
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
	
	public Scene getScene(int i)
	{
		return scenes.get(i%scenes.size());
	}
	public void nextScene()
	{
		this.sceneCounter++;
		currentScene=getScene(sceneCounter);
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
