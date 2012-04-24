package gtcmtswarmbot.mobilecontrol;

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
	
	Bot()
	{
		vel = new PVector();
	}

	Bot(int x, int y)
	{
		vel = new PVector();
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

}

