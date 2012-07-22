package com.google.android.DemoKit;





import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.android.DemoKit.RobotFaceView.Arena;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class RobotFaceView extends SurfaceView implements OnTouchListener,
SurfaceHolder.Callback, Runnable {

	Paint paint1, paint2, blackpaint, blackpaintText, redPaintHighlight,
	greenPaint, yellowPaint, goldPaint, coralPaint, blackPaintDebugText;

	private SurfaceHolder mHolder;

	public BoeBotController bbc;
	public FaceController fc;
	public BeatTimer bt;

	public long jiggleTimer;
	public long jiggleTimerBigger;
	public boolean jiggle;

	RobotFaceViewThread thread;
	int cnt;

	public boolean simulateVel;
	public Vector simulatedVels;
	public PVector mySimulatedVel;
	public boolean jiggleBigger;


	public boolean touching;
	public int touchX,touchY;
	public int prevTouchX,prevTouchY;
	public int deltaTouchX,deltaTouchY;
	public int initialTouchX,initialTouchY;

	public boolean drawFace=true;

	public RobotFaceView(Context context) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		// TODO Auto-generated constructor stub
		init(mHolder, context);
	}

	public RobotFaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		init(mHolder, context);
	}

	public RobotFaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mHolder = getHolder();
		mHolder.addCallback(this);
		init(mHolder, context);
	}

	void init(SurfaceHolder holder, Context context) {
		paint1 = new Paint();
		paint2 = new Paint();
		blackpaint = new Paint();
		blackpaintText = new Paint();
		redPaintHighlight = new Paint();
		greenPaint = new Paint();
		yellowPaint = new Paint();
		goldPaint = new Paint();
		blackPaintDebugText = new Paint();

		coralPaint = new Paint();

		paint1.setColor(Color.WHITE);
		paint1.setAntiAlias(true);

		paint2.setColor(Color.BLUE);
		paint2.setAntiAlias(true);
		// paint2.setStyle(Paint.Style.STROKE);

		blackpaint.setColor(Color.BLACK);
		blackpaint.setAntiAlias(true);

		blackpaintText.setTextSize(25);
		blackpaintText.setColor(Color.BLACK);
		blackpaintText.setAntiAlias(true);
		
		blackPaintDebugText.setTextSize(50);
		blackpaintText.setColor(Color.BLACK);
		blackpaintText.setAntiAlias(true);

		redPaintHighlight.setColor(Color.RED);
		redPaintHighlight.setAntiAlias(true);
		redPaintHighlight.setStyle(Paint.Style.STROKE);

		greenPaint.setColor(Color.GREEN);
		greenPaint.setAntiAlias(true);

		yellowPaint.setColor(Color.YELLOW);
		yellowPaint.setAntiAlias(true);

		goldPaint.setColor(Color.rgb(255,215,0));

		coralPaint.setColor(Color.rgb(240,220,255));


		thread = new RobotFaceViewThread(holder, context);
		this.setOnTouchListener(this);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// canvas.drawLine(0, 0, 20, 20, paint);
		// canvas.drawLine(20, 0, 0, 20, paint);
		// paint.
		// canvas.drawCircle(20, 40, 10000, paint);
		// canvas.drawRect(0, 0, 20, 20, paint);

		/*
		 * 

		if (this.bbc != null) {
			int sz = 30;
			for (int i = 0; i < bbc.sfxrseq.length; i++) {
				if (bbc.sfxrseq[i])
					canvas.drawRect(0 + 20 * i, 0 * sz, 20 + 20 * i, 1 * sz,
							paint1);
				else
					canvas.drawRect(0 + 20 * i, 0 * sz, 20 + 20 * i, 1 * sz,
							paint2);

				// c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for (int i = 0; i < bbc.instrumentseq.length; i++) {
				if (bbc.instrumentseq[i])
					canvas.drawRect(0 + 20 * i, 1 * sz, 20 + 20 * i, 2 * sz,
							paint1);
				else
					canvas.drawRect(0 + 20 * i, 1 * sz, 20 + 20 * i, 2 * sz,
							paint2);

				// c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for (int i = 0; i < bbc.fseq.length; i++) {
				if (bbc.fseq[i])
					canvas.drawRect(0 + 20 * i, 3 * sz, 20 + 20 * i, 4 * sz,
							paint1);
				else
					canvas.drawRect(0 + 20 * i, 3 * sz, 20 + 20 * i, 4 * sz,
							paint2);

				// c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for (int i = 0; i < bbc.bseq.length; i++) {
				if (bbc.bseq[i])
					canvas.drawRect(0 + 20 * i, 4 * sz, 20 + 20 * i, 5 * sz,
							paint1);
				else
					canvas.drawRect(0 + 20 * i, 4 * sz, 20 + 20 * i, 5 * sz,
							paint2);

				// c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for (int i = 0; i < bbc.rseq.length; i++) {
				if (bbc.rseq[i])
					canvas.drawRect(0 + 20 * i, 5 * sz, 20 + 20 * i, 6 * sz,
							paint1);
				else
					canvas.drawRect(0 + 20 * i, 5 * sz, 20 + 20 * i, 6 * sz,
							paint2);

				// c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for (int i = 0; i < bbc.lseq.length; i++) {
				if (bbc.lseq[i])
					canvas.drawRect(0 + 20 * i, 6 * sz, 20 + 20 * i, 7 * sz,
							paint1);
				else
					canvas.drawRect(0 + 20 * i, 6 * sz, 20 + 20 * i, 7 * sz,
							paint2);

				// c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}


		}
		 */
		invalidate();


	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thread.message = new TimedMessage();

		thread.modeButton = new NoseButton();
		thread.modeButton.y=getHeight()-getHeight()/4;
		thread.modeButton.x=getWidth()-getWidth()/16;

		thread.danceButton = new NoseButton();
		thread.danceButton.y=getHeight()-getHeight()/4;
		thread.danceButton.x=getWidth()/16;


		thread.robotEyes = new Eyes();
		thread.robotTeeth = new Teeth(getWidth() / 2, getHeight() - getHeight()
				/ 4);
		thread.robotNose = new NoseButton();

		int hh=90;
		int ww= (int) ((float)hh*(4.0f/3.0f));
		thread.arena = new Arena(getWidth()/2, getHeight()/5,ww,hh);

		/*
		thread.danceButton = new MyButton(getWidth() / 8, getHeight()
				- getHeight() / 16, 35, "Dance");
		thread.cameraButton = new MyButton(getWidth() - getWidth() / 8,
				getHeight() - getHeight() / 16, 35, "Camera");
		 */

		int s=20;
		thread.tempup = new MyButton(2*s,2*s+s,2*s,"tempoup");
		thread.tempdown = new MyButton(getWidth()-2*s,2*s+s,2*s,"tempodown");

		thread.divup = new MyButton(2*s,2*s+5*s,2*s,"divup");
		thread.divdown = new MyButton(getWidth()-2*s,2*s+5*s,2*s,"divdown");


		thread.setRunning(true);
		thread.start();

		Log.d("SURFACECREATED", "W:" + getWidth() + "  H:" + getHeight());



		///
		//doTest();

	}

	//////test
	void doTest()
	{
		this.simulateVel=true;

		mySimulatedVel = new PVector((float)Math.random(),(float)Math.random());
		mySimulatedVel.normalize();
		mySimulatedVel.mult(2);
		bbc.myposx=640/2;
		bbc.myposy=480/2;
		/*
		Bot testBot = new Bot();
		testBot.x=640/4;
		testBot.y=480/4;
		testBot.ID=0;
		bbc.allBots.add(testBot);

		testBot = new Bot();
		testBot.x=640-640/4;
		testBot.y=480-480/4;
		testBot.ID=1;
		bbc.allBots.add(testBot);
		 */



		for(int i=0;i<8;i++)
		{
			Bot testBot = new Bot(bbc);
			testBot.x=(int) (Math.random()*640);
			testBot.y=(int) (Math.random()*480);
			testBot.ID=i;
			testBot.camang=(float) ((  2*Math.random()-1  ) *180 );
			bbc.otherBots.add(testBot);
		}



		if(simulateVel)
		{
			simulatedVels = new Vector();
			for(int i=0;i<bbc.otherBots.size();i++)
			{
				PVector v = new PVector((float)Math.random(),(float)Math.random());
				v.normalize();
				v.mult(2);
				simulatedVels.add(v);
			}
		}


	}


	/////endtest

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	class RobotFaceViewThread extends Thread {

		private SurfaceHolder mSurfaceHolder;
		private Handler mHandler;
		private Resources mRes;
		private boolean running;

		List<Point> points = new ArrayList<Point>();

		public Eyes robotEyes;
		public Teeth robotTeeth;
		public NoseButton robotNose, modeButton,tempoUP,tempoDOWN, danceButton;

		public Arena arena;

		public MyButton cameraButton, tempup,tempdown, divup,divdown;
		public TimedMessage message;
		boolean useCamera;


		long noFaceTimer;
		int prevFaces=0;
		boolean noFaces;

		public RobotFaceViewThread(SurfaceHolder holder, Context context) {

			mSurfaceHolder = holder;
			// mHandler = handler;
			Context mContext = context;
			mRes = context.getResources();

			// robotEyes= new Eyes();

		}

		@Override
		public void run() {
			Canvas c;// = mSurfaceHolder.lockCanvas(null);
			// Log.d("in thread", "in run: ");
			while (running) {
				// Log.d("in thread", "in run: ");
				c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {

						if(drawFace)
						{
							render(c);
							// _panel.onDraw(c);
							robotEyes.run(c);
							robotTeeth.run(c);
							robotNose.run(c);
							//cameraButton.run(c);
							danceButton.run(c);
							modeButton.run(c);
							tempup.run(c);
							tempdown.run(c);
							divup.run(c);
							divdown.run(c);
							message.run(c);

							arena.run(c);


							if(bbc!=null)
							{
								c.drawText("camang:"  + new DecimalFormat("#.##").format( bbc.camang) , 0, getHeight()-getHeight()/21, blackpaintText);
								//c.drawText("dif:" + new DecimalFormat("#.##").format( bbc.angleAzimuthDiff) , getWidth()/2-getWidth()/8, getHeight()-getHeight()/21, blackpaintText);
								//c.drawText("az: " + new DecimalFormat("#.##").format( bbc.angleAzimuth) , getWidth()-getWidth()/3.25f, getHeight()-getHeight()/21, blackpaintText);
								c.drawText("ir0: " + bbc.mActivity.ic.ir0 , 0, getHeight()-getHeight()/14, blackpaintText);
								c.drawText("id: " + bbc.ID , 0, getHeight()-getHeight()/7, blackpaintText);
								c.drawText("nn: " + bbc.numNeighbors +"," + bbc.myExtendedNeighbors.size(), getWidth()-getWidth()/3.25f, getHeight()-getHeight()/14, blackpaintText);
								c.drawText("div: " + new DecimalFormat("#.##").format( bbc.mActivity.beatTimer.div) , getWidth()-getWidth()/3.25f, getHeight()-getHeight()/7, blackpaintText);
								c.drawText("mpsc " +  bbc.mActivity.beatTimer.myPlaySessionCount + "," + bbc.mActivity.beatTimer.myGeneralPlaySessionCount, getWidth()-getWidth()/3.25f, getHeight()-getHeight()/28, blackpaintText);
								c.drawText("pm " +  bbc.mActivity.beatTimer.possibleMeasures.size() , getWidth()/2, getHeight()-getHeight()/28, blackpaintText);

								c.drawText("vel: " + new DecimalFormat("#.##").format( bbc.myBehavior.desiredVel.x) + ","+ new DecimalFormat("#.##").format( bbc.myBehavior.desiredVel.y) , getWidth()/3, getHeight()-getHeight()/14, blackpaintText);
								c.drawText("lr: " +  bbc.lbyte + ","+  bbc.rbyte , getWidth()/3, getHeight()-getHeight()/7, blackpaintText);


							}
							if(jiggle)
							{
								if(System.currentTimeMillis()-jiggleTimer>1000)
								{
									jiggle=false;

								}
								if(System.currentTimeMillis()-jiggleTimerBigger>bt.globalTimeInterval*2)
								{

									jiggleBigger=false;
								}

							}



						}
						else//debug view
						{
							renderDebug(c);
						}
						
						

					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}

			// synchronized (mSurfaceHolder) {
			// render(c);
			// update();

			// render(this.);
		}

		void toggleUseCamera() {
			useCamera = !useCamera;
		}

		void setUseCamera(boolean b) {
			useCamera = b;
		}

		public void setRunning(boolean run) {
			running = run;
		}

		public void update() {

		}

		public void render(Canvas canvas) {
			if(bbc.avatarMoving)
			{
				canvas.drawPaint(greenPaint);
			}
			else
			{
				canvas.drawPaint(goldPaint);
			}

			if (true) {
				try {
					for (Point point : points) {

						canvas.drawCircle(point.x, point.y, 5, paint1);

						point.update();

						// Log.d(TAG, "Painting: "+point);
					}

				} catch (Exception e) {

				}
			}

			for (int i = 0; i < 1; i++) {

				// canvas.drawRect(getWidth()/2-20, getHeight()/2-20,
				// getWidth()/2+20, getHeight()/2+20, paint);
			}

			/*
			 * for (int i =0 ; i<sequencer.length; i++) {
			 * if(beatTimerThread.generalIndex%16==i) {
			 * sequencer[i].highlight=true; if(sequencer[i].on &&
			 * !sequencer[i].playedOnce ) { if(useSample) {
			 * _soundPool.play(_playbackFile, 1, 1, 0, 0, 1); } else {
			 * atest.replay(); //atest.replayRandom(); }
			 * sequencer[i].playedOnce=true; } } else {
			 * sequencer[i].highlight=false; sequencer[i].playedOnce=false; }
			 * 
			 * sequencer[i].run(canvas); }
			 * 
			 * mySlider.run(canvas); //wv.run(canvas);
			 */

		}

	}
	
	public void renderDebug(Canvas c)
	{
		int ss=0;
		
		c.drawPaint(paint1);
		String text="Testing--ID  " +  bbc.ID;
		c.drawText(text , 0, ss+=50, blackPaintDebugText);
		
		text="integer neighbors " +  bbc.numNeighbors;
		c.drawText(text , 0, ss+=50, blackPaintDebugText);
		
		text="neighbors sz: " + bbc.myNeighbors.size();
		c.drawText(text , 0, ss+=50, blackPaintDebugText);
		text="";
		for(int i=0;i<bbc.myNeighbors.size();i++)
		{
			Bot b= bbc.myNeighbors.get(i);
			text+="" + b.ID + ",";
		}
		c.drawText(text , 0, ss+=50, blackPaintDebugText);
		
		
		text="exten. neigh sz: " + bbc.myExtendedNeighbors.size() ;
		c.drawText(text , 0, ss+=50, blackPaintDebugText);
		text="";
		for(int i=0;i<bbc.myExtendedNeighbors.size();i++)
		{
			Bot b= bbc.myExtendedNeighbors.get(i);
			text+="" + b.ID + ",";
		}
		c.drawText(text , 0, ss+=50, blackPaintDebugText);
		c.drawText(bbc.neighborThread.inter, 0, ss+=50, blackPaintDebugText);
		
		text="other bots--" + bbc.otherBots.size();
		c.drawText(text , 0, ss+=50, blackPaintDebugText);
		text="";
		for(int i=0;i<bbc.otherBots.size();i++)
		{
			Bot b= bbc.otherBots.get(i);
			text+="" + b.ID + ",";
		}
		c.drawText(text , 0, ss+=50, blackPaintDebugText);
		
		text="p= " +  bbc.p;
		c.drawText(text , 0, ss+=50, blackPaintDebugText);
		//c.drawText(text , 0, 100, blackPaintDebugText);
		
		//c.drawText("camang:"  + new DecimalFormat("#.##").format( bbc.camang) , 0, getHeight()-getHeight()/21, blackpaintText);
		
	}

	class Point {
		float x, y;

		float vx, vy;

		@Override
		public String toString() {
			return x + ", " + y;
		}

		public void update() {
			x += vx;
			y += vy;

			if (x < 0 || x > getWidth())
				vx *= -1;
			if (y < 0 || y > getHeight())
				vy *= -1;

		}
	}
	


	class Eyes {
		long blinkTimer;

		Eye e1, e2;

		boolean closeOpen;
		boolean closeState;
		boolean openState;
		long openTimer, closeTimer;
		int brate = 16;

		int sz = 80;
		int tsz = 50;

		int cbuff = 0;
		int obuff = 10;

		boolean drunk = false;
		long drunkTimer;

		boolean showlog = false;
		long glookTimer;

		public Eyes() {

			int w1 = getWidth();
			int h1 = getHeight();
			e1 = new Eye(w1 / 4, h1 / 2);
			e2 = new Eye(w1 - w1 / 4, h1 / 2);
			Log.d("WHAT ARE W H", "W:" + w1 + "  H:" + h1);

			openTimer = System.currentTimeMillis();
			closeTimer = System.currentTimeMillis();

			glookTimer = System.currentTimeMillis();
			drunkTimer = glookTimer;
		}

		void resetEyePositions(int x, int y) {
			e1 = new Eye(x / 4, y / 2);
			e2 = new Eye(x - x / 4, y / 2);

		}

		void update() {
			if (closeOpen && !(closeState || openState)) {
				tsz += brate;
				if (showlog)
					Log.d("eye", "closing");
			}

			if (!closeOpen && !(closeState || openState)) {
				tsz -= brate;
				if (showlog)
					Log.d("eye", "opening");

			}

			if (tsz > sz + cbuff) {
				tsz = sz + cbuff;
				closeOpen = true;

				closeState = true;
				closeTimer = System.currentTimeMillis();
				if (showlog)
					Log.d("eye", "closed");
			}
			if (tsz < 0 + obuff) {
				tsz = 0 + obuff;
				closeOpen = false;
				openState = true;
				openTimer = System.currentTimeMillis();
				if (showlog)
					Log.d("eye", "open");
			}

			if (openState) {
				if (System.currentTimeMillis() - openTimer > 750) {
					openState = false;
					closeOpen = true;
					if (showlog)
						Log.d("eye", "openstate finished");
				}
			}

			if (closeState) {
				if (System.currentTimeMillis() - closeTimer > 500) {
					closeState = false;
					closeOpen = false;
					if (showlog)
						Log.d("eye", "closeState finished");
				}
			}

			if (drunk) {
				if (System.currentTimeMillis() - drunkTimer > 500 + 500 * Math
						.random()) {
					cbuff = (int) (15 * Math.random());
					obuff = (int) (15 * Math.random() + 10);
				}
			} else {
				if (System.currentTimeMillis() - glookTimer > 1500) {
					glookTimer += 1000;

					double choi = Math.random();
					e1.p.choice = choi;
					e2.p.choice = choi;

					int xta = (int) map((float) Math.random(), 0, 1, -(sz) / 2,
							(sz) / 2);
					int yta = (int) map((float) Math.random(), 0, 1, -(sz) / 2,
							(sz) / 2);

					e1.p.xtarget = xta;
					e1.p.ytarget = yta;
					e2.p.xtarget = xta;
					e2.p.ytarget = yta;

					if (choi < .5) {
						e1.p.xtarget = 0;
						e1.p.ytarget = 0;
						e2.p.xtarget = 0;
						e2.p.ytarget = 0;
					} else {
						e1.p.xtarget = xta;
						e1.p.ytarget = yta;
						e2.p.xtarget = xta;
						e2.p.ytarget = yta;
					}

				}
			}
		}

		void render() {
		}

		void run(Canvas c) {
			update();
			render();

			e1.run(c);
			e2.run(c);
		}

		// inner class for pair of eyes
		class Eye {

			int x, y;

			Pupil p;

			String tt = "testing testing";
			String other;

			Eye(int x, int y) {
				this.x = x;
				this.y = y;
				p = new Pupil();

			}

			void update() {
				other = " x:" + bbc.opcvFD.x + " y:" + bbc.opcvFD.y + " sz:"
				+ bbc.opcvFD.sz;
			}

			void render(Canvas c) {
				// eyelid//always drawn
				// fill(0);
				// ellipse(x,y,sz,sz);

				float f1,f2,f3,f4;
				float m=21;
				f1=m*(float)(2*Math.random()-1);
				f2=m*(float)(2*Math.random()-1);
				f3=m*(float)(2*Math.random()-1);
				f4=m*(float)(2*Math.random()-1);


				float leftx = x - sz;
				float topy = y - sz;
				float rightx = x + sz;
				float bottomy = y + sz;
				RectF ovalBounds;

				if(jiggleBigger)
				{
					ovalBounds = new RectF(leftx+f1, topy+f2, rightx+f3, bottomy+f4);
				}
				else
				{
					ovalBounds = new RectF(leftx, topy, rightx, bottomy);
				}

				/*
				if(bbc.danceSequencer)
				{
					c.drawOval(ovalBounds, greenPaint);
				}
				else
				{
					c.drawOval(ovalBounds, blackpaint);
				}
				 */
				c.drawOval(ovalBounds, blackpaint);


				// eye
				// fill(255);
				// ellipse(x,y,sz,sz-tsz);

				float leftx2 = x - sz;
				float topy2 = y - (sz - tsz);
				float rightx2 = x + sz;
				float bottomy2 = y + (sz - tsz);
				RectF ovalBounds2 ;

				if(jiggleBigger)
				{
					ovalBounds2 = new RectF(leftx2, topy2, rightx2, bottomy2);
				}
				else
				{
					ovalBounds2 = new RectF(leftx2, topy2, rightx2, bottomy2);
				}
				//RectF ovalBounds2 = new RectF(leftx2, topy2, rightx2, bottomy2);

				/*
				if (bbc.getMapping()==6) {
					c.drawOval(ovalBounds2, paint2);
				} else 
				if (bbc.getMapping()==1){

					c.drawOval(ovalBounds2, yellowPaint);
				}
				if (bbc.getMapping()==0) {

					c.drawOval(ovalBounds2, paint1);
				}
				 */
				c.drawOval(ovalBounds2, paint1);






				// ////////text for eyelash??
				// We must keep track of our position along the curve
				float arclength = 0;
				// For every box
				// this is temporary
				tt = other;
				for (int i = 0; i < tt.length(); i++) {

					// The character and its width
					char currentChar='0';
					if(i<tt.length())
					{
						currentChar = tt.charAt(i);
					}
					else
					{

					}
					// Instead of a constant width, we check the width of each
					// character.
					float w = blackpaintText.measureText(tt, i, i) + 15;
					// Each box is centered so we move half the width
					arclength += w / 2;

					// Angle in radians is the arclength divided by the radius
					// Starting on the left side of the circle by adding PI
					float theta = (float) (Math.PI + arclength / (float) sz);

					// pushMatrix();
					c.save();

					// Polar to Cartesian conversion allows us to find the point
					// along the curve. See Chapter 13 for a review of this
					// concept.
					c.translate((float) (x + (sz) * Math.cos(theta)),
							(float) (y + (sz) * Math.sin(theta)));
					// Rotate the box (rotation is offset by 90 degrees)
					c.rotate((float) ((theta + Math.PI / 2.0) * (180.0 / Math.PI)));

					// Display the character
					// fill(0);
					// stroke(0);
					// text(currentChar,0,0);
					c.drawText(Character.toString(currentChar), 0, 0,
							blackpaintText);

					// popMatrix();
					c.restore();

					// Move halfway again
					arclength += w / 2;
				}

			}

			void run(Canvas c) {
				update();
				render(c);
				p.run(c);

			}

			// innerclass for eye
			class Pupil {
				int xoff;
				int yoff;

				int xtarget;
				int ytarget;

				long lookTimer;
				double choice;

				Pupil() {

					lookTimer = System.currentTimeMillis();
				}

				void update() {

					if (bbc.getMapping()==6) {

						int fw = (int) bbc.opcvFD.x;
						int fh = (int) bbc.opcvFD.y;
						int ww = bbc.opcvFD.mw;
						int hh = bbc.opcvFD.mh;

						if (fw == 0 || fh == 0) {
							xoff = 0;
						} else {
							xoff = (int) map(fw - ww / 2 + 100, -(ww) / 2,
									(ww) / 2, -(sz * .5f) / 2, (sz * .5f) / 2);
						}

						if (fw == 0 || fh == 0) {
							yoff = 0;
						} else {
							yoff = (int) map(fh - hh / 2 + 100, -(hh) / 2,
									(hh) / 2, -(sz * .5f) / 2, (sz * .5f) / 2);
						}
					}

					// look straight or randomly

					if(drunk) 
					{ 
						if(System.currentTimeMillis()-lookTimer>1500)
						{ 
							lookTimer+=1000;

							choice=Math.random();

							xtarget=(int)
							map((float)Math.random(),0,1,-(sz)/2,(sz)/2);
							ytarget=(int)
							map((float)Math.random(),0,1,-(sz)/2,(sz)/2);; 
						} 
					}


					if(choice<.5) 
					{ xtarget=0; ytarget=0; } 
					else {
						if(xoff<xtarget) { xoff++; } if(xoff>xtarget) { xoff--; }
						if(yoff<ytarget) { yoff++; } if(yoff>ytarget) { yoff--; }
					}


				}

				void render(Canvas c) {
					// fill(0);
					// ellipse(x,y,.2*sz,.2*sz);

					int rnd;


					float leftx = (x - xoff) - .2f * sz;
					float topy = (y + yoff) - .2f * sz;
					float rightx = (x - xoff) + .2f * sz;
					float bottomy = (y + yoff) + .2f * sz;

					RectF ovalBounds;
					if(!jiggle)
					{
						ovalBounds = new RectF(leftx, topy, rightx, bottomy);
					}
					else
					{
						float f1,f2,f3,f4;
						float m=3;
						f1=m*(float)(2*Math.random()-1);
						f2=m*(float)(2*Math.random()-1);
						f3=m*(float)(2*Math.random()-1);
						f4=m*(float)(2*Math.random()-1);
						ovalBounds = new RectF(leftx+f1, topy+f2, rightx+f3, bottomy+f4);
					}

					//RectF ovalBounds = new RectF(leftx, topy, rightx, bottomy);
					c.drawOval(ovalBounds, blackpaint);
				}

				void run(Canvas c) {
					update();
					render(c);
				}
			}

		}

	}

	// //end eyes

	// start teeth
	class Teeth {
		int numTeeth = bbc.SEQUENCERLENGTH;

		Tooth[] t;
		Tooth[][] rteeth;

		int toothSize = 6;
		int startx, starty;

		int state = 0;

		public Teeth(int xx, int yy) {
			startx = xx;
			starty = yy;

			t = new Tooth[numTeeth];
			rteeth = new Tooth[4][];

			/*
			for(int i=0; i<4;i++)
			{
				rteeth[i]= new Tooth[numTeeth];
			}
			 */



			for (int i = 0; i < t.length; i++) {

				t[i] = new Tooth(startx + (i - numTeeth / 2) * toothSize,
						starty, toothSize, i);
			}

			/*
			for(int i=0; i<4;i++)
			{
				for (int j = 0; j < t.length; j++)
				{
					rteeth[i][j]=new Tooth(startx  + (j - numTeeth / 2) * toothSize,
							(i+1)*toothSize + starty, toothSize, j);
				}

			}
			 */



		}

		void update() {





			if(bbc.isSilent(bbc.instrumentseq) && !bbc.danceSequencer)
			{
				state=2;
			}
			else
			{
				if(bbc.getMapping()==6 || bbc.getMapping()==1)
				{
					int c=0;
					for(int i=0; i<bbc.sfxrseq.length;i++)
					{
						if(bbc.sfxrseq[i])
						{
							c++;
						}
					}
					if(c<3)
					{
						state=2;
					}
					else if(c>=3 && c<=4)
					{
						state=0;
					}
					else
					{
						state=1;
					}					
				}
				else
				{
					state=0;
				}
			}

			if(bbc.getMapping()==6)
			{

				if(thread.prevFaces==0 && bbc.opcvFD.numFaces==0 )
				{
					//start counting
					if(!thread.noFaces)
					{
						thread.noFaceTimer=System.currentTimeMillis();
					}
					thread.noFaces=true;
				}
				else
				{
					//don't bother counting
					thread.noFaces=false;
				}
				if(thread.noFaces)
				{
					if(System.currentTimeMillis()-thread.noFaceTimer>3000)
					{
						state=2;
						bbc.clearRhythm(bbc.sfxrseq);
						bbc.clearRhythm(bbc.instrumentseq);
					}
				}
			}
			thread.prevFaces=(int) bbc.opcvFD.numFaces;



			for (int i = 0; i < t.length; i++) {
				if (state == 0) {
					t[i].ytarget = starty;
				}

				if (state == 1) {
					int off = 0;
					if (i > t.length / 2) {
						off = (int) map(i, 0, t.length / 2, 20, 0);
					} else {
						off = (int) map(i, t.length / 2, t.length, 0, 20);
					}
					t[i].ytarget = starty + off;
				}
				if (state == 2) {
					int off = 0;
					if (i > t.length / 2) {
						off = (int) map(i, 0, t.length / 2, 0, 20);
					} else {
						off = (int) map(i, t.length / 2, t.length, 20, 0);
					}
					t[i].ytarget = starty + off;
				}
			}
		}

		void render() {
		}

		void run(Canvas c) {
			update();
			render();

			for (int i = 0; i < t.length; i++) {
				t[i].run(c);
			}


			////////////////
			/*
			for(int i=0; i<4;i++)
			{
				for (int j = 0; j < t.length; j++)
				{
					rteeth[i][j].run(c);
				}

			}
			 */


		}

		public class Tooth {
			int x, y, sz;

			int ytarget;
			int ID;

			public Tooth(int x, int y, int sz, int ID) {
				this.x = x;
				this.y = y;
				this.sz = sz / 2;
				ytarget = y;
				this.ID = ID;
			}

			void update() {
				if (ytarget < y) {
					y -= 1;
				} else if (ytarget > y) {
					y += 1;
				}
			}

			void render(Canvas c) {
				// rectMode(CENTER);
				// fill(255);
				// rect(this.x,this.y,sz,sz);
				float m=1;
				float f1=m*(float)(2*Math.random()-1);

				if(jiggleBigger)
				{
					c.drawRect(x - sz+f1, y - sz+f1, x + sz+f1, y + sz+f1, paint1);
				}
				else
				{
					c.drawRect(x - sz, y - sz, x + sz, y + sz, paint1);
				}

				if (bbc != null) {

					if (bbc.instrumentseq[ID]) {
						c.drawRect(x - sz, y - sz, x + sz, y + sz, paint2);
					}

					if (bbc.currentIndex == ID) {
						c.drawRect(x - sz, y - sz, x + sz, y + sz,
								redPaintHighlight);
					}

				}

			}

			void run(Canvas c) {
				update();
				render(c);
			}
		}
	}

	// // end teeth

	// start nosebutton
	class NoseButton {

		int sz = 40;

		boolean pressed;
		int x, y;

		NoseButton() {
			x = getWidth() / 2;
			y = getHeight()-getHeight() / 3;
		}

		void run(Canvas c) {

			update();
			render(c);
		}

		void update() {

		}

		void render(Canvas c) {
			if (!pressed)
				c.drawCircle(x, y, sz, blackpaint);
			else
				c.drawCircle(x, y, sz, paint1);

		}

		boolean inButton(float xx, float yy)
		{

			double di = Math.sqrt(Math.pow(xx - this.x, 2)
					+ Math.pow(yy - this.y, 2));
			Log.d("di", ""+ di);
			if(di<this.sz)
			{
				//flashTimer=System.currentTimeMillis();
				return true;
			}

			return false;
		}

	}

	// end nose button


	// start arena

	class Arena
	{

		int x, y;
		int w, h;

		Arena(int x, int y, int w, int h)
		{
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;
		}

		void run(Canvas c)
		{
			update();
			render(c);

		}

		void update()
		{

		}

		void render(Canvas c)
		{
			int leftx=x-w;
			int rightx=x+w;
			int topy=y-h;
			int bottomy=y+h;

			RectF r = new RectF(leftx, topy, rightx, bottomy);
			c.drawRect(r, redPaintHighlight);

			int cx=(int) map(bbc.myposx,0,640,leftx,rightx);
			int cy=(int) map(bbc.myposy,0,480,topy,bottomy);
			//Log.d("rfv", "myposx:"+ bbc.myposx+ "myposy:"+ bbc.myposy+  " cx" + cx + "  cy: " +cy);
			if(cx<leftx)
			{
				cx=leftx;
			}
			if(cx>rightx)
			{
				cx=rightx;
			}
			if(cy<topy)
			{
				cy=topy;
			}
			if(cy>bottomy)
			{
				cy=bottomy;
			}
			c.drawCircle(cx, cy, 3, greenPaint);


			if(simulateVel)
			{
				bbc.myposx+=mySimulatedVel.x;
				bbc.myposy+=mySimulatedVel.y;

				if(bbc.myposx<0)
				{
					bbc.myposx=0;
					mySimulatedVel.x*=-1;
				}
				if(bbc.myposy<0)
				{
					//b.y+=1;
					bbc.myposy=0;
					mySimulatedVel.y*=-1;
				}
				if(bbc.myposx>640)
				{
					//b.x-=1;
					bbc.myposx=640;
					mySimulatedVel.x*=-1;
				}
				if(bbc.myposy>480)
				{
					//b.y-=1;
					bbc.myposy=480;
					mySimulatedVel.y*=-1;
				}

			}
			for(int i=0; i<bbc.otherBots.size(); i ++)
			{
				Bot b=  bbc.otherBots.get(i);
				/////////////////
				if(simulateVel)
				{
					if(simulateVel && simulatedVels!=null)
					{
						//Log.d("","did it get");



						PVector simV = (PVector) simulatedVels.get(i);
						b.x+=simV.x;
						b.y+=simV.y;



						if(touching)
						{
							PVector l = new PVector(b.x,b.y);
							PVector t = new PVector(touchX,touchY);
							PVector d= PVector.sub(t,l);
							d.normalize();
							d.mult(1);

							simV.add(d);	
							simV.limit(2);
						}



						if(b.x<0)
						{
							b.x=0;
							simV.x*=-1;
						}
						if(b.y<0)
						{
							//b.y+=1;
							b.y=0;
							simV.y*=-1;
						}
						if(b.x>640)
						{
							//b.x-=1;
							b.x=640;
							simV.x*=-1;
						}
						if(b.y>480)
						{
							//b.y-=1;
							b.y=480;
							simV.y*=-1;
						}


					}
				}
				////////////

				int bx=(int) map(b.x,0,640,leftx,rightx);
				int by=(int) map(b.y,0,480,topy,bottomy);
				if(bx<leftx)
				{
					bx=leftx;
				}
				if(bx>rightx)
				{
					bx=rightx;
				}
				if(by<topy)
				{
					by=topy;
				}
				if(by>bottomy)
				{
					by=bottomy;
				}

				c.drawCircle(bx, by, 2.5f, blackpaint);


				//int nv =  (int) map(bbc.neighborBound, 0, 640, 0, this.w);
				//c.drawCircle(bx, by, nv, redPaintHighlight);

				if(bbc.myNeighbors.contains(b))
				{
					c.drawCircle(bx, by, 3.0f, redPaintHighlight);
				}
				if(bbc.myExtendedNeighbors.contains(b))
				{
					c.drawCircle(bx, by, 3.0f, coralPaint);
				}




			}



		}



	}

	// end arena

	// MyButton
	class MyButton {
		int sz = 35;

		boolean pressed;
		int x, y;

		String s;

		int type=0;
		int shape=0;

		long flashTimer;

		MyButton(int x, int y, int sz, String s) {
			this.x = x;
			this.y = y;
			this.sz = sz;
			this.s = s;

			flashTimer=System.currentTimeMillis();
		}

		void run(Canvas c) {

			update();
			render(c);
		}

		void update() {

		}

		void render(Canvas c) {
			float ss=blackpaint.measureText(s,0,s.length()); 
			if (pressed) {

				c.drawCircle(x, y, sz, greenPaint);

			} else {

				c.drawCircle(x, y, sz, paint1);
				//c.drawText(s + ": off", x-ss/2, y, blackpaintText);
			}
			c.drawText(s, x-ss/2, y, blackpaintText);

		}

		boolean inButton(float xx, float yy)
		{
			double di = Math.sqrt(Math.pow(xx - this.x, 2)
					+ Math.pow(yy - this.y, 2));
			if(di<this.sz)
			{
				flashTimer=System.currentTimeMillis();
				return true;
			}

			return false;
		}

	}

	// end MyButton


	//start timed message
	class TimedMessage
	{
		long timeout;
		String msg;
		long timer;
		boolean display;
		TimedMessage()
		{
			this.msg="";
			this.timeout=2000;
		}

		void update()
		{

		}
		void render(Canvas c)
		{

			if(display)
			{
				float dt =blackpaintText.measureText(msg);
				c.drawText(msg, getWidth()/2-dt/2, 30, blackpaintText);

				if(System.currentTimeMillis()-timer < timeout)
				{

				}
				else
				{
					display=false;
				}
			}

		}

		void resetTimer()
		{
			timer=System.currentTimeMillis();
		}
		void displayMessage(String s)
		{
			display=true;
			msg=s;
			resetTimer();
		}

		void run(Canvas c)
		{
			update();
			render(c);
		}


	}

	//end timed message

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		//Log.d("onTouch","called");

		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			float x = event.getX();
			float y = event.getY();

			touching=true;
			prevTouchX=touchX;
			prevTouchY=touchY;
			touchX=(int) x;
			touchY=(int) y;
			deltaTouchX=touchX-prevTouchX;
			deltaTouchY=touchY-prevTouchY;

		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getX();
			float y = event.getY();

			touching=true;
			prevTouchX=touchX;
			prevTouchY=touchY;
			touchX=(int) x;
			touchY=(int) y;
			deltaTouchX=touchX-prevTouchX;
			deltaTouchY=touchY-prevTouchY;
			/*
			 * Point point = new Point(); point.x = event.getX(); point.y =
			 * event.getY(); point.vx = (float) (Math.random()-.5); point.vy =
			 * (float) (Math.random()-.5); thread.points.add(point);
			 * invalidate(); Log.d("robotfaceView", "point: " + point);
			 */

			if(thread.modeButton.inButton(x, y))
			{
				this.thread.modeButton.pressed = true;
				Log.d("hit mode button", "x:" + x + " y:"+ y);
				cnt++;
				this.thread.robotEyes.drunk = false;

				if(cnt%3==0)
				{
					cnt=0;
				}
				if(cnt==2)
				{
					cnt=2;
					bbc.setMapping(6);//was 6
					thread.message.displayMessage("mapping: " + cnt + "  how close your face is...neighbor");

				}
				else
				{
					bbc.setMapping(cnt);
					if(cnt==1)
						thread.message.displayMessage("mapping: " + cnt + "angle");
					else
					{
						thread.message.displayMessage("mapping: " + cnt + " nothing..");
					}

				}

			}
			else
			{
				this.thread.modeButton.pressed = false;
			}

			if(thread.danceButton.inButton(x, y))
			{
				this.thread.danceButton.pressed = true;
				Log.d("hit dance button", "x:" + x + " y:"+ y);
				bbc.toggleDance();

				if(bbc.danceSequencer)
				{
					bbc.setMapping(0);

					bbc.randomMirrorSP();


				}
				else
				{
					bbc.setMapping(cnt);
					bbc.clearAllMovement();
					bbc.stop();
				}


			}
			else
			{
				this.thread.danceButton.pressed = false;
			}




			// check if x y near nose
			double f = Math.sqrt(Math.pow(x - thread.robotNose.x, 2)
					+ Math.pow(y - thread.robotNose.y, 2));
			if (f < thread.robotNose.sz) {
				bbc.setMapping(0);
				bbc.instrumentseq[bbc.currentIndex] = !bbc.instrumentseq[bbc.currentIndex];
				bbc.sfxrseq[bbc.currentIndex] = !bbc.sfxrseq[bbc.currentIndex];
				Log.d("robotfaceView", "f: " + f);

				// this coudl be..but shoudl not be..should be depenednt on
				// state it hink
				this.thread.robotEyes.e1.tt = "manual input";
				this.thread.robotEyes.e2.tt = "manual input";

				this.thread.robotNose.pressed = true;
				this.thread.robotEyes.drunk = true;
				this.thread.robotEyes.cbuff = 10;

				thread.message.displayMessage("manual input");

				jiggleTimer=System.currentTimeMillis();
				jiggle=true;

			} else {
				// shoudl not be.
				//this.thread.robotNose.pressed = false;
				/*
				this.thread.robotEyes.e1.tt = "angle mapping";
				this.thread.robotEyes.e2.tt = "angle mapping";

				bbc.setMapping(1);
				Log.d("robotfaceView", "f: " + f);
				this.thread.robotEyes.drunk = false;
				this.thread.robotEyes.cbuff = 0;
				this.thread.useCamera = false;
				 */
			}



			// check if hit eye1
			/*
			 * 

			double deye = Math.sqrt(Math.pow(x - thread.robotEyes.e1.x, 2)
					+ Math.pow(y - thread.robotEyes.e1.y, 2));
			if (deye < thread.robotEyes.sz) {
				thread.useCamera = !thread.useCamera;
				if (thread.useCamera) {
					bbc.setMapping(6);
				} else {
					bbc.setMapping(0);
				}
			}

			// check if hit eye2
			double deye2 = Math.sqrt(Math.pow(x - thread.robotEyes.e2.x, 2)
					+ Math.pow(y - thread.robotEyes.e2.y, 2));
			if (deye2 < thread.robotEyes.sz) {
				bbc.danceSequencer = !bbc.danceSequencer;

				if (bbc.danceSequencer) {
					bbc.randomMirrorSP();

				} else {
					bbc.clearAllMovement();
					bbc.stop();
				}

			}
			 */

			// check dance button and camera button
			/*
			if (thread.danceButton.inButton(x,y)) {
				thread.danceButton.pressed = !thread.danceButton.pressed;
				if (thread.danceButton.pressed) {
					bbc.setMapping(6);
					bbc.danceSequencer = true;
				} else {
					bbc.setMapping(1);
					bbc.danceSequencer = false;
				}
			}

			if (thread.cameraButton.inButton(x, y)) {
				thread.cameraButton.pressed = !thread.cameraButton.pressed;
				if (thread.cameraButton.pressed) {
					bbc.setMapping(6);
					thread.useCamera = true;
				} else {
					bbc.setMapping(1);
					thread.useCamera = false;
				}
			}
			 */



			if(thread.tempdown.inButton(x, y))
			{

				bbc.tempoDown();
				thread.message.displayMessage("tempo: " + bbc.getTempo());
			}
			if(thread.tempup.inButton(x, y))
			{
				bbc.tempoUp();
				thread.message.displayMessage("tempo: " + bbc.getTempo());
			}

			if(thread.divup.inButton(x, y))
			{
				bbc.divUpFloat();
				thread.message.displayMessage("div: " + bbc.getDiv());
			}

			if(thread.divdown.inButton(x, y))
			{
				bbc.divDownFloat();
				thread.message.displayMessage("div: " + bbc.getDiv());
			}


			return true;

		}

		if (event.getAction() == MotionEvent.ACTION_UP) {

			/*
			thread.robotTeeth.state++;
			if (thread.robotTeeth.state > 3) {
				thread.robotTeeth.state = 0;
			}
			 */


			Log.d("robotfaceView", "motionUP: " + thread.robotTeeth.state);
			this.thread.robotNose.pressed = false;

			float x = event.getX();
			float y = event.getY();
			prevTouchX=touchX;
			prevTouchY=touchY;
			touchX=(int) x;
			touchY=(int) y;
			deltaTouchX=touchX-prevTouchX;
			deltaTouchY=touchY-prevTouchY;

			touching=false;



			return true;

		}

		return false;
	}


	public void doJiggle()
	{
		jiggleTimer=System.currentTimeMillis();
		jiggle=true;

	}

	public void doJiggleBigger()
	{
		jiggleTimerBigger=System.currentTimeMillis();
		jiggleBigger=true;

	}
	public float map(float value, float istart, float istop, float ostart,
			float ostop) {
		return ostart + (ostop - ostart)
		* ((value - istart) / (istop - istart));
	}


	public void doSim()
	{

	}
	
	
	
	


}
