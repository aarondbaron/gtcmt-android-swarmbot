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

	TextView fromServer;

	Handler handler = new Handler();
	private DemoKitActivity mActivity;
	int myID=0;

	long syncTimer=0;
	long waitTime=2000;
	long t1,t2;
	
	boolean syncFlag=false;

	public ClientCode(DemoKitActivity mActivity, BoeBotController BBC)
	{
		bbc=BBC;
		bbc.ID=myID;
		this.mActivity = mActivity;
		attachToView();
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
					
					if(line.contains("mode"))
					{
						String test [] = line.split(",");
						if(test[1]=="avatar")
						{						
						}						
					}
					
					if(line.contains("setID"))
					{
						String test [] = line.split(",");
						myID= Integer.parseInt(test[1]);
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mActivity.getApplicationContext(), "ID set to -- " +  myID + " -- by server", Toast.LENGTH_LONG).show();
							}
						});
						

					}

					
					if(line.contains("getPattern"))
					{
						String s= "pattern," + myID + "," + bbc.patternToString(bbc.instrumentseq);
						out.println(s);
					}

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

					//[ID]position:
					//[ID]stop
					//[ID]forward
					//[ID]backward
					if(line.contains("stop"+myID))
					{
						bbc.stop();
						bbc.moveToLoc(false);
						bbc.setWander(false);
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
					}

					if(line.contains("temporary"))
					{
						bbc.rfv.thread.message.displayMessage("temporary wander");
						bbc.danceSequencer=false;
						bbc.clearAllMovement();
						bbc.myBehavior  = new Behavior(bbc);
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

						int map = (int) Float.parseFloat(test[1]);
						bbc.setMapping(map);
						if(map==1)
						{
							bbc.rfv.thread.message.displayMessage("mapping: use compass" );
						}
						if(map==6)
						{
							bbc.rfv.thread.message.displayMessage("mapping: use size of your face" );
						}

						Log.d("client","   map" + map);
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


					if(line.contains("wander"+myID))
					{
						bbc.myBehavior.initWander();
						bbc.myBehavior.initWanderComplete=true;
						bbc.setWander(true);					
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
						int ID = (int) Float.parseFloat(test[3]);

						//Log.i("pos","x:"+test[1]);

						if(ID==myID)
						{
							Log.i("clientcode","mypos: " + "x:"+test[1]);
							boolean posLost=false;
							if(test[1]=="x" || test[2]=="x")
							{
								posLost=true;							
							}
							bbc.positionLost=posLost;

							if(!bbc.positionLost)
							{
								int newx=(int) Float.parseFloat(    line.split(",")[1]       ) ;
								int newy=(int) Float.parseFloat(    line.split(",")[2]  )    ;


								/*
								bbc.myvelx=bbc.myposx-newx;
								bbc.myvely=bbc.myposy-newy;						
								bbc.myangle = (float) Math.atan2(bbc.myvelx, bbc.myvely);
								 */


								bbc.myposx=(int) Float.parseFloat(    line.split(",")[1]       ) ;
								bbc.myposy= (int) Float.parseFloat(    line.split(",")[2]  )    ;
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


							int newx=(int) Float.parseFloat(    line.split(",")[1]       ) ;
							int newy=(int) Float.parseFloat(    line.split(",")[2]  )    ;

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
								newBot.ID=ID;
								bbc.otherBots.add(newBot);
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
									newBot.ID=ID;
									Log.d("clientcode", "new bot position set ");
								}
								//newBot.setAngle(angle)
								//newBot.angle = (float) Math.atan2(b.vy, b.vx);
								//newBot.azimuthAngle=0.00000f; //this has to be broadcast and parsed
								newBot.positionLost=posLost;
								bbc.otherBots.add(newBot);
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


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if(v.getId()== connectToServer.getId())
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
