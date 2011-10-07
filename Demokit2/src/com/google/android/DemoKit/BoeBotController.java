package com.google.android.DemoKit;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;




public class BoeBotController implements OnClickListener, SensorEventListener
{

	private final int bbs1, bbs2;
	private final byte mCommandTarget1;
	private final byte mCommandTarget2;
	private final byte mCommandTarget3;
	private final byte mCommandTarget4;
	private TextView mLabel;
	private Slider mSlider;
	private DemoKitActivity mActivity;

	private Button forward, backward,rotLeft,rotRight, stop,randomiseAll, tempoUp,tempoDown, instrumentOn, instrumentOff, toggleSequencer,useSensorsButton;

	private EditText et,acctext, comptext;
	
	private RectView view1;
	private Paint paint;
	
	public boolean[] fseq,bseq,rseq,lseq, sfxrseq, instrumentseq;
	//SurfaceView sv;
	Thread svthread;

	int currentIndex;

	boolean sequencerMode;
	boolean useSensors;
	boolean targetMode;
	
	int instrument;
	
	float accx,accy,accz;
	public float[] accvals = new float[3];
	//public SensorManager mSensorManager;
	public Sensor mAccelerometer, mCompass,mMagfield;
	NumberFormat df ;
	
	
	public float[] data = new float[3];
	public float[] mGData = new float[3];
    public float[] mMData = new float[3];
    public float[] magvals = new float[3];
    
    public float[] mR = new float[16];
    public float[] mI = new float[16];
    public float[] mOrientation = new float[3];
    
    
    int targetx,targety, targetvelx, targetvely,myposx,myposy,myvelx, myvely;
    float myangle, targetangle;
    int numNeighbors;
    
