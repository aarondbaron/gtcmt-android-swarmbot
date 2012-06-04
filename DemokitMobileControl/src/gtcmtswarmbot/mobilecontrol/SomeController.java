package gtcmtswarmbot.mobilecontrol;

import java.util.Vector;

import android.util.Log;

public class SomeController {

	public Vector allBots;
	public int ID;
	public int neighborBound;
	public boolean[] instrumentseq;
	public boolean[] sfxrseq;
	public boolean[] avatarseq;
	public boolean avatarMode;
	public boolean useSFXR;
	public boolean directControl;
	
	
	Behavior behavior;
	public boolean danceSequencer;
	public Bot currentAvatar;
	
	int currentIndex;
	public Vector beacons;
	public boolean[] receivedSequence;
	
	int SEQUENCERLENGTH=48;
	public boolean sequencerMode=true;
	
	DemokitMobileControlActivity mActivity;
	
	BeatTimer bt;
	
	
	long logTimer;
	public int[] MSDeg;
	
	SomeController(DemokitMobileControlActivity demokitMobileControlActivity)
	{
		
		this.mActivity= demokitMobileControlActivity;
		allBots=new Vector();
		beacons = new Vector();
		
		instrumentseq = new boolean[SEQUENCERLENGTH];
		sfxrseq = new boolean[SEQUENCERLENGTH];
		avatarseq = new boolean[SEQUENCERLENGTH];
		receivedSequence = new boolean[SEQUENCERLENGTH];
		
		behavior = new Behavior();
		
		neighborBound=75;
		
		

		MSDeg = new int[8];
		MSDeg[0]=0;
		MSDeg[1]=2;
		MSDeg[2]=4;
		MSDeg[3]=5;
		MSDeg[4]=7;
		MSDeg[5]=9;
		MSDeg[6]=11;
		MSDeg[7]=12;
		
	}

	public void resetIndex() {
		// TODO Auto-generated method stub
		mActivity.beatTimer.resetIndex();
	}

	
	String patternToString(boolean[] b)
	{
		String s = "";

		for(int i=0; i < b.length; i++)
		{
			if(b[i])
			{
				s += "1";
			}
			else
			{
				s+="0";
			}
		}

		return s;
	}
	
	String notesToString(int[] b)
	{
	  String s = "";

	  for (int i=0; i < b.length; i++)
	  {
	    s+=b[i]+",";
	  }

	  return s;
	}
	
	
	void clearRhythm(boolean b[]) 
	{
		for(int i=0;i<b.length;i++)
		{
			b[i]=false;
		}
		//rebuildMusicShape();
	}

	public void setWanderVector(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setWanderDance(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setWander(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void moveToLoc(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void avatarForward() {
		// TODO Auto-generated method stub
		
	}

	public void avatarRotLeft() {
		// TODO Auto-generated method stub
		
	}

	public void avatarBackward() {
		// TODO Auto-generated method stub
		
	}

	public void avatarRotRight() {
		// TODO Auto-generated method stub
		
	}

	public void setMapping(int map) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	/////////////////////////////////////
	
	boolean[] euclidArray(int m, int k)
	{

		if(k<0||m<0)
		{
			return new boolean[k];
		}
		if(k<m||m==0)
			return new boolean[k];


		Vector d[] = new Vector[m];
		for(int i=0; i <m; i++)
		{
			d[i] = new Vector();
			d[i].add("1");
		}

		int dif=k-m;
		//Number of zeros

		for(int i=0; i< dif; i++)
		{
			//println(i%m);
			d[i%m].add("0");
		}

		Vector r = new Vector();

		for(int i=0; i< d.length;i++)
		{
			r.addAll(d[i]);
		}


		boolean b[]= new boolean[k];
		for(int i =0; i < r.size(); i++)
		{
			String s = (String) r.get(i);
			//print(s);
			if(s.equals("1"))
			{
				b[i]=true;
			}
		}
		//println();
		return b;
	}

	
	void fillEuclid(int a, boolean b[])
	{
		//clearRhythm(b);

		boolean[] ea=euclidArray(a,b.length);
		for(int i=0; i<b.length;i++)
		{

			b[i]=ea[i];

		}

	}
	
	void fillNow(boolean b[])
	{
		b[this.currentIndex]=true;
	}

	public void numberOfNeigbhors() {
		// TODO Auto-generated method stub
		
		boolean doThing=false;
		if(System.currentTimeMillis()-logTimer>1000)
		{
			
			logTimer=System.currentTimeMillis();
			doThing=true;
		}
		
		for(int i=0;i<allBots.size();i++)
		{
			Bot b = (Bot)allBots.get(i);
			b.numN=0;
			PVector p1 = new PVector(b.x,b.y);
			for(int j=0;j<allBots.size();j++)
			{
				Bot b2 = (Bot) allBots.get(j);
				PVector p2 = new PVector(b2.x,b2.y);
				
				
				float d = PVector.dist(p1, p2);
				
				if(doThing)
				{
					Log.d("distance," ,  "b1 " + b.ID+ " b2: " + b2.ID + " d:" +d + " neigbhorbound " + this.neighborBound);
				}
				if(d< this.neighborBound   && !b.equals(b2))
				{
					
					b.numN+=1;
					 
				}
			}
		}
		doThing=false;
		
		//Log.d("dfdf","");
		
	}

	 
	public int getMSDegree(int n)
	{
		
		return MSDeg[n%MSDeg.length];
	}
	
	public int getFightSongNote( int n)
	{
		
		int res=72;
		 
		
		switch (n)
		{
		case 0:
			res=71;
			break;
		case 1:
			res=72+0;
			break;
		case 2:
			res=72+2;
			break;
		case 3:
			res=72+4;
			break;
		case 4:
			res=72+5;
			break;
		case 5:
			res=72+7;
			break;
		case 6:
			res=72+9;
			break;
		case 7:
			res=72+12;
			break;
		 
		default:
			res=72;
			break;
		
		
		}
		
	   return res;
	}
	
	
}
