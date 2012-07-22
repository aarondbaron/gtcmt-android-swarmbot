package com.google.android.DemoKit;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;

import com.google.android.DemoKit.ClientCode.ClientThread;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class BeatTimer extends Thread{

	int generalIndex, previousIndex;

	long globalTimer;
	long globalHalfTimer;

	long globalTimeInterval;
	float div=1.10f;

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
	Vector possibleNotes;
	Vector possibleMeasures;
	boolean possibleMeasuresSilent;
	boolean [] testMeasure;
	float cang;

	LinkedHashSet notebots;
	Iterator itr;
	int measureInd;
	boolean incMIndOnce;

	int gilcounter=0;

	int currentGilNeighbors, prevGilNeighbors;
	boolean noChangeNeighbors;
	
	int currentExtendedNeighbors,prevExtendedNeighbors;
	boolean noChangeExtendedNeighbors;
	
	public int myBiggerPlaySessionCount;
	public int myPlaySessionCount;
	boolean gilSeparate;
	long gilSeparateTimer;
	int timesLoop=4;

	public long hasCompletedGilsTimer;

	public boolean myPlayFlag;

	public int myGeneralPlaySessionCount;

	public int completedGilsCount;
	
	public long gilTime;
	
	public Vector<Bot> tExtendedNeighbors;
	
	public long deTimer;


	BeatTimer()
	{
		//globalTimeInterval=50;
		globalTimeInterval=75;
		
		globalTimer=System.currentTimeMillis();
		globalHalfTimer=System.currentTimeMillis();
		
		appStartTimeMillis=globalTimer;
		Log.d("beatTimer created", "checking here ");

		currentMeasure = new Measure();
		possibleMeasures = new Vector();
		possibleNotes = new Vector();
		tExtendedNeighbors = new Vector();
		
		gilTime=120*1000;
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
						//bbc.stopAll();
						//bbc.myBehavior.doStop();
						//bbc.myBehavior.desiredVel.mult(0);
					}
					else
					{
						 
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

			globalHalfTimer=globalTimer;
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
					case 222999: //extended neighbor
						bbc.fillEuclid(bbc.myExtendedNeighbors.size()*2+4, bbc.sfxrseq);
						bbc.fillEuclid(bbc.myExtendedNeighbors.size()*2+4, bbc.instrumentseq);
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

								//this should be here
								bbc.randomlyChangeOnce=true;
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

					case 27://virus

						if(bbc.numNeighbors>0 && bbc.isSilent(bbc.instrumentseq))
						{
							bbc.setRhythm(bbc.receivedSequence);
							/*
							if(bbc.isSilent(bbc.receivedSequence))
							{

							}
							else
							{
								bbc.setRhythm(bbc.receivedSequence);
							}
							 */
						}
						else
						{
							if(!bbc.isSilent(bbc.instrumentseq))
							{
								if(!bbc.swapOnce)
								{
									//bbc.swapRandomPortion(bbc.instrumentseq, bbc.receivedSequence );
									bbc.swapPortion(bbc.instrumentseq, bbc.receivedSequence, 0, bbc.instrumentseq.length/2);
									bbc.setRhythm(bbc.instrumentseq);
									bbc.swapOnce=true;
								}
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

					case 101: //play a song completely

						currentMeasure = bbc.mySong.getMeasure((int) this.generalMeasure);
						bbc.setRhythm(currentMeasure.toRhythm());
						//mActivity.aTest.setFrequency(m);
						break;

					case 102://play a song only with repsectot neighbors

						currentMeasure = bbc.mySong.getMeasure(bbc.numNeighbors);
						bbc.setRhythm(currentMeasure.toRhythm());

						break;

					case 103:  //play song wit respect to angle (this should go well with orbit
						int angind = (int) bbc.map(angle1, 0, 360, 0, bbc.mySong.numMeasures()-1);

						currentMeasure = bbc.mySong.getMeasure(angind);
						bbc.setRhythm(currentMeasure.toRhythm());

						break;

					case 104: //play song with respect ot speed

						break;

					case 105: //play a song and shift wrt neigbhor
						currentMeasure = new Measure(bbc.mySong.getMeasure((int) this.generalMeasure) );
						for(int i=0;i<bbc.numNeighbors*4;i++)
						{
							currentMeasure.shiftLeft();
						}
						bbc.setRhythm(currentMeasure.toRhythm());
						break;

					case 106: //play a song with angle.. such that it plays the song continuously, but shifted index according ot angle

						int theang = (int) bbc.map(angle1, 0, 360, 0, bbc.mySong.numMeasures()-1);

						currentMeasure = bbc.mySong.getMeasure((int) this.generalMeasure+theang);
						bbc.setRhythm(currentMeasure.toRhythm());


						break;

					case 110: // play a song in full but only according to my bell note
						currentMeasure = bbc.mySong.getMeasure((int) this.generalMeasure);
						bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));

						break;

					case 111: //play a song but only choose measures that contain my own note AND from other neighbors

						Vector notesToFind = new Vector();
						notesToFind.add(new Integer(bbc.myNote));

						for(int i=0;i<bbc.otherBots.size();i++)
						{
							Bot b = (Bot) bbc.otherBots.get(i);
							if(b.distToMe-20<bbc.neighborBound)
							{
								//notesToFind.add( new Integer( bbc.MSDeg[b.ID]+72 ) );
								//notesToFind.add( new Integer(bbc.getMSDegree(b.ID)+72 ));
								notesToFind.add( new Integer(bbc.getFightSongNote(b.ID) ));
							}							

						}

						if(!bbc.findOnce)//find once is necessary if using random numbers
						{

							possibleMeasures=bbc.mySong.getMeasuresWithTheseNotes(notesToFind);

							if(possibleMeasures.size()!=0)
							{

								if(bbc.numNeighbors==0)
								{
									currentMeasure  = (Measure) possibleMeasures.get((int) (Math.random()*possibleMeasures.size()) );
								}
								else
								{																	
									currentMeasure  = (Measure) possibleMeasures.get((int) (generalIndex%possibleMeasures.size()) );
								}
								bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));

								bbc.findOnce=true;//ok if here ?
							}

						}



						break;

					case 112: //gil's

						if(bbc.numNeighbors==0)
						{
							bbc.clearRhythm(bbc.instrumentseq);
							bbc.clearRhythm(bbc.sfxrseq);

						}
						else
						{
							Vector ntf = new Vector();
							ntf.add(new Integer(bbc.myNote));

							for(int i=0;i<bbc.otherBots.size();i++)
							{
								Bot b = (Bot) bbc.otherBots.get(i);
								//if(b.isNeighbor)
								if(b.distToMe-20<bbc.neighborBound)
								{
									//ntf.add( new Integer( bbc.MSDeg[b.ID]+72 ) );
									//ntf.add( new Integer( bbc.getMSDegree(b.ID)+72 ) );
									ntf.add( new Integer( bbc.getFightSongNote(b.ID) ));
								}							

							}

							if(!bbc.findOnce)
							{

								possibleMeasures=bbc.mySong.getMeasuresWithAnyNotes(ntf);

								if(possibleMeasures.size()!=0)
								{
									currentMeasure  = (Measure) possibleMeasures.get((int) (generalMeasure%possibleMeasures.size())  );
									bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));

									bbc.findOnce=true;//ok if here ?
									gilcounter++;

									if(gilcounter>=64)
									{
										gilcounter=0;
										bbc.setMapping(110);
										//this.mapping=110;//shoudl probably an inform method..and let the inform method do the mapping set
									}


								}

							}
						}
						break;

					case 113: //play song but only with bell and shift measure on angle  (needs fixing)
						Vector nnttff = new Vector();
						nnttff.add(new Integer(bbc.myNote));

						for(int i=0;i<bbc.otherBots.size();i++)
						{
							Bot b = (Bot) bbc.otherBots.get(i);
							//if(b.isNeighbor)
							if(b.distToMe-20<bbc.neighborBound)
							{
								//notesToFind.add( new Integer( bbc.MSDeg[b.ID]+72 ) );
								//nnttff.add( new Integer(bbc.getMSDegree(b.ID)+72 ));
								nnttff.add( new Integer(bbc.getFightSongNote(b.ID) ));
							}							

						}

						if(!bbc.findOnce)
						{

							possibleMeasures=bbc.mySong.getMeasuresWithTheseNotes(nnttff);



							if(possibleMeasures.size()!=0)
							{

								int shift = (int) bbc.map(angle1,0,360,0,8);



								currentMeasure  = (Measure) possibleMeasures.get( (int) ((generalMeasure+shift)%possibleMeasures.size()) );
								bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));

								bbc.findOnce=true;//ok if here ?
							}

						}

						break;

					case 114:

						int angind2 = (int) bbc.map(angle1, 0, 360, 0, bbc.mySong.numMeasures()-1);

						currentMeasure = bbc.mySong.getMeasure(angind2);
						bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));

						break;

					case 120: //for wander then follow in line
						if(bbc.myBehavior.wanderVector)
						{

						}
						if(bbc.myBehavior.wtfilFinalTargetReached)
						{

						}
						if(bbc.myBehavior.wanderThenFollowInLine)
						{

						}


						break;

					case 121:
						Vector measures = bbc.mySong.getMeasures(measureInd,measureInd+1);
						boolean allSilent=true;
						for(int i=0;i<measures.size();i++)
						{
							Measure m = (Measure) measures.get(i);
							if(!bbc.isSilent ( m.toBellRhythm(bbc.myNote)))
							{
								allSilent=false;
							}

						}

						currentMeasure=bbc.mySong.getMeasure((int) (measureInd+generalMeasure%2));
						boolean[] bb=currentMeasure.toBellRhythm(bbc.myNote);

						//
						if (!allSilent)
						{
							//this.separation = getDefaultSeparation();
							//this.alignment  = getDefaultAlignment();
							//this.cohesion = getDefaultCohesion();
							bbc.myBehavior.formationType="circle";
							bbc.myBehavior.setFormation(true);
							bbc.myBehavior.setOrbitCenter(false);



							if (bbc.numNeighbors==0 )
							{

								bbc.setRhythm(bb);
								for (int i=0;i<(bbc.ID+4)*4;i++)
								{
									bbc.shiftRhythmLeft(bbc.instrumentseq);
									bbc.shiftRhythmLeft(bbc.sfxrseq);
								}
							}
							else
							{
								bbc.setRhythm(bb);
							}
						}
						else
						{
							bbc.myBehavior.setFormation(false);
							bbc.setRhythm(bb);
							//this.separation=5;
							// this.alignment=0;
							//this.cohesion=0;
							if(generalMeasure%2==0)
							{
								bbc.myBehavior.setOrbitCenter(true);
								bbc.myBehavior.orbitClockwise=true;
								// bbc.myBehavior.orbit(new PVector(width/2,height/2), 200,true);
							}
							else
							{
								bbc.myBehavior.setOrbitCenter(true);
								bbc.myBehavior.orbitClockwise=false;
								//this.orbit(new PVector(width/2,height/2), 200,false);
							}
						}
						//


						break;

					case 122: // choreographed angle 1 choose the subset to follow each other in line// the ones that dont play dont move.//play according t angle
						Vector measures2 = bbc.mySong.getMeasures(measureInd,measureInd+1);
						boolean allSilent2=true;
						for(int i=0;i<measures2.size();i++)
						{
							Measure m = (Measure) measures2.get(i);
							if(!bbc.isSilent ( m.toBellRhythm(bbc.myNote)))
							{
								allSilent2=false;
							}

						}

						currentMeasure=bbc.mySong.getMeasure((int) (measureInd+generalMeasure%2));
						boolean[] bb2=currentMeasure.toBellRhythm(bbc.myNote);

						// get which bots are playing this...
						LinkedHashSet notebots = bbc.mySong.getNotesFromMeasures(measureInd, measureInd+1);

						// follow the appropriate bot in the list




						// map rhyhtm accoring to angle if notebots





						break;


					case 123:  ///this is one in which angles ar eused...//ones that are in it move..ones that are not in measure dont

						//get 2 phrases to make a measure (fight song)
						possibleMeasures = bbc.mySong.getMeasures(measureInd, measureInd+1);


						possibleMeasuresSilent=true;
						for (int i=0;i<possibleMeasures.size();i++)
						{
							Measure m = (Measure) possibleMeasures.get(i);
							if (!bbc.isSilent ( m.toBellRhythm(bbc.myNote)))
							{
								possibleMeasuresSilent=false;
							}
						}

						currentMeasure= bbc.mySong.getMeasure((int) (measureInd+generalMeasure%2));
						testMeasure =currentMeasure.toBellRhythm( bbc.myNote);

						if (!possibleMeasuresSilent)
						{
							/*
					      this.separation = getDefaultSeparation();
					       this.alignment  = getDefaultAlignment();
					       this.cohesion = getDefaultCohesion();
							 */
							//resume();
						}
						else
						{
							/*
					      this.separation=5;
					       this.alignment=0;
					       this.cohesion=0;
							 */
							bbc.stopAll();
							bbc.myBehavior.setSeparation(true);
						}



						// get which bots are playing this...

						notebots =  bbc.mySong.getNotesFromMeasures(measureInd, measureInd+1);
						itr = notebots.iterator();
						/*
						if (bbc.ID==0)
						{
							System.out.println("LinkedHashSet contains : ");
							while (itr.hasNext ())
								System.out.println(itr.next());
						}
						 */

						Vector botsToFollow = new Vector();
						for (int i=0; i<bbc.otherBots.size(); i++)
						{
							Bot b = (Bot) bbc.otherBots.get(i);

							//int botsnote= bbc.getMSDegree(b.ID);
							//int bnote = botsnote+72; // got to get appropriate note
							int bnote = bbc.getFightSongNote(b.ID);
							if (notebots.contains(new Integer(bnote)))
							{
								botsToFollow.add(b);


							}
							else
							{
								bbc.myBehavior.setSeparation(true);

								/*
								PVector bloc = new PVector(b.x,b.y);
								PVector loc = new PVector(bbc.myposx,bbc.myposy);
								if ( PVector.dist(bloc,  loc)<100)
								{
									bbc.myBehavior.evade(bloc);
								}
								 */

							}



						}

						if (!this.possibleMeasuresSilent&&botsToFollow.size()!=0)
						{
							Bot toFollow = (Bot)botsToFollow.get(0);
							if (toFollow.ID>bbc.ID)
							{
								if (generalMeasure%2==0)
								{
									bbc.myBehavior.setOrbitCenter(true);
									bbc.myBehavior.orbitClockwise=true;				
									//this.orbit(new PVector(width/2, height/2), 200, false);
									Log.d("","orbitting clockwise because to follow is" + toFollow.ID);
								}
								else
								{
									bbc.myBehavior.setOrbitCenter(true);
									bbc.myBehavior.orbitClockwise=false;	

									Log.d("","orbitting because to follow is" + toFollow.ID);
									//this.orbit(new PVector(width/2, height/2), 200, false);
									//println("orbitting because to follow is" + toFollow.ID);
								}
							}
							else
							{
								bbc.myBehavior.follow(toFollow);
								//bbc.myBehavior.leaderFollow(toFollow);
								Log.d("","id: " + bbc.ID+ " following" + toFollow.ID);
							}
						}

						// follow the appropriate bot in the list




						// map rhyhtm accoring to angle if notebots
						cang = bbc.camang;
						if(cang<0)
						{
							cang=360+cang;
						}


						int angShift= (int) bbc.map(cang, 0, 360, 0, bbc.SEQUENCERLENGTH/2);

						bbc.setRhythm(this.testMeasure );
						for (int i=0;i<angShift;i++)
						{


							bbc.shiftRhythmLeft(bbc.sfxrseq);
							bbc.shiftRhythmLeft(bbc.instrumentseq);
						}



						if (generalMeasure%12==0 )
						{
							bbc.clearRhythm(bbc.sfxrseq);
							bbc.clearRhythm(bbc.instrumentseq);
							if (!incMIndOnce)
							{
								measureInd+=2;
								incMIndOnce=true;
								//flockFunctionTimer2=JMSL.now();
							}





							currentMeasure=bbc.mySong.getMeasure(measureInd);
							bbc.setRhythm( currentMeasure.toBellRhythm(bbc.myNote)) ;
							//LinkedHashSet nn =currentMeasure.uniqueNotes();
							//println("unique notes: "+ nn.size());
							//mcount=0;
							//println(measureInd);
						}


						break;


					case 124:  //map all measure(two "measures") to an angle (simple)

						cang = bbc.camang;
						if(cang<0)
						{
							cang=360+cang;
						}

						int mshift= (int) bbc.map(cang, 0, 360, 0, bbc.mySong.numMeasures());

						if(mshift%2==1)
						{
							mshift-=1;							
						}

						/*
						possibleMeasures = bbc.mySong.getMeasures(mshift, mshift+1);


						possibleMeasuresSilent=true;
						for (int i=0;i<possibleMeasures.size();i++)
						{
							Measure m = (Measure) possibleMeasures.get(i);
							if (!bbc.isSilent ( m.toBellRhythm(bbc.myNote)))
							{
								possibleMeasuresSilent=false;
							}
						}
						 */

						currentMeasure= bbc.mySong.getMeasure((int) (mshift+generalMeasure%2));
						testMeasure =currentMeasure.toBellRhythm( bbc.myNote);

						bbc.setRhythm(testMeasure);					

						break;

					case 125: // simple speed


						//temporary solution

						//not moving
						if(bbc.lbyte==128&&bbc.rbyte==128)
						{
							bbc.clearRhythm(bbc.instrumentseq);
							bbc.clearRhythm(bbc.sfxrseq);
						}
						else
						{
							currentMeasure= bbc.mySong.getMeasure((int) ( generalMeasure));
							testMeasure =currentMeasure.toBellRhythm( bbc.myNote);
							bbc.setRhythm(testMeasure);
						}

						/*
						//full forward
						if(bbc.rbyte==128-20 && bbc.lbyte==128+20)
						{
							currentMeasure= bbc.mySong.getMeasure((int) ( generalMeasure));
							testMeasure =currentMeasure.toBellRhythm( bbc.myNote);
							bbc.setRhythm(testMeasure);
						}

						//might not need the ones below???



						//turning one way
						if(bbc.rbyte>128-20 && bbc.lbyte==128+20)
						{



							currentMeasure= bbc.mySong.getMeasure((int) ( generalMeasure+4));
							testMeasure =currentMeasure.toBellRhythm( bbc.myNote);

							bbc.setRhythm(testMeasure);
						}

						 // turning another 
						if(bbc.rbyte==128-20 && bbc.lbyte<128+20)
						{


							currentMeasure= bbc.mySong.getMeasure((int) ( generalMeasure+8));
							testMeasure =currentMeasure.toBellRhythm( bbc.myNote);
							bbc.setRhythm(testMeasure);
						}
						 */



						break;

					case 126: // choreographed neighbor // follow  subset freeze tag


						break;

					case 127: //choreographed speed /  foollow subset freeze

						break;

					case 128: //gil's function v1 as it was...

						break;

					case 129: //gil's function v1 modified for stopping instead of cohesion/and using extended neighbors
						//timesLoop=4;

						possibleNotes.clear();
						possibleNotes.add(new Integer( bbc.myNote));
						Vector ne = new Vector();

						for (int i=0;i<bbc.otherBots.size();i++)
						{
							Bot b = (Bot) bbc.otherBots.get(i);
							if(b.distToMe-20<bbc.neighborBound)//coudl be same thing as is neighbor
							{
								ne.add(b);
								possibleNotes.add( new Integer(bbc.getFightSongNote(b.ID) ));
							}	
						}

						
						
						//currentGilNeighbors=ne.size();
						/*
						if (currentGilNeighbors!=prevGilNeighbors)
						{
							noChangeNeighbors=false;

							prevGilNeighbors=currentGilNeighbors;
						}
						else
						{
							noChangeNeighbors=true; 
							//println("no change: " + noChangeNeighbors + " id:" + ID);
						}
						*/
						
						currentExtendedNeighbors = bbc.myExtendedNeighbors.size();
						if (currentExtendedNeighbors!=prevExtendedNeighbors)
						{
							noChangeExtendedNeighbors=false;
							prevExtendedNeighbors=currentExtendedNeighbors;
						}
						else
						{
							noChangeExtendedNeighbors=true; 
							//println("no change: " + noChangeNeighbors + " id:" + ID);
						}
						

						possibleMeasures= bbc.mySong.getMeasuresWithTheseNotes(possibleNotes);

						if (gilSeparate)
						{

							bbc.myBehavior.setSeparation(true);
							bbc.myBehavior.setWanderVector(true);


							for (int i=0;i<ne.size();i++)
							{
								Bot b = (Bot) ne.get(i);
								//bbc.myBehavior.evade(new PVector(b.x,b.y), false);
							}

							if (System.currentTimeMillis()-gilSeparateTimer>4000)
							{

								 
								bbc.myBehavior.setSeparation(false);

								gilSeparate=false;
							}
						}


						if (!hasCompletedGils())
						{
							if (possibleMeasures.size()!=0 && myPlaySessionCount<timesLoop)
							{
								if(!gilSeparate)
								{
									//if(bbc.numNeighbors!=0)
									if(ne.size()!=0)
									{
										bbc.stop();
										bbc.myBehavior.doStop();
										bbc.myBehavior.setSeparation(false);
										bbc.setWanderVector(false);
									}
									else
									{
										bbc.setWanderVector(true);
									}
								}


								currentMeasure  = (Measure) possibleMeasures.get( (int) (  (generalMeasure)%possibleMeasures.size()  ) );
								//currentMeasure  = (Measure) possibleMeasures.get( (int) (  (generalMeasure+generalMeasure%2)%possibleMeasures.size()  ) );
								//currentMeasure  = (Measure) possibleMeasures.get( (int) (  (generalMeasure)%possibleMeasures.size()  ) );

								bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));

								if (!this.myPlayFlag)
								{
									myPlaySessionCount++;
									myGeneralPlaySessionCount++;
									this.myPlayFlag=true;
								}
							}
							else///posibiles measures ==0 or playsessioncount>=timeslopps
							{

								if (possibleMeasures.size()==0)
								{
									bbc.myBehavior.setSeparation(true);
									bbc.myBehavior.setWanderVector(true);
									bbc.clearRhythm(bbc.sfxrseq);
									bbc.clearRhythm(bbc.instrumentseq);
									gilSeparate=true;
									gilSeparateTimer=System.currentTimeMillis();
								}
								
								if(this.myPlaySessionCount>=timesLoop)//??
								{
									
								}

								
							}
						}
						else //has completed gils
						{
							bbc.doRhythmMove=true;
							 
							bbc.myBehavior.setWanderVector(false);
							bbc.myBehavior.setSeparation(true);

							currentMeasure = bbc.mySong.getMeasure((int) generalMeasure);
							bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));						
							

							if (bbc.ID!=0)
							{
								
								/*
								Bot bbb= (Bot) bbc.otherBots.get(0);
								//bbc.myBehavior.seek(new PVector(bbb.x, bbb.y));
								bbc.targetx=bbb.x;
								bbc.targety=bbb.y;
								bbc.moveToLoc(true);
								*/
								
								bbc.myBehavior.setFollowInLine(true);
							}
							else
							{
								//bbc.myBehavior.orbit(new PVector(640/2, 480/2), 200, true);
								bbc.myBehavior.setOrbitCenter(true);
								
							}
						}


						// boolean noChangeNeighbors=true;   
						//if (myPlaySessionCount>=timesLoop&&noChangeNeighbors)
						if (myPlaySessionCount>=timesLoop&&noChangeExtendedNeighbors)
						{
							myBiggerPlaySessionCount++;
							myPlaySessionCount=0;

							gilSeparate=true;
							gilSeparateTimer=System.currentTimeMillis();
						}
						else
						{
							//if (!noChangeNeighbors)
							if (!noChangeExtendedNeighbors)
							{
								myPlaySessionCount=0;
							}
						}


						if (ne.size()==0 && !hasCompletedGils())
						{
							bbc.myBehavior.setWanderVector(true);
							bbc.clearRhythm(bbc.sfxrseq);
							bbc.clearRhythm(bbc.instrumentseq);

							if (myPlaySessionCount<2)
							{
								myPlaySessionCount=0;
							}
						}



						break;

					case 130: // gil's version 2 as is

						break;

					case 131: // gil's version 2 modified for stopping

						//timesLoop=4;

						this.possibleNotes = new Vector();
						possibleNotes.add(new Integer( bbc.myNote));
						Vector ne2 = new Vector();

						//Vector botsToFollow  = new Vector();
						//Vector botsToEvade = new Vector();

						for (int i=0;i<bbc.otherBots.size();i++)
						{
							Bot b = (Bot) bbc.otherBots.get(i);    
							if(b.distToMe-20<bbc.neighborBound)//coudl be same thing as is neighbor
							{
								ne2.add(b); 
								possibleNotes.add( new Integer(bbc.getFightSongNote(b.ID) ));
 
							}
						}
 
						/*
						currentGilNeighbors=ne2.size();
						if (currentGilNeighbors!=prevGilNeighbors)
						{
							noChangeNeighbors=false;

							prevGilNeighbors=currentGilNeighbors;
						}
						else
						{
							noChangeNeighbors=true; 
						}
						*/
						currentExtendedNeighbors = bbc.myExtendedNeighbors.size();
						if (currentExtendedNeighbors!=prevExtendedNeighbors)
						{
							noChangeExtendedNeighbors=false;
							prevExtendedNeighbors=currentExtendedNeighbors;
						}
						else
						{
							noChangeExtendedNeighbors=true; 
							//println("no change: " + noChangeNeighbors + " id:" + ID);
						}

						possibleMeasures= bbc.mySong.getMeasuresWithTheseNotes( possibleNotes);

						if (gilSeparate)
						{
							 
							bbc.myBehavior.setSeparation(true);
							bbc.setWanderVector(true);


							for (int i=0;i<ne2.size();i++)
							{
								Bot b = (Bot) ne2.get(i);
							}


							if (System.currentTimeMillis()-gilSeparateTimer>4000)
							{

								bbc.myBehavior.setSeparation(false);

								gilSeparate=false;
							}
						}

						if (hasCompletedGils())
						{
							if (completedGilsCount>bbc.mySong.numMeasures()*2)
							{
								resetGil();
							}
						}

						if (myBiggerPlaySessionCount<2 )
							//if (!hasCompletedGils())
						{
							if (possibleMeasures.size()!=0 && myPlaySessionCount<timesLoop)
							{
								//if(bbc.numNeighbors!=0)
								if(ne2.size()!=0)
								{
									bbc.stop();
									bbc.myBehavior.doStop();
									bbc.myBehavior.setSeparation(false);
								}
								else
								{
									bbc.setWanderVector(true);
								}

								currentMeasure  = (Measure) possibleMeasures.get( (int) (  (generalMeasure+generalMeasure%2)%possibleMeasures.size()  ) );
								//currentMeasure  = (Measure) possibleMeasures.get( (int) (  (generalMeasure)%possibleMeasures.size()  ) );

								bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));

								if (!myPlayFlag)
								{
									myPlaySessionCount++;
									myGeneralPlaySessionCount++;
									myPlayFlag=true;
								}

								if (ne2.size()==0)
								{
									// wander();
									bbc.setWanderVector(true);
									bbc.clearRhythm(bbc.instrumentseq);
									bbc.clearRhythm(bbc.sfxrseq);
								}
							}
							else
							{

								if (possibleMeasures.size()==0)
								{
									//wander();
									bbc.myBehavior.setWanderVector(true);
								}


								bbc.clearRhythm(bbc.sfxrseq);
								bbc.clearRhythm(bbc.instrumentseq);
							}
						}
						else
						{

							currentMeasure = bbc.mySong.getMeasure((int) generalMeasure);
							bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));


							//G doesn't work well so do formation
							bbc.myBehavior.formationType="circle";
							bbc.myBehavior.setFormation(true);
							bbc.doRhythmMove=true;

						}

						// boolean noChangeNeighbors=true;   
						//if (myPlaySessionCount>=timesLoop&&noChangeNeighbors)
						if (myPlaySessionCount>=timesLoop&&noChangeExtendedNeighbors)
						{
							myBiggerPlaySessionCount++;
							myPlaySessionCount=0;

							gilSeparate=true;
							gilSeparateTimer=System.currentTimeMillis();
						}
						else
						{
							//if (!noChangeNeighbors)
							if (!noChangeExtendedNeighbors)
							{
								myPlaySessionCount=0;
							}
						}
 

						if (ne2.size()==0)
						{
 

							if (myPlaySessionCount<2)
							{
								myPlaySessionCount=0;
							}
						}

						//Vector measures = mySong.getMeasures(measureInd, measureInd+1);



						break;
						
					case 132: // gils v1 modified for rotate to an angle instead of stopping
						
						//timesLoop=4;

						possibleNotes.clear();
						possibleNotes.add(new Integer( bbc.myNote));
						Vector nene = new Vector();

						for (int i=0;i<bbc.otherBots.size();i++)
						{
							Bot b = (Bot) bbc.otherBots.get(i);
							if(b.distToMe-20<bbc.neighborBound)//coudl be same thing as is neighbor
							{
								nene.add(b);
								possibleNotes.add( new Integer(bbc.getFightSongNote(b.ID) ));
							}	
						}

						
						
						//currentGilNeighbors=ne.size();
						/*
						if (currentGilNeighbors!=prevGilNeighbors)
						{
							noChangeNeighbors=false;

							prevGilNeighbors=currentGilNeighbors;
						}
						else
						{
							noChangeNeighbors=true; 
							//println("no change: " + noChangeNeighbors + " id:" + ID);
						}
						*/
						
						currentExtendedNeighbors = bbc.myExtendedNeighbors.size();
						if (currentExtendedNeighbors!=prevExtendedNeighbors)
						{
							noChangeExtendedNeighbors=false;
							prevExtendedNeighbors=currentExtendedNeighbors;
						}
						else
						{
							noChangeExtendedNeighbors=true; 
							//println("no change: " + noChangeNeighbors + " id:" + ID);
						}
						

						possibleMeasures= bbc.mySong.getMeasuresWithTheseNotes(possibleNotes);

						if (gilSeparate)
						{

							bbc.myBehavior.setSeparation(true);
							bbc.myBehavior.setWanderVector(true);
							bbc.myBehavior.setRotate(false);


							for (int i=0;i<nene.size();i++)
							{
								Bot b = (Bot) nene.get(i);
								//bbc.myBehavior.evade(new PVector(b.x,b.y), false);
							}

							if (System.currentTimeMillis()-gilSeparateTimer>4000)
							{

								 
								bbc.myBehavior.setSeparation(false);
								gilSeparate=false;
							}
						}


						if (!hasCompletedGils())
						{
							if (possibleMeasures.size()!=0 && myPlaySessionCount<timesLoop)
							{
								if(!gilSeparate)
								{
									//if(bbc.numNeighbors!=0)
									if(nene.size()!=0)
									{
										//bbc.stop();
										bbc.myBehavior.setRotate(true);
										//bbc.myBehavior.doStop();
										bbc.myBehavior.setSeparation(false);
										bbc.moveToLoc(true);
										bbc.targetx=bbc.myposx;
										bbc.targety=bbc.myposy-100;
									}
									else
									{
										bbc.setWanderVector(true);
										bbc.moveToLoc(false);
										bbc.myBehavior.setRotate(false);
										
									}
								}


								currentMeasure  = (Measure) possibleMeasures.get( (int) (  (generalMeasure)%possibleMeasures.size()  ) );
								//currentMeasure  = (Measure) possibleMeasures.get( (int) (  (generalMeasure+generalMeasure%2)%possibleMeasures.size()  ) );
								//currentMeasure  = (Measure) possibleMeasures.get( (int) (  (generalMeasure)%possibleMeasures.size()  ) );

								bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));

								if (!this.myPlayFlag)
								{
									myPlaySessionCount++;
									myGeneralPlaySessionCount++;
									this.myPlayFlag=true;
								}
							}
							else///posibiles measures ==0 or playsessioncount>=timeslopps
							{

								if (possibleMeasures.size()==0)
								{
									bbc.myBehavior.setSeparation(true);
									bbc.myBehavior.setWanderVector(true);
									bbc.clearRhythm(bbc.sfxrseq);
									bbc.clearRhythm(bbc.instrumentseq);
									gilSeparate=true;
									gilSeparateTimer=System.currentTimeMillis();
								}
								
								if(this.myPlaySessionCount>=timesLoop)//??
								{
									
								}

								
							}
						}
						else //has completed gils
						{
							bbc.doRhythmMove=true;
							 
							bbc.myBehavior.setWanderVector(false);
							bbc.myBehavior.setSeparation(true);

							currentMeasure = bbc.mySong.getMeasure((int) generalMeasure);
							bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));						
							

							if (bbc.ID!=0)
							{
								
								/*
								Bot bbb= (Bot) bbc.otherBots.get(0);
								//bbc.myBehavior.seek(new PVector(bbb.x, bbb.y));
								bbc.targetx=bbb.x;
								bbc.targety=bbb.y;
								bbc.moveToLoc(true);
								*/
								
								bbc.myBehavior.setFollowInLine(true);
							}
							else
							{
								//bbc.myBehavior.orbit(new PVector(640/2, 480/2), 200, true);
								bbc.myBehavior.setOrbitCenter(true);
								
							}
						}


						// boolean noChangeNeighbors=true;   
						//if (myPlaySessionCount>=timesLoop&&noChangeNeighbors)
						if (myPlaySessionCount>=timesLoop&&noChangeExtendedNeighbors)
						{
							myBiggerPlaySessionCount++;
							myPlaySessionCount=0;

							gilSeparate=true;
							gilSeparateTimer=System.currentTimeMillis();
						}
						else
						{
							//if (!noChangeNeighbors)
							if (!noChangeExtendedNeighbors)
							{
								myPlaySessionCount=0;
							}
						}


						if (nene.size()==0 && !hasCompletedGils())
						{
							bbc.myBehavior.setWanderVector(true);
							bbc.clearRhythm(bbc.sfxrseq);
							bbc.clearRhythm(bbc.instrumentseq);

							if (myPlaySessionCount<2)
							{
								myPlaySessionCount=0;
							}
						}



						break;

						
					case 133:
						
						
						if(bbc.myExtendedNeighbors.size()==0)
						{
							bbc.myBehavior.setWanderVector(true);
						}
						else
						{
							
						}
						
						break;
						
					case 134: //use extended neigbhors along with distributed euclid
						
						bbc.fillDistributedEuclid();
						
						boolean sameExtended =false;
						if(tExtendedNeighbors.size()==bbc.myExtendedNeighbors.size())
						{
							sameExtended=true;
						}
						this.tExtendedNeighbors.clear();
						this.tExtendedNeighbors.addAll(bbc.myExtendedNeighbors);
						BotComparator bc = new BotComparator();
						bc.compareBy("ID");
						Collections.sort(tExtendedNeighbors,bc);
						
						if(tExtendedNeighbors.size()==0)
						{
							bbc.myBehavior.setWanderVector(true);
						}
						else
						{
							bbc.myBehavior.setWanderVector(false);
						}
						
						if(sameExtended)
						{
							
						}
						else
						{
							deTimer = System.currentTimeMillis();
						}
						
						if(deTimer-System.currentTimeMillis()>1000*5)
						{
							//bbc.myBehavior.setWanderVector(true);
							deTimer=System.currentTimeMillis();
						}
						
						
						
						
						
						
						break;
						
						
					case 135 :  // wander then follow in line according to 
						
						
						
						
						break;
						 

					case 150:  //shift received sequence  based on angle

						//boolean[] t = bbc.receivedSequence.clone();
						int numdiv=6;
						int per=(int) (360.0/angle1);

						int shifts=per*numdiv;

						bbc.setRhythm(bbc.receivedSequence);
						for(int i=0; i <shifts; i++)
						{
							bbc.shiftRhythmRight(bbc.instrumentseq);
							bbc.shiftRhythmRight(bbc.sfxrseq);
						}


						//int sh = (int) bbc.map(angle1, 0, 360, 1, bbc.instrumentseq.length/3);


						break;

					case 151:  //shift received sequence based on neighbors

						int perN=6;
						int shiftneighbor=perN*bbc.numNeighbors;
						bbc.setRhythm(bbc.receivedSequence);
						for(int i=0; i <shiftneighbor; i++)
						{
							bbc.shiftRhythmRight(bbc.instrumentseq);
							bbc.shiftRhythmRight(bbc.sfxrseq);
						}

						break;

					case 152:  //shift received sequence based on speed
						
						
						

						break;
						
					case 153: //shift song measure based on angle
						
						
						
						int songShiftAngle = (int) bbc.map(angle1,0,360,0,bbc.mySong.numMeasures()/2);

						this.currentMeasure=bbc.mySong.getMeasure((int) ((generalMeasure + songShiftAngle)%bbc.mySong.numMeasures()));
						 
						bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));
						
						
						break;
						
					case 154: //shift song measure based on (extended) neigbhor
						

						//int songShiftNeighbor = (int) bbc.map(bbc.myExtendedNeighbors.size(),0,bbc.otherBots.size(),0,bbc.mySong.numMeasures()/2);

						//this.currentMeasure=bbc.mySong.getMeasure((int) ((generalMeasure + songShiftNeighbor)%bbc.mySong.numMeasures()));
						this.currentMeasure=bbc.mySong.getMeasure((int) ((generalMeasure + bbc.myExtendedNeighbors.size())%bbc.mySong.numMeasures()));
						 
						bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));
						
						break;
						
					case 155: // shift song measure  based on speed
						
						break;

					case 160: //shift song measre based on distance from a target (circle)
						
						//get circle target
						PVector targ = new PVector();
						if( true)//
						{
							PVector start = new PVector(640/2, 480/2);
							float r=200;

							float frac=0;
							if(bbc.usingController)
							{
								frac = (float)(2*Math.PI) / (bbc.otherBots.size());
							}
							else
							{
								frac = (float)(2*Math.PI) / (bbc.otherBots.size()+1);
							}


							float x = (float) (r*Math.sin( (float)(bbc.ID+bbc.myBehavior.formationOffset)*frac  ));
							float y= (float) (-r*Math.cos( (float)(bbc.ID+bbc.myBehavior.formationOffset)*frac  ));
							targ = new PVector(start.x+x, start.y+y);
						}
						
						PVector loc = new PVector(bbc.myposx,bbc.myposy);
						int thin = (int) bbc.map(PVector.dist(loc, targ),0,50,0,1);//every distance of 50 shift by 1
						
						this.currentMeasure=bbc.mySong.getMeasure((int) ((generalMeasure + thin)%bbc.mySong.numMeasures()));
						
						bbc.setRhythm(currentMeasure.toBellRhythm(bbc.myNote));
						
						break;
						
					case 161: //fill euclid based on distance to circle target...
						
						
						PVector targ2 = new PVector();
						if( true)
						{
							PVector start = new PVector(640/2, 480/2);
							float r=200;

							float frac=0;
							if(bbc.usingController)
							{
								frac = (float)(2*Math.PI) / (bbc.otherBots.size());
							}
							else
							{
								frac = (float)(2*Math.PI) / (bbc.otherBots.size()+1);
							}


							float x = (float) (r*Math.sin( (float)(bbc.ID+bbc.myBehavior.formationOffset)*frac  ));
							float y= (float) (-r*Math.cos( (float)(bbc.ID+bbc.myBehavior.formationOffset)*frac  ));
							targ2 = new PVector(start.x+x, start.y+y);
						}
						
						bbc.fillEuclidDist(targ2);
						
						/*
						PVector loc2 = new PVector(bbc.myposx,bbc.myposy);
						int thin2 = (int) bbc.map(PVector.dist(loc2, targ2),0,50,0,1);//every distance of 50 shift by 1
						bbc.setRhythm(bbc.receivedSequence);
						for(int i=0;i<thin2*6;i++)
						{
							bbc.shiftRhythmLeft(bbc.instrumentseq);
							bbc.shiftRhythmLeft(bbc.sfxrseq);
						}
						*/
						
						break;
						
					case 162: //shift based on distance to target...
						
						break;
						
					case 163: //filleuclid based on distance to targetx targety
						
						PVector ttt = new PVector(bbc.targetx,bbc.targety);
						bbc.fillEuclidDist(ttt);
						
						break;
						
					case 164://same rhythm, but only the correct notes when it reaches the targets of the circle or whatever target.
						
						//get circle target
						PVector targ22 = new PVector();
						if( true)//
						{
							PVector start = new PVector(640/2, 480/2);
							float r=200;

							float frac=0;
							if(bbc.usingController)
							{
								frac = (float)(2*Math.PI) / (bbc.otherBots.size());
							}
							else
							{
								frac = (float)(2*Math.PI) / (bbc.otherBots.size()+1);
							}


							float x = (float) (r*Math.sin( (float)(bbc.ID+bbc.myBehavior.formationOffset)*frac  ));
							float y= (float) (-r*Math.cos( (float)(bbc.ID+bbc.myBehavior.formationOffset)*frac  ));
							targ22 = new PVector(start.x+x, start.y+y);
						}
						
						PVector loc22 = new PVector(bbc.myposx,bbc.myposy);
						int thin22 = (int) bbc.map(PVector.dist(loc22, targ22),0,50,0,1);//every distance of 50 shift by 1
						
						this.currentMeasure=bbc.mySong.getMeasure((int) ((generalMeasure)%bbc.mySong.numMeasures()));
						
						bbc.setRhythm(currentMeasure.toBellRhythm( bbc.getFightSongNote( bbc.ID+ thin22%(bbc.otherBots.size()+1)  ))) ;
						
						break;
					 
					case 165:
						
						 //get circle target
					    PVector target = new PVector();
					    if ( true)//
					    {
					      PVector start = new PVector(640/2, 480/2);
					      float r=150;



					      float frac = (float)(2*Math.PI) / (bbc.otherBots.size()+1);
					      float x = (float) (r*Math.sin( (float)(bbc.ID)*frac  ));
					      float y= (float) (-r*Math.cos( (float)(bbc.ID)*frac  ));
					      target = new PVector(start.x+x, start.y+y);
					    }

					    bbc.myBehavior.moveTo(target);
					    ////////////
					    PVector llc = new PVector(bbc.myposx,bbc.myposy);
					    int th = (int) bbc.map(PVector.dist(llc, target), 0, 50, 0, 1);//every distance of 50 is 1
					     
					    if (th==0)
					    {
					      this.currentMeasure=bbc.mySong.getMeasure((int) ((generalMeasure)%bbc.mySong.numMeasures()));
					      bbc.setRhythm(currentMeasure.toBellRhythm(bbc.getFightSongNote( bbc.ID  )));
					    }
					    else
					    {
					      this.currentMeasure=bbc.mySong.getMeasure((int) ((generalMeasure)%bbc.mySong.numMeasures()));

					      LinkedHashSet un=currentMeasure.uniqueNotes();
					      Vector vv = new Vector(un);
					      
					      boolean[] base = currentMeasure.toBellRhythm(bbc.getFightSongNote( bbc.ID  ));

					      for (int i = 0;i < th;i++)
					      {
					        Integer ii = (Integer) vv.get(i%vv.size());

					         boolean[] t = currentMeasure.toBellRhythm(ii.intValue());

					        for (int m = 0; m < t.length;m ++)
					        {
					          base[m]= base[m] || t[m];
					        }
					      }
					      bbc.setRhythm(base);
					    }
					    //////////////////
						
						break;
					
						
					case 171: //  play according to song measure but embellish
						
						
						break;
						
					case 199: 


						break;


						////////////////////testing headwriting
					case 200:

						int mmval=20;
						if(this.generalMeasure%2==0)
						{
							bbc.writeHead(128-mmval);
						}
						else
						{
							bbc.writeHead(128+mmval);
						}

						break;

					case 201:

						int mmmval=30;
						int tind=this.generalIndex%bbc.instrumentseq.length;						
						int vv = (int) bbc.map(tind, 0, bbc.instrumentseq.length-1, 128-mmmval, 128+mmmval);						
						bbc.writeHead(vv);

						break;

					case 202:
						int mval=30;
						if(this.generalMeasure%2==0)
						{
							int testIndex=this.generalIndex%bbc.instrumentseq.length;						
							int val = (int) bbc.map(testIndex, 0, bbc.instrumentseq.length-1, 128-mval, 128+mval);						
							bbc.writeHead(val);
							Log.d("beattimer headval", "" + val + " index: " + testIndex );
						}
						else
						{
							int testIndex=this.generalIndex%bbc.instrumentseq.length;						
							int val = (int) bbc.map(testIndex, 0, bbc.instrumentseq.length-1, 128+mval, 128-mval);						
							bbc.writeHead(val);
							Log.d("beattimer headval", "" + val + " index: " + testIndex );
						}
						break;
					case 203:
						int mvall=36;
						if(this.generalMeasure%2==0)
						{
							int testIndex=this.generalIndex%bbc.instrumentseq.length;
							if(testIndex%4==0)
							{
								int val = (int) bbc.map(testIndex, 0, bbc.instrumentseq.length-1, 128-mvall, 128+mvall);						
								bbc.writeHead(val);
								Log.d("beattimer headval", "" + val + " index: " + testIndex );
							}
						}
						else
						{

							int testIndex=this.generalIndex%bbc.instrumentseq.length;						
							if(testIndex%4==0)
							{
								int val = (int) bbc.map(testIndex, 0, bbc.instrumentseq.length-1, 128+mvall, 128-mvall);						
								bbc.writeHead(val);
								Log.d("beattimer headval", "" + val + " index: " + testIndex );
							}
						}
						break;
					case 204:
						int mvalll=36;
						if(this.generalMeasure%2==0)
						{
							int testIndex=this.generalIndex%bbc.instrumentseq.length;
							if(testIndex% (bbc.instrumentseq.length/4)==0)
							{
								int val = (int) bbc.map(testIndex, 0, bbc.instrumentseq.length-1, 128-mvalll, 128+mvalll);						
								bbc.writeHead(val);
								Log.d("beattimer headval", "" + val + " index: " + testIndex );
							}
						}
						else
						{

							int testIndex=this.generalIndex%bbc.instrumentseq.length;						
							if(testIndex%(bbc.instrumentseq.length/4)==0)
							{
								int val = (int) bbc.map(testIndex, 0, bbc.instrumentseq.length-1, 128+mvalll, 128-mvalll);						
								bbc.writeHead(val);
								Log.d("beattimer headval", "" + val + " index: " + testIndex );
							}
						}
						break;

					default: ; break;

					}

					if(bbc.headMove)
					{
						if(!bbc.isSilent(bbc.instrumentseq))
						{

						}
						else
						{

						}
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
								//mActivity.aTest.setFrequencyAP();
								mActivity.aTest.setNote(bbc.myNote);
							}
							else
							{
								mActivity.aTest.setNote(currentMeasure.notes[bbc.currentIndex]);
								//mActivity.aTest.properIncrement();
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
						if(bbc.useInstrument)
						{
							bbc.playInstrument();
						}
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
			if(System.currentTimeMillis() - globalTimer> (float)globalTimeInterval/div)
			//if(System.currentTimeMillis() - globalHalfTimer> globalTimeInterval+globalTimeInterval/div)
			{////////////////////////////////
				//allowedToFire=false;
				//globalHalfTimer += globalTimeInterval;
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

				bbc.findOnce=false;
				this.myPlayFlag=false;

				if(hasCompletedGils())
				{
					this.completedGilsCount++;
				}
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


	boolean hasCompletedGils()
	{
		if(System.currentTimeMillis()-this.hasCompletedGilsTimer>gilTime)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	void resetGil()
	{
		completedGilsCount=0;

		bbc.doRhythmMove=false;
		
		hasCompletedGilsTimer=System.currentTimeMillis();

		bbc.myBehavior.sacWeights[0]=1;
		bbc.myBehavior.sacWeights[1]=1;
		bbc.myBehavior.sacWeights[2]=1;


		myBiggerPlaySessionCount=0;
		myGeneralPlaySessionCount=0;
		myPlaySessionCount=0;

	}



}
