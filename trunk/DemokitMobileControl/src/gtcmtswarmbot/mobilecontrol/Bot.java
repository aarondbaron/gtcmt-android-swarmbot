package gtcmtswarmbot.mobilecontrol;

import java.util.LinkedHashSet;
import java.util.Vector;



//a class for keeping track of other bots
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

	PVector vel;

	boolean inspect=false;
	public boolean vibrateOnce;

	Vector neighbors;

	Vector extendedNeighbors;

	boolean queried;



	int numN;
	
	int numNeighbors;
	
	SomeController bbc;

	Bot(SomeController bbc)
	{
		vel = new PVector();		
		neighbors =  new Vector();
		extendedNeighbors = new Vector();
		this.bbc=bbc;


	}

	Bot(int x, int y, SomeController bbc)
	{
		vel = new PVector();
		neighbors =  new Vector();
		extendedNeighbors = new Vector();
		this.bbc=bbc;
		this.x=x;
		this.y=y;

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

	public boolean isNearCursor(float tx, float ty, int bx, int by) {
		// TODO Auto-generated method stub
		int sz=50;

		PVector t = new PVector(tx,ty);

		//int bx=(int) map(b.x,0,640,leftx,rightx);
		//int by=(int) map(b.y,0,480,topy,bottomy);

		PVector loc = new PVector(bx,by);
		if(PVector.dist(t,loc)<50)
		{
			return true;
		}
		else
		{
			return false;
		}

		/*
		if(tx>this.x-sz && tx < this.x+sz && ty>this.y-sz && ty < this.y+sz)
		{
			return true;
		}
		else
		{
			return false;
		}
		 */


	}

	public void inspect()
	{
		inspect=!inspect;

	}



	
	
	
	Vector getNeighbors( )
	  {

		PVector loc = new PVector(this.x,this.y);
	    numNeighbors = 0;
	    for (int i=0;i<bbc.allBots.size();i++)
	    {
	      Bot b = (Bot) bbc.allBots.get(i);
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

