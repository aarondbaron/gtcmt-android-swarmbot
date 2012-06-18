package com.google.android.DemoKit;

 

import java.util.LinkedHashSet;
import java.util.Vector;

// a class for keeping track of other bots
public class Bot
{
	int x, y;
	int vx, vy;
	private float angle;
	float azimuthAngle;
	float camang;

	int ID;
	
	boolean isNeighbor;
	boolean positionLost;
	
	float distToMe;
	
	int numN;
	
	//PVector vel;
	
	BoeBotController bbc;
	
	
	Vector neighbors;

	Vector extendedNeighbors;

	boolean queried;
	
	int numNeighbors;


	Bot(BoeBotController bbc)
	{
		//vel = new PVector();
		this.bbc=bbc;
		
		neighbors = new Vector();
		extendedNeighbors = new Vector();
	}

	Bot(int x, int y, BoeBotController bbc)
	{
		this.bbc=bbc;
		this.x=x;
		this.y=y;
		//vel = new PVector();
		
		neighbors = new Vector();
		extendedNeighbors = new Vector();
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

		setAngle((float) Math.atan2(vy, vx));

	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getAngle() {
		return angle;
	}
//////////////////////////////////////////////////
	
	
	
	Vector getNeighbors( )
	  {

		PVector loc = new PVector(this.x,this.y);
	    numNeighbors = 0;
	    Vector bots = new Vector();
	    bots.addAll(bbc.otherBots);
	    //ots.add(new Bot(bbc.myposx,bbc.myposy,bbc)); //do you need this?
	    
	    for (int i=0;i<bots.size();i++)
	    {
	      Bot b = (Bot) bots.get(i);
	      PVector bloc = new PVector(b.x,b.y);
	      if (loc.dist(bloc)<bbc.neighborBound && !this.equals(b))
	      {

	        numNeighbors++;
	        if (!neighbors.contains(b))
	        {
	          neighbors.add(b);
	        }
	      }
	      else
	      {
	        neighbors.remove(b);
	      }
	    }

	    return neighbors;
	  }
	
	
	Vector getExtendedNeighbors()
	{


		//println("myID" + this.ID + "  i have " + neighbors.size() + " neighbors");
		//extendedNeighbors.clear();
		Vector ext = new Vector();
		ext.add(this);
		
		//ext.add(new Bot(bbc.myposx,bbc.myposy,bbc)); //do you need this?

		for (int i=0;i<neighbors.size();i++)
		{
			Bot  b = (Bot) neighbors.get(i);
			// println("this.ID " + this.ID + " is dealing with :" +b.ID);
			if (!b.queried)
			{
				b.queried=true;

				//println("" + b.ID + "was just  queried and now going to extend" );
				ext.addAll(b.getExtendedNeighbors());
			}
			else
			{
				//println("" + b.ID + "was already quereid" );
				ext.addAll(neighbors);

				//return ext;
			}
		}

		return new Vector(new LinkedHashSet(ext));
	}
	
}

