package com.aaron.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ServerActivity extends Activity implements OnClickListener {

	private TextView serverStatus, serverIP;
	public TextView beatNumber;
	public TextView fromClient;
	public TextView test;
	// default ip
	public static String SERVERIP = "10.0.2.15";

	// designate a port
	public static final int SERVERPORT = 8080;

	private Handler handler = new Handler();

	public ServerSocket serverSocket;

	public boolean[] array;
	public boolean sequencerMode;
	public int currentIndex;

	BeatTimer bt;

	AudioTest aTest;
	String line;
	PrintWriter out;

	ServerView sView;

	boolean serverConnected;
	
	Button resetIndex, randomisePattern;
	private boolean rstInd, rndPattern;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.server);
		setContentView(R.layout.main);
		//serverStatus = (TextView) findViewById(R.id.server_status);
		serverStatus = (TextView) findViewById(R.id.textView1);
		serverIP = (TextView) findViewById(R.id.serverip);
		beatNumber = (TextView) findViewById(R.id.beatNumber);
		//fromClient = (TextView) findViewById(R.id.fromClient);
		test = (TextView) findViewById(R.id.textView2);

		SERVERIP = getLocalIpAddress();
		serverIP.setText("hello" + SERVERIP);


		aTest = new AudioTest();


		bt = new BeatTimer();
		bt.mActivity=this;
		bt.setRunning(true);
		bt.start();



		Thread fst = new Thread(new ServerThread());
		fst.start();

		array = new boolean[16];
		sequencerMode=true;

		array[0]=true;
		
		resetIndex = (Button) findViewById(R.id.button1);
		resetIndex.setOnClickListener(this);
		
		randomisePattern = (Button) findViewById(R.id.button2);
		randomisePattern.setOnClickListener(this);

	}

	public class ServerThread implements Runnable {

		public void run() {


			Log.d("ServerActivity", "running");

			try {
				if (SERVERIP != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							serverStatus.setText("Listening on IP: " + SERVERIP);
						}
					});
					serverSocket = new ServerSocket(SERVERPORT);
					while (true) {


						Log.d("ServerActivity", "listening for clients.");
						// listen for incoming clients
						Socket client = serverSocket.accept();
						Log.d("ServerActivity", "accepted");
						serverConnected=true;
						handler.post(new Runnable() {
							@Override
							public void run() {
								serverStatus.setText("Connected.");
								Log.d("ServerActivity", "connected");
								//
								//beatNumber.setText("beat" + currentIndex);


							}
						});

						while(serverConnected)
						{
							try 
							{

								//Log.d("ServerActivity", "S: Sending command.");
								PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
								BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
								
								// where you issue the commands

								if(bt.generalIndex%16==0)
								{
									Log.d("ServerActivity", "send on beat.");
									out.println("Hello clients. my index!: " + currentIndex);
								}
								if(rstInd)
								{
									Log.d("rst Ind", "resetindexpressed");
									out.println("rst");
								  rstInd=false;
								  bt.resetIndex();
								}
								if(rndPattern)
								{
									Log.d("rst Ind", "resetindexpressed");
									out.println("rnd");
									rndPattern=false;
									fillRhythm(4,array);
								  bt.resetIndex();
								
								}
								//out.flush();
								//out.close();

								handler.post(new Runnable() {
									@Override
									public void run() {
										serverStatus.setText("Connected." + currentIndex);
										test.setText("" + currentIndex);
										//
										//beatNumber.setText("beat" + currentIndex);

									}
								});


								//Log.d("ServerActivity", "S: Sent.");

								if(in.ready())
								{
									while ((line = in.readLine()) != null) {

										Log.d("ServerActivity", "line" + line);
										if(in.ready())
										{
											
											
										}
										else
										{
											break;
										}

									}
								}
								
								
							}
							catch(Exception e)
							{
								Log.e("ServerActivity", "C: Error", e);

							}
						}

					}
				} else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							serverStatus.setText("Couldn't detect internet connection.");
						}
					});
				}
			} catch (Exception e) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						serverStatus.setText("Error");
					}
				});
				e.printStackTrace();
			}


			Log.d("ServerActivity", "end of run");
		}


	}



	// gets the ip address of your phone's network
	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
				}
			}
		} catch (SocketException ex) {
			Log.e("ServerActivity", ex.toString());
		}
		return null;
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			// make sure you close the socket upon exiting
			serverSocket.close();
			bt.setRunning(false);
			bt.stop();

			aTest.t.stop();


		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	void fillRhythm(int a, boolean b[])
	{
		clearRhythm(b);


		if(a==0)
		{

			return;
		}
		for (int i=0; i<b.length;i++)
		{

			if(i%a==0)
			{
				b[i]=true;
			}
		}


	}

	void invertRhythm(boolean b[])
	{
		for (int i=0; i<b.length;i++)
		{
			b[i]=!b[i];
		}
	}

	void clearRhythm(boolean b[]) 
	{
		for(int i=0;i<b.length;i++)
		{
			b[i]=false;
		}
		//rebuildMusicShape();
	}

	// fill particular places
	void fillPosition(int n[], int sn, boolean b[])
	{    
		clearRhythm(b);
		for(int i=0; i<sn;i++)
		{
			if(i<b.length&&i>=0)
			{
				b[n[i]]=true;
			}
			else
			{
				//System.out.println("tried to put: " + n[i]);
			}
		}
		//rebuildMusicShape();
	}

	boolean isSilent(boolean b[])
	{
		boolean f=false;
		for (int i=0; i < b.length;i++)
		{
			f = f || b[i];
		}  

		return !f;
	}

	void sendBeat(int beat)
	{
		//beatNumber.setText("beat: " + beat);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Log.d("onclick", "reset index preseed");
		if(arg0.getId()==resetIndex.getId())
		{
			Log.d("onclick", "reset index preseed");
			rstInd=true;
		}
		
		if(arg0.getId()==randomisePattern.getId())
		{
			Log.d("onclick", "randomisePattern index preseed");
			rndPattern=true;
		}
	}
	
	boolean[] euclidArray(int m, int k)
	  {
	    if(k<m)
	      return new boolean[k];

	    if(m==0)
	      return new boolean[k];

	    Vector d[] = new Vector[m];
	    for(int i=0; i <m; i++)
	    {
	      d[i] = new Vector();
	      d[i].add("1");
	    }

	    int dif=k-m;
	    //number of zeros

	    for(int i=0; i< dif; i++)
	    {
	      //println(i%m);
	      d[i%m].add("0");
	    }

	    Vector r = new Vector();

	    for(int i=0; i< d.length;i++)
	    {
	      r.addAll(d[i]);
	    }


	    boolean b[]= new boolean[k];
	    for(int i =0; i < r.size(); i++)
	    {
	      String s = (String) r.get(i);
	      //print(s);
	      if(s.equals("1"))
	      {
	        b[i]=true;
	      }
	    }
	    //println();
	    return b;
	  }


}
