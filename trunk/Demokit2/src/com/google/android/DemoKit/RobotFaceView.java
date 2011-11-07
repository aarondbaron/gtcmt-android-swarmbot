package com.google.android.DemoKit;


import java.util.ArrayList;
import java.util.List;


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

public class RobotFaceView extends SurfaceView implements OnTouchListener, SurfaceHolder.Callback, Runnable{

	Paint paint1,paint2,blackpaint;

	private SurfaceHolder       mHolder;

	public BoeBotController bbc;
	public FaceController fc;
	public BeatTimer bt;

	RobotFaceViewThread thread;

	public RobotFaceView(Context context) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		// TODO Auto-generated constructor stub
		init(mHolder,context);
	}

	public RobotFaceView(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		init(mHolder,context);
	}


	public RobotFaceView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context,attrs, defStyle);
		mHolder = getHolder();
		mHolder.addCallback(this);
		init(mHolder,context);
	}


	void init(SurfaceHolder holder, Context context)
	{
		paint1 = new Paint();
		paint2 = new Paint();
		blackpaint = new Paint();

		paint1.setColor(Color.WHITE);
		paint1.setAntiAlias(true);

		paint2.setColor(Color.BLUE);
		paint2.setAntiAlias(true);
		paint2.setStyle(Paint.Style.STROKE);

		blackpaint.setColor(Color.BLACK);
		blackpaint.setAntiAlias(true);



		thread  = new RobotFaceViewThread(holder, context);
		this.setOnTouchListener(this);
	}


	@Override
	public void onDraw(Canvas canvas) {
		//canvas.drawLine(0, 0, 20, 20, paint);
		//canvas.drawLine(20, 0, 0, 20, paint);
		//paint.
		//canvas.drawCircle(20, 40, 10000, paint);
		//canvas.drawRect(0, 0, 20, 20, paint);

		if(this.bbc!=null)
		{
			int sz=30;
			for(int i=0; i<bbc.sfxrseq.length;i++)
			{
				if(bbc.sfxrseq[i])
					canvas.drawRect(0+20*i, 0*sz, 20+20*i, 1*sz, paint1);
				else
					canvas.drawRect(0+20*i, 0*sz, 20+20*i, 1*sz, paint2);

				//c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for(int i=0; i<bbc.instrumentseq.length;i++)
			{
				if(bbc.instrumentseq[i])
					canvas.drawRect(0+20*i, 1*sz, 20+20*i, 2*sz, paint1);
				else
					canvas.drawRect(0+20*i, 1*sz, 20+20*i, 2*sz, paint2);

				//c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for(int i=0; i<bbc.fseq.length;i++)
			{
				if(bbc.fseq[i])
					canvas.drawRect(0+20*i, 3*sz, 20+20*i, 4*sz, paint1);
				else
					canvas.drawRect(0+20*i, 3*sz, 20+20*i, 4*sz, paint2);

				//c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for(int i=0; i<bbc.bseq.length;i++)
			{
				if(bbc.bseq[i])
					canvas.drawRect(0+20*i, 4*sz, 20+20*i, 5*sz, paint1);
				else
					canvas.drawRect(0+20*i, 4*sz, 20+20*i, 5*sz, paint2);

				//c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for(int i=0; i<bbc.rseq.length;i++)
			{
				if(bbc.rseq[i])
					canvas.drawRect(0+20*i, 5*sz, 20+20*i, 6*sz, paint1);
				else
					canvas.drawRect(0+20*i, 5*sz, 20+20*i, 6*sz, paint2);

				//c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

			for(int i=0; i<bbc.lseq.length;i++)
			{
				if(bbc.lseq[i])
					canvas.drawRect(0+20*i, 6*sz, 20+20*i, 7*sz, paint1);
				else
					canvas.drawRect(0+20*i, 6*sz, 20+20*i, 7*sz, paint2);

				//c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);

			}

		}

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

		thread.robotEyes= new Eyes();
		thread.robotTeeth = new Teeth(getWidth()/2,getHeight()-getHeight()/4);
		thread.setRunning(true);
		thread.start();

		Log.d("SURFACECREATED","W:" + getWidth() + "  H:" + getHeight());



	}

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
		public RobotFaceViewThread(SurfaceHolder holder, Context context) 
		{

			mSurfaceHolder = holder;
			//mHandler = handler;
			Context mContext = context;
			mRes = context.getResources();

			//robotEyes= new Eyes();

		}

		@Override
		public void run()
		{
			Canvas c;// = mSurfaceHolder.lockCanvas(null);
			//Log.d("in thread", "in run: ");
			while (running) {
				//Log.d("in thread", "in run: ");
				c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						render(c);
						//_panel.onDraw(c);
						robotEyes.run(c);
						robotTeeth.run(c);
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
			//render(c);
			//update();

			//render(this.);
		}

		public void setRunning(boolean run) {
			running = run;
		}

		public void update()
		{

		}
		public void render(Canvas canvas)
		{
			canvas.drawColor(Color.GRAY);

			if(true)
			{
				try 
				{
					for (Point point : points)
					{


						canvas.drawCircle(point.x, point.y, 5, paint1);

						point.update();

						// Log.d(TAG, "Painting: "+point);
					}




				} catch(Exception e)
				{


				}
			}

			for (int i =0 ; i<1; i++)				
			{

				//canvas.drawRect(getWidth()/2-20, getHeight()/2-20, getWidth()/2+20, getHeight()/2+20, paint);
			}

			/*
			for (int i =0 ; i<sequencer.length; i++)				
			{
				if(beatTimerThread.generalIndex%16==i)
				{
					sequencer[i].highlight=true;
					if(sequencer[i].on && !sequencer[i].playedOnce )
					{
						if(useSample)
						{
						_soundPool.play(_playbackFile, 1, 1, 0, 0, 1);
						}
						else
						{
							atest.replay();
							//atest.replayRandom();
						}
						sequencer[i].playedOnce=true;
					}
				}
				else
				{
					sequencer[i].highlight=false;
					sequencer[i].playedOnce=false;
				}

				sequencer[i].run(canvas); 
			}

			mySlider.run(canvas);
			//wv.run(canvas);


			 */


		}

	}

	class Point {
		float x, y;

		float vx, vy;

		@Override
		public String toString() {
			return x + ", " + y;
		}

		public void update()
		{
			x+=vx;
			y+=vy;

			if(x<0 || x> getWidth())
				vx*=-1;
			if(y<0 || y> getHeight())
				vy*=-1;


		}
	}

	class Eyes
	{
		long blinkTimer;

		Eye e1,e2;

		boolean closeOpen;
		boolean closeState;
		boolean openState;
		long openTimer,closeTimer;
		int brate=16;

		int sz=100;
		int tsz=50;

		boolean showlog=false;
		public Eyes()
		{

			int w1 = getWidth();
			int h1 = getHeight();
			e1 = new Eye(w1/4,h1/2);
			e2 = new Eye(w1-w1/4,h1/2);
			Log.d("WHAT ARE W H","W:" + w1 + "  H:" + h1);



			openTimer=System.currentTimeMillis();
			closeTimer=System.currentTimeMillis();
		}

		void resetEyePositions(int x, int y)
		{
			e1 = new Eye(x/4,y/2);
			e2 = new Eye(x-x/4,y/2);

		}

		void update()
		{
			if(closeOpen && !(closeState || openState))
			{
				tsz+=brate;
				if(showlog)
					Log.d("eye","closing");
			}

			if(!closeOpen && !(closeState || openState))
			{
				tsz-=brate;
				if(showlog)
					Log.d("eye","opening");

			}


			if(tsz>sz)
			{
				tsz=sz;
				closeOpen=true;

				closeState=true;
				closeTimer=System.currentTimeMillis();
				if(showlog)
					Log.d("eye","closed");
			}
			if(tsz<0)
			{
				tsz =0;
				closeOpen=false;
				openState=true;
				openTimer=System.currentTimeMillis();
				if(showlog)
					Log.d("eye","open");
			}


			if(openState)
			{
				if(System.currentTimeMillis()-openTimer>750)
				{
					openState=false; 
					closeOpen=true;
					if(showlog)
						Log.d("eye","openstate finished");
				}
			}

			if(closeState)
			{
				if(System.currentTimeMillis()-closeTimer>750)
				{
					closeState=false; 
					closeOpen=false;
					if(showlog)
						Log.d("eye","closeState finished");
				}
			}
		}
		void render()
		{			
		}
		void run(Canvas c)
		{
			update();
			render();

			e1.run(c);
			e2.run(c);
		}

		//inner class for pair of eyes
		class Eye
		{


			int x,y;





			Pupil p;
			
			String text = "";
			Eye(int x,int y)
			{
				this.x=x;
				this.y=y;
				p=new Pupil();



			}

			void update()
			{

			}

			void render(Canvas c)
			{
				//eyelid//always drawn
				//fill(0);
				//ellipse(x,y,sz,sz);

				float leftx = x-sz;
				float topy = y-sz;
				float rightx = x+sz;
				float bottomy = y+sz;
				RectF ovalBounds = new RectF(leftx, topy, rightx, bottomy);
				c.drawOval(ovalBounds, blackpaint);


				//eye
				//fill(255);
				//ellipse(x,y,sz,sz-tsz);

				float leftx2 = x-sz;
				float topy2 = y-(sz-tsz);
				float rightx2 = x+sz;
				float bottomy2 = y+(sz-tsz);
				RectF ovalBounds2 = new RectF(leftx2, topy2, rightx2, bottomy2);
				c.drawOval(ovalBounds2, paint1);
			}

			void run(Canvas c)
			{
				update();
				render(c);
				p.run(c);

			}


			//innerclass for eye
			class Pupil
			{
				Pupil()
				{


				}
				void update()
				{
				}

				void render(Canvas c)
				{
					//fill(0);
					//ellipse(x,y,.2*sz,.2*sz);

					float leftx = x-.2f*sz;
					float topy = y-.2f*sz;
					float rightx = x+.2f*sz;
					float bottomy = y+.2f*sz;
					RectF ovalBounds = new RectF(leftx, topy, rightx, bottomy);
					c.drawOval(ovalBounds, blackpaint);
				}

				void run(Canvas c)
				{
					update();
					render(c);
				}
			}

		}


	}
	////end ends


	//start teeth
	class Teeth 
	{
		int numTeeth=16;

		Tooth[] t;
		int toothSize=12;
		int startx, starty;

		int state=0;
		public Teeth(int xx,int yy)
		{
			startx=xx;
			starty=yy;

			t=new Tooth[numTeeth];
			for(int i=0; i <t.length;i++)
			{

				t[i]= new Tooth(startx+(i-numTeeth/2)*toothSize, starty, toothSize,i);
			}
		}

		void update()
		{
			for(int i=0; i <t.length;i++)
			{
				if(state==0)
				{
					t[i].ytarget=starty;
				}
				
				if(state==1)
				{
					int off=0;
					if(i>t.length/2)
					{
						off=(int) map(i,0,t.length/2,20,0);
					}
					else
					{
						off=(int) map(i,t.length/2,t.length,0,20);
					}
					t[i].ytarget=starty+off;
				}
				if(state==2)
				{
					int off=0;
					if(i>t.length/2)
					{
						off=(int) map(i,0,t.length/2,0,20);
					}
					else
					{
						off=(int) map(i,t.length/2,t.length,20,0);
					}
					t[i].ytarget=starty+off;
				}
			}
		}

		void render()
		{
		}

		void run(Canvas c)
		{
			update();
			render();

			for(int i=0; i <t.length;i++)
			{
				t[i].run(c);
			}
		}

		public class Tooth
		{
			int x,y,sz;

			int ytarget;
			int ID;
			public Tooth(int x, int y, int sz, int ID)
			{
				this.x=x;
				this.y=y;
				this.sz=sz/2;
				ytarget=y;
				this.ID=ID;
			} 



			void update()
			{
				if(ytarget<y)
				{
					y-=1;
				}
				else
					if(ytarget>y)
					{
						y+=1;
					}
			}

			void render(Canvas c)
			{
				//rectMode(CENTER);
				//fill(255);
				//rect(this.x,this.y,sz,sz);
				
				
				c.drawRect(x-sz, y-sz, x+sz, y+sz, paint1);
				
				if(bbc!=null)
				{
					if(bbc.currentIndex==ID)
					{
						c.drawRect(x-sz, y-sz, x+sz, y+sz, blackpaint);
					}
					
					if(bbc.instrumentseq[ID])
					{
						c.drawRect(x-sz, y-sz, x+sz, y+sz, paint2);
					}
					
				}
				
			}

			void run(Canvas c)
			{
				update();
				render(c);
			}
		}
	}



	//// end teeth
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			Point point = new Point();
			point.x = event.getX();
			point.y = event.getY();
			point.vx = (float) (Math.random()-.5);
			point.vy = (float) (Math.random()-.5);
			thread.points.add(point);
			invalidate();
			Log.d("robotfaceView", "point: " + point);
			return true;
			
		}

		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			
			thread.robotTeeth.state++;
			if(thread.robotTeeth.state>3)
			{
				thread.robotTeeth.state=0;
			}
			Log.d("robotfaceView", "motionUP: " +thread.robotTeeth.state );
			return true;

		}

		return false;
	}


	public float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}

}




