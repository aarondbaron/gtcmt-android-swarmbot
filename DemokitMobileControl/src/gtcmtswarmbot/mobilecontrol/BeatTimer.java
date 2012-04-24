package gtcmtswarmbot.mobilecontrol;

import android.os.Handler;
import android.util.Log;



public class BeatTimer extends Thread{

	int generalIndex, previousIndex;

	long globalTimer;

	long globalTimeInterval;
	long div=2;

	boolean generalTimingFlag;
	boolean generalMeasureFlag;

	long generalMeasure;
	long previousMeasure;

	long appStartTimeMillis;
	Handler handler=new Handler();
	private boolean running;

	SomeController bbc;


	DemokitMobileControlActivity mActivity;

	boolean wander;
	boolean move2Loc;
	boolean orient2Loc;

	boolean seek;

	int mapping=0;

	boolean onceFlag=false;
	boolean smuted=false;

	boolean measureFlag=false;

	boolean wanderDance;
	long wanderDanceTimer;
	boolean wanderDanceOnce;

	boolean separation;
	boolean alignment;
	boolean cohesion;

	boolean followInLine;

	boolean wanderVector;



	BeatTimer(DemokitMobileControlActivity demokitMobileControlActivity)
	{
		this.mActivity= demokitMobileControlActivity;
		globalTimeInterval=50;

		this.bbc=mActivity.sc;

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

			/*
			if(mActivity.client!=null)
			{
				if(!mActivity.client.initialConnect )
				{
					if(System.currentTimeMillis()-mActivity.client.initialConnectTimer>4000)
					{
						mActivity.client.doStuff();					
						mActivity.client.initialConnect=true;
					}
				}
			}
			 */



		}
	}

	void update()
	{


		if(System.currentTimeMillis() - globalTimer> globalTimeInterval)
		{


			globalTimer += globalTimeInterval;
			incrementGeneralIndex();
			//allowedToFire=true;
			generalTimingFlag=true;

			//Log.d("beat timer" , "" + globalTimer);


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



					if(bbc.sfxrseq[bbc.currentIndex])
					{

						test=false;


					}
					else
					{

					}
					if(bbc.instrumentseq[bbc.currentIndex])
					{

					}




				}



			}

		}
		else
		{
			if(System.currentTimeMillis() - globalTimer> globalTimeInterval/div)
			{////////////////////////////////
				//allowedToFire=false;
				generalTimingFlag=false;
				if(bbc!=null)
				{
					if(mActivity.arenaView!=null)
					{
						if(mActivity.arenaView.surfCreated)
						{


							if(mActivity.arenaView.thread.sequencer.seq[bbc.currentIndex%mActivity.arenaView.thread.sequencer.seq.length])
							{
								if(mActivity.arenaView.thread.showSequencer)
								{
									mActivity.arenaView.vib.vibrate(globalTimeInterval/2);
								}
							}
						}
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
		{
			bbc.currentIndex=generalIndex%bbc.instrumentseq.length;
			//Log.d("beat timer" , "" + bbc.currentIndex);

			if(bbc.currentIndex==0)
			{

				generalMeasure++;

				//Log.d("beat timer" , "measure" + generalMeasure);

			}

		}


	}

	public void setRunning(boolean run) {
		running = run;
	}

	public void resetIndex()
	{
		generalIndex=0;
		globalTimer=System.currentTimeMillis();
		generalMeasure=0;
		Log.d("beattimer","restting indices");
	}


}