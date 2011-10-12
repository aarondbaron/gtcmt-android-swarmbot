package com.google.android.DemoKit;

// a class for keeping track of other bots
public class Bot
{
	int x, y;
	int vx, vy;
	float angle;
	float azimuthAngle;

	int ID;

	Bot()
	{

	}

	Bot(int x, int y)
	{
	}

	int[] getPos()
	{	
		return new int[]{x,y};
	}
	void setPos(int x, int y)
	{
		this.x=x;
		this.y=y;
	}
	int[] getVel()
	{
		return new int[]{vx,vy};
	}
	void setVel(int vx, int vy)
	{
		this.vx=vx;
		this.vy=vy;

		angle = (float) Math.atan2(vy, vx);

	}

}

