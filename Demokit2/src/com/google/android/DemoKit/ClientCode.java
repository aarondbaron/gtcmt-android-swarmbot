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


public class ClientCode implements OnClickListener{
	
	
	boolean connected;
	public String serverIpAddress;
	public String line;
	
	
	BeatTimer bt;
	
	Button connectToServer;
	EditText serverIp;
	
	Handler handler = new Handler();
	private DemoKitActivity mActivity;
	
	public ClientCode(DemoKitActivity mActivity)
	{
		this.mActivity = mActivity;
		//this.bt=bt;
		
		/*
		connectToServer.setOnClickListener(this);		
		connectToServer.findViewById(R.id.connectServer);
		
		serverIp.findViewById(R.id.serverIP);
		*/
		
		attachToView();
	}
	
	public void attachToView()
	{
		connectToServer = (Button) mActivity.findViewById(R.id.connectServer);
		connectToServer.setOnClickListener(this);		
		serverIp = (EditText) mActivity.findViewById(R.id.serverIP);
		
	}
	
	
	
	///////////
	public class ClientThread implements Runnable {
		

		public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                //Socket socket = new Socket(serverAddr, ServerActivity.SERVERPORT);
                Socket socket = new Socket(serverAddr, 8080);
                connected = true;
                //BufferedReader in;
                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (connected) {
                	try {
                		Log.d("client", "before creating bufread");
                		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                		line = null;

                		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                		while ((line = in.readLine()) != null) {
                			
                			if(in.ready())
                			{
                				Log.d("in status", "ready");
                			}
                			else
                			{
                				Log.d("in status", "not ready");
                			}
                			Log.d("from ServerActivity", line);

                			if(line.equals("rst"))
                			{
                				bt.resetIndex();
                			}
                			if(line.equals("rnd"))
                			{
                				bt.resetIndex();
                				//fillRhythm(4,array);
                			}
                			//fromClient.setText(""+ line);
                			handler.post(new Runnable() {
                				@Override
                				public void run() {
                					// do whatever you want to the front end
                					// this is where you can be creative
                					//fromserver.setText("got" + line);
                					/*
                                	out.println("hello client. blhalbadsfdsafsdfadsf \n");

                					 */
                				}
                			});
                			
                			if(bt.generalIndex%16==0)
                			{
                				out.println("hello server");
                			}

                		}

                		break;
                	} catch (Exception e) {
                		handler.post(new Runnable() {
                			@Override
                			public void run() {
                				//fromserver.setText("Oops. Connection interrupted. Please reconnect your phones.");
                			}
                		});
                		e.printStackTrace();
                	}
                    
 
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
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
