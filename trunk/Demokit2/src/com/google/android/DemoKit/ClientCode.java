package com.google.android.DemoKit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;



import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ClientCode implements OnClickListener{


	boolean connected;
	public String serverIpAddress;
	public String line;

	BoeBotController bbc;
	BeatTimer bt;

	Button connectToServer, incrementID;
	EditText serverIp;

	TextView fromServer,velest,angest;

	Handler handler = new Handler();
	private DemoKitActivity mActivity;
	int myID=0;

	long syncTimer=0;
	long waitTime=2000;
	long t1,t2;

	boolean syncFlag=false;

	long initialConnectTimer;
	boolean initialConnect=false;
	
	boolean messageFlag = false;
	String message;
	public ClientCode(DemoKitActivity mActivity, BoeBotController BBC)
	{
		bbc=BBC;
		bbc.ID=myID;
		this.mActivity = mActivity;
		attachToView();


		initialConnectTimer = System.currentTimeMillis();

	}

	public void attachToView()
	{
		connectToServer = (Button) mActivity.findViewById(R.id.connectServer);
		connectToServer.setOnClickListener(this);		
		serverIp = (EditText) mActivity.findViewById(R.id.serverIP);
		//serverIp.setText("143.215.110.128");
		//serverIp.setText("192.168.1.27");
		//serverIp.setText("172.17.10.16");
		//serverIp.setText("10.20.0.2");
		serverIp.setText("10.0.1.4");

		fromServer=(TextView)mActivity.findViewById(R.id.textView1);
		velest=(TextView)mActivity.findViewById(R.id.vestimates);
		angest=(TextView)mActivity.findViewById(R.id.angleestimates);

		incrementID = (Button) mActivity.findViewById(R.id.incrementID);
		incrementID.setOnClickListener(this);
	}



	///////////
	public class ClientThread implements Runnable {


		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
				Log.d("ClientActivity", "C: Connecting...");
				Socket socket = new Socket(serverAddr, 8080);
				connected = true;
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

				while ((line = in.readLine()) != null) {
					if(line.contains("header"))
					{

					}


					if(line.contains("sync"))
					{
						String test [] = line.split(",");
						//bbc.resetIndex();

						long mtime=Long.parseLong(test[1]);

						if(mtime==0)
						{

						}
						else
						{
							long timeDiff=Long.parseLong(test[2]);
							Log.d("client", "myID: " + myID  +  "  sync: time diff -- " + timeDiff);

							waitTime=mtime-timeDiff;						
							//wait until appropriate time
							//4seconds if id 0, 4 seconds - time from 0 to 1 if id 1;
							syncTimer=System.currentTimeMillis();

							syncFlag=true;
						}


					}

					if(System.currentTimeMillis()-syncTimer>waitTime && syncFlag)
					{
						bbc.resetIndex();
						syncFlag=false;
					}
					
					if(messageFlag)
					{
						out.println(message);
						messageFlag=false;
						//Log.d("client","sending message "  + message);
					}

					if(line.contains("mode"))
					{
						String test [] = line.split(",");
						if(test[1]=="avatar")
						{						
						}						
					}

					if(line.contains("djembe"))
					{
						mActivity.beatTimer.generalMeasure=0;
						switch(bbc.dj)
						{
						case 0: bbc.setRhythm(bbc.djembe0);break;
						case 1: bbc.setRhythm(bbc.djembe1);break;
						case 2: bbc.setRhythm(bbc.djembe2);break;
						case 3: bbc.setRhythm(bbc.djembe3);break;
						default: ;


						}

						bbc.dj++;
						if(bbc.dj>=4)
						{
							bbc.dj=0;
						}
						Log.d("client", "djembe");
					}

					if(line.contains("setID"))
					{
						String test [] = line.split(",");
						myID= Integer.parseInt(test[1]);
						bbc.ID=myID;
						
						////check other bots
						for(int i=0;i<bbc.otherBots.size();i++)
						{
							Bot b = (Bot)bbc.otherBots.get(i);
							if(b.ID==myID)
							{
								bbc.otherBots.remove(b);
								
							}
						}
						
						
						///
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity.getApplicationContext(), "ID set -- " +  myID + " -- by server--" + bbc.otherBots.size(), Toast.LENGTH_LONG).show();
							}
						});


					}


					if(line.contains("getPattern"))
					{
						String s= "pattern," + myID + "," + bbc.patternToString(bbc.instrumentseq);
						out.println(s);
					}

					/*
					if(line.contains("mapping"))
					{
						String test [] = line.split(",");
						if(test[1]=="angle")
						{
							bbc.setMapping(1);

						}
						if(test[1]=="neighbor")
						{
							bbc.setMapping(2);

						}
						if(test[1]=="speed")
						{
							bbc.setMapping(3);

						}
						if(test[1]=="ID")
						{
							bbc.setMapping(4);

						}
						if(test[1]=="avatar")
						{

							bbc.setMapping(7);
						}

					}
					*/

					if(line.contains("pattern"))
					{
						String test [] = line.split(",");

						char[] a= test[1].toCharArray();


						for(int i=0;i<bbc.instrumentseq.length;i++)
						{
							if(i<a.length)
							{
								if(a[i]=='0')
								{
									bbc.instrumentseq[i]=false;
									bbc.sfxrseq[i]=false;

									//temporary only
									bbc.avatarseq[i]=false;
								}
								else
								{
									bbc.instrumentseq[i]=true;
									bbc.sfxrseq[i]=true;

									//temporary only
									bbc.avatarseq[i]=true;
								}
							}

						}

						if(mActivity.beatTimer.generalMeasure%2==0)
						{

						}
						else
						{
							//int t = (int ) map(PVector.dist(this.loc,currentAvatar.loc), nearbyBoidBounds,500,0,number/2);   
							//bbc.loseN(bbc.instrumentseq,1);
							//bbc.loseN(bbc.sfxrseq,1);
						}

					}


					if(line.contains("avatarpattern"))
					{
						String test [] = line.split(",");

						char[] a= test[1].toCharArray();

						bbc.avatarMode=true;

						for(int i=0;i<bbc.instrumentseq.length;i++)
						{
							if(i<a.length)
							{
								if(a[i]=='0')
								{
									bbc.avatarseq[i]=false;
									if(myID==0)
									{
										bbc.instrumentseq[i]=false;
										bbc.sfxrseq[i]=false;
									}
								}
								else
								{
									bbc.avatarseq[i]=false;
									if(myID==0)
									{
										bbc.instrumentseq[i]=false;
										bbc.sfxrseq[i]=false;
									}
								}
							}

						}

						if(mActivity.beatTimer.generalMeasure%2==0)
						{

						}
						else
						{
							//int t = (int ) map(PVector.dist(this.loc,currentAvatar.loc), nearbyBoidBounds,500,0,number/2);   
							//bbc.loseN(bbc.instrumentseq,1);
							//bbc.loseN(bbc.sfxrseq,1);
						}

					}
					
					if(line.contains("clearRhythm"))
					{
						bbc.clearRhythm(bbc.instrumentseq);
						bbc.clearRhythm(bbc.sfxrseq);
					}
					
					if(line.contains("useSFXR"))
					{
						bbc.useSFXR=!bbc.useSFXR;
					
					}
					
					if(line.contains("directControl"))
					{
						bbc.directControl=!bbc.directControl;
					}
					
					if(line.contains("separation"))
					{
						bbc.myBehavior.toggleSeparation();
					}
					if(line.contains("alignment"))
					{
						bbc.myBehavior.toggleAlignment();
					}
					if(line.contains("cohesion"))
					{
						bbc.myBehavior.toggleCohesion();
					}
					
					if(line.contains("followInLine"))
					{
						bbc.myBehavior.setFollowInLine(true);
						//bbc.myBehavior.followInLine();
					}
					
					if(line.contains("vmInterval"))
					{
						String test [] = line.split(",");
						
						bbc.myBehavior.vmInterval=Long.parseLong(test[1]);
						Log.d("client","vmInterval" + bbc.myBehavior.vmInterval);
						
						
					}

					//[ID]position:
					//[ID]stop
					//[ID]forward
					//[ID]backward
					if(line.contains("stopAll"))
					{
						bbc.stop();
						bbc.moveToLoc(false);
						bbc.setWander(false);
						bbc.setWanderDance(false);
						bbc.setWanderVector(false);
						bbc.myBehavior.setFollowInLine(false);
						Log.d("LINE","stop");
						bbc.danceSequencer=false;
						
						bbc.directControl=true;
						
					}
					if(line.contains("stop"+myID))
					{
						bbc.stop();
						bbc.moveToLoc(false);
						bbc.setWander(false);
						bbc.setWanderDance(false);
						bbc.setWanderVector(false);
						bbc.myBehavior.setFollowInLine(false);
						Log.d("LINE","stop");
						bbc.danceSequencer=false;
					}
					if(line.contains("forward"+myID))
					{
						bbc.forward();

						Log.d("LINE","forward");
						bbc.danceSequencer=false;
					}

					if(line.contains("backward"+myID))
					{
						bbc.backward();

						Log.d("LINE","backward");
						bbc.danceSequencer=false;
					}

					if(line.contains("rotleft"+myID))
					{
						bbc.rotLeft();

						Log.d("LINE","rotleft");
						bbc.danceSequencer=false;
					}

					if(line.contains("rotright"+myID))
					{
						bbc.rotRight();

						Log.d("LINE","rotright");
						bbc.danceSequencer=false;
					}

					if(line.contains("leftmotor"+myID))
					{
						String test [] = line.split(",");
						int i = Integer.parseInt(test[1]);
						bbc.writeL(i);  //gotta check this...

						Log.d("LINE","leftmotor");


						bbc.danceSequencer=false;
					}
					if(line.contains("rightmotor"+myID))
					{
						String test [] = line.split(",");
						int i = Integer.parseInt(test[1]);
						bbc.writeR(i);

						Log.d("LINE","rightmotor");
						bbc.danceSequencer=false;
					}

					if(line.contains("calibrate"+myID))
					{
						bbc.calibrationAngle=bbc.angleAzimuth;

						Log.d("LINE","calibration done");
					}

					if(line.contains("calibrateAll"))
					{
						bbc.calibrationAngle=bbc.angleAzimuth;

						Log.d("LINE","calibration All done");
					}

					if(line.contains("neighborBound"))
					{
						String test [] = line.split(",");
						bbc.neighborBound= Integer.parseInt(test[1]);
					}

					if(line.contains("orientAll"))
					{
						String test [] = line.split(",");

						int x=(int) Float.parseFloat(test[1]);
						int y=(int) Float.parseFloat(test[2]);

						bbc.targetx=x;
						bbc.targety=y;

						bbc.orientToLoc(true);
						Log.d("ClientCode","orientx:"+x+"orienty:"+y);
						//bbc.moveBehavior.orient2Loc(x, y);

					}

					if(line.contains("dance"))
					{
						bbc.danceSequencer=!bbc.danceSequencer;
						bbc.rfv.thread.message.displayMessage("dance:" + bbc.danceSequencer);
						if(bbc.danceSequencer)
						{
							bbc.randomMirrorSP();
						}
						else
						{
							bbc.clearAllMovement();
							bbc.stop();

						}

						Log.d("ClientCode","dance: " + bbc.danceSequencer);

					}
					if(line.contains("eudanse"))
					{
						bbc.setMapping(0);
						bbc.danceSequencer=!bbc.danceSequencer;

						if(bbc.danceSequencer)
						{
							bbc.euclidDance();
							bbc.fillEuclid(4, bbc.sfxrseq);
							bbc.fillEuclid(4, bbc.instrumentseq);
						}
						else
						{
							bbc.clearAllMovement();
							bbc.clearRhythm(bbc.sfxrseq);
							bbc.clearRhythm(bbc.instrumentseq);
							bbc.stop();

						}
						bbc.rfv.thread.message.displayMessage("eudance:" + bbc.danceSequencer);

						Log.d("ClientCode","dance: " + bbc.danceSequencer);

					}
					if(line.contains("wndnce"))
					{
						bbc.setMapping(2);
						bbc.myBehavior.initWander();
						bbc.myBehavior.initWanderComplete=true;
						bbc.setWanderDance(true);


					}
					if(line.contains("tempoup"))
					{
						bbc.tempoUp();
						bbc.rfv.thread.message.displayMessage("tempo:" + bbc.getTempo());
						Log.d("client","tempoup" + bbc.getTempo());

					}
					if(line.contains("tempodown"))
					{
						bbc.tempoDown();
						bbc.rfv.thread.message.displayMessage("tempo:" + bbc.getTempo());
						Log.d("client","tempodown"+ bbc.getTempo());
					}
					if(line.contains("setTempo"))
					{

						String test [] = line.split(",");
						mActivity.beatTimer.globalTimeInterval=Integer.parseInt(test[1]);
						Log.d("client","setTempo"+ bbc.getTempo());
					}

					if(line.contains("temporary"))
					{
						bbc.rfv.thread.message.displayMessage("temporary wander");
						bbc.danceSequencer=false;
						bbc.clearAllMovement();
						//bbc.myBehavior  = new Behavior(bbc);
						bbc.myBehavior.m1=false;
						bbc.myBehavior.m2=true;
						//backward();
						bbc.forward();				
						bbc.myposx=200;
						bbc.myposy=200;				
						//mActivity.beatTimer.wander=true;
						bbc.setWander(true);
					}


					if(line.contains("mapping"))
					{
						String test [] = line.split(",");

						//check to make sure we get the mapping...
						if(test.length>=2)
						{
							int map = (int) Float.parseFloat(test[1]);
							bbc.setMapping(map);
							if(map==1)
							{
								if(bbc.rfv.thread.message!=null)
								{
									bbc.rfv.thread.message.displayMessage("mapping: use compass" );
								}
							}
							if(map==6)
							{
								if(bbc.rfv.thread.message!=null)
								{
									bbc.rfv.thread.message.displayMessage("mapping: use size of your face" );
								}
							}

							Log.d("client","   map" + map);
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(mActivity.getApplicationContext(), "mapping set -- " +  bbc.mActivity.beatTimer.mapping + " -- by server--" + bbc.otherBots.size() + "-nn-" + bbc.numNeighbors, Toast.LENGTH_LONG).show();
								}
							});
							
							
						}
						
					}

					if(line.contains("reuc"))
					{
						bbc.setMapping(0);
						int n = (int) (8*Math.random()+1);
						bbc.rfv.thread.message.displayMessage("randomeuclid" );
						bbc.fillEuclid(n, bbc.instrumentseq);
						bbc.fillEuclid(n, bbc.sfxrseq);
					}

					if(line.contains("seuc"))
					{
						bbc.setMapping(0);
						String test [] = line.split(",");
						int se = (int) Float.parseFloat(test[1]);
						bbc.rfv.thread.message.displayMessage("euclid " + se );

						bbc.fillEuclid(se, bbc.instrumentseq);
						bbc.fillEuclid(se, bbc.sfxrseq);
					}

					if(line.contains("silence"))
					{
						bbc.rfv.thread.message.displayMessage("silence " );
						bbc.setMapping(0);
						bbc.clearRhythm(bbc.instrumentseq);
						bbc.clearRhythm(bbc.sfxrseq);
					}

					if(line.contains("fnow"))
					{
						bbc.fillNow(bbc.instrumentseq);
						bbc.fillNow(bbc.sfxrseq);

					}


					if(line.contains("wanderAll"))
					{
						/*
						bbc.myBehavior.initWander();
						bbc.myBehavior.initWanderComplete=true;
						bbc.setWander(true);					
						*/
						bbc.setWanderVector(true);
					}

					if(line.contains("wander"+myID))
					{
						/*
						bbc.myBehavior.initWander();
						bbc.myBehavior.initWanderComplete=true;
						bbc.setWander(true);					
						*/
						bbc.setWanderVector(true);
					}
					if(line.contains("move"))
					{
						String test [] = line.split(",");

						int ID = (int) Float.parseFloat(test[3]);
						Log.e("move","move to position!");



						if(ID==myID)
						{			

							if(!bbc.positionLost)
							{
								int x=(int) Float.parseFloat(    line.split(",")[1]       ) ;
								int y=(int) Float.parseFloat(    line.split(",")[2]  )    ;
								Log.e("move","move to x:"+x+"y:"+y);
								bbc.targetx=x;
								bbc.targety=y;
								bbc.myBehavior.phase1move=true;
								bbc.myBehavior.phase2move=false;
								bbc.moveToLoc(true);



								//bbc.moveBehavior.move2Loc(x, y);

								//////////////////////////////////
								//////////////////////////////////
								bbc.target.x=x;
								bbc.target.y=y;


								Log.e("move","move to done x:"+x+"y:"+y);
							}
							else
							{

							}
						}

					}

					if(line.contains("cheat"))
					{
						String test [] = line.split(",");
						int ID = (int) Float.parseFloat(test[3]);

						if(ID==myID)
						{

						}
						else
						{

						}

					}

					if(line.contains("startMove"))
					{
						String test [] = line.split(",");
						int v1 = (int) Float.parseFloat(test[1]);
						int v2 = (int) Float.parseFloat(test[2]);

						bbc.writeL(v1);
						bbc.writeR(v2);

					}
					if(line.contains("stopMove"))
					{

						bbc.stop();
					}




					handler.post(new Runnable() {
						@Override
						public void run() {
							fromServer.setText("position: "+line);
						}
					});

					//if it has myID
					if(line.contains("position"))
					{

						String test [] = line.split(",");
						//note...used to be 3..now its 4 because of new format
						int ID = (int) Float.parseFloat(test[4]);

						//Log.i("pos","x:"+test[1]);

						if(ID==myID)
						{
							//Log.i("clientcode","mypos: " + "x:"+test[1]);
							boolean posLost=false;
							if(test[1]=="x" || test[2]=="x")
							{
								posLost=true;							
							}
							bbc.positionLost=posLost;

							if(!bbc.positionLost)
							{
								//int newx=(int) Float.parseFloat(    line.split(",")[1]  );
								//int newy=(int) Float.parseFloat(    line.split(",")[2]  );
								//position
								int newx=(int) Float.parseFloat(   test[1]  );
								int newy=(int) Float.parseFloat(    test[2]  );								
								//angle from cam
								float newang= Float.parseFloat(test[3]); 


								bbc.camang=newang;
								/*
								bbc.myvelx=bbc.myposx-newx;
								bbc.myvely=bbc.myposy-newy;						
								bbc.myangle = (float) Math.atan2(bbc.myvelx, bbc.myvely);
								 */
								//given the structure of the robot...assuming we know how it moves..
								//dont' take position estimate if lybte or rbyte is 128
								if(  ! (bbc.lbyte == 128 && bbc.rbyte == 128)  )
								{
									bbc.pastx[bbc.vxyindex]=newx;
									bbc.pasty[bbc.vxyindex]=newy;
									bbc.vxs[bbc.vxyindex]=bbc.myposx-newx;
									bbc.vys[bbc.vxyindex]=bbc.myposy-newy;
									bbc.aest[bbc.vxyindex] = (float)  Math.atan2(bbc.vys[bbc.vxyindex], bbc.vxs[bbc.vxyindex]);

									bbc.e.px[bbc.e.iter]=newx;
									bbc.e.py[bbc.e.iter]=newy;
									bbc.e.times[bbc.e.iter]=System.currentTimeMillis();
									//bbc.vxs[bbc.vxyindex]=bbc.myposx-newx;
									//bbc.vys[bbc.vxyindex]=bbc.myposy-newy;
									//bbc.aest[bbc.vxyindex] = (float)  Math.atan2(bbc.vys[bbc.vxyindex], bbc.vxs[bbc.vxyindex]);


									int testind;
									if(bbc.vxyindex+1 >= bbc.vxs.length)
									{
										testind=0;
									}
									else
									{
										testind =bbc.vxyindex+1;
									}
									bbc.vest.x=newx-bbc.pastx[testind];
									bbc.vest.x=newx-bbc.pastx[testind];

									bbc.avest=bbc.vest.heading2D();

									bbc.vxyindex++;

									if(bbc.vxyindex>=bbc.vxs.length)
									{
										bbc.vxyindex=0;									
									}


									handler.post(new Runnable() {
										@Override
										public void run() {
											String s1 ="vel--";
											String s2= "ang--";

											float a1,a2,a3;
											a1=0;
											a2=a1;
											a3=a2;
											for(int i=0;i<bbc.vxs.length;i++)
											{
												s1 += "x:" + bbc.vxs[i] + "," + " y:" + bbc.vys[i];
												s2 += bbc.aest[i] + ",";

												a1+=bbc.vxs[i];
												a2+=bbc.vys[i];
												a3+=bbc.aest[i];


											}

											a1=a1/(float)bbc.vxs.length;
											a2=a2/(float)bbc.vys.length;
											a3=a3/(float)bbc.aest.length;


											velest.setText(s1 + " -- " + a1 + " , " + a2);
											angest.setText(s2 + " -- " + a3);

											//Log.d("client"," bytes "+bbc.lbyte+  "," + bbc.rbyte);
										}
									});


								}

								//write position
								//bbc.myposx=(int) Float.parseFloat(    line.split(",")[1]       ) ;
								//bbc.myposy= (int) Float.parseFloat(    line.split(",")[2]  )    ;
								bbc.myposx = newx;
								bbc.myposy = newy;



							}
							else
							{
								//do nothing..keep previous position?  or perhaps stop until something happens
								bbc.stop();

							}


						}		
						else   /// not my ID
						{
							boolean posLost=false;
							if(test[1]=="x" || test[2]=="x")
							{
								posLost=true;							
							}


							int newx=(int) Float.parseFloat(    test[1]       ) ;
							int newy=(int) Float.parseFloat(    test[2]  )    ;

							float newang = Float.parseFloat( test[3]);

							//bbc.targetvelx=bbc.targetx-newx;
							//bbc.targetvely=bbc.targety-newy;						
							//bbc.targetangle = (float) Math.atan2(bbc.targetvelx, bbc.targetvely);


							//							bbc.targetx=(int) Float.parseFloat(    line.split(",")[1]       ) ;
							//							bbc.targety=(int) Float.parseFloat(    line.split(",")[2]  )  ;

							///////////////////////// new way

							//this would be ideal if we have a comparator...but for now
							//Bot b =bbc.otherBots.get(ID);
							//Log.d("clientcode", "size of otherBots " + bbc.otherBots.size());
							if(bbc.otherBots.size()==0)
							{
								Log.d("clientcode", "adding new bot: " + ID);
								Bot newBot = new Bot();
								newBot.setPos(newx, newy);
								newBot.camang=newang;
								newBot.ID=ID;
								bbc.otherBots.add(newBot);
								if(ID==0)
								{
									bbc.currentAvatar=newBot;
								}

								double dist=Math.sqrt( Math.pow((bbc.myposx-newBot.x),2) + Math.pow((bbc.myposy-newBot.y),2) ) ;
								bbc.distances.add(new Float(dist));
							}

							//boolean[] createNew= new boolean[bbc.otherBots.size()];
							boolean cnew=true;

							for(int i =0 ; i < bbc.otherBots.size();i++)
							{
								Bot b = bbc.otherBots.get(i);

								if(b.ID==ID)
								{
									//Log.d("clientcode", "b.id==id  " + ID);
									//b.setVel(newx - b.x, newy-b.y);
									if(!posLost)
									{
										b.setPos(newx,newy);
										b.camang=newang;


										//double dist=Math.sqrt( Math.pow((bbc.myposx-b.x),2) + Math.pow((bbc.myposy-b.y),2) ) ;
										//bbc.distances.set(ID, new Float(dist));


									}
									//b.angle=(float) Math.atan2(b.vy, b.vx);
									//b.azimuthAngle=0.000000f;// this has to be broadcast and parsed
									b.positionLost=posLost;

									cnew=false;
									break;
								}
								else //this means that the ID wasn't found in otherBots..so we need ot create it..
								{



								}
							}

							if(cnew)
							{
								Log.d("clientcode", "new bot -- b.id!=id  " + ID);
								Bot newBot = new Bot();
								if(!posLost)
								{
									newBot.setPos(newx, newy);
									newBot.camang=newang;
									newBot.ID=ID;

									double dist=Math.sqrt( Math.pow((bbc.myposx-newBot.x),2) + Math.pow((bbc.myposy-newBot.y),2) ) ;
									bbc.distances.add(new Float(dist));

									Log.d("clientcode", "new bot position set ");
								}
								//newBot.setAngle(angle)
								//newBot.angle = (float) Math.atan2(b.vy, b.vx);
								//newBot.azimuthAngle=0.00000f; //this has to be broadcast and parsed
								newBot.positionLost=posLost;
								bbc.otherBots.add(newBot);

								if(ID==0)
								{
									bbc.currentAvatar=newBot;
								}
							}




						}

					}

					bbc.numberOfNeigbhors();

					//Log.d("ClientActivity", line);
				}
			} catch (Exception e) {
				Log.e("ClientActivity", "C: Error", e);
				connected = false;
			}
		}
	}

	/////////////
	
	public void sendMessage(String s)
	{
		message=s;
		this.messageFlag=true;
	}

	public void doStuff()
	{
		Log.d("ClientCode", "attempting to connect");
		if (!connected) {

			serverIpAddress = serverIp.getText().toString();
			if (!serverIpAddress.equals("")) {
				Thread cThread = new Thread(new ClientThread());
				cThread.start();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if(v.getId()== connectToServer.getId())
		{
			doStuff();
		}

		if(v.getId()==incrementID.getId())
		{

			myID++;
			if(myID>2)
			{
				myID=0;
			}
			Log.d("clientcode","incrementing id: " + myID);
			Toast.makeText(mActivity.getApplicationContext(), "incremented.  id is now " + myID, Toast.LENGTH_SHORT).show();

		}


	}



}
