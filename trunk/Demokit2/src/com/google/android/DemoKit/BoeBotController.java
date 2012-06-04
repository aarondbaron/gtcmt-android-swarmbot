package com.google.android.DemoKit;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.Vector;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
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
	public final byte mCommandTarget1;
	public final byte mCommandTarget2;
	public final byte mCommandTarget3;
	public final byte mCommandTarget4;
	public TextView mLabel, azimuthlabel,move2locLabel, byteLabel;
	private Slider mSlider;
	public DemoKitActivity mActivity;

	private Button forward, backward,rotLeft,rotRight, stop,randomiseAll, tempoUp,tempoDown, instrumentOn, instrumentOff, toggleSequencer,useSensorsButton, wanderButton;

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

	public int[] vxs,vys;
	public int vxyindex;
	public float[] aest;
	public int[] pastx,pasty;
	public PVector vest;
	public float avest;


	int targetx,targety, targetvelx, targetvely,myposx,myposy,myvelx, myvely;
	float myangle, targetangle;
	int numNeighbors;
	PVector target;
	PVector vel;

	int rbyte, lbyte, headbyte;
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
	public float  angleAzimuthDiff;

	Vector<Bot> otherBots;
	Bot currentAvatar;

	Behavior myBehavior;
	//Behavior moveBehavior;

	float calibrationAngle;

	boolean positionLost;

	RobotFaceView rfv;

	int ID;

	public FdView opcvFD;

	float myFreq=440;

	boolean danceSequencer;

	boolean[] avatarseq;

	boolean avatarMode;

	int neighborBound=75;

	int SEQUENCERLENGTH=48;

	boolean[] djembe0,djembe1,djembe2,djembe3;
	int[][] kuku;
	int kid;
	int eid;

	public int[] reg;

	int dj=0;

	OutputController oc;

	float camang;

	Estimator e;

	Vector distances;

	boolean useSFXR=false;

	boolean directControl=false;


	Vector beacons;

	long nComTimer;
	boolean nComEnable;
 
	
	LinkedHashSet queue;
	boolean[] receivedSequence ;
	
	int servo3;
	
	Song mySong;
	
	 
	
	public boolean isnComEnable() {
		return nComEnable;
	}

	public void setnComEnable(boolean nComEnable) {
		this.nComEnable = nComEnable;
	}

	public BoeBotController(DemoKitActivity activity, int servo1, int servo2) {
		mActivity = activity;
		mActivity.bbc=this;
		bbs1 = servo1;
		bbs2 = servo2;
		instrument = 3;
		
		servo3=3;
		
		mCommandTarget1 = (byte) (bbs1 - 1 + 0x10);
		mCommandTarget2 = (byte) (bbs2 - 1 + 0x10);
		mCommandTarget3 = (byte) (instrument - 1 + 0x10);
		mCommandTarget4 = (byte) (1 - 1 + 0x10);

		fseq = new boolean[SEQUENCERLENGTH];
		bseq = new boolean[SEQUENCERLENGTH];
		rseq = new boolean[SEQUENCERLENGTH];
		lseq = new boolean[SEQUENCERLENGTH];

		sfxrseq = new boolean[SEQUENCERLENGTH];
		instrumentseq = new boolean[SEQUENCERLENGTH];

		avatarseq = new boolean[SEQUENCERLENGTH];
		
		receivedSequence = new boolean[SEQUENCERLENGTH];


		djembe0 = new boolean[SEQUENCERLENGTH];
		djembe1 = new boolean[SEQUENCERLENGTH];
		djembe2 = new boolean[SEQUENCERLENGTH];
		djembe3 = new boolean[SEQUENCERLENGTH];

		initializeDjembe();
		initializeKuku();
		initializeReggaeton();


		//////////////////////////
		//setRhythm(djembe0);
		//this.setMapping(12);


		//svthread = new Thread();
		//svthread.start();

		myBehavior = mActivity.behavior;

		mActivity.beatTimer.bbc=this;
		sequencerMode=true;

		vxs = new int[10];
		vys = new int[10];
		aest=new float[10];
		pastx=new int[10];
		pasty = new int[10];

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
		beacons = new Vector();

		//moveBehavior = new Behavior(this);
		target = new PVector();
		vel = new PVector();

		e = new Estimator(this);

		distances=new Vector();

		nComTimer =System.currentTimeMillis();
		setnComEnable(false);
		queue = new LinkedHashSet();
		
		//mySong= new FightSong();
		//mySong = new OldMacDonald();
		//mySong = new Joyful();
		//mySong = new LionSleeps(); 
		chooseSong(0);
		
		//this.setMapping(204);
		
		
	}

	private void initializeReggaeton() {
		// TODO Auto-generated method stub
		reg = new int[] {
				0, 9, 12, 18, 24, 33, 36, 42
		};
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

		wanderButton = (Button) mActivity.findViewById(R.id.wanderbutton);



		/*
		view1 = (RectView) mActivity.findViewById(R.id.view1);
		view1.bbc=this;
		view1.setVisibility(View.GONE);
		 */

		et = (EditText) mActivity.findViewById(R.id.editText1);
		//acctext =  (EditText) mActivity.findViewById(R.id.acctext);
		//comptext = (EditText) mActivity.findViewById(R.id.comptext);
		//azimuthlabel = (TextView) mActivity.findViewById(R.id.az);
		move2locLabel=(TextView)mActivity.findViewById(R.id.Mov2Loc);
		byteLabel = (TextView) mActivity.findViewById(R.id.bytevaltextview);





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

		wanderButton.setOnClickListener(this);
		//et.setOnTouchListener(this);

		//sv = (SurfaceView) mActivity.findViewById(R.id.surfaceView1);

		//view1.draw(canvas)

		mActivity.client = new ClientCode(mActivity,this);

		//myBehavior= new Behavior(this);
		myBehavior=mActivity.behavior;
		myBehavior.bbc=this;
		myBehavior.start();

		/*
		DemoKitPhone d= (DemoKitPhone)mActivity;
		this.rfv = d.mfc.rfv;
		this.rfv.bbc=this;
		 */
		this.rfv=(RobotFaceView)mActivity.findViewById(R.id.robotFaceView);
		this.rfv.bbc=this;
		this.rfv.bt=mActivity.beatTimer;


		opcvFD = (FdView) mActivity.findViewById(R.id.fdview);

		vest = new PVector();

		mActivity.aTest.bbc=this;
		
		MSDeg = new int[8];
		MSDeg[0]=0;
		MSDeg[1]=2;
		MSDeg[2]=4;
		MSDeg[3]=5;
		MSDeg[4]=7;
		MSDeg[5]=9;
		MSDeg[6]=11;
		MSDeg[7]=12;

	}

	////???
	public final Runnable mUpdateUITimerTask = new Runnable() {
		public void run() {
			// do whatever you want to change here, like:
			byteLabel.setText("lbyte" + lbyte);
		}
	};
	public final Handler mHandler = new Handler();
	public boolean embellishOnce;
	public int embellishCounter;
	public boolean randomlyChangeOnce;
	public boolean shiftOnce;
	public boolean changeOnce;
	public boolean swapOnce;
	public boolean findOnce;
	public boolean useSong=true;
	public int myNote=72;
	public int[] MSDeg;
	public boolean avatarMoving;
	public boolean usingController;
	public boolean headMove;
	

	/////??



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




				//myBehavior  = new Behavior(this);
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

			if(arg0.getId()==wanderButton.getId())
			{
				if(this.myBehavior!=null)
				{
					this.myposx=100;
					this.myposy=100;

					myBehavior.initWanderComplete=false;
					myBehavior.initWander();
					myBehavior.initWanderComplete=true;
					setWander(true);	


				}
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

	public long getDiv()
	{

		return mActivity.beatTimer.div;
	}

	public void divUp()
	{
		mActivity.beatTimer.div++;

	}
	public void divDown()
	{
		if(mActivity.beatTimer.div>2)
		{
			mActivity.beatTimer.div--;
		}

	}




	public void forwardReal()
	{
		///////////////
		rbyte= 128-20;
		writeR(rbyte);
		lbyte= 128+20;
		writeL(lbyte);
		/////////////
	}
	public void turnLeft()
	{
		////assuming forward real was called

		if(lbyte>128)
		{
			lbyte--;
			writeL(lbyte);
		}


	}

	public void turnRight()
	{
		////assuming forward real was called
		if(rbyte<128)
		{
			rbyte++;
			writeL(rbyte);
		}

	}
	public void slowDown()
	{
		if(rbyte<128)
		{
			rbyte++;
			writeR(rbyte);
		}		
		if(lbyte>128)
		{
			lbyte--;
			writeL(lbyte);
		}

	}
	public void forward()
	{

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 0);
		rbyte= 0;

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 255);
		lbyte= 255;

	}


	public void backward()
	{
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 255);
		rbyte =  255;
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 0);
		lbyte =   0;
	}

	public void rotLeft()
	{

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 0);
		rbyte =   0;
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 0);
		lbyte=  0;
	}

	public void rotRight()
	{
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 255);
		rbyte= 255;

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 255);
		lbyte = 255;
	}

	public void stop()
	{

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte) 128);

		rbyte= 128;
		lbyte= 128;

		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte) 128);
	}

	public void moveToLoc(boolean b)
	{

		mActivity.beatTimer.move2Loc=b;
		myBehavior.move2Loc=b;
	}

	public void orientToLoc(boolean b)
	{
		//set value
		mActivity.beatTimer.orient2Loc=b;
		myBehavior.orient2Loc=b;
	}

	PVector steer(PVector target, boolean slowdown) {
		float maxspeed=2;
		float maxforce =.05f;
		PVector steer;  // The steering vector
		PVector desired = PVector.sub(target, new PVector (this.myposx,this.myposy) );  // A vector pointing from the location to the target
		float d = desired.mag(); // Distance from the target is the magnitude of the vector
		// If the distance is greater than 0, calc steering (otherwise return zero vector)
		if (d > 0) {  
			// Normalize desired
			desired.normalize();
			// Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
			if ((slowdown) && (d < 100.0f)) desired.mult(maxspeed*(d/100.0f)); // This damping is somewhat arbitrary
			else desired.mult(maxspeed);
			// Steering = Desired minus Velocity
			PVector vel = new PVector(getSpeed()*(float)Math.cos(getAngle()) , getSpeed()*(float)Math.sin(getAngle()));
			steer = PVector.sub(desired, vel);
			steer.limit(maxforce);  // Limit to maximum steering force
		} 
		else {
			steer = new PVector(0, 0);
		}
		return steer;
	}


	public void writeL(int b)
	{
		lbyte =b;
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget2, (byte)b);

	}
	public void writeR(int b)
	{
		rbyte=b;
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND,
				mCommandTarget1, (byte)b);
	}
	
	public void writeHead(int b)
	{
		headbyte=b;
		byte commandTarget= (byte) (this.servo3 - 1 + 0x10);
		mActivity.sendCommand(DemoKitActivity.LED_SERVO_COMMAND, commandTarget, (byte) b);
	}

	public float getSpeed()
	{
		float f = 10; 
		return f;
	}
	public float getAngle()
	{		
		return this.angleAzimuth;
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
		myBehavior.wander=false;
		myBehavior.wanderDance=false;
		myBehavior.wanderVector=false;
		myBehavior.move2Loc=false;

		this.setWanderDance(false);
		this.setWanderVector(false);

		mActivity.beatTimer.move2Loc=false;

	}

	public void setWander(boolean b)
	{

		mActivity.beatTimer.wander=b;
		myBehavior.wander=b;


	}

	public void setWanderDance(boolean b)
	{

		mActivity.beatTimer.wanderDance=b;
		mActivity.beatTimer.wanderDanceOnce=false;

		myBehavior.wanderDance=b;
		myBehavior.wanderDanceOnce=false;

	}

	public void setWanderVector(boolean b)
	{

		mActivity.beatTimer.wanderVector=b;

		myBehavior.wanderVector=b;


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
			return new boolean[k];
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

	void setRhythm(boolean[] b)
	{
		for(int i=0; i<b.length;i++)
		{
			sfxrseq[i]=b[i];
			instrumentseq[i]=b[i];

		}
	}

	void copyRhythm(boolean[] src, boolean[] dst)
	{
		for(int i=0;i<dst.length;i++)
		{
			if(i<src.length-1)
			{
				dst[i]=src[i];
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

	void shiftRhythmRight(boolean[] b)
	{
		//roll[number-1].pressed=roll[0].pressed;
		boolean temp = b[b.length-1];
		for (int i=b.length-1; i>0;i--)
		{
			b[i]=b[i-1];
		}

		b[0] = temp;
	}

	void shiftRhythmLeft(boolean[] b)
	{
		//roll[number-1].pressed=roll[0].pressed;
		boolean temp = b[0];
		for (int i=0; i<b.length-1;i++)
		{
			b[i]=b[i+1];
		}

		b[b.length - 1] = temp;
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

	void fillNow(boolean b[])
	{
		b[this.currentIndex]=true;
	}

	void geneswap(boolean[] b1, boolean[] b2)
	{
		if (b1.length!=b2.length)
		{
			return;
		}

		int n= (int) (Math.random()*b1.length-1);

		int step=1;
		int stop=n+ 3*step + (int) (Math.random()*6*step);
		if (stop>=b1.length)
		{
			stop=b1.length-1;
		}
		// println("" + n + " " + stop);

		int m=n%step;
		for (int i=n;i<stop;i++)
		{
			boolean temp=b1[i];
			b1[i]=b2[i];
			b2[i]=temp;
		}
	}


	int closestEuclidean(boolean[] b)
	{
		int[] hammingcount = new int[b.length];
		int closestIndex=0;
		for (int i=0; i< b.length;i++)
		{
			boolean[] bb = euclidArray(i, b.length);
			hammingcount[i]= hammingDist(bb,b);

			if (hammingcount[i]<hammingcount[closestIndex])
			{
				closestIndex=i;
			}
		}


		return closestIndex;
	}


	int hammingDist(boolean[] b1, boolean[] b2)
	{
		if(b1.length!=b2.length)
		{
			return 0; 
		}

		//must assume that lengths are equal here.
		int n=0;

		for (int i=0; i<b1.length;i++)
		{
			if (  (b1[i] && b2[i]) || (!b1[i] && !b2[i] )   )
			{

				//n++;
			}
			else
			{
				n++;
			}
		}

		return n;
	}


	int[] getHits(boolean[] b)
	{
		Vector h= new Vector();
		for(int i=0;i< b.length;i++)
		{
			if(b[i])
			{
				h.add(new Integer(i));
			} 

		}

		int [] hits = new int[h.size()];
		for(int i=0;i<hits.length;i++)
		{
			Integer dd=(Integer)h.get(i);
			hits[i]=(int) dd.intValue(); 
		}


		return hits;

	}

	/*
	void embellish1(boolean[] b)
	{
		int[] hits = getHits( b );

		int c1 = (int) (Math.random()*hits.length);

		int choice = hits[c1];	

		if(choice==0)
		{
			b[choice]=true;
			b[choice+1]=true;
			b[b.length-1]=true;
			return;
		}

		if(choice==b.length-1)
		{
			b[choice]=true;
			b[0]=true;
			b[choice-1]=true;
			return;
		}

		b[choice]=true;
		b[choice+1]=true;
		b[choice-1]=true;

	}
	 */

	//////embellish by putting a number of repeated hits somehwere where there is at least one hit
	void embellish(boolean[] b, int number)
	{

		if (number<=0)
		{
			return;
		}

		if (number%2!=0 )
		{
			number++;
		}

		int[] things = new int[number];
		int[] hits = getHits(b);

		if (hits.length==0)
		{
			hits = new int[] { 0  };
		}

		int c1 = (int) (Math.random()*hits.length);	  
		int choice = hits[c1];
		for (int i=0;i<things.length;i++)
		{
			if (i%2==1)
			{
				int index=(int)Math.ceil( (float)i/2 );
				things[i]=choice+(index);
			} 
			if (i%2==0)
			{
				int index=(int) Math.ceil( (float)i/2 );
				things[i]=choice-(index+1);
			}
			if (things[i]<0)
			{
				things[i]+=b.length;
			}
			if (things[i]>b.length-1)
			{
				things[i]=things[i]%b.length;
			}


		}

		for(int i=0;i<things.length;i++)
		{
			if(things[i]>=0&&things[i]<b.length)
			{
				b[things[i]]=true; 
			}
		}
	}

	// embellish, but also specifiy a skip width
	void embellish(boolean[] b, int number, int skip)
	{

		if (number<=0)
		{
			return;
		}

		if (number%2!=0 )
		{
			number++;
		}

		int[] things = new int[number];
		int[] hits = getHits(b);

		if (hits.length==0)
		{
			hits = new int[] { 0  };
		}

		int c1 = (int) (Math.random()*hits.length);	  
		int choice = hits[c1];
		for (int i=0;i<things.length;i++)
		{
			if (i%2==1)
			{
				int index=(int)Math.ceil( (float)i/2 );
				things[i]=choice+(index + i*skip);
			} 
			if (i%2==0)
			{
				int index=(int) Math.ceil( (float)i/2 );
				things[i]=choice-(index+1 +  (i+1)* skip );
			}
			if (things[i]<0)
			{
				things[i]+=b.length;
			}
			if (things[i]>b.length-1)
			{
				things[i]=things[i]%b.length;
			}


		}

		for(int i=0;i<things.length;i++)
		{
			if(things[i]>=0&&things[i]<b.length)
			{
				b[things[i]]=true; 
			}
		}
	}

	////////////////embellish by making a section of repeated hits
	void embellish2(boolean[] b, int section, int step)
	{

		int stop=section+ 3*step + (int) (Math.random()*6*step);
		if (stop>=b.length)
		{
			stop=b.length-1;
		}

		int m=section%step;
		for (int i=section;i<stop;i++)
		{
			if (i%step==m)
			{
				b[i]=true;
			}
			else
			{
				b[i]=false;
			}
		}
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




	//distance to avatar...calculate for all bots
	//
	void distanceToAvatar()
	{

	}
	///////////////musical mappings

	//map based on number of neigbhors

	void numberOfNeigbhors()
	{
		numNeighbors=0;
		//int rad =150;
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

		//distances

		for(int i =0 ; i < otherBots.size();i++)
		{
			
			 
			Bot b = otherBots.get(i);

			double dist=Math.sqrt( Math.pow((myposx-b.x),2) + Math.pow((myposy-b.y),2) ) ;

			b.distToMe=(float) dist;
			if( dist < neighborBound )	
			{
				numNeighbors++;

				b.isNeighbor=true;

				//communicate to neighbor
				if(isnComEnable())
				{
					
					queue.add(b);
					mActivity.client.sendMessage2("com,"+ mActivity.client.myID + "," + b.ID + "," + "query" + "," + "nnnn");
					/*
					if(System.currentTimeMillis()-nComTimer >350 + (long)Math.random()*50)
					{
						//mActivity.client.sendMessage("com,"+ mActivity.client.myID + "," + b.ID + "," + "query" + "," + "nnnn");
						
						queue.add(b);
						mActivity.client.sendMessage2("com,"+ mActivity.client.myID + "," + b.ID + "," + "query" + "," + "nnnn");
						
						nComTimer = System.currentTimeMillis();
					}
					*/
				}

			}
			else
			{
				b.isNeighbor=false;
			}

		}


	}


	//in relation to avatar
	void avatarMapping(int a)
	{
		switch(a)
		{
		case 0: // play avatar and rhythm shift based on distance from avatar 
			if(mActivity.client.myID==0)
			{

			}
			else
			{

				float dist=0;
				if(currentAvatar!=null)
				{
					PVector av=new PVector(currentAvatar.x,currentAvatar.y);
					PVector loc = new PVector(this.myposx,this.myposy);

					dist = av.dist(loc);
				}

				setRhythm(avatarseq);
				int num = (int) map(dist,20,600,0,4);
				for(int i=0;i<num;i++)
				{
					shiftRhythmRight(instrumentseq);
					shiftRhythmRight(sfxrseq);
				}
			}
			break;
		case 1: // rhythm split based on ID-- from avatar 

			if(mActivity.client.myID==0)
			{

			}
			else
			{
				clearRhythm(instrumentseq);
				clearRhythm(sfxrseq);
				if(ID%2==0)
				{
					for(int i=0;i<avatarseq.length;i++)
					{
						if(i%2==0)
						{
							instrumentseq[i]=avatarseq[i];
							sfxrseq[i]=avatarseq[i];
						}
					}
				}
				else
				{
					for(int i=0;i<avatarseq.length;i++)
					{
						if(i%2==1)
						{
							instrumentseq[i]=avatarseq[i];
							sfxrseq[i]=avatarseq[i];
						}
					}
				}
			}


			break;
		case 2: 
			if(mActivity.client.myID==0)
			{

			}
			else
			{

				float dist=0;
				if(currentAvatar!=null)
				{
					PVector av=new PVector(currentAvatar.x,currentAvatar.y);
					PVector loc = new PVector(this.myposx,this.myposy);

					dist = av.dist(loc);
				}

				setRhythm(avatarseq);
				int num = (int) map(dist,20,600,0,4);
				for(int i=0;i<num;i++)
				{
					shiftRhythmRight(instrumentseq);
					shiftRhythmRight(sfxrseq);
				}
			}
			break;


		case 5: 
			// different sets of rhythsms between avatar and others.  either avatar  changes as get closer, or the others do.
			// change by index modification..addition or removing..
			break;

		default: ; 

		}

		if(mActivity.client.myID==0)
		{

			return;
		}
		else
		{

		}
		//if( Math.sqrt( Math.pow((myposx-b.x),2) + Math.pow((myposy-b.y),2) ) < neighborBound )	

	}



	//avatarFunctions?? ..could also be human functions..or some of them could be.
	void av1()
	{
		//p1
		clearRhythm(instrumentseq);
		clearRhythm(sfxrseq);
		//get global seq pattern and fill according to id
		int szz = otherBots.size()+1;//totalFlock.boids.size();//12
		int myind= mActivity.client.myID;//totalFlock.boids.indexOf(this);//0
		int tlength=instrumentseq.length;//gseq.seq.length;//16

		// if 0, index 0 and 12. if 1, 1 and 13. if 2, 2 and 14. 3, 3 and 15. if 4, just 4


		int divisions= tlength/(myind+1);    
		int nums= tlength%(myind+1) ;

		Vector indices = new Vector();

		boolean test=true;
		int ii=0;
		while(test)
		{
			int tind=myind+ii*szz;
			if(tind>=tlength)
			{
				test=false; 
			}
			else
			{
				indices.add(new Integer(tind));
				ii++;
			}
		}


		/////

		/*
		int extra = numNeighbors + 1;
		for(int i=0; i <indices.size();i++)
		{

			Integer d = (Integer) indices.get(i);
			int dd= d.intValue()%tlength;
			boolean b = avatarseq[dd];       
			instrumentseq[dd] = b;
			sfxrseq[dd]=b;

		}
		 */


		///approaching the idea...	

		int extra = numNeighbors + 1;
		for(int i=0; i <indices.size();i++)
		{

			Integer d = (Integer) indices.get(i);
			for(int j=0;j<extra;j++)
			{
				int dd = d.intValue() + j*tlength/2;
				dd= dd%tlength;
				boolean b = avatarseq[dd];       
				instrumentseq[dd] = b;
				sfxrseq[dd]=b;
			}
		}



		//in the future
		/*
		for(int i=0; i< myNeigbhors.size(); i++)
		{
			Boid b = (Boid) myNeigbhors.get(i);
			boolean[] b1=b.getBoolArray();
			boolean[] b2=getBoolArray();


			for(int j=0;j<number;j++)
			{
				roll[j].pressed=b1[j]||b2[j];

			}

			//b.copyFromMusicShape(this);
			//copyFromMusicShape(b);

		}
		 */






	}

	//split rhythm into two groups
	void splitRhythm(int a)
	{
		clearRhythm(instrumentseq);
		clearRhythm(sfxrseq);
		boolean[] b = avatarseq;

		if(numNeighbors%2==0)
		{
			for(int i=0; i<b.length;i++)
			{
				if(i%2==0)
				{
					instrumentseq[i]=b[i];
					sfxrseq[i]=b[i];
				}

			}
		}
		else
		{
			for(int i=0; i<b.length;i++)
			{
				if(i%2==1)
				{
					instrumentseq[i]=b[i];
					sfxrseq[i]=b[i];
				}

			}
		}

	}


	void setMapping(int a)
	{
		//this.avatarMode=false;
		mActivity.beatTimer.mapping=a;


	}

	int getMapping()
	{
		return mActivity.beatTimer.mapping;
	}
	////////////////////

	boolean[] getPattern()
	{

		return instrumentseq;
	}

	String patternToString(boolean[] b)
	{
		String s = "";

		for(int i=0; i < b.length; i++)
		{
			if(b[i])
			{
				s += "1";
			}
			else
			{
				s+="0";
			}
		}

		return s;
	}
	
	String notesToString(int[] b)
	{
	  String s = "";

	  for (int i=0; i < b.length; i++)
	  {
	    s+=b[i]+",";
	  }

	  return s;
	}

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

	double distanceToTarget(double x, double y)
	{
		return Math.sqrt( Math.pow((myposx-x),2) + Math.pow((myposy-y),2) );
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
			//azimuthlabel.setText("azimuth:" + azimuth);
			angleAzimuthDiff=azimuth-angleAzimuth;
			angleAzimuth=azimuth;
		}


	}

	void resetIndex()
	{

		mActivity.beatTimer.resetIndex();


	}


	class MusicBehavior
	{
		MusicBehavior()
		{

		}

		void euclid()
		{

		}
		void fillRhythm()
		{

		}
		void copyFromBot()
		{

		}
		void orientation()
		{

		}
		void neigbhor()
		{

		}
		void speed()
		{

		}


	}



	void loseN(boolean[]b, int n)
	{
		//first count how many hits
		int numhits=0;

		Vector inds = new Vector();
		for (int i=0; i <b.length;i++)
		{
			if (b[i] )
			{
				numhits++;
				inds.add(new Integer(i));
			}
		}

		if(n>numhits)
		{
			n=numhits; 
		}

		for(int i=0;i<n;i++)
		{

			int nn= (int)Math.random()*inds.size();      
			Integer ii = (Integer) inds.get(nn);      
			inds.remove(nn);//forgot to puthtis in last time
			b[ii.intValue()]=false;      
		}   

	}


	int[] randomRangeList(int n, int mi, int ma)
	{
		int[] result = new int[n];
		int r = ma-mi;

		Vector v = new Vector();
		for (int i=0; i< r;i++)
		{
			v.add(new Integer(mi+i));
		}

		for (int i=0;i<n;i++)
		{
			int c = (int)(Math.random()* (double)v.size());
			Integer ii= (Integer) v.get(c);
			result[i]= ii.intValue();
		}


		return result;
	}

	void fillRhythmDist(Bot b)
	{
		//copyFromMusicShape(b);//until we can...copy avatarpat
		setRhythm(avatarseq);

		PVector p1 = new PVector(b.x,b.y);
		PVector p2= new PVector(this.myposx,this.myposy);

		float d = map(PVector.dist(p1, p2), neighborBound, 500, 0, instrumentseq.length);
		if (d<0)
		{
			d=0;
		}

		int nums[] = randomRangeList((int)d, 0, instrumentseq.length);

		for (int i=0; i<nums.length; i++)
		{
			sfxrseq[nums[i]]=!sfxrseq[nums[i]];
			instrumentseq[nums[i]]=!instrumentseq[nums[i]];
		}

		//rebuildMusicShape();
	}

	float distanceFromAvatar()
	{
		float f=0;

		if(ID!=0)
		{
			for(int i = 0; i<otherBots.size();i++)
			{
				Bot b= otherBots.get(i);
				if(b.ID==0)
				{
					f=PVector.dist(new PVector(this.myposx,this.myposy), new PVector (b.x,b.y));
					break;
				}

			}
		}



		return f;

	}




	void initializeKuku()
	{
		int[] d0= new int[] {      
				0, 2, 3, 5, 7, 8, 10, 11, 12
		};
		int[] d1= new int[] {
				0, 4, 8, 10, 12, 13
		};
		int[] d2= new int[] {
				0, 2, 3, 6, 8, 10, 11, 14
		};
		int[] d3= new int[] {
				0, 1, 3, 4, 5, 6, 8, 9, 11, 12, 13, 14
		};

		kuku = new int[4][];
		kuku[0]=d0;
		kuku[1]=d1;
		kuku[2]=d2;
		kuku[3]=d3;

		dchoice();
	}

	boolean[] getKuku(int k)
	{
		boolean[] b = new boolean[this.SEQUENCERLENGTH];;
		if(k<0|| k>=kuku.length)
		{
			return b;
		}

		for(int i=0;i<kuku[k].length;i++)
		{
			if(kuku[k][i]<b.length)
			{
				b[kuku[k][i]]=true;
			}
		}


		return b;

	}
	void initializeDjembe()
	{


		int[] d0= new int[] {      
				0, 2, 3, 5, 7, 8, 10, 11, 12
		};
		int[] d1= new int[] {
				0, 4, 8, 10, 12, 13
		};
		int[] d2= new int[] {
				0, 2, 3, 6, 8, 10, 11, 14
		};
		int[] d3= new int[] {
				0, 1, 3, 4, 5, 6, 8, 9, 11, 12, 13, 14
		};

		////////////////////////////////////////////
		//int[] d0= new int[]{0,2,3,5,7,8,10,11,12};
		if(this.SEQUENCERLENGTH==32)
		{
			for(int i=0;i<d0.length;i++)
			{
				d0[i]=d0[i]*2;
			}
		}

		for(int i=0;i<d0.length;i++)
		{
			if(d0[i]<djembe0.length)
			{
				djembe0[d0[i]]=true;
			}
		}


		//////////////////////////////////////
		//int[] d1= new int[]{0,4,8,10,12,13};
		if(this.SEQUENCERLENGTH==32)
		{
			for(int i=0;i<d1.length;i++)
			{
				d1[i]=d1[i]*2;
			}
		}

		for(int i=0;i<d1.length;i++)
		{
			if(d1[i]<djembe1.length)
			{
				djembe1[d1[i]]=true;
			}
		}


		///////////////////////////
		//int[] d2= new int[]{0,2,3,6,8,10,11,14};
		if(this.SEQUENCERLENGTH==32)
		{
			for(int i=0;i<d2.length;i++)
			{
				d2[i]=d2[i]*2;
			}
		}

		for(int i=0;i<d2.length;i++)
		{
			if(d2[i]<djembe2.length)
			{
				djembe2[d2[i]]=true;
			}
		}


		/////////////////////////////
		//int[] d3= new int[]{0,1,3,4,5,6,8,9,11,12,13,14};
		if(this.SEQUENCERLENGTH==32)
		{
			for(int i=0;i<d3.length;i++)
			{
				d3[i]=d3[i]*2;
			}
		}

		for(int i=0;i<d3.length;i++)
		{
			if(d3[i]<djembe3.length)
			{
				djembe3[d3[i]]=true;
			}
		}


	}

	void dchoice()
	{
		int d=this.SEQUENCERLENGTH/16;


		for (int k=0;k<kuku.length;k++)
		{
			for (int i=0;i<kuku[k].length;i++)
			{
				kuku[k][i]=kuku[k][i]*d;
			}
		}
	}

	public boolean[] getReggaeton() {
		// TODO Auto-generated method stub
		boolean[] b = new boolean[this.SEQUENCERLENGTH];;

		for(int i=0;i<this.reg.length;i++)
		{
			b[reg[i]]=true;
		}

		return b;
	}

	public void swapRandomPortion(boolean[] seq1,boolean[] seq2) {
		// TODO Auto-generated method stub
		
		if(seq1.length==0||seq2.length==0)
		{
			return;
		}
		int sz=Math.min(seq1.length,seq2.length);
		
		int start = (int) Math.random()*sz/2;
		
		int stop = start + (int) (Math.random()*(sz-start));
		
		for(int i=start;i<stop;i++)
		{
			 
			if(i<sz-1)
			{
				boolean temp = seq1[i];
				seq1[i]=seq2[i];
				seq2[i]=temp;
			}
		}
		
		
	}
	
	public void swapPortion(boolean[] seq1,boolean[] seq2, int start, int stop)
	{
		if(seq1.length!=seq2.length)
		{
			return;
		}
		if(seq1.length==0|| seq2.length==0)
		{
			return;
		}
		
		if(start>seq1.length-1 || stop > seq1.length-1)
		{
			return;
		}
		
		if(start>stop)
		{
			return;
		}
		
		int sz= seq1.length;
		
		for(int i=start;i<stop;i++)
		{
			
			if(i<sz-1)
			{
				boolean temp = seq1[i];
				seq1[i]=seq2[i];
				seq2[i]=temp;
			}
		}
		
		
		
		
	}


	//if 48, what euclid works?  0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
	// 1, 2, 3, 4, 6, 8, 12, 16
	
	
	void chooseSong(int i)
	{
		switch(i)
		{
		case 0:
			this.mySong=new FightSong();
			break;
		case 1:
			this.mySong= new OldMacDonald();
			break;
		case 2:
			this.mySong = new Joyful();
			break;
		case 3:
			this.mySong= new LionSleeps();
			break;
		case 4: 
			//this.mySong = new Tetris();
			this.mySong = new TestSong();
			break;
		case 10: 
			this.mySong = new TestSong();
			break;
			
		default:
			this.mySong=new TestSong();
		
		
		}
	}
	
	public void setMyNote(int n)
	{
		this.myNote=n;	
	}
	
	public int getMSDegree(int n)
	{
		
		return MSDeg[n%MSDeg.length];
	}
	
	public int getFightSongNote( int n)
	{
		
		int res=72;
		 
		
		switch (n)
		{
		case 0:
			res=71;
			break;
		case 1:
			res=72+0;
			break;
		case 2:
			res=72+2;
			break;
		case 3:
			res=72+4;
			break;
		case 4:
			res=72+5;
			break;
		case 5:
			res=72+7;
			break;
		case 6:
			res=72+9;
			break;
		case 7:
			res=72+12;
			break;
		 
		default:
			res=72;
			break;
		
		
		}
		
	   return res;
	}

	public void setAvatarMode(boolean b) {
		// TODO Auto-generated method stub
		
		this.avatarMode=b;
		
	}

	public void usingController(boolean b) {
		// TODO Auto-generated method stub
		this.usingController=b;
	}


}
