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
	
	int[] getHits(boolean[] b)
	{
		Vector h= new Vector();
		for(int i=0;i< b.length;i++)
		{
			if(b[i])
			{
				h.add(new Integer(i));
			} 

		}

		int [] hits = new int[h.size()];
		for(int i=0;i<hits.length;i++)
		{
			Integer dd=(Integer)h.get(i);
			hits[i]=(int) dd.intValue(); 
		}


		return hits;

	}

	/*
	void embellish1(boolean[] b)
	{
		int[] hits = getHits( b );

		int c1 = (int) (Math.random()*hits.length);

		int choice = hits[c1];	

		if(choice==0)
		{
			b[choice]=true;
			b[choice+1]=true;
			b[b.length-1]=true;
			return;
		}

		if(choice==b.length-1)
		{
			b[choice]=true;
			b[0]=true;
			b[choice-1]=true;
			return;
		}

		b[choice]=true;
		b[choice+1]=true;
		b[choice-1]=true;

	}
	 */

	//////embellish by putting a number of repeated hits somehwere where there is at least one hit
	void embellish(boolean[] b, int number)
	{

		if (number<=0)
		{
			return;
		}

		if (number%2!=0 )
		{
			number++;
		}

		int[] things = new int[number];
		int[] hits = getHits(b);

		if (hits.length==0)
		{
			hits = new int[] { 0  };
		}

		int c1 = (int) (Math.random()*hits.length);	  
		int choice = hits[c1];
		for (int i=0;i<things.length;i++)
		{
			if (i%2==1)
			{
				int index=(int)Math.ceil( (float)i/2 );
				things[i]=choice+(index);
			} 
			if (i%2==0)
			{
				int index=(int) Math.ceil( (float)i/2 );
				things[i]=choice-(index+1);
			}
			if (things[i]<0)
			{
				things[i]+=b.length;
			}
			if (things[i]>b.length-1)
			{
				things[i]=things[i]%b.length;
			}


		}

		for(int i=0;i<things.length;i++)
		{
			if(things[i]>=0&&things[i]<b.length)
			{
				b[things[i]]=true; 
			}
		}
	}

	// embellish, but also specifiy a skip width
	void embellish(boolean[] b, int number, int skip)
	{

		if (number<=0)
		{
			return;
		}

		if (number%2!=0 )
		{
			number++;
		}

		int[] things = new int[number];
		int[] hits = getHits(b);

		if (hits.length==0)
		{
			hits = new int[] { 0  };
		}

		int c1 = (int) (Math.random()*hits.length);	  
		int choice = hits[c1];
		for (int i=0;i<things.length;i++)
		{
			if (i%2==1)
			{
				int index=(int)Math.ceil( (float)i/2 );
				things[i]=choice+(index + i*skip);
			} 
			if (i%2==0)
			{
				int index=(int) Math.ceil( (float)i/2 );
				things[i]=choice-(index+1 +  (i+1)* skip );
			}
			if (things[i]<0)
			{
				things[i]+=b.length;
			}
			if (things[i]>b.length-1)
			{
				things[i]=things[i]%b.length;
			}


		}

		for(int i=0;i<things.length;i++)
		{
			if(things[i]>=0&&things[i]<b.length)
			{
				b[things[i]]=true; 
			}
		}
	}

	////////////////embellish by making a section of repeated hits
	void embellish2(boolean[] b, int section, int step)
	{

		int stop=section+ 3*step + (int) (Math.random()*6*step);
		if (stop>=b.length)
		{
			stop=b.length-1;
		}

		int m=section%step;
		for (int i=section;i<stop;i++)
		{
			if (i%step==m)
			{
				b[i]=true;
			}
			else
			{
				b[i]=false;
			}
		}
	}

 

	boolean isSilent(boolean b[])
	{
		boolean f=false;
		for (int i=0; i < b.length;i++)
		{
			f = f || b[i];
		}  

		return !f;
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