	public BoeBotController(DemoKitActivity activity, int servo1, int servo2) {
		mActivity = activity;
		bbs1 = servo1;
		bbs2 = servo2;
		instrument = 3;
		mCommandTarget1 = (byte) (bbs1 - 1 + 0x10);
		mCommandTarget2 = (byte) (bbs2 - 1 + 0x10);
		mCommandTarget3 = (byte) (instrument - 1 + 0x10);
		mCommandTarget4 = (byte) (1 - 1 + 0x10);

		fseq = new boolean[16];
		bseq = new boolean[16];
		rseq = new boolean[16];
		lseq = new boolean[16];
		
		sfxrseq = new boolean[16];
		instrumentseq = new boolean[16];
		
		//svthread = new Thread();
		//svthread.start();

		mActivity.beatTimer.bbc=this;
		sequencerMode=true;
		
		
		mAccelerometer = mActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mActivity.mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
		
		//mCompass = mActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		mMagfield = mActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mActivity.mSensorManager.registerListener(this, mMagfield, SensorManager.SENSOR_DELAY_UI);
		
		
		df = DecimalFormat.getInstance();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);
		
		
		
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		
		
		
	}

	public void attachToView() {
		// TODO Auto-generated method stub
		//mLabel = (TextView) targetView.getChildAt(0);
		forward = (Button) mActivity.findViewById(R.id.forward);
		backward = (Button) mActivity.findViewById(R.id.backward);
		rotLeft = (Button) mActivity.findViewById(R.id.rotLeft);
		rotRight = (Button) mActivity.findViewById(R.id.rotRight);
		stop = (Button) mActivity.findViewById(R.id.stop);
		randomiseAll = (Button) mActivity.findViewById(R.id.randomiseAll);
		tempoUp = (Button) mActivity.findViewById(R.id.tempoUp);
		tempoDown = (Button) mActivity.findViewById(R.id.tempoDown);
		
		instrumentOn = (Button) mActivity.findViewById(R.id.instrumentOn);
		instrumentOff = (Button) mActivity.findViewById(R.id.instrumentOff);
		
		toggleSequencer = (Button) mActivity.findViewById(R.id.toggleSeq);
		useSensorsButton = (Button) mActivity.findViewById(R.id.useSensorsButton);	
		
		view1 = (RectView) mActivity.findViewById(R.id.view1);
		view1.bbc=this;
		
		view1.setVisibility(View.GONE);
		
		et = (EditText) mActivity.findViewById(R.id.editText1);
		acctext =  (EditText) mActivity.findViewById(R.id.acctext);
		comptext = (EditText) mActivity.findViewById(R.id.comptext);
		


		forward.setOnClickListener(this);
		backward.setOnClickListener(this);
		rotLeft.setOnClickListener(this);
		rotRight.setOnClickListener(this);
		stop.setOnClickListener(this);
		randomiseAll.setOnClickListener(this);
		tempoUp.setOnClickListener(this);
		tempoDown.setOnClickListener(this);
		
		instrumentOn.setOnClickListener(this);
		instrumentOff.setOnClickListener(this);
		
		toggleSequencer.setOnClickListener(this);
		useSensorsButton.setOnClickListener(this);
		
		//et.setOnTouchListener(this);

		//sv = (SurfaceView) mActivity.findViewById(R.id.surfaceView1);

		//view1.draw(canvas)
		
		mActivity.client = new ClientCode(mActivity,this);
	}



	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		//if(arg0.)
		boolean isChecked =false;
		/*
		if (isChecked) {
			forward.setBackgroundDrawable(mOnBackground);
		} else {
			mButton.setBackgroundDrawable(mOffBackground);
		}

		 */
		if (mActivity != null) {

			if(arg0.getId()==forward.getId())
			{
				forward();
				clearAll();
				//mActivity.aTest.replayRandom();
				sequencerMode=false;

			}

			if(arg0.getId()==backward.getId())
			{
				backward();
				clearAll();
				//mActivity.aTest.replayRandom();
				sequencerMode=false;
			}

			if(arg0.getId()==rotLeft.getId())
			{

				rotLeft();
				clearAll();
				//mActivity.aTest.replayRandom();
				sequencerMode=false;
			}

			if(arg0.getId()==rotRight.getId())
			{
				rotRight();
				clearAll();
				//mActivity.aTest.replayRandom();
				sequencerMode=false;
			}

			if(arg0.getId()==stop.getId())
			{
				stop();
				clearAll();
				//mActivity.aTest.replayRandom();
				sequencerMode=false;
			}
			
			if(arg0.getId()==randomiseAll.getId())
			{
				randomiseAll();
				//ensureSamePosition();
				//mActivity.aTest.replayRandom();
				sequencerMode=true;
				
			}
			
			if(arg0.getId()==tempoUp.getId())
			{
				if(mActivity.beatTimer.globalTimeInterval>25)
				{
					mActivity.beatTimer.globalTimeInterval-=25;
					et.setText("" + mActivity.beatTimer.globalTimeInterval);
					sequencerMode=true;
				}
				
				//playInstrument();
			}
			if(arg0.getId()==tempoDown.getId())
			{
				mActivity.beatTimer.globalTimeInterval+=25;
				et.setText("" + mActivity.beatTimer.globalTimeInterval);
				sequencerMode=true;
				
				//stopInstrument();
			}
			
			if(arg0.getId()==instrumentOn.getId())
			{
				playInstrument();
				mActivity.aTest.replayRandom();
				sequencerMode=false;
			}
			
			if(arg0.getId()==instrumentOff.getId())
			{
				stopInstrument();
				mActivity.aTest.replayRandom();
				sequencerMode=false;
			}
			
			if(arg0.getId() == toggleSequencer.getId())
			{
				sequencerMode=!sequencerMode;
			}
			
			if(arg0.getId() == useSensorsButton.getId())
			{
				useSensors=!useSensors;
			}






		}


	}

	public void forward()
	{

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 0);

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 255);
		
	}

	public void backward()
	{
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 255);

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 0);

	}

	public void rotLeft()
	{

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 0);

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 0);
	}

	public void rotRight()
	{
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 255);

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 255);
	}

	public void stop()
	{

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 128);

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 128);
	}
	
	public void playInstrument()
	{
		byte instr1=0;
		mActivity.sendCommand(DemoKitActivity.RELAY_COMMAND,
				instr1, (byte) 255);
		
		/*
		mActivity.sendCommand(DemoKitActivity.RELAY_COMMAND,
				mCommandTarget4, 1);
	*/
	}
	
	public void stopInstrument()
	{
		byte instr1=0;
		mActivity.sendCommand(DemoKitActivity.RELAY_COMMAND,
				instr1, (byte) 0);
		
		/*
		mActivity.sendCommand(DemoKitActivity.RELAY_COMMAND,
				mCommandTarget4, 0 );
				*/
	
	}

	public void fixConflict()
	{

		for(int i=0; i<fseq.length;i++)
		{
			if(fseq[i] && bseq[i]||rseq[i]||lseq[i])
			{
			
				if(Math.random()<.5)
				{
					fseq[i]=false;
				}
				else
				{
					fseq[i]=true;
				}
				bseq[i]=false;
				rseq[i]=false;
				lseq[i]=false;
			}
			
			if(bseq[i] && fseq[i]||rseq[i]||lseq[i])
			{
				if(Math.random()<.5)
				{
					bseq[i]=false;
				}
				else
				{
					bseq[i]=true;
				}
				
				fseq[i]=false;				
				rseq[i]=false;
				lseq[i]=false;
			}
			
			if(rseq[i] && fseq[i]||bseq[i]||lseq[i])
			{
				
				if(Math.random()<.5)
				{
					rseq[i]=false;
				}
				else
				{
					rseq[i]=true;
				}
				
				fseq[i]=false;
				bseq[i]=false;				
				lseq[i]=false;
			}
			
			if(lseq[i] && fseq[i]||bseq[i]||rseq[i])
			{
				if(Math.random()<.5)
				{
					lseq[i]=false;
				}
				else
				{
					lseq[i]=true;
				}
				
				fseq[i]=false;
				bseq[i]=false;
				rseq[i]=false;
			
			}
			
			
		}

	}


	public void randomiseSequence(boolean[] b)
	{
		for(int i=0; i<b.length;i++)
		{
			if(Math.random()<.5)
			{
				b[i]=true;
			}
			else
			{
				b[i]=false;
			}
		}

	}
	public void randomiseAll()
	{
		//randomiseSequence(fseq);
		//randomiseSequence(bseq);
		//randomiseSequence(lseq);
		//randomiseSequence(rseq);
		//randomiseSequence(instrumentseq);
		//fixConflict();
		clearRhythm(fseq);
		clearRhythm(bseq); 
		clearRhythm(rseq); 
		clearRhythm(lseq); 
		clearRhythm(instrumentseq); 
		clearRhythm(sfxrseq);
		
		mActivity.aTest.soundType(6);
		
		for(int i=0; i<fseq.length;i++)
		{
			if(Math.random()>.5)
			{
				int v=(int)( 6.0*Math.random());
				switch(v)
				{
				case 0:
					fseq[i]=true;
					break;
				case 1:
					bseq[i]=true;
					break;
				case 2: 
					rseq[i]=true;
					break;
				case 3:
					lseq[i]=true;
					break;
				case 4:
					sfxrseq[i]=true;
					break;
				case 5:
					instrumentseq[i]=true;
					break;
				default: ;

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
	
	void clearAll()
	{
		
		clearRhythm(fseq);
		clearRhythm(rseq);
		clearRhythm(lseq);
		clearRhythm(bseq);
		
		clearRhythm(sfxrseq);
		clearRhythm(instrumentseq);
		
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

	boolean isCompletelySilent()
	{
		boolean f=false;
		for (int i=0; i < fseq.length;i++)
		{
			f = f || fseq[i] || bseq[i] || lseq[i] || rseq[i];
		}  

		return !f;
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


	void ensureSamePosition()
	{
		int counts[] = {0,0,0,0 };

		for (int i=0; i < fseq.length;i++)
		{
			if(fseq[i])
			{
				counts[0]=counts[0]+1; 
			}
			if(bseq[i])
			{
				counts[1]=counts[1]+1; 
			} 
			if(lseq[i])
			{
				counts[2]=counts[2]+1; 
			} 
			if(rseq[i])
			{
				counts[3]=counts[3]+1;
			}   

		}

		if(counts[0]==counts[1] && counts[2]==counts[3])
		{
			return; 

		}


		if(counts[0]>fseq.length/2)
		{
			int numToDelete=counts[0]-fseq.length/2;

			for (int i=0; i < fseq.length;i++)
			{
				if(numToDelete==0)
				{
					break; 
				}
				if(fseq[i])
				{
					fseq[i]=false; 
					numToDelete--;
				}
			}

		}

		if(counts[1]>fseq.length/2)
		{
			int numToDelete=counts[1]-fseq.length/2;

			for (int i=0; i < fseq.length;i++)
			{
				if(numToDelete==0)
				{
					break; 
				}
				if(bseq[i])
				{
					bseq[i]=false; 
					numToDelete--;
				}
			}
		}

		if(counts[2]>fseq.length/2)
		{
			int numToDelete=counts[2]-fseq.length/2;

			for (int i=0; i < fseq.length;i++)
			{
				if(numToDelete==0)
				{
					break; 
				}
				if(lseq[i])
				{
					lseq[i]=false; 
					numToDelete--;
				}
			}
		}

		if(counts[3]>fseq.length/2)
		{
			int numToDelete=counts[3]-fseq.length/2;

			for (int i=0; i < fseq.length;i++)
			{
				if(numToDelete==0)
				{
					break; 
				}
				if(rseq[i])
				{
					rseq[i]=false; 
					numToDelete--;
				}
			}
		}



		////
		if(counts[0]<counts[1])
		{

			int remaining = counts[1]-counts[0];
			for (int i=0; i < fseq.length;i++)
			{

				if(remaining<=0)
				{
					break; 
				}

				if(!fseq[i]&&!bseq[i])
				{
					remaining--; 
					fseq[i]=true;
				}

			}



		}
		else
		{
			int remaining = counts[0]-counts[1];
			for (int i=0; i < fseq.length;i++)
			{
				if(remaining<=0)
				{
					break; 
				}

				if(!fseq[i]&&!bseq[i])
				{
					remaining--; 
					bseq[i]=true;
				}

			}
		}

		if(counts[2]<counts[3])
		{
			int remaining = counts[3]-counts[2];
			for (int i=0; i < fseq.length;i++)
			{
				if(remaining<=0)
				{
					break; 
				}
				if(!lseq[i]&&!rseq[i])
				{
					remaining--; 
					lseq[i]=true;
				}

			}
		}
		else
		{
			int remaining = counts[2]-counts[3];
			for (int i=0; i < fseq.length;i++)
			{
				if(remaining<=0)
				{
					break; 
				}
				if(!lseq[i]&&!rseq[i])
				{
					remaining--; 
					rseq[i]=true;
				}

			}
		}






	}




	void randomMirror()
	{
		for(int i=0; i<fseq.length/2;i++)
		{
			int d = (int)(2*Math.random());
			if(d==1)
			{
				int r = (int)(4*Math.random());

				switch (r)
				{
				case 0 :
					fseq[i]=true;
					fseq[fseq.length-1-i]=true;
					break;
				case 1 :
					bseq[i]=true;
					bseq[fseq.length-1-i]=true;
					break;
				case 2 :
					lseq[i]=true;
					lseq[fseq.length-1-i]=true;
					break;
				case 3 :
					rseq[i]=true;
					rseq[fseq.length-1-i]=true;
					break;

				default:
					; 
				}
			}
			else
			{
			}
		}
	}

	void randomMirrorSP()
	{
		for(int i=0; i<fseq.length/2;i++)
		{
			int d = (int)(2*Math.random());
			if(d==1)
			{
				int r = (int)(4*Math.random());

				switch (r)
				{
				case 0 :
					fseq[i]=true;
					bseq[fseq.length-1-i]=true;
					break;
				case 1 :
					bseq[i]=true;
					fseq[fseq.length-1-i]=true;
					break;
				case 2 :
					lseq[i]=true;
					rseq[fseq.length-1-i]=true;
					break;
				case 3 :
					rseq[i]=true;
					lseq[fseq.length-1-i]=true;
					break;

				default:
					; 
				}
			}
			else
			{
			}
		}
	}
	
	
	///////////////musical mappings
	
	//map based on number of neigbhors
	
	void numberOfNeigbhors()
	{
		numNeighbors=0;
		//get coords of other
		int otherx=targetx;
		int othery= targety;
		
		//determine if close by in a circle		
		int rad =150;
		sequencerMode=true;
		if( Math.sqrt( Math.pow((myposx-otherx),2) + Math.pow((myposy-othery),2) ) < rad )
		{
			
			numNeighbors++;
			fillRhythm(2, instrumentseq); 
			fillRhythm(2, sfxrseq); 
			
		}
		else
		{
			fillRhythm(5,instrumentseq);
			fillRhythm(5, sfxrseq); 
		}
		
		
		
	}
	
	
	////////////////////
	
	public float map(float value, float istart, float istop, float ostart, float ostop) {
		   return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	     }


	///////////////////////////////////////////////////////
	//targeting functions
	
	void setTarget(int x, int y)
	{
		targetx=x;
		targety=y;
	}
	
	void seekTarget()
	{
		
		int dx = targetx-myposx;
		int dy = targety-myposy;
		
		double angle = Math.atan2(dy, dx);
		double mag = Math.sqrt(Math.pow(dx, 2)+ Math.pow(dy, 2));
		
		
		//determine orientation
		// or assume orientation was facing down.
		
		//rotate to position and move
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////
	class SVThread extends Thread
	{
		SurfaceView v;
		Canvas c;
		public SVThread(SurfaceView sv)
		{
			sv=v;
			//c= sv.
		}


		@Override
		public void run()
		{

			v.draw(c);
		}


	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		/*
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
		*/
		
		/*
		int type = event.sensor.getType();
		if (type == Sensor.TYPE_ACCELEROMETER)
		{
			
			accx=event.values[0];
			accy=event.values[1];
			accz=event.values[2];
			
			accvals = event.values.clone();
			data = mGData;
			

			acctext.setText("" + df.format(accx) + ","  + df.format(accy) + "," + df.format(accz));
		} else
			if (type == Sensor.TYPE_MAGNETIC_FIELD) {
	            data = mMData;
	            magvals = event.values.clone();
			}
		//for (int i=0 ; i<3 ; i++)
          //  data[i] = event.values[i];
		SensorManager.getRotationMatrix(mR, mI, accvals, magvals);
		SensorManager.getOrientation(mR, mOrientation);
		
        float incl = SensorManager.getInclination(mI);
        
        //comptext.setText("" + df.format(magvals[0]) + ","  + df.format(magvals[1]) + "," + df.format(magvals[2]));
        
        comptext.setText("" + df.format(mOrientation[0]) + ","  + df.format(mOrientation[1]) + "," + df.format(mOrientation[2]));
        
		*/
	}
}
