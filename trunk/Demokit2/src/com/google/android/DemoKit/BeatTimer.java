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

	Measure currentMeasure;


	BeatTimer()
	{
		globalTimeInterval=50;

		globalTimer=System.currentTimeMillis();
		appStartTimeMillis=globalTimer;
		Log.d("beatTimer created", "checking here ");
		
		currentMeasure = new Measure();
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

					float angle1=bbc.camang;
					if(angle1<0)
					{
						angle1=360+angle1;
					}


					if(ic!=null)
					{
						/*
						if(ic.ir0>20)
						{
							bbc.fillNow(bbc.sfxrseq);
							bbc.fillNow(bbc.instrumentseq);
						}
						 */
					}
					// this shoudl be in boebotcontroller.
					switch(mapping)
					{
					case 0: break;
					case 1: //angle

						//int f = (int) bbc.map((float)bbc.angleAzimuth, 0, 360, 1, bbc.sfxrseq.length/2);
						//int f = (int) bbc.map((float)(bbc.avest + Math.PI), 0, (float)(2*Math.PI), 1, bbc.sfxrseq.length/2);
						//int f = (int) bbc.map((float)bbc.camang, 0, 360, 1, bbc.instrumentseq.length/2);
						int f = (int) bbc.map(angle1, 0, 360, 1, bbc.instrumentseq.length/3);
						//bbc.fillRhythm(f, bbc.sfxrseq);
						bbc.fillEuclid(f, bbc.sfxrseq);
						bbc.fillEuclid(f, bbc.instrumentseq);
						break;
					case 2: //neighbor
						bbc.fillEuclid(bbc.numNeighbors*2+4, bbc.sfxrseq);
						bbc.fillEuclid(bbc.numNeighbors*2+4, bbc.instrumentseq);
						break;
					case 3: //speed 

						//temporary solution
						if(bbc.lbyte==128&&bbc.rbyte==128)
						{
							bbc.fillEuclid(1, bbc.instrumentseq);
							bbc.fillEuclid(1, bbc.sfxrseq);
						}

						if(bbc.rbyte==128-20 && bbc.lbyte==128+20)
						{
							bbc.fillEuclid(bbc.instrumentseq.length/2, bbc.instrumentseq);
							bbc.fillEuclid(bbc.sfxrseq.length/2, bbc.sfxrseq);
						}

						if(bbc.rbyte>128-20 && bbc.lbyte==128+20)
						{
							bbc.fillEuclid(6, bbc.instrumentseq);
							bbc.fillEuclid(6, bbc.sfxrseq);
						}

						if(bbc.rbyte==128-20 && bbc.lbyte<128+20)
						{
							bbc.fillEuclid(10, bbc.instrumentseq);
							bbc.fillEuclid(10, bbc.sfxrseq);
						}



						break;
					case 4: //ID fill position using euclid
						//boolean[] t=bbc.euclidArray(bbc.otherBots.size()+1, bbc.instrumentseq.length);
						int sk=1;
						bbc.fillEuclid((bbc.ID*sk)%bbc.sfxrseq.length, bbc.sfxrseq);
						bbc.fillEuclid((bbc.ID*sk)%bbc.instrumentseq.length, bbc.instrumentseq);
						break;
					case 5: //ID fill position
						int skip=3;
						bbc.fillPosition((bbc.ID*skip)%bbc.sfxrseq.length, bbc.sfxrseq);
						bbc.fillPosition((bbc.ID*skip)%bbc.instrumentseq.length, bbc.instrumentseq);						
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

						bbc.setRhythm(bbc.avatarseq);

						int step=4;

						for(int i=0; i <bbc.ID*step; i++)
						{
							bbc.shiftRhythmRight(bbc.instrumentseq);
							bbc.shiftRhythmRight(bbc.sfxrseq);
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
						int frmang=(int) bbc.map(angle1, 0, 360, 1, bbc.instrumentseq.length/3);
						if(frmang<0)
						{
							frmang=0;
						}
						if(frmang>3)
						{
							frmang=3;
						}
						bbc.setRhythm(bbc.getKuku(frmang));
						break;

					case 12:

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

					case 13:

						int frmang2=0;
						//temporary solution
						if(bbc.lbyte==128&&bbc.rbyte==128)
						{
							frmang2=0;
						}

						if(bbc.rbyte==128-20 && bbc.lbyte==128+20)
						{
							frmang2=1;
						}

						if(bbc.rbyte>128-20 && bbc.lbyte==128+20)
						{
							frmang2=2;
						}

						if(bbc.rbyte==128-20 && bbc.lbyte<128+20)
						{
							frmang2=3;
						}

						bbc.setRhythm(bbc.getKuku(frmang2));

						break;


					case 14:
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

					case 15:
						int ma2=(int) ((this.generalMeasure+bbc.ID)%4);
						bbc.setRhythm(bbc.getKuku(ma2));//
						break;

					case 16:
						int ffff=( Math.abs(4-bbc.numNeighbors)%4);
						bbc.setRhythm(bbc.getKuku(ffff));
						break;//




					case 17: //angle embellish


						if(!bbc.embellishOnce && bbc.embellishCounter>=1)
						{
							//int ae2 = (int) bbc.map((float)bbc.camang, 0, 360, 1, bbc.instrumentseq.length/2);
							//int ae = (int) bbc.map((float)bbc.camang, 0, 360, 1, 10);

							int ae2 = (int) bbc.map(angle1, 0, 360, 1, bbc.instrumentseq.length/2);
							int ae = (int) bbc.map(angle1, 0, 360, 1, 10);

							bbc.embellish2(bbc.instrumentseq, ae2, 4);
							bbc.embellish(bbc.instrumentseq, ae, 0);
							bbc.embellishOnce=true;
							bbc.embellishCounter=0;

							bbc.copyRhythm(bbc.instrumentseq, bbc.sfxrseq);

						}
						else
						{
							//this is the base rhythm
							bbc.setRhythm(bbc.getReggaeton());
						}





						break;

					case 18: //neighbor embellish

						if(bbc.numNeighbors>0)
						{
							if(!bbc.embellishOnce && bbc.embellishCounter>=1)
							{
								if(bbc.numNeighbors==1)
								{
									double tr = Math.random();
									if(tr>.5)
									{
										//bbc.embellish2(bbc.instrumentseq, (int)(Math.random()* bbc.instrumentseq.length), 4);
										bbc.embellish(bbc.instrumentseq, 4, 0);
										bbc.embellishOnce=true;
										bbc.embellishCounter=0;

										bbc.copyRhythm(bbc.instrumentseq, bbc.sfxrseq);
									}
									else
									{
										bbc.embellish2(bbc.instrumentseq, (int)(Math.random()* bbc.instrumentseq.length), 4);
										//bbc.embellish(bbc.instrumentseq, 4, 0);
										bbc.embellishOnce=true;
										bbc.embellishCounter=0;

										bbc.copyRhythm(bbc.instrumentseq, bbc.sfxrseq);
									}
								}
								else
								{

									bbc.embellish2(bbc.instrumentseq, (int)(Math.random()* bbc.instrumentseq.length), 4);
									bbc.embellish(bbc.instrumentseq, 4, 0);
									bbc.embellishOnce=true;
									bbc.embellishCounter=0;

									bbc.copyRhythm(bbc.instrumentseq, bbc.sfxrseq);


									//bbc.setRhythm(bbc.getReggaeton());
								}

							}
						}
						else
						{
							//this is the base rhythm
							bbc.setRhythm(bbc.getReggaeton());
						}

						break;

					case 19: //speed embellish

						break;

					case 20: //flee embellish

						if(bbc.myBehavior.fleeBeacon)
						{
							if(!bbc.embellishOnce && bbc.embellishCounter>=1)
							{
								bbc.embellish2(bbc.instrumentseq, (int)(Math.random()* bbc.instrumentseq.length), 4);
								bbc.embellish(bbc.instrumentseq, 4, 0);
								bbc.embellishOnce=true;
								bbc.embellishCounter=0;

								bbc.copyRhythm(bbc.instrumentseq, bbc.sfxrseq);
							}
						}
						else
						{
							bbc.setRhythm(bbc.getReggaeton());
						}
						break;

					case 21: // follow in line behaviors 



						break;


					case 22: //shift based on avatar  dist

						if(bbc.ID!=0)
						{
							if(bbc.isSilent(bbc.avatarseq))
							{
								boolean[] tt= bbc.getKuku(0);
								bbc.avatarseq=tt.clone();

							}
							bbc.setRhythm(bbc.avatarseq);


							float d=bbc.distanceFromAvatar();

							int numS=(int) bbc.map(d,0,500,1,bbc.instrumentseq.length/2);
							int stepp=2;

							for(int i=0; i <numS*stepp; i++)
							{
								bbc.shiftRhythmLeft(bbc.instrumentseq);
								bbc.shiftRhythmLeft(bbc.sfxrseq);
							}
						}

						break;

					case 23: 

						if(bbc.myBehavior.wander || (bbc.myBehavior.wanderThenFollowInLine && bbc.numNeighbors==0) )
						{							
							if(!bbc.randomlyChangeOnce)
							{
								int indexToChange = (int) (Math.random()*bbc.instrumentseq.length);
								bbc.instrumentseq[indexToChange]=!bbc.instrumentseq[indexToChange];
							}							 
						}
						else
						{
							//if()
						}


						break;

						//these are going to be what to do when received a message from a neighbor//
					case 24: 
						/*
						if(from<bbc.ID)
						{
							Log.d("client com" , "set rhythm from: " + from );
							bbc.setRhythm(b);
						}
						 */

						if(bbc.numNeighbors>0)
						{
							bbc.setRhythm(bbc.receivedSequence);
						}
						else
						{
							if(!bbc.shiftOnce)
							{
								if(Math.random()<.5)
								{
									bbc.shiftRhythmLeft(bbc.instrumentseq);
									bbc.shiftRhythmLeft(bbc.sfxrseq);

								}
								else
								{
									bbc.shiftRhythmRight(bbc.instrumentseq);
									bbc.shiftRhythmRight(bbc.sfxrseq);
								}
								bbc.shiftOnce=true;
							}
						}
						break;




					case 25://if get nearby then randomly choose which one gets to be copied--- otheriwse shift?
						//
						break;
					case 26: //if get nearby then swap portion?
						if(bbc.numNeighbors==0)
						{

						}
						else
						{
							if(!bbc.swapOnce)
							{
								//bbc.swapRandomPortion(bbc.instrumentseq, bbc.receivedSequence );
								bbc.swapPortion(bbc.instrumentseq, bbc.receivedSequence, 0, bbc.instrumentseq.length/2);
								bbc.setRhythm(bbc.instrumentseq);
								bbc.swapOnce=true;
							}
						}

						break;
					case 100: //if alone keep same, else randomly change?
						if(bbc.numNeighbors==0)
						{

						}
						else
						{
							if(!bbc.changeOnce)
							{
								for(int i=0;i<bbc.numNeighbors;i++)
								{
									int indexToChange = (int) (Math.random()*bbc.instrumentseq.length); 
									bbc.instrumentseq[indexToChange] = ! bbc.instrumentseq[indexToChange];
									bbc.sfxrseq[indexToChange] = ! bbc.sfxrseq[indexToChange];
								}
								bbc.changeOnce=true;
							}
						}
						break;
						
					case 101:
						
						currentMeasure = bbc.fightSong.getMeasure((int) this.generalMeasure);
						bbc.setRhythm(currentMeasure.toRhythm());
						//mActivity.aTest.setFrequency(m);



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


							//// or

							//mActivity.aTest.setFrequency(440);
							//mActivity.aTest.setFrequencyRP();
							if(!bbc.useSong)
							{
								mActivity.aTest.setFrequencyAP();
							}
							else
							{
								 mActivity.aTest.setFrequency(currentMeasure.notes[bbc.currentIndex]);
							}
							mActivity.aTest.properIncrement();
						}
					}
					else
					{
						mActivity.aTest.setFrequency(0);
						mActivity.aTest.setIncrement(0);
						mActivity.aTest.setAngle(0);
					}
					if(bbc.instrumentseq[bbc.currentIndex])
					{
						test=false;
						bbc.playInstrument();
						bbc.rfv.doJiggle();
						bbc.rfv.doJiggleBigger();
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
			bbc.currentIndex=generalIndex%bbc.instrumentseq.length;

			if(bbc.currentIndex==0)
			{

				generalMeasure++;


				///////////////other stuff
				bbc.embellishOnce=false;
				bbc.embellishCounter++;

				bbc.randomlyChangeOnce=false;

				bbc.shiftOnce=false;

				bbc.changeOnce=false;
				
				bbc.swapOnce =false;
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
