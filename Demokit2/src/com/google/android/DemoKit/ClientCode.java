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
					Log.d("LINE", "test" + test.length);
					Log.d("LINE", "test" + test[0]);
					Log.d("LINE", "test" + test[1]);
					Log.d("LINE", "test" + test[2]);
					Log.d("LINE", "test" + test[3]);

					int ID = Integer.parseInt(test[3]);
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

    			Log.d("ClientActivity", line);
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
