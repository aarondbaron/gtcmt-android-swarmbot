package com.google.android.DemoKit;

import com.google.android.DemoKit.ClientCode.ClientThread;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

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

	BoeBotController bbc;
	InputController ic;
	OutputController oc;

	DemoKitActivity mActivity;

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



		}
	}

	void update()
	{
		///////////////////////////////////////
		//behaviors taken out
		////////////////////////////////////////////
		if(bbc!=null)
		{

			//mActivity.bbc.mHandler.postDelayed(mActivity.bbc.mUpdateUITimerTask, 500);

			/*
			handler.post(new Runnable() {
				@Override	
				public void run() {
					if(bbc!=null)
					{
						bbc.byteLabel.setText("lbyte" + bbc.lbyte + "rbyte" );
					}

				}
			});
			 */


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



					// this shoudl be in boebotcontroller.
					switch(mapping)
					{
					case 0: break;
					case 1: //angle

						//int f = (int) bbc.map((float)bbc.angleAzimuth, 0, 360, 1, bbc.sfxrseq.length/2);
						//int f = (int) bbc.map((float)(bbc.avest + Math.PI), 0, (float)(2*Math.PI), 1, bbc.sfxrseq.length/2);
						int f = (int) bbc.map((float)bbc.camang, 0, 360, 1, bbc.instrumentseq.length/2);
						//bbc.fillRhythm(f, bbc.sfxrseq);
						bbc.fillEuclid(f, bbc.sfxrseq);
						bbc.fillEuclid(f, bbc.instrumentseq);
						break;
					case 2: //neighbor
						bbc.fillEuclid(bbc.numNeighbors*2+4, bbc.sfxrseq);
						bbc.fillEuclid(bbc.numNeighbors*2+4, bbc.instrumentseq);
						break;
					case 3: //speed 
						break;
					case 4: //ID fill position using euclid
						boolean[] t=bbc.euclidArray(bbc.otherBots.size()+1, bbc.instrumentseq.length);

						break;
					case 5: //ID fill position
						bbc.fillPosition(bbc.ID, bbc.sfxrseq);
						bbc.fillPosition(bbc.ID, bbc.instrumentseq);						
						break;
					case 6: //fill based on camera face size
						int fff=(int) bbc.map(bbc.opcvFD.sz,160,400,1,8);
						if(bbc.opcvFD.numFaces!=0)
						{
							bbc.fillEuclid(fff, bbc.sfxrseq);
							bbc.fillEuclid(fff, bbc.instrumentseq);			
						}
						else
						{

							//bbc.clearRhythm(bbc.sfxrseq);
							//bbc.clearRhythm(bbc.instrumentseq);
						}
						break;

					case 7: //avatarIdea
						if(mActivity.client.myID==0)
						{
							if(generalMeasure%2==0)
							{							
								//play
								smuted=false;
							}
							else
							{
								//don't play
								smuted=true;
							}
						}
						else
						{
							if(generalMeasure%2==1)
							{								
								//play and modify
								smuted=false;
								for(int i=0;i<bbc.instrumentseq.length;i++)
								{									
									if(bbc.instrumentseq[i])
									{											
									}								
								}
							}
							else
							{
								//don't play
								smuted=true;
							}
						}
						break;

					case 8:
						int c = (int) (this.generalMeasure % (bbc.otherBots.size()+1) );
						if(c==bbc.ID)
						{


						}
						break;



					case 9: ///shift avatar based on ID

						//bbc.clearRhythm(bbc.instrumentseq);
						//bbc.clearRhythm(bbc.sfxrseq);

						bbc.setRhythm(bbc.avatarseq);;

						for(int i=0; i <bbc.ID; i++)
						{
							bbc.shiftRhythmLeft(bbc.instrumentseq);
							bbc.shiftRhythmLeft(bbc.sfxrseq);
						}

						break;

					case 10:
						if(mActivity.client.myID!=0)
						{
							bbc.splitRhythm(0);
							//bbc.av1();
						}
						break;
					
					case 11:
						
						int ff=(bbc.numNeighbors%4);
						bbc.setRhythm(bbc.getKuku(ff));
						break;//
						/*
						switch(ff)
						{
						case 0: bbc.setRhythm(bbc.djembe0);
						break;
						case 1: bbc.setRhythm(bbc.djembe1);
						break;
						case 2: bbc.setRhythm(bbc.djembe2);
						break;
						case 3: bbc.setRhythm(bbc.djembe3);
						break;
						
						default: ;
						
						
						}
						*/
						
						
					case 12:
						int ma=(int) (this.generalMeasure%4);
						bbc.setRhythm(bbc.getKuku(ma));//
						break;
						/*
						switch(ma)
						{
						case 0: bbc.setRhythm(bbc.djembe0);
						break;
						case 1: bbc.setRhythm(bbc.djembe1);
						break;
						case 2: bbc.setRhythm(bbc.djembe2);
						break;
						case 3: bbc.setRhythm(bbc.djembe3);
						break;
						
						default: ;
						
						}
						*/
						
					case 13:
						int ma2=(int) ((this.generalMeasure+bbc.ID)%4);
						bbc.setRhythm(bbc.getKuku(ma2));//
						break;
						
					case 14:
						int ffff=( Math.abs(4-bbc.numNeighbors)%4);
						bbc.setRhythm(bbc.getKuku(ffff));
						break;//
						
						
					case 15: //angle embellish
						
						
						
						break;

					case 16: //neighbor embellish
						
						
						break;
						
					case 17: //speed embellish
						
						break;
						
					case 18: //flee embellish
						
						if(bbc.myBehavior.fleeBeacon)
						{
							if(!bbc.embellishOnce && bbc.embellishCounter>=1)
							{
								bbc.embellish2(bbc.instrumentseq, (int)(Math.random()* bbc.instrumentseq.length), 4);
						    	bbc.embellish(bbc.instrumentseq, 4, 0);
						    	bbc.embellishOnce=true;
						    	bbc.embellishCounter=0;
							}
						}
						else
						{
							bbc.setRhythm(bbc.getReggaeton());
						}
						break;



					default: ; break;

					}

					if(bbc.danceSequencer)
					{
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
					}

					if(bbc.sfxrseq[bbc.currentIndex])
					{
						
						test=false;
						
						if(bbc.useSFXR)
						{
							mActivity.aTest.soundType(7);
							mActivity.aTest.replay();
						}
					}
					if(bbc.instrumentseq[bbc.currentIndex])
					{
						test=false;
						bbc.playInstrument();
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
							int f = (int) bbc.map((float)ic.ir0, 0, 512, 0, bbc.sfxrseq.length);
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
			if(System.currentTimeMillis() - globalTimer> globalTimeInterval/div)
			{////////////////////////////////
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

					if(bbc.danceSequencer)
					{
						bbc.stop();
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
			bbc.currentIndex=generalIndex%bbc.fseq.length;

			if(bbc.currentIndex==0)
			{
				
				generalMeasure++;
				
				
				///////////////other stuff
				bbc.embellishOnce=false;
				bbc.embellishCounter++;
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
