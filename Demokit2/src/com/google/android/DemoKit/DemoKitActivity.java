/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.DemoKit;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
//import android.nfc.NfcAdapter.CreateNdefMessageCallback;
//import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
//import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class DemoKitActivity extends Activity implements Runnable /*, CreateNdefMessageCallback, OnNdefPushCompleteCallback*/ {
	private static final String TAG = "DemoKit";

	private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";

	private UsbManager mUsbManager;
	private PendingIntent mPermissionIntent;
	private boolean mPermissionRequestPending;

	UsbAccessory mAccessory;
	ParcelFileDescriptor mFileDescriptor;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;

	private static final int MESSAGE_SWITCH = 1;
	private static final int MESSAGE_TEMPERATURE = 2;
	private static final int MESSAGE_LIGHT = 3;
	private static final int MESSAGE_JOY = 4;

	public static final byte LED_SERVO_COMMAND = 2;
	public static final byte RELAY_COMMAND = 3;
	
	/*
	 * 
	 */
	
	Behavior behavior;
	BeatTimer beatTimer;
	AudioTest aTest;
	AndroidAudioDevice aDevice;
	ClientCode client;
	BoeBotController bbc;
	InputController ic;
	
	public SensorManager mSensorManager;

	
	public SurfaceView sv;
	
	private static final int MESSAGE_SENT = 1;
    private static final int MESSAGE_RECEIVED=2;
    //NfcAdapter mNfcAdapter;
    
	
	protected class SwitchMsg {
		private byte sw;
		private byte state;

		public SwitchMsg(byte sw, byte state) {
			this.sw = sw;
			this.state = state;
		}

		public byte getSw() {
			return sw;
		}

		public byte getState() {
			return state;
		}
	}

	protected class TemperatureMsg {
		private int temperature;

		public TemperatureMsg(int temperature) {
			this.temperature = temperature;
		}

		public int getTemperature() {
			return temperature;
		}
	}

	protected class LightMsg {
		private int light;

		public LightMsg(int light) {
			this.light = light;
		}

		public int getLight() {
			return light;
		}
	}

	protected class JoyMsg {
		private int x;
		private int y;

		public JoyMsg(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
				}
			}
		}
	};

	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		if (getLastNonConfigurationInstance() != null) {
			mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			openAccessory(mAccessory);
		}

		setContentView(R.layout.main);

		enableControls(false);
		
		/*
		 * 
		 */
		
		behavior = new Behavior(this);
		//behavior.start();
		
		beatTimer= new BeatTimer();		
		beatTimer.mActivity = this;	
		beatTimer.setRunning(true);
		beatTimer.start();
		
		int fs=44100;
		
		aDevice= new AndroidAudioDevice(fs);
		aTest= new AudioTest();
		
		
		mSensorManager = (SensorManager) getSystemService(android.content.Context.SENSOR_SERVICE);
		
		
		//client = new ClientCode(this);
		//client.attachToView();
		//mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
		
		
		/*
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            //mInfoText = (TextView) findViewById(R.id.textView);
           // mInfoText.setText("NFC is not available on this device.");
        }
        // Register callback to set NDEF message
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        // Register callback to listen for message-sent success
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        */
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (mAccessory != null) {
			return mAccessory;
		} else {
			return super.onRetainNonConfigurationInstance();
		}
		
	}

	@Override
	public void onResume() {
		super.onResume();

		Intent intent = getIntent();
		if (mInputStream != null && mOutputStream != null) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
		
		/*
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
        */
	}

	@Override
	public void onPause() {
		super.onPause();
		closeAccessory();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
		
		aTest.onStop();
		boolean retry = true;
		//thread.setRunning(false);
		beatTimer.setRunning(false);
		while (retry) {
			try {
				//thread.join();
				beatTimer.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}
		
		
	}

	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "DemoKit");
			thread.start();
			Log.d(TAG, "accessory opened");
			enableControls(true);
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	private void closeAccessory() {
		enableControls(false);

		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
		}
	}

	protected void enableControls(boolean enable) {
	}

	private int composeInt(byte hi, byte lo) {
		int val = (int) hi & 0xff;
		val *= 256;
		val += (int) lo & 0xff;
		return val;
	}

	public void run() {
		int ret = 0;
		byte[] buffer = new byte[16384];
		int i;

		while (ret >= 0) {
			try {
				ret = mInputStream.read(buffer);
			} catch (IOException e) {
				break;
			}

			i = 0;
			while (i < ret) {
				int len = ret - i;

				switch (buffer[i]) {
				case 0x1:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_SWITCH);
						m.obj = new SwitchMsg(buffer[i + 1], buffer[i + 2]);
						mHandler.sendMessage(m);
					}
					i += 3;
					break;

				case 0x4:
					if (len >= 3) {
						Message m = Message.obtain(mHandler,
								MESSAGE_TEMPERATURE);
						m.obj = new TemperatureMsg(composeInt(buffer[i + 1],
								buffer[i + 2]));
						mHandler.sendMessage(m);
					}
					i += 3;
					break;

				case 0x5:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_LIGHT);
						m.obj = new LightMsg(composeInt(buffer[i + 1],
								buffer[i + 2]));
						mHandler.sendMessage(m);
					}
					i += 3;
					break;

				case 0x6:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_JOY);
						m.obj = new JoyMsg(buffer[i + 1], buffer[i + 2]);
						mHandler.sendMessage(m);
					}
					i += 3;
					break;

				default:
					Log.d(TAG, "unknown msg: " + buffer[i]);
					i = len;
					break;
				}
			}

		}
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SWITCH:
				SwitchMsg o = (SwitchMsg) msg.obj;
				handleSwitchMessage(o);
				break;

			case MESSAGE_TEMPERATURE:
				TemperatureMsg t = (TemperatureMsg) msg.obj;
				handleTemperatureMessage(t);
				break;

			case MESSAGE_LIGHT:
				LightMsg l = (LightMsg) msg.obj;
				handleLightMessage(l);
				break;

			case MESSAGE_JOY:
				JoyMsg j = (JoyMsg) msg.obj;
				handleJoyMessage(j);
				break;

			}
		}
	};

	public void sendCommand(byte command, byte target, int value) {
		byte[] buffer = new byte[3];
		if (value > 255)
			value = 255;

		buffer[0] = command;
		buffer[1] = target;
		buffer[2] = (byte) value;
		if (mOutputStream != null && buffer[1] != -1) {
			try {
				mOutputStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "write failed", e);
			}
		}
	}

	protected void handleJoyMessage(JoyMsg j) {
	}

	protected void handleLightMessage(LightMsg l) {
	}

	protected void handleTemperatureMessage(TemperatureMsg t) {
		/*
		if(t.temperature>125)
		{
			this.bbc.fillNow(bbc.sfxrseq);
		}
		*/
	}

	protected void handleSwitchMessage(SwitchMsg o) {
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
	}
	
	public float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
	
	class SVThread
	{
		
		SVThread()
		{
			
			
		}
	}


	/*
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		// TODO Auto-generated method stub
		mHandler1.obtainMessage(MESSAGE_SENT).sendToTarget();
		Log.d("onNdefPushComplete", "sent");
	}
	*/
	
	  /** This handler receives a message from onNdefPushComplete */
    /*private final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                break;
            }
        }
    };*/
    
    //should this be for ereceiveing?
    
    /*private final Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_RECEIVED:
                Toast.makeText(getApplicationContext(), "Message REceived!", Toast.LENGTH_LONG).show();
                
                break;
            }
        }
    };
    */
    
	/*
	@Override
	public NdefMessage createNdefMessage(NfcEvent arg0) {
		Time time = new Time();
        time.setToNow();
        String text = ("Beam me up!\n\n" +
                "Beam Time: " + time.format("%H:%M:%S") + "pattern:");
        
        
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        "application/aaron.tests.DrawingExampleActivity", text.getBytes())
        
          //,NdefRecord.createApplicationRecord("com.example.android.beam")
        });
        Log.d("createNDefMessage", "msg ready");
        return msg;
	}
	*/
	
	 /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    /*public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }*/
    
    
    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    /*void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        //mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
        String s=new String(msg.getRecords()[0].getPayload());
       // drawView.setIncoming(s);
        String[] ss=s.split(":");
        String seq= ss[ss.length-1];
        char[] seq2=seq.toCharArray();
        
        Log.d("processintent", Integer.toString(ss.length));
        
        
        mHandler2.obtainMessage(MESSAGE_RECEIVED).sendToTarget();
        Log.d("processintent", s);
    }*/
	
	
	@Override
	public void finish()
	{
		//this.t.stop();
		
		System.exit(0);
	}
	
	
}
