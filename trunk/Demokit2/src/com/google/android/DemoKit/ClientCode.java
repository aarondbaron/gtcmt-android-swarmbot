package com.google.android.DemoKit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;



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

	PrintWriter out2;

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

				out2 = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


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

					//currently as of 3/29 the server is sending vel back to all...
					/*
					if(line.contains("vel,"))
					{
						Log.d("clientCode","vel was sent of all bots");
					}
					 */

					if(line.contains("testing"))
					{
						Log.d("clientCode",line);
					}



					if(line.contains("com,"))
					{
						//substring 0 is com
						//substring 1 is from who
						//substring 2, to whom
						//substring 3 what 
						//substring 4 ..the data????

						String test [] = line.split(",");
						int from= Integer.parseInt(test[1]);
						int tome=Integer.parseInt(test[2]);

						Log.d("client com" , "from: " + from );

						if(tome==bbc.ID)
						{
							String whatToDo = test[3];
							String data = test[4];

							if(whatToDo.equals("query"))
							{
								//assume just rhythm query for now
								sendMessage("com," + bbc.ID+ "," + from + "," + "response," + bbc.patternToString(bbc.instrumentseq));
								//sendMessage2("com," + bbc.ID+ "," + from + "," + "response," + bbc.patternToString(bbc.instrumentseq));

								 


							}
							if(whatToDo.equals("response"))
							{
								char[] a= data.toCharArray();
								boolean[] b  = new boolean[bbc.instrumentseq.length];
								for(int i=0;i<bbc.instrumentseq.length;i++)
								{
									if(i<a.length)
									{
										if(a[i]=='0')
										{
											b[i] = false;
											bbc.receivedSequence[i]=false;
											//bbc.instrumentseq[i]=false;
											//bbc.sfxrseq[i]=false;

											//temporary only
											//bbc.avatarseq[i]=false;
										}
										else
										{
											b[i]=true;
											bbc.receivedSequence[i]=true;
											//bbc.instrumentseq[i]=true;
											//bbc.sfxrseq[i]=true;

											//temporary only
											//bbc.avatarseq[i]=true;
										}
									}

								}



								///this should be a mapping behavior
								/*
								if(from<bbc.ID)
								{
									Log.d("client com" , "set rhythm from: " + from );
									bbc.setRhythm(b);
								}
								 */

							}
							/*
							if(whatToDo.equals("songmode"))
							{
								
							}
							*/

						}
					}

					if(line.contains("mode"))
					{
						String test [] = line.split(",");
						if(test[1]=="avatar")
						{						
						}						
					}
					
					if(line.contains("setAvatar"))
					{
						String test [] = line.split(",");
						int t= Integer.parseInt(test[1]);
						if(t==bbc.ID)
						{
							bbc.setAvatarMode(true);
						}
						else
						{
							bbc.setAvatarMode(false);
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

						 
						//bbc.setMyNote(bbc.getMSDegree(bbc.ID)+72   );
						bbc.setMyNote(bbc.getFightSongNote(bbc.ID));

						///
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity.getApplicationContext(), "ID set -- " +  myID + " -- by server--" + bbc.otherBots.size(), Toast.LENGTH_LONG).show();
							}
						});


					}

					if(line.contains("setNComEnable"))
					{
						bbc.setnComEnable(!bbc.nComEnable);
						
						Log.d("client ncom", "balue: " + bbc.nComEnable);
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

					/*
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
					*/

					if(line.contains("clearRhythm"))
					{
						bbc.clearRhythm(bbc.instrumentseq);
						bbc.clearRhythm(bbc.sfxrseq);
					}

					if(line.contains("useSFXR"))
					{
						bbc.useSFXR=!bbc.useSFXR;

					}
					if(line.contains("useSong"))
					{
						bbc.useSong=!bbc.useSong;
					}

					if(line.contains("directControl"))
					{
						bbc.directControl=!bbc.directControl;
					}

					if(line.contains("sacWeights"))
					{
						String test [] = line.split(",");						
						float t[] = new float[3];						
						for(int i=0;i<t.length;i++)
						{
							int ind=i+1;
							if(ind<test.length)
							{
								t[i]=Float.parseFloat(test[i+1]);
							}							
						}						
						bbc.myBehavior.sacWeights=t.clone();						
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
					////////////////////////

					if(line.contains("orbitCenter"))
					{
						String test [] = line.split(",");
						int dist=(int) Float.parseFloat(test[1]);

						int clockwise=(int) Float.parseFloat(test[2]);
						if(clockwise==0)
						{
							bbc.myBehavior.orbitClockwise=false;
						}
						else
						{
							bbc.myBehavior.orbitClockwise=true;
						}


						bbc.myBehavior.orbitDist=dist;
						bbc.myBehavior.setOrbitCenter(true);



						Log.d("client","orbit center: " + dist);

					}
					if(line.contains("orbitAvatar"))
					{
						String test [] = line.split(",");
						int dist=(int) Float.parseFloat(test[1]);

						bbc.myBehavior.orbitDist=dist;
						bbc.myBehavior.setOrbitAvatar(true);

						Log.d("client","orbit avatar: " + dist);
					}

					//orbitInLine
					if(line.contains("orbitInLine"))
					{
						bbc.myBehavior.setOrbitInLine(true);
						//bbc.myBehavior.followInLine();
					}

					if(line.contains("formation,"))
					{

						String test [] = line.split(",");
						Log.d("client","formation " + test[1] );

						int d=80;
						if(test.length>=3)
						{
							d=(int) Float.parseFloat(test[2]);
						}

						if(test[1].equals("circle"))
						{
							bbc.myBehavior.formationType=test[1];
							bbc.myBehavior.setFormation(true);
							bbc.myBehavior.distFormation=d;
							Log.d("client","formation " + test[1] );
						}
						if(test[1].equals("square"))
						{
							bbc.myBehavior.formationType=test[1];
							bbc.myBehavior.distFormation=d;
							bbc.myBehavior.setFormation(true);
						}
						if(test[1].equals("horizontal"))
						{
							bbc.myBehavior.formationType=test[1];
							bbc.myBehavior.distFormation=d;
							bbc.myBehavior.setFormation(true);
						}
						if(test[1].equals("vertical"))
						{
							bbc.myBehavior.formationType=test[1];
							bbc.myBehavior.distFormation=d;
							bbc.myBehavior.setFormation(true);
						}
						if(test[1].equals("diagonal"))
						{
							bbc.myBehavior.formationType=test[1];
							bbc.myBehavior.distFormation=d;
							bbc.myBehavior.setFormation(true);
						}
						if(test[1].equals("partialCircle"))
						{
							bbc.myBehavior.formationType=test[1];
							bbc.myBehavior.distFormation=d;
							bbc.myBehavior.setFormation(true);
						}

					}
					if(line.contains("breath1"))
					{
						bbc.myBehavior.setBreath1(true);
					}

					if(line.contains("breath2"))
					{
						bbc.myBehavior.setBreath2(true);
					}

					if(line.contains("followInLine"))
					{
						bbc.myBehavior.setFollowInLine(true);
						//bbc.myBehavior.followInLine();
					}
					if(line.contains("followMouse"))
					{
						String test [] = line.split(",");

						bbc.myBehavior.setFollowMouse(true);
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
						doStop();

						/*
						bbc.stop();
						bbc.moveToLoc(false);
						bbc.setWander(false);
						bbc.setWanderDance(false);
						bbc.setWanderVector(false);
						bbc.myBehavior.setFollowInLine(false);
						bbc.myBehavior.setOrbitAvatar(false);
						bbc.myBehavior.setOrbitCenter(false);
						bbc.myBehavior.setFollowMouse(false);
						bbc.myBehavior.setOrbitInLine(false);
						bbc.myBehavior.setWanderThenFollow(false);
						bbc.myBehavior.setWanderThenOrbit(false);
						bbc.myBehavior.setWanderThenFollowInLine(false);
						bbc.myBehavior.setWanderThenOrbitInLine(false);
						bbc.myBehavior.setBreath1(false);
						bbc.myBehavior.setBreath2(false);
						bbc.myBehavior.setFormation(false);
						bbc.myBehavior.setWanderVector(false);

						bbc.myBehavior.setSeparation(false);
						bbc.myBehavior.setAlignment(false);
						bbc.myBehavior.setCohesion(false);


						bbc.directControl=false;


						Log.d("LINE","stop");
						bbc.danceSequencer=false;
						 */



					}
					if(line.contains("stop"+myID))
					{
						bbc.stop();
						bbc.moveToLoc(false);
						bbc.setWander(false);
						bbc.setWanderDance(false);
						bbc.setWanderVector(false);
						bbc.myBehavior.setFollowInLine(false);
						bbc.myBehavior.setOrbitAvatar(false);
						bbc.myBehavior.setOrbitCenter(false);
						bbc.myBehavior.setFollowMouse(false);
						bbc.myBehavior.setOrbitInLine(false);
						bbc.myBehavior.setWanderThenFollow(false);
						bbc.myBehavior.setWanderThenOrbit(false);
						bbc.myBehavior.setWanderThenFollowInLine(false);
						bbc.myBehavior.setWanderThenOrbitInLine(false);
						bbc.myBehavior.setBreath1(false);
						bbc.myBehavior.setBreath2(false);
						bbc.myBehavior.setFormation(false);
						bbc.myBehavior.setWanderVector(false);

						bbc.directControl=false;


						Log.d("LINE","stop");
						bbc.danceSequencer=false;


					}
					if(line.contains("forward"+myID))
					{
						bbc.forward();

						//bbc.myBehavior.forwardVector();

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
					
					if(line.contains("headMotor,"))
					{
						String test [] = line.split(",");
						int i = Integer.parseInt(test[1]);
						
						bbc.writeHead(i);
						
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

					if(line.contains("chooseSong,"))
					{
						String test [] = line.split(",");
						int song = (int) Float.parseFloat(test[1]);
						bbc.chooseSong(song);
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



					if(line.contains("wanderThenFollow"))
					{
						bbc.myBehavior.setWanderThenFollow(true);						
					}

					if(line.contains("wanderThenOrbit"))
					{
						bbc.myBehavior.setWanderThenOrbit(true);						

					}

					if(line.contains("wtfil"))
					{
						bbc.myBehavior.setWanderThenFollowInLine(true);						
					}

					if(line.contains("wtoil"))
					{
						bbc.myBehavior.setWanderThenOrbitInLine(true);						

					}



					if(line.contains("wanderAll"))
					{
						/*
						bbc.myBehavior.initWander();
						bbc.myBehavior.initWanderComplete=true;
						bbc.setWander(true);					
						 */
						bbc.setWanderVector(true);
						bbc.myBehavior.setWanderVector(true);
						bbc.myBehavior.setSeparation(true);
					}

					if(line.contains("wander"+myID))
					{
						/*
						bbc.myBehavior.initWander();
						bbc.myBehavior.initWanderComplete=true;
						bbc.setWander(true);					
						 */
						bbc.setWanderVector(true);
						bbc.myBehavior.setWanderVector(true);
						bbc.myBehavior.setSeparation(true);
					}
					if(line.contains("controller,"))
					{
						Log.d("client","controller move");
						String test [] = line.split(",");
						
						bbc.usingController(true);


						//just do this for now....
						bbc.myBehavior.setSeparation(true);

						if(test.length>=2)
						{
							int code=(int) Float.parseFloat(    test[1]       ) ;
							if(code==999) // stop all
							{

								doStop();

								Log.d("client Controller","stop");
								bbc.danceSequencer=false;


							}
							if(code==998) // move with mouse continuously
							{

								bbc.myBehavior.setSeparation(true);

								int x=(int) Float.parseFloat(    test[2]       ) ;
								int y=(int) Float.parseFloat(    test[3]  )    ;

								Log.d("controller move","move to x:"+x+"y:"+y);
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


								Log.d("controller move"," :"+x+"y:"+y);
							}
							if(code==997)
							{
								if(test.length>=3)
								{
									bbc.myBehavior.formationType=test[2];
									int d = (int) Float.parseFloat(  test[3] );
									bbc.myBehavior.setFormation(true);
									bbc.myBehavior.distFormation=d;
									Log.d("client","formation " + test[2] );
								}

							}
							if(code==996)
							{

								int dist=(int) Float.parseFloat(test[2]);

								int clockwise=(int) Float.parseFloat(test[3]);
								if(clockwise==0)
								{
									bbc.myBehavior.orbitClockwise=false;
								}
								else
								{
									bbc.myBehavior.orbitClockwise=true;
								}


								bbc.myBehavior.orbitDist=dist;
								bbc.myBehavior.setOrbitCenter(true);
								Log.d("client","controller orbit center: " + dist);
							}
							if(code==9988) //tug move
							{
								if(test.length==4)
								{
									bbc.myBehavior.setSeparation(true);
									int x=(int) Float.parseFloat( test[2] ) ;
									int y=(int) Float.parseFloat( test[3] )    ;
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
									Log.d("controller TUG move"," :"+x+"y:"+y);
								}
								if(test.length==2)
								{
									bbc.moveToLoc(false);
									bbc.myBehavior.setFollowMouse(false);
								}
							}

							if(code==9987) //avatar tug move
							{
								if(bbc.ID==0)//only avatar
								{
									if(test.length==4)
									{
										bbc.myBehavior.setSeparation(true);
										int x=(int) Float.parseFloat( test[2] ) ;
										int y=(int) Float.parseFloat( test[3] )    ;
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
										Log.d("controller avatar TUG move"," :"+x+"y:"+y);
										
										if(bbc.ID==0)
										{
											bbc.avatarMoving=true;
										}
									}
									if(test.length==2)
									{
										bbc.moveToLoc(false);
										if(bbc.ID==0)
										{
											bbc.avatarMoving=false;
										}
										bbc.myBehavior.setFollowMouse(false);
									}
								}
								else
								{
									//else follow avatar or follow in line???
									if(test.length==4)
									{
										bbc.myBehavior.setFollowInLine(true);
									
									}
									if(test.length==2)
									{
										bbc.moveToLoc(false);
										bbc.myBehavior.setFollowMouse(false);
										bbc.myBehavior.setFollowInLine(false);
									}
									
								}
							}
							if(code==99879)
							{
								
								//follow   avatar
								//bbc.myBehavior.follow((Bot)bbc.otherBots.get(0));
								bbc.myBehavior.botTarget=bbc.currentAvatar;
								bbc.myBehavior.setFollowAvatar(true);
								
							}
							if(code==99878)
							{
								//follow in line with avatar
								bbc.myBehavior.setFollowInLine(true);
							}
							if(code==99877)
							{
								//evade avatar
								
								bbc.myBehavior.setEvadeAvatar(true);
								
							}
							if(code==99876)
							{
								
							}
							if(code==9986) // move relative
							{
								bbc.myBehavior.setSeparation(true);

								int x=(int) Float.parseFloat(    test[2]       ) ;
								int y=(int) Float.parseFloat(    test[3]  )    ;

								Log.d("controller move relative","move relative x:"+x+"y:"+y);
								bbc.targetx=bbc.myposx+1000*x;
								bbc.targety=bbc.myposy+1000*y;
								bbc.myBehavior.phase1move=true;
								bbc.myBehavior.phase2move=false;
								bbc.moveToLoc(true);
								
								//should we just make a move relative behavior?
								
							}
							
							if(code==995) //wander
							{
								bbc.setWanderVector(true);
								bbc.myBehavior.setWanderVector(true);
								bbc.myBehavior.setSeparation(true);
							}

							if(code==800)
							{
								int map = (int) Float.parseFloat(test[2]);
								bbc.setMapping(map);
								
								/*
								if(map==0)
								{
									bbc.clearRhythm(bbc.instrumentseq);
									bbc.clearRhythm(bbc.sfxrseq);
								}
								*/

								Log.d("client","  controller map" + map);
								handler.post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(mActivity.getApplicationContext(), "mapping set -- " +  bbc.mActivity.beatTimer.mapping + " -- by server--" + bbc.otherBots.size() + "-nn-" + bbc.numNeighbors, Toast.LENGTH_LONG).show();
									}
								});
							}
							if(code==801)
							{								 
								
								char[] a= test[2].toCharArray();
								boolean[] b  = new boolean[bbc.instrumentseq.length];
								for(int i=0;i<bbc.instrumentseq.length;i++)
								{
									if(i<a.length)
									{
										if(a[i]=='0')
										{
											b[i] = false;
											bbc.receivedSequence[i]=false;
											//bbc.instrumentseq[i]=false;
											//bbc.sfxrseq[i]=false;

											//temporary only
											//bbc.avatarseq[i]=false;
										}
										else
										{
											b[i]=true;
											bbc.receivedSequence[i]=true;
											//bbc.instrumentseq[i]=true;
											//bbc.sfxrseq[i]=true;

											//temporary only
											//bbc.avatarseq[i]=true;
										}
									}

								}
								
								bbc.setRhythm(bbc.receivedSequence);
								
								
								handler.post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(mActivity.getApplicationContext(), "sequence-- "  , Toast.LENGTH_LONG).show();
									}
								});
							}
						}

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


					//beacons
					if(line.contains("toggleBeacon"))
					{						
						bbc.myBehavior.toggleUseBeacon();									
					}
					if(line.contains("setBeaconRadius"))
					{
						String test [] = line.split(",");

						int rad=(int) Float.parseFloat(test[1]);

						for(int i=0;i<bbc.beacons.size();i++)
						{
							Beacon b = new Beacon();
							b.radius=rad;							
						}						
					}
					if(line.contains("beacon"))
					{
						String test [] = line.split(",");
						int ID,type,x,y,rad;

						ID=(int) Float.parseFloat(test[1]);
						type=(int) Float.parseFloat(test[2]);
						x=(int) Float.parseFloat(test[3]);
						y=(int) Float.parseFloat(test[4]);
						rad=(int) Float.parseFloat(test[5]);

						if(bbc.beacons.size()==0)
						{
							Beacon b = new Beacon();
							b.ID=ID;
							b.type=type;
							b.x=x;
							b.y=y;
							b.radius=rad;
							bbc.beacons.add(b);
						}
						for(int i=0;i<bbc.beacons.size();i++)
						{
							Beacon b = (Beacon)bbc.beacons.get(i);
							if(b.ID==ID)
							{
								b.type=type;
								b.x=x;
								b.y=y;
								b.radius=rad;
							}					

							//Log.d("client","beacon," + ID+","+type+","+ x+","+y+"," +rad);

						}



					}

					//if it has myID
					if(line.contains("position"))
					{

						String test [] = line.split(",");
						// new format  [position][x][y][angle][id]
						if(test.length!=5)
						{

							Log.d("client","length of test is " + test.length + " \n " + test.toString());

						}
						else
						{




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

	public void sendMessage2(String s)
	{
		if(System.currentTimeMillis()-bbc.nComTimer>300)
		{
			Iterator<?> i = bbc.queue.iterator();		
			Bot b = (Bot) i.next();
			i.remove();
			String ss="com,"+ mActivity.client.myID + "," + b.ID + "," + "query" + "," + "nnnn";

			Log.d("client sendMessage2","qsize: " +bbc.queue.size() + " " + ss );
			out2.println(ss);

			bbc.nComTimer=System.currentTimeMillis();
		}
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

	public void doStop()
	{
		bbc.stop();
		bbc.moveToLoc(false);
		bbc.setWander(false);
		bbc.setWanderDance(false);
		bbc.setWanderVector(false);
		bbc.myBehavior.setFollowInLine(false);		
		bbc.myBehavior.setOrbitCenter(false);
		bbc.myBehavior.setFollowMouse(false);
		bbc.myBehavior.setOrbitInLine(false);
		bbc.myBehavior.setWanderThenFollow(false);
		bbc.myBehavior.setWanderThenOrbit(false);
		bbc.myBehavior.setWanderThenFollowInLine(false);
		bbc.myBehavior.setWanderThenOrbitInLine(false);
		bbc.myBehavior.setBreath1(false);
		bbc.myBehavior.setBreath2(false);
		bbc.myBehavior.setFormation(false);
		bbc.myBehavior.setWanderVector(false);
		
		bbc.myBehavior.setFollowAvatar(false);
		bbc.myBehavior.setEvadeAvatar(false);
		bbc.myBehavior.setOrbitAvatar(false);

		bbc.myBehavior.setSeparation(false);
		bbc.myBehavior.setAlignment(false);
		bbc.myBehavior.setCohesion(false);


		bbc.directControl=false;


		Log.d("LINE","stop");
		bbc.danceSequencer=false;
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
