package com.aaron.tests;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.util.Log;

public class BeatTimer extends Thread{
	
	int generalIndex, previousIndex;
	
	long globalTimer;
	
	long globalTimeInterval;
	
	boolean generalTimingFlag;
	
	long appStartTimeMillis;

	private boolean running;
	
	//BoeBotController bbc;
	//InputController ic;
	//OutputController oc;
	
	ClientActivity mActivity;
	
	BeatTimer()
	{
		globalTimeInterval=125;
		
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
			if(mActivity!=null)
			{
				if(mActivity.sequencerMode)
				{
					boolean test=true;

					if(mActivity.array[mActivity.currentIndex])
					{

						test=false;
						//bbc.forward();
						mActivity.aTest.soundType(0);
						mActivity.aTest.replay();

					}

					if(test)
					{
						//bbc.stop();
						//mActivity.aTest.replayRandom();

					}
					
					
							//int f = 3;
							//mActivity.fillRhythm(f, mActivity.array);
						
				}
			}

		}
		else
		{
			if(System.currentTimeMillis() - globalTimer> globalTimeInterval/2)
			  {
				  //allowedToFire=false;
				  generalTimingFlag=false;
				  if(mActivity!=null)
				  {
					  if(mActivity.sequencerMode)
					  {
						  //mActivity.stopInstrument();
					  }
				  }
			  }
			  
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
	  //Log.d("ta",""+generalIndex);
	  if(mActivity!=null)
	  {
		  mActivity.currentIndex=generalIndex%mActivity.array.length;
		  

          
		  //mActivity.setBeat(generalIndex%mActivity.array.length);
		  //mActivity.beatNumber.setText("gen" + generalIndex + " cur: " + mActivity.currentIndex);
	  }
	}
	
	public void setRunning(boolean run) {
        running = run;
    }

	public void resetIndex()
	{
		generalIndex=0;
	}


}
