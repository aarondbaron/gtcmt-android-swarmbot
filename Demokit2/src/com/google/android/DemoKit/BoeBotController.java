package com.google.android.DemoKit;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Vector;


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
	public TextView mLabel, azimuthlabel,move2locLabel;
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

	int rbyte, lbyte;
	public int modDistance;


	//private SensorManager mSensorManager;
	private Sensor mOrientationSensor;
	//data for orientation values filtering (average using a ring buffer)
	static private final int RING_BUFFER_SIZE=10;
	private float[][][] mAnglesRingBuffer;
	private int mNumAngles;
	private int mRingBufferIndex;
	private float[][] mAngles;
	
	public float  angleAzimuth;
	
	Vector<Bot> otherBots;
	
	Behavior myBehavior;
	//Behavior moveBehavior;
	
	float calibrationAngle;
	
	boolean positionLost;
	
	RobotFaceView rfv;
	
	int ID;
	
	public FdView opcvFD;
	
	float myFreq=440;
	
	boolean danceSequencer;

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

		/*
		mAccelerometer = mActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mActivity.mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);		
		//mCompass = mActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);		
		mMagfield = mActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mActivity.mSensorManager.registerListener(this, mMagfield, SensorManager.SENSOR_DELAY_UI);
		 */

		//mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//		mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
		//	     mOrientationSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//	     
		//	     mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);

		//mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//		mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);
		mOrientationSensor=mActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//	     
		mActivity.mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);

		// initialize the ring buffer for orientation values
		mNumAngles=0;
		mRingBufferIndex=0;
		mAnglesRingBuffer=new float[RING_BUFFER_SIZE][3][2];
		mAngles=new float[3][2];
		mAngles[0][0]=0;
		mAngles[0][1]=0;
		mAngles[1][0]=0;
		mAngles[1][1]=0;
		mAngles[2][0]=0;
		mAngles[2][1]=0;



		df = DecimalFormat.getInstance();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);



		Paint paint = new Paint();
		paint.setColor(Color.RED);

		otherBots = new Vector();
		
		//moveBehavior = new Behavior(this);

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
		azimuthlabel = (TextView) mActivity.findViewById(R.id.az);
		move2locLabel=(TextView)mActivity.findViewById(R.id.Mov2Loc);


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
		
		myBehavior= new Behavior(this);
		
		/*
		DemoKitPhone d= (DemoKitPhone)mActivity;
		this.rfv = d.mfc.rfv;
		this.rfv.bbc=this;
		*/
		this.rfv=(RobotFaceView)mActivity.findViewById(R.id.robotFaceView);
		this.rfv.bbc=this;
		this.rfv.bt=mActivity.beatTimer;
		
		opcvFD = (FdView) mActivity.findViewById(R.id.fdview);
		
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
				//sequencerMode=false;

			}

			if(arg0.getId()==backward.getId())
			{
				backward();
				clearAll();
				//mActivity.aTest.replayRandom();
				//sequencerMode=false;
			}

			if(arg0.getId()==rotLeft.getId())
			{

				rotLeft();
				clearAll();
				//mActivity.aTest.replayRandom();
				//sequencerMode=false;
			}

			if(arg0.getId()==rotRight.getId())
			{
				rotRight();
				clearAll();
				//mActivity.aTest.replayRandom();
				//sequencerMode=false;
			}

			if(arg0.getId()==stop.getId())
			{
				stopAll();
			}

			if(arg0.getId()==randomiseAll.getId())
			{
				//block this only temporary
				/*
				randomiseAll();
				//ensureSamePosition();
				//mActivity.aTest.replayRandom();
				sequencerMode=true;
				*/
				
				
				
				
				myBehavior  = new Behavior(this);
				myBehavior.m1=false;
				myBehavior.m2=true;
				//backward();
				forward();				
				this.myposx=200;
				this.myposy=200;				
				mActivity.beatTimer.wander=true;
				
				
				
				//orient test
				/*
				this.myposx=640/2;
				this.myposy=480/2;
				myBehavior = new Behavior(this);
				this.targetx=0;
				this.targety=0;
				this.orientToLoc(true);
				*/
				
				//wander test
				/*
				this.myposx=640/2;
				this.myposy=480/2;
				myBehavior.initWander();
				this.setWander(true);
				*/
				
				
				
				
				this.setMapping(1);
				
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
	
	public void tempoUp()
	{
		if(mActivity.beatTimer.globalTimeInterval>25)
		{
			mActivity.beatTimer.globalTimeInterval-=25;
			et.setText("" + mActivity.beatTimer.globalTimeInterval);
			sequencerMode=true;
		}
	}
	public void tempoDown()
	{
		mActivity.beatTimer.globalTimeInterval+=25;
		et.setText("" + mActivity.beatTimer.globalTimeInterval);
		sequencerMode=true;
	}
	
	public long getTempo()
	{
		return mActivity.beatTimer.globalTimeInterval;
	}

	public void forward()
	{

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 0);
		rbyte=(byte)0;

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 255);
		lbyte=(byte)255;

	}

	public void backward()
	{
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 255);
		rbyte = (byte)255;
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 0);
		lbyte = (byte) 0;
	}

	public void rotLeft()
	{

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 0);
		rbyte = (byte) 0;
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 0);
		lbyte=(byte) 0;
	}

	public void rotRight()
	{
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 255);
		rbyte=(byte)255;

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 255);
		lbyte =(byte)255;
	}

	public void stop()
	{

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 128);

		rbyte=(byte)128;
		lbyte=(byte)128;

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 128);
	}
	
	public void moveToLoc(boolean b)
	{
		
		mActivity.beatTimer.move2Loc=b;
	}
	
	public void orientToLoc(boolean b)
	{
		//set value
		mActivity.beatTimer.orient2Loc=b;
	}
	public void writeL(int b)
	{
		lbyte =b;
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte)b);

	}
	public void writeR(int b)
	{
		rbyte=b;
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte)b);
	}

	public int getRByte()
	{
		return rbyte;
	}
	public int getLByte()
	{
		return lbyte;
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
	public void stopAll()
	{
		stop();
		clearAll();
		//mActivity.aTest.replayRandom();
		sequencerMode=false;
		mActivity.beatTimer.wander=false;
		mActivity.beatTimer.move2Loc=false;
	}
	
	public void setWander(boolean b)
	{
		if(b)
		{
			mActivity.beatTimer.wander=b;
		}
		else
		{
			mActivity.beatTimer.wander=b;
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
	
	void randomDance()
	{
		
		int debt0=0;
		int debt1=0;
		int debt2=0;
		int debt3=0;
		int totalDebt=0;
		
		for(int i=0; i<fseq.length;i++)
		{
			int choice=(int) (4*Math.random());
			switch(choice)
			{
			 case 0: 
				 if(totalDebt<fseq.length-i)
					 
				 debt0++; break;
			 case 1: 
				 debt1++;break;
			 case 2: 
				 debt2++;break;
			 case 3: 
				 debt3++;break;
			 
			 default: ; break;
			}
			totalDebt=debt0+debt1+debt2+debt3;
			
			
		}
		
		
		
	}


	boolean[] euclidArray(int m, int k)
	{
		
		if(k<0||m<0)
		{
			return new boolean[0];
		}
	  if(k<m||m==0)
	    return new boolean[k];


	  Vector d[] = new Vector[m];
	  for(int i=0; i <m; i++)
	  {
	    d[i] = new Vector();
	    d[i].add("1");
	  }

	  int dif=k-m;
	  //Number of zeros

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
	
	void fillEuclid(int a, boolean b[])
	{
		//clearRhythm(b);
		
		boolean[] ea=euclidArray(a,b.length);
		for(int i=0; i<b.length;i++)
		{
			
			b[i]=ea[i];
			
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
	
	void clearAllMovement()
	{
		clearRhythm(fseq);
		clearRhythm(rseq);
		clearRhythm(lseq);
		clearRhythm(bseq);
	}

	
	
	void fillPosition(int n, boolean b[])
	{
		if(n>=b.length || n<0)
			return;
		
		clearRhythm(b);
		b[n]=true;
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
	
	void euclidDance()
	{
		boolean[] b = new boolean[fseq.length];
		
		int n= (int)(7*Math.random()) +1;
		fillEuclid(7,b);
		clearAllMovement();
		
		int cnt=0;
		for(int i =0; i< b.length; i++)
		{
			if(b[i])
			{
				switch(cnt)
				{
				case 0: fseq[i]=true; break;
				case 1: bseq[i]=true; break;
				case 2: rseq[i]=true; break;
				case 3: lseq[i]=true; break;
				default: ; break;
				}
				
				cnt++;
				if(cnt%4==0)
				{
					cnt=0;
				}
			}
		}
		
		
		
		
	 
	}

	void randomMirrorSP()
	{
		for(int i=0; i<fseq.length/2;i++)
		{
			int d = (int)(5*Math.random());
			//this is just to allow for silences on occasion.  25% prob
			if(d>=1)
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
	
	void toggleDance()
	{
		
		danceSequencer=!danceSequencer;
		rfv.thread.message.displayMessage("danceMode: " + danceSequencer);
	}
	
	void setDanceSequencer(boolean b)
	{
		danceSequencer=b;
		rfv.thread.message.displayMessage("danceMode: " + danceSequencer);
	}

	//
	void setFreq(float f)
	{
		

	    
		myFreq=f;
		this.mActivity.aTest.sdata.freq=myFreq;
	}
	
	void setRandomPentFreq()
	{
		int ind = (int) (map((float)Math.random(),0,1,30,mActivity.aTest.pSet.length-30));
		float fp3 = (float) Math.pow(2,(float)(this.mActivity.aTest.pSet[ind]-this.mActivity.aTest.base)/12.0f) *261.6255650f;

	}
	
	//
	///////////////musical mappings

	//map based on number of neigbhors

	void numberOfNeigbhors()
	{
		numNeighbors=0;
		int rad =150;
		/*
		//get coords of other
		int otherx=targetx;
		int othery= targety;

		//determine if close by in a circle		
		
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
		*/
		
		for(int i =0 ; i < otherBots.size();i++)
		{
			Bot b = otherBots.get(i);
			
			if( Math.sqrt( Math.pow((myposx-b.x),2) + Math.pow((myposy-b.y),2) ) < rad )	
			{
				numNeighbors++;
			
			}
			
		}


	}

	void setMapping(int a)
	{
		
		mActivity.beatTimer.mapping=a;
		
		
	}
	
	int getMapping()
	{
		return mActivity.beatTimer.mapping;
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


		
		if(event.sensor==mOrientationSensor) {
    		if(mNumAngles==RING_BUFFER_SIZE) {
    			// subtract oldest vector
	    		mAngles[0][0]-=mAnglesRingBuffer[mRingBufferIndex][0][0];
	    		mAngles[0][1]-=mAnglesRingBuffer[mRingBufferIndex][0][1];
	    		mAngles[1][0]-=mAnglesRingBuffer[mRingBufferIndex][1][0];
	    		mAngles[1][1]-=mAnglesRingBuffer[mRingBufferIndex][1][1];
	    		mAngles[2][0]-=mAnglesRingBuffer[mRingBufferIndex][2][0];
	    		mAngles[2][1]-=mAnglesRingBuffer[mRingBufferIndex][2][1];
    		} else {
    			mNumAngles++;
    		}

    		// convert angles into x/y
    		mAnglesRingBuffer[mRingBufferIndex][0][0]=(float) Math.cos(Math.toRadians(event.values[0]));
    		mAnglesRingBuffer[mRingBufferIndex][0][1]=(float) Math.sin(Math.toRadians(event.values[0]));
    		mAnglesRingBuffer[mRingBufferIndex][1][0]=(float) Math.cos(Math.toRadians(event.values[1]));
    		mAnglesRingBuffer[mRingBufferIndex][1][1]=(float) Math.sin(Math.toRadians(event.values[1]));
    		mAnglesRingBuffer[mRingBufferIndex][2][0]=(float) Math.cos(Math.toRadians(event.values[2]));
    		mAnglesRingBuffer[mRingBufferIndex][2][1]=(float) Math.sin(Math.toRadians(event.values[2]));

    		// accumulate new x/y vector
    		mAngles[0][0]+=mAnglesRingBuffer[mRingBufferIndex][0][0];
    		mAngles[0][1]+=mAnglesRingBuffer[mRingBufferIndex][0][1];
    		mAngles[1][0]+=mAnglesRingBuffer[mRingBufferIndex][1][0];
    		mAngles[1][1]+=mAnglesRingBuffer[mRingBufferIndex][1][1];
    		mAngles[2][0]+=mAnglesRingBuffer[mRingBufferIndex][2][0];
    		mAngles[2][1]+=mAnglesRingBuffer[mRingBufferIndex][2][1];

    		mRingBufferIndex++;
    		if(mRingBufferIndex==RING_BUFFER_SIZE) {
    			mRingBufferIndex=0;
    		}

			// convert back x/y into angles
			float azimuth=(float) Math.toDegrees(Math.atan2((double)mAngles[0][1], (double)mAngles[0][0]));
			float pitch=(float) Math.toDegrees(Math.atan2((double)mAngles[1][1], (double)mAngles[1][0]));
			float roll=(float) Math.toDegrees(Math.atan2((double)mAngles[2][1], (double)mAngles[2][0]));
			//mCompassRenderer.setOrientation(azimuth, pitch, roll);
			if(azimuth<0) azimuth=(360+azimuth)%360;
			//mHeadingView.setText(getString(R.string.heading)+": "+(int)azimuth+"°");
			azimuthlabel.setText("azimuth:" + azimuth);
			angleAzimuth=azimuth;
    	}

		 
	}


}
