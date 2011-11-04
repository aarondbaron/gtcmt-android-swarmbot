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


public class ClientCode implements OnClickListener{


	boolean connected;
	public String serverIpAddress;
	public String line;

	BoeBotController bbc;
	BeatTimer bt;

	Button connectToServer;
	EditText serverIp;

	TextView fromServer;

	Handler handler = new Handler();
	private DemoKitActivity mActivity;

	public ClientCode(DemoKitActivity mActivity, BoeBotController BBC)
	{
		bbc=BBC;
		this.mActivity = mActivity;
		attachToView();
	}

	public void attachToView()
	{
		connectToServer = (Button) mActivity.findViewById(R.id.connectServer);
		connectToServer.setOnClickListener(this);		
		serverIp = (EditText) mActivity.findViewById(R.id.serverIP);
		serverIp.setText("143.215.110.128");
		fromServer=(TextView)mActivity.findViewById(R.id.textView1);
	}



	///////////
	public class ClientThread implements Runnable {
		int myID=2;

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
					}
					if(line.contains("forward"+myID))
					{
						bbc.forward();

						Log.d("LINE","forward");
					}

					if(line.contains("backward"+myID))
					{
						bbc.backward();

						Log.d("LINE","backward");
					}

					if(line.contains("rotleft"+myID))
					{
						bbc.rotLeft();

						Log.d("LINE","rotleft");
					}

					if(line.contains("rotright"+myID))
					{
						bbc.rotRight();

						Log.d("LINE","rotright");
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

						

						if(ID==myID)
						{
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

							for(int i =0 ; i < bbc.otherBots.size();i++)
							{
								Bot b = bbc.otherBots.get(i);

								if(b.ID==ID)
								{
									//b.setVel(newx - b.x, newy-b.y);
									b.setPos(newx,newy);
									//b.angle=(float) Math.atan2(b.vy, b.vx);
									//b.azimuthAngle=0.000000f;// this has to be broadcast and parsed
									b.positionLost=posLost;
								}
								else //this means that the ID wasn't found in otherBots..so we need ot create it..
								{
									Bot newBot = new Bot();
									newBot.setPos(newx, newy);
									//newBot.setAngle(angle)
									//newBot.angle = (float) Math.atan2(b.vy, b.vx);
									//newBot.azimuthAngle=0.00000f; //this has to be broadcast and parsed
									b.positionLost=posLost;
									bbc.otherBots.add(newBot);

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


	}



}