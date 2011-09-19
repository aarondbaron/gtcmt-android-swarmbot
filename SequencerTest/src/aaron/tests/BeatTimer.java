package aaron.tests;

import android.util.Log;

public class BeatTimer extends Thread{
	
	int generalIndex, previousIndex;
	
	long globalTimer;
	
	long globalTimeInterval;
	
	boolean generalTimingFlag;
	
	long appStartTimeMillis;

	private boolean running;
	
	BeatTimer()
	{
		globalTimeInterval=100;
		
		globalTimer=System.currentTimeMillis();
		appStartTimeMillis=globalTimer;
		Log.d("beatTimer created", "checking here ");
	}
	
	@Override
	public void run()
	{
		Log.d("in running", "checking HERE FOR RUNNING IF THE THREd started ");
		while(running)
		{
			//Log.d("in running", "checking HERE FOR RUNNING IF THE THREd started ");
			update();
		}
	}
	
	void update()
	{
		//Log.d("in update", "timefromStart - globalTimer" + (timeFromStart() - globalTimer) );
		if(System.currentTimeMillis() - globalTimer> globalTimeInterval)
		  {
		    globalTimer += globalTimeInterval;
		    incrementGeneralIndex();
		    //allowedToFire=true;
		    generalTimingFlag=true;
		    
		    /*
		    for(int k=0; k <sequence.size(); k++ )
		    {
		      Sequence s = (Sequence) Sequence.get(k);
		      s.myTrack.incrementIndex();
		      if(s.myTrack.tempIndex!=s.myTrack.currentIndex)
		      {
		        s.myTrack.triggerFlag=false;
		      }
		    }
		    */
		  }
		  else
		  {
		    //allowedToFire=false;
			  generalTimingFlag=false;
		  }

	}
	
	long timeFromStart()
	{
	  return System.currentTimeMillis()-appStartTimeMillis;
	}
	
	void incrementGeneralIndex()
	{
	  generalIndex++;
	  //allowedToFire=true;
	}
	
	public void setRunning(boolean run) {
        running = run;
    }



}
