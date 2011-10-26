package com.google.android.DemoKit;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class BeatTimer extends Thread{
	
	int generalIndex, previousIndex;
	
	long globalTimer;
	
	long globalTimeInterval;
	
	boolean generalTimingFlag;
	
	long appStartTimeMillis;
	Handler handler=new Handler();
	private boolean running;
	
	BoeBotController bbc;
	InputController ic;
	OutputController oc;

	DemoKitActivity mActivity;
	
	boolean wander;
	boolean move2Loc;
	
	BeatTimer()
	{
		globalTimeInterval=125*2;
		
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
		
		if(wander)
		{
			if(bbc.myBehavior!=null)
			{
				//Log.d("beatTimer", "wander");
				bbc.myBehavior.fullWander();
			}
		}
		
		if(move2Loc)
		{
			if(bbc.moveBehavior!=null)
			{
				bbc.moveBehavior.move();
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					bbc.move2locLabel.setText("angle: "+bbc.modDistance+
							"\ncalibrate ang="+bbc.calibrationAngle+
							"\ntargetx="+bbc.targetx+
							"\ntargetx="+bbc.targety);
				}
			});
			
		}
		if(bbc!=null)
		{
			if(bbc.moveBehavior!=null)
			{
				if(bbc.moveBehavior.boundaryReached())
				{
					bbc.stopAll();
					move2Loc=false;
					wander=false;
				}
			}
		}
		//Log.d("in update", "timefromStart - globalTimer" + (timeFromStart() - globalTimer) );
		if(System.currentTimeMillis() - globalTimer> globalTimeInterval)
		{
			/*
			if(wander)
			{
				if(bbc.myBehavior!=null)
				{
					Log.d("beatTimer", "wander");
					bbc.myBehavior.wander();
				}
			}
			*/
			
			
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
					
					int f = (int) bbc.map((float)bbc.angleAzimuth, 0, 360, 1, bbc.sfxrseq.length/2);
					//bbc.fillRhythm(f, bbc.sfxrseq);
					bbc.fillEuclid(f, bbc.sfxrseq);
					
					/*
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
					*/
					if(bbc.sfxrseq[bbc.currentIndex])
					{
						test=false;
						
						mActivity.aTest.soundType(6);
						mActivity.aTest.replay();
					}
					
					
					if(bbc.instrumentseq[bbc.currentIndex])
					{
						test=false;
						bbc.playInstrument();
						//mActivity.aTest.soundType(3);
						//mActivity.aTest.replay();
					}
						
					/*
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
					*/
					
					
				}
				
				////////////////////////////
				//not sequencer mode...but free range... 
				/*
				if(bbc.instrumentseq[bbc.currentIndex])
				{
					
					bbc.playInstrument();
					//mActivity.aTest.soundType(3);
					//mActivity.aTest.replay();
				}
				*/
				
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
					  else
					  {
						  //bbc.stopInstrument();
						  
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
