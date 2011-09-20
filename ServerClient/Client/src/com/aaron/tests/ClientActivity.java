package com.aaron.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ClientActivity extends Activity  implements OnClickListener{

    private EditText serverIp;

    private Button connectPhones;

    private String serverIpAddress = "";

    private boolean connected = false;

    private Handler handler = new Handler();
    
    public int serverBeatNumber;
    
    public TextView fromserver;
    
    AudioTest aTest;

	public boolean sequencerMode;

	public boolean[] array;
	BeatTimer bt;
	public int currentIndex;
	BufferedReader in;
	
	String n;
	
	private String line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        serverIp = (EditText) findViewById(R.id.server_ip);
        connectPhones = (Button) findViewById(R.id.connect_phones);
        
        fromserver = (TextView) findViewById(R.id.fromserver);
        fromserver.setText("testing");
        
        connectPhones.setOnClickListener(this);
        
        
        bt = new BeatTimer();
        bt.mActivity=this;
        bt.setRunning(true);
        bt.start();
        
        array = new boolean[16];
        array[0]=true;
        aTest = new AudioTest();
        sequencerMode=true;
        

        
        
        //atest.
    }

    

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


                	///////////////////////

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
                				fillRhythm(4,array);
                			}
                			//fromClient.setText(""+ line);
                			handler.post(new Runnable() {
                				@Override
                				public void run() {
                					// do whatever you want to the front end
                					// this is where you can be creative
                					fromserver.setText("got" + line);
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
                				fromserver.setText("Oops. Connection interrupted. Please reconnect your phones.");
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
    
    
    
    
/////////////////////////////////////////
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId()== connectPhones.getId())
		{
			if (!connected) {
				serverIpAddress = serverIp.getText().toString();
				if (!serverIpAddress.equals("")) {
					Thread cThread = new Thread(new ClientThread());
					cThread.start();
				}
			}
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
	
	
	@Override
    protected void onStop() {
        super.onStop();
        try {
             // make sure you close the socket upon exiting
             //socket.close();
             bt.setRunning(false);
             bt.stop();
             
             aTest.t.stop();
             
             
         } catch (Exception e) {
             e.printStackTrace();
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
