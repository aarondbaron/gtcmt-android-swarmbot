package com.google.android.DemoKit;

import android.util.Log;

public class BeatTimer extends Thread{
	
	int generalIndex, previousIndex;
	
	long globalTimer;
	
	long globalTimeInterval;
	
	boolean generalTimingFlag;
	
	long appStartTimeMillis;

	private boolean running;
	
	BoeBotController bbc;
	InputController ic;
	OutputController oc;
	
	DemoKitActivity mActivity;
	
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
			if(bbc!=null)
			{
				if(bbc.sequencerMode)
				{
					boolean test=true;

					if(bbc.fseq[bbc.currentIndex])
					{

						test=false;
						bbc.forward();
						//mActivity.aTest.soundType(0);
						//mActivity.aTest.replay();

					}

					if(bbc.bseq[bbc.currentIndex])
					{
						test=false;
						bbc.backward();
						//mActivity.aTest.soundType(1);
						//mActivity.aTest.replay();
					}

					if(bbc.rseq[bbc.currentIndex])
					{
						test=false;
						bbc.rotLeft();
						//mActivity.aTest.soundType(2);
						//mActivity.aTest.replay();
					}

					if(bbc.lseq[bbc.currentIndex])
					{
						test=false;
						bbc.rotRight();
						//mActivity.aTest.soundType(3);
						//mActivity.aTest.replay();
					}
					
					if(bbc.sfxrseq[bbc.currentIndex])
					{
						test=false;
						
						//mActivity.aTest.soundType(6);
						mActivity.aTest.replay();
					}
					
					if(bbc.instrumentseq[bbc.currentIndex])
					{
						test=false;
						bbc.playInstrument();
						//mActivity.aTest.soundType(3);
						//mActivity.aTest.replay();
					}

					if(test)
					{
						bbc.stop();
						//mActivity.aTest.replayRandom();

					}
					
					if(ic!=null)
					{
						if(bbc.useSensors)
						{
							int f = (int) bbc.map((float)ic.ir0, 0, 512, 0, 16);
							bbc.fillRhythm(f, bbc.sfxrseq);
						}
						
					}
				}
			}

		}
		else
		{
			if(System.currentTimeMillis() - globalTimer> globalTimeInterval/2)
			  {
				  //allowedToFire=false;
				  generalTimingFlag=false;
				  if(bbc!=null)
				  {
					  if(bbc.sequencerMode)
					  {
						 bbc.stopInstrument();
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
	  if(bbc!=null)
		  bbc.currentIndex=generalIndex%bbc.fseq.length;
	}
	
	public void setRunning(boolean run) {
        running = run;
    }

	public void resetIndex()
	{
		generalIndex=0;
		globalTimer=System.currentTimeMillis();
	}


}