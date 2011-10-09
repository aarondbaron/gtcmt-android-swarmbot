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
		fromServer=(TextView)mActivity.findViewById(R.id.textView1);
	}



	///////////
	public class ClientThread implements Runnable {
		int myID=1;

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
						/*
					Log.d("LINE", "test" + test.length);
					Log.d("LINE", "test" + test[0]);
					Log.d("LINE", "test" + test[1]);
					Log.d("LINE", "test" + test[2]);
					Log.d("LINE", "test" + test[3]);
						 */
						int ID = (int) Float.parseFloat(test[3]);
						if(ID==myID)
						{
							int newx=(int) Float.parseFloat(    line.split(",")[1]       ) ;
							int newy=(int) Float.parseFloat(    line.split(",")[2]  )    ;



							bbc.myvelx=bbc.myposx-newx;
							bbc.myvely=bbc.myposy-newy;						
							bbc.myangle = (float) Math.atan2(bbc.myvelx, bbc.myvely);

							bbc.myposx=(int) Float.parseFloat(    line.split(",")[1]       ) ;
							bbc.myposy= (int) Float.parseFloat(    line.split(",")[2]  )    ;


						}		
						else
						{
							int newx=(int) Float.parseFloat(    line.split(",")[1]       ) ;
							int newy=(int) Float.parseFloat(    line.split(",")[2]  )    ;

							bbc.targetvelx=bbc.targetx-newx;
							bbc.targetvely=bbc.targety-newy;						
							bbc.targetangle = (float) Math.atan2(bbc.targetvelx, bbc.targetvely);


							bbc.targetx=(int) Float.parseFloat(    line.split(",")[1]       ) ;
							bbc.targety=(int) Float.parseFloat(    line.split(",")[2]  )  ;
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


	public class Behavior
	{
		private boolean LRMotorSweep;
		private boolean incDec;

		byte step;

		boolean m1, m2;
		boolean m1IncDec, m2IncDec;

		int lowm1, highm1, lowm2 , highm2 , delta;

		//int pos1;// = 180;    // variable to store the servo position 
		//int pos2; //= 180;

		long timer;
		long interval=1000000;  //1 second  -- will change in constructor

		public Behavior()
		{
			step = (byte) 10;
			interval=interval/8;
		}


		void wander()
		{
			//Description..
			//behavior1
			//initially it will move forward
			//then one wheel will decrease to 128 
			// that wheel will then increase back up to 255 
			// the next wheel will decrease to 128
			//then increase to 255.
			//behavior 2
			// when encountering the edge, it must turn appropriately and after angle is correct, or time, it will 
			//go back to behavior1


			//move forward for sometime by default..



			////assuming m1 L m2 R

			//for some time shift wheel power down so bot starts to turn
			if(System.currentTimeMillis()-timer>interval)
			{
				timer+=interval;

				if(m1)
				{
					//increment speed phase Left
					if(m1IncDec)
					{
						if(bbc.getLByte()<highm1)
						{
							bbc.writeL( (byte) (bbc.getLByte()+ (byte)delta)  ); //pos1+=delta
						}
						else
						{
							//time to switch
							m1=false;
							m2=true;

							m1IncDec=false;
						}
					}
					else
					{
						//decrement speed phase Left
						if(bbc.getLByte()>lowm1)
						{
							bbc.writeL((byte) (bbc.getLByte() - (byte)delta) ); // pos1-=delta
						}
						else
						{
							//time to switch
							//m1=false;
							//m2=true;
							m1IncDec=true;
						}
					}
				}


				if(m2)
				{


					if(m2IncDec)
					{
						//increment speed phase RIGHT
						if(bbc.getRByte()>highm2)
						{
							bbc.writeR((byte)( bbc.getRByte()-delta)  ) ; // pos2-=delta;
						}
						else
						{
							//time to switch
							m1=true;
							m2=false;

							m2IncDec=false;
						}
					}
					else
					{
						//decrement speed phase Right
						if(bbc.getRByte()<lowm2)
						{
							bbc.writeR((byte) (bbc.getRByte()+delta));//  pos2+=delta;
						}
						else
						{
							//time to switch
							//m1=true;
							//m2=false;          
							m2IncDec=true;
						}
					}
				}			
			}
			
			// if reach boundary
			if(boundaryReached() )
			{
				int xbound=10;
				if(bbc.myposx < 0 + xbound)
				{
					//get what current angle is
					if(bbc.myangle>90 || bbc.myangle <270)
					{
						//this should probably be rotate until something
						bbc.rotLeft();
					}
					else
					{

						//rotate until some condition
						bbc.rotRight();
					}
				}
			}

		}

		boolean boundaryReached()
		{
			int maxh,maxw;
			maxh=1000;
			maxw=1000;

			if(bbc.myposx <0 )
			{

			}
			if(bbc.myposy<0)
			{

			}
			if(bbc.myposx>maxw)
			{

			}
			if(bbc.myposx>maxh)
			{

			}


			return true;
		}

	}

}
