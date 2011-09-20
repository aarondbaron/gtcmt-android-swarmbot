package com.aaron.tests;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ServerView extends View{

	BeatTimer bt;
	private TextView serverStatus;
	private TextView serverIP;
	private TextView beatNumber;
	private TextView fromClient;
	private TextView test;
	private Object SERVERIP;
	
	public ServerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		//setContentView(R.layout.main);
		setFocusable(true);
        //serverStatus = (TextView) findViewById(R.id.server_status);
        serverStatus = (TextView) findViewById(R.id.textView1);
        serverIP = (TextView) findViewById(R.id.serverip);
        beatNumber = (TextView) findViewById(R.id.beatNumber);
        fromClient = (TextView) findViewById(R.id.fromClient);
        test = (TextView) findViewById(R.id.textView2);
        
        SERVERIP = getLocalIpAddress();
        serverIP.setText("hello" + SERVERIP);

	}
	
	public ServerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public ServerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
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
	
	
	
	

}
