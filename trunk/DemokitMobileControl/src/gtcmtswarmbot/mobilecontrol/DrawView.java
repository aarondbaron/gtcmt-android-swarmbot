package gtcmtswarmbot.mobilecontrol;




import  gtcmtswarmbot.mobilecontrol.groupings.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

import gtcmtswarmbot.mobilecontrol.DrawView.Cursor;
import gtcmtswarmbot.mobilecontrol.DrawView.NumberPick;
import gtcmtswarmbot.mobilecontrol.enums.Mapping;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnLongClickListener;

public class DrawView extends SurfaceView implements OnTouchListener , SurfaceHolder.Callback, OnLongClickListener{

	private Paint paint1;
	private Paint paint2;
	private Paint blackPaint;
	private Paint blackPaintText;
	private Paint whitePaintText;
	private Paint redPaintHighlight;
	private Paint greenPaintHighlight;
	private Paint greenPaint;
	private Paint yellowPaint;
	private Paint whitePaintStroke;
	DrawViewThread thread;

	SomeController bbc;

	private SurfaceHolder mHolder;

	DemokitMobileControlActivity mActivity;
	public String mode;

	public long tugMoveTimer;

	public long inspectTimer;
	boolean inspecting;
	//boolean vibrateOnce;

	Bot botToInspect;

	Vibrator vib;

	boolean surfCreated;
	boolean simulateVel;

	Vector simulatedVels;

	int screenWidth;
	int screenHeight;


	boolean touching;
	int touchX,touchY;
	int prevTouchX,prevTouchY;
	int deltaTouchX,deltaTouchY;
	int initialTouchX,initialTouchY;

	int selectedI,selectedJ;
	long longTouchTimer;

	List<List<String>> groupings;

	public boolean doTraverse;
	public long traverseTimer;
	public long traverseInterval=500;

	public DrawView(Context context, SomeController bbc) {
		//super(context);
		// TODO Auto-generated constructor stub



		super(context);

		mActivity = (DemokitMobileControlActivity) context;
		mActivity.registerForContextMenu(this);	

		this.bbc=bbc;

		mHolder = getHolder();
		mHolder.addCallback(this);
		// TODO Auto-generated constructor stub
		init(mHolder, context);

		mode="";

		tugMoveTimer =System.currentTimeMillis();
		inspectTimer = System.currentTimeMillis();

		botToInspect=null;




		vib = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thread.setRunning(true);
		thread.start();

		Log.d("SURFACECREATED", "W:" + getWidth() + "  H:" + getHeight());

		screenWidth=getWidth();
		screenHeight = getHeight();
		float w=640;
		float h =480;

		float factor = w/h;



		//int woff=10;
		//int hoff=10;
		//thread.arena = new Arena(getWidth()/2, getHeight()/2,getWidth()/2-woff,getHeight()/2-hoff);		
		thread.arena = new Arena(getWidth()/2, getHeight()/2, (int) ((getWidth()/2)/factor) ,(int)( getHeight()/2) );		
		thread.sequencer = new Sequencer(this);



		surfCreated=true;

		thread.songMaker = new SongMaker(this);

		thread.numberPick = new NumberPick();



		/*
		doTest();
		this.traverseTimer=System.currentTimeMillis();
		this.doTraverse=false;
		 */



	}

	void doTest()
	{
		this.simulateVel=true;
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
			bbc.allBots.add(testBot);
		}

		bbc.registry = new Vector<List<String>>();
		for(int i=0;i<bbc.allBots.size();i++)
		{
			bbc.registry.add(new Vector<String>());
		}


		if(simulateVel)
		{
			simulatedVels = new Vector();
			for(int i=0;i<bbc.allBots.size();i++)
			{
				PVector v = new PVector((float)Math.random(),(float)Math.random());
				v.normalize();
				v.mult(2);
				simulatedVels.add(v);
			}
		}


	}





	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		boolean defaultResult = v.onTouchEvent(event);

		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) 
		{
			touching=true;

			float x = event.getX();
			float y = event.getY();
			thread.arena.cursor.x=(int) x;
			thread.arena.cursor.y=(int) y;


			prevTouchX=touchX;
			prevTouchY=touchY;
			touchX=(int) x;
			touchY=(int) y;
			deltaTouchX=touchX-prevTouchX;
			deltaTouchY=touchY-prevTouchY;
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				initialTouchX=touchX;
				initialTouchY=touchY;
			}

			//Log.d("xy","touch:"+ touchX + "," + touchY+ " prevTouch:"+ prevTouchX + "," + prevTouchY+ " delta:"+ deltaTouchX + "," + deltaTouchY);

			if(this.thread.arenaSongmaker)
			{
				float[] fff= thread.songMaker.g.pointInside(touchX,touchY);
				int i =Math.round( fff[0]);
				int j = Math.round( fff[1]);

				if(i>thread.songMaker.g.gridVals.length)
				{

				}
				else
				{
					Log.d("coords","" + i + "," + j);
					if (i<0)
					{
						i=0;
					}
					if (i>thread.songMaker.g.ny-1)
					{
						i=thread.songMaker.g.ny-1;
					}
					if (j<0)
					{
						j=0;
					}
					if (j>thread.songMaker.g.nx-1)
					{
						j=thread.songMaker.g.nx-1;
					}



					selectedI=i;
					selectedJ=j;


					////
					if (this.thread.songMaker.state.equals("lock"))
					{
						if (!this.thread.songMaker.trigValLock)
						{
							if (this.thread.songMaker.g.gridVals[i][j])
							{
								this.thread.songMaker.trigVal=false;
								this.thread.songMaker.trigValLock=true;
							}
							else
							{
								this.thread.songMaker.trigVal=true;
								this.thread.songMaker.trigValLock=true;
							}
						}
						this.thread.songMaker.g.setGridCell(i, j, this.thread.songMaker.trigVal);
						CMeasure c= this.bbc.myComposition.currentMeasure;
						this.thread.songMaker.g.gridToCMeasure();
					}
				}

				/*
				float ff= thread.songMaker.cp.pointInside(touchX);
				if(this.touchY>this.screenHeight/2 && this.touchY<screenHeight)
				{
					int c= (int) Math.round( ff );
					if(c<thread.songMaker.cp.keyVals.size()&&c>0)
					{
						//Boolean b = (Boolean) thread.songMaker.cp.keyVals.get(c);
						//b = new Boolean(true);
					}


				}
				 */


			}

			if(this.mode.equals("hitMode"))
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					mActivity.client.sendMessage("controller,"+ 803 ) ;
				}
			}

			if(mode.equals("drawn")||mode.equals("Path"))
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(thread.arena.isInside(x,y))
					{
						thread.addLock=true;
						thread.lines.clear();
						thread.addLock=false;
						thread.lines.add(new PVector(x,y));
					}
				}
				else
				{
					if(thread.arena.isInside(x,y))
					{

						thread.lines.add(new PVector(x,y));
					}
				}

			}

			if(mode.equals("TugMove"))
			{
				if(System.currentTimeMillis()-tugMoveTimer>125)
				{
					int xx = (int) map(thread.arena.cursor.x,thread.arena.leftx,thread.arena.rightx,0,640);
					int yy = (int) map(thread.arena.cursor.y,thread.arena.topy,thread.arena.bottomy,0,480);
					mActivity.client.sendMessage("controller,"+ 9988 + "," + xx + "," + yy) ;
					tugMoveTimer = System.currentTimeMillis();
				}
			}
			if(mode.equals("AvatarMove"))
			{
				if(System.currentTimeMillis()-tugMoveTimer>125)
				{
					int xx = (int) map(thread.arena.cursor.x,thread.arena.leftx,thread.arena.rightx,0,640);
					int yy = (int) map(thread.arena.cursor.y,thread.arena.topy,thread.arena.bottomy,0,480);
					mActivity.client.sendMessage("controller,"+ 9987 + "," + xx + "," + yy) ;
					tugMoveTimer = System.currentTimeMillis();
				}
			}
			if(mode.equals("RotateToAngle"))
			{
				if(System.currentTimeMillis()-tugMoveTimer>125)
				{
					int xx = (int) map(thread.arena.cursor.x,thread.arena.leftx,thread.arena.rightx,0,640);
					int yy = (int) map(thread.arena.cursor.y,thread.arena.topy,thread.arena.bottomy,0,480);

					float rx = thread.arena.cursor.x-getWidth()/2;
					float ry = thread.arena.cursor.y-getHeight()/2;

					if(rx==0)
					{
						rx=.0000001f;
					}

					float ang = (float) Math.atan2(ry, rx);
					//ang = (float) Math.toDegrees(ang);

					mActivity.client.sendMessage("controller,"+ 9985 + "," + ang) ;

					tugMoveTimer = System.currentTimeMillis();
				}
			}

			if(this.mode.equals("editSequencer"))
			{

			}

			boolean vibrateOnceCheck=false;
			/*
			if(vibrateOnce)
			{

			}
			else
			{
				this.inspectTimer=System.currentTimeMillis();
			}
			 */

			if(this.mode.equals("Inspect"))
			{


				for(int i=0;i<bbc.allBots.size();i++)
				{
					Bot b = (Bot) bbc.allBots.get(i);

					int bx=(int) map(b.x,0,640,this.thread.arena.leftx,this.thread.arena.rightx);
					int by=(int) map(b.y,0,480,this.thread.arena.topy,this.thread.arena.bottomy);

					if(b.isNearCursor(x,y, bx, by))
					{
						//Log.d("inspect","x: " + x + "  y:" + y  + "||bx: " + b.x + "  by:" + b.y);
						b.inspect=true;

						if(!b.vibrateOnce)
						{
							vib.vibrate(50);
							b.vibrateOnce=true;
						}
						else
						{
							this.inspectTimer=System.currentTimeMillis();
						}
						if(System.currentTimeMillis()-this.inspectTimer>1000)
						{

							//inspecting=true;
							Log.d("sending query","bot " + b.ID);
							this.inspectTimer=System.currentTimeMillis();
							mActivity.client.sendMessage("com,"+ mActivity.client.myID + "," + b.ID + "," + "query" + "," + "nnnn");


						}
					}
					else
					{
						b.inspect=false;
						b.vibrateOnce=false;
						//Log.d("fail inspect","x: " + x + "  y:" + y + "||bx: " + b.x + "  by:" + b.y);
					}
				}

			}



			if(inspecting)
			{

			}



			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			float x = event.getX();
			float y = event.getY();

			prevTouchX=touchX;
			prevTouchY=touchY;
			touchX=(int) x;
			touchY=(int) y;
			deltaTouchX=touchX-prevTouchX;
			deltaTouchY=touchY-prevTouchY;

			touching=false;


			if(this.thread.arenaSongmaker)
			{
				if(this.thread.songMaker.state.equals("lock")  )
				{
					this.thread.songMaker.trigValLock=false;
				}

				if(this.thread.songMaker.state.equals("release"))
				{

					float[] fff= thread.songMaker.g.pointInside(touchX,touchY);
					int i =Math.round( fff[0]);
					int j = Math.round( fff[1]);



					///////////////////
					if(i>thread.songMaker.g.gridVals.length)
					{

					}
					else
					{
						Log.d("coords","" + i + "," + j);
						if (i<0)
						{
							i=0;
						}
						if (i>thread.songMaker.g.ny-1)
						{
							i=thread.songMaker.g.ny-1;
						}
						if (j<0)
						{
							j=0;
						}
						if (j>thread.songMaker.g.nx-1)
						{
							j=thread.songMaker.g.nx-1;
						}



						selectedI=i;
						selectedJ=j;


						///////////

						boolean val = this.thread.songMaker.g.gridVals[i][j];
						this.thread.songMaker.g.setGridCell(i, j, !val);

						if(!val)
						{
							//mActivity.client.sendMessage("controller,"+ 803 + "," + i);
							mActivity.client.sendMessage("controller,"+ 903 + "," + i + "," + j);
						}
						else
						{
							mActivity.client.sendMessage("controller,"+ 9033 + "," + i + "," + j);
						}


						//this.thread.songMaker.g.setGridCell(i, j, this.thread.songMaker.trigVal);
						CMeasure c= this.bbc.myComposition.currentMeasure;
						this.thread.songMaker.g.gridToCMeasure();
					}




				}

			}

			if(thread.numberPick.show)
			{
				thread.numberPick.bTimeInterval=thread.numberPick.startTime;
				thread.numberPick.bTimer=System.currentTimeMillis();

				if(thread.numberPick.pick.isPressed)
				{
					thread.numberPick.pickit();
				}
			}


			thread.arena.fixCursor();

			inspecting=false;
			//vibrateOnce=false;

			if(this.mode.equals("Move"))
			{
				for(int i=0;i<bbc.allBots.size();i++)
				{
					//mActivity.client.sendMessage("move,"+x+","+y+","+i);
					//mActivity.client.sendMessage("998,"+x+","+y+","+i);
					//bbc.mActivity.client.sendMessage("vel,"+ bbc.mActivity.client.myID + "," + new DecimalFormat("#.##").format(v.x) + "," + new DecimalFormat("#.##").format(v.y)) ;
					int xx = (int) map(thread.arena.cursor.x,thread.arena.leftx,thread.arena.rightx,0,640);

					int yy = (int) map(thread.arena.cursor.y,thread.arena.topy,thread.arena.bottomy,0,480);

					mActivity.client.sendMessage("controller,"+ 998 + "," + xx + "," + yy) ;

					Bot b = (Bot) bbc.allBots.get(i);
					b.vibrateOnce=false;
				}
			}



			if(this.mode.equals("TugMove"))
			{
				mActivity.client.sendMessage("controller,"+ 9988) ;
			}
			if(this.mode.equals("AvatarMove"))
			{
				mActivity.client.sendMessage("controller,"+ 9987) ;
			}

			if(mode.equals("RotateToAngle"))
			{
				mActivity.client.sendMessage("controller,"+ 9985 ) ;	 
			}

			if(this.mode.equals("drawn"))
			{

			}
			if(this.mode.equals("Path"))
			{

			}

			if(this.mode.equals("editSequencer"))
			{

				thread.sequencer.checkInside((int)x, (int)y);
				mActivity.client.sendMessage("controller,"+ 801 + "," + this.thread.sequencer.getMySequence());

			}

		}



		return false;
	}


	void init(SurfaceHolder holder, Context context) {
		paint1 = new Paint();
		paint2 = new Paint();
		blackPaint = new Paint();
		blackPaintText = new Paint();
		whitePaintText = new Paint();
		redPaintHighlight = new Paint();
		greenPaintHighlight = new Paint();

		greenPaint = new Paint();
		yellowPaint = new Paint();
		whitePaintStroke = new Paint();

		paint1.setColor(Color.WHITE);
		paint1.setAntiAlias(true);

		paint2.setColor(Color.BLUE);
		paint2.setAntiAlias(true);
		// paint2.setStyle(Paint.Style.STROKE);

		blackPaint.setColor(Color.BLACK);
		blackPaint.setAntiAlias(true);

		blackPaintText.setTextSize(25);
		blackPaintText.setColor(Color.BLACK);
		blackPaintText.setAntiAlias(true);

		whitePaintText.setTextSize(20);
		whitePaintText.setColor(Color.WHITE);
		whitePaintText.setAntiAlias(true);

		redPaintHighlight.setColor(Color.RED);
		redPaintHighlight.setAntiAlias(true);
		redPaintHighlight.setStyle(Paint.Style.STROKE);

		greenPaintHighlight.setColor(Color.RED);
		greenPaintHighlight.setAntiAlias(true);
		greenPaintHighlight.setStyle(Paint.Style.STROKE);

		greenPaint.setColor(Color.GREEN);
		greenPaint.setAntiAlias(true);

		yellowPaint.setColor(Color.YELLOW);
		yellowPaint.setAntiAlias(true);

		whitePaintStroke.setColor(Color.WHITE);
		whitePaintStroke.setAntiAlias(true);
		whitePaintStroke.setStyle(Paint.Style.STROKE);

		thread = new DrawViewThread(holder, context);
		this.setOnTouchListener(this);
		this.setOnLongClickListener(this);
	}



	@Override
	public void onDraw(Canvas canvas) {

		invalidate();
	}

	class DrawViewThread extends Thread {

		public NumberPick numberPick;
		public Sequencer sequencer;
		private SurfaceHolder mSurfaceHolder;
		private Handler mHandler;
		private Resources mRes;
		private boolean running;

		Arena arena;


		Vector lines;
		public boolean showSequencer;
		public boolean addLock;

		public boolean arenaSongmaker=false;

		SongMaker songMaker;


		DrawViewThread(SurfaceHolder holder, Context context) 
		{

			mSurfaceHolder = holder;
			//mHandler = handler;
			Context mContext = context;
			mRes = context.getResources();

			lines = new Vector();

		}

		public void setRunning(boolean run) {
			running = run;
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

						if(!arenaSongmaker)
						{
							arena.run(c);
						}
						else
						{
							songMaker.run(c);
							//Log.d("drawview","songmker running");
						}

						if(numberPick.show)
						{
							numberPick.run(c);
						}

						if(showSequencer)
						{
							sequencer.run(c);
						}
						//_panel.onDraw(c);

						if(!mActivity.client.initialConnect)
						{
							if(System.currentTimeMillis()-mActivity.client.initialConnectTimer>4000)
							{
								mActivity.client.doStuff();					
								mActivity.client.initialConnect=true;
							}
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

		}

	}
	public void render(Canvas canvas) {
		canvas.drawColor(Color.DKGRAY);
	}
	//start cursor

	class Cursor
	{
		int x,y;

		int mn,mx, sz, amt;

		boolean active;

		boolean growShrink;

		Cursor()
		{
			active=true;
			x=getWidth()/2;
			y=getHeight()/2;				
			mn=10;
			mx=25;
			sz=10;
			amt=1;
			growShrink=true;
		}


	}
	//end cursor
	// start arena

	class Arena
	{

		int x, y;
		int w, h;

		Cursor cursor;

		int leftx=x-w;
		int rightx=x+w;
		int topy=y-h;
		int bottomy=y+h;

		Arena(int x, int y, int w, int h)
		{
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;

			leftx=x-w;
			rightx=x+w;
			topy=y-h;
			bottomy=y+h;

			cursor = new Cursor();
		}

		public boolean isInside(float x2, float y2) {
			// TODO Auto-generated method stub
			if(x2> leftx && x2< rightx && y2> topy && y2 <bottomy)
			{
				Log.d("isinside", "x: " + x2 + ", y:" + y2 + " leftx:" + leftx);
				return true;
			}
			else
			{
				return false;
			}
		}

		void run(Canvas c)
		{
			update();
			render(c);

		}

		void update()
		{

			if(doTraverse)
			{
				if(System.currentTimeMillis()-traverseTimer>traverseInterval)
				{
					traverseTimer+=traverseInterval;

					for(int i=0;i<bbc.allBots.size();i++)
					{
						Bot b = (Bot) bbc.allBots.get(i);

						bbc.registry.get(i).clear();
						for(int j=0;j<bbc.allBots.size();j++)
						{
							Bot b2= (Bot) bbc.allBots.get(j);
							PVector p1 = new PVector(b.x,b.y);
							PVector p2 = new PVector( b2.x,b2.y);
							if(p1.dist(p2) -20<bbc.neighborBound)
							{
								bbc.registry.get(i).add("" + b2.ID);					
							}
						}

						groupings = Traverse.getGroupings(bbc.registry);

					}
				}
			}
			/*
			List<List<String>> registry = new Vector<List<String>>();
			for(int i=0;i<bbc.allBots.size();i++)
			{
				registry.add(new Vector<String>());
			}
			 */
			/*
			registry.add(new Vector<String>());
			registry.add(new Vector<String>());
			registry.add(new Vector<String>());
			registry.add(new Vector<String>());
			registry.add(new Vector<String>());
			 */

		}

		void render(Canvas c)
		{


			RectF r = new RectF(leftx, topy, rightx, bottomy);
			c.drawRect(r, redPaintHighlight);

			//draw cursor instead
			//int cx=getWidth()/2;
			//int cy= getHeight()/2;
			//int cx=(int) map(bbc.myposx,0,640,leftx,rightx);
			//int cy=(int) map(bbc.myposy,0,480,topy,bottomy);
			//Log.d("rfv", "myposx:"+ bbc.myposx+ "myposy:"+ bbc.myposy+  " cx" + cx + "  cy: " +cy);
			fixCursor();
			if(cursor.active)
			{
				c.drawCircle(cursor.x, cursor.y, 3, redPaintHighlight);

				//draw the pulsing cursor
				if(mode.equals("Move")|| mode.equals("TugMove"))
				{
					cursor.sz+=cursor.amt;
					if(cursor.sz>cursor.mx)
					{
						cursor.amt=-1;
						cursor.growShrink=false;
					}
					if(cursor.sz<cursor.mn)
					{
						cursor.amt=1;
						cursor.growShrink=true;
					}
					c.drawCircle(cursor.x, cursor.y, cursor.sz, redPaintHighlight);
				}

				if(mode.equals("RotateToAngle")&& touching)
				{
					c.drawLine(getWidth()/2, getHeight()/2, cursor.x, cursor.y, redPaintHighlight);
				}
			}

			//bbc.numberOfNeigbhors();

			///////////////////////
			if(groupings!=null)
			{
				if(doTraverse)
				{
					for(int i = 0; i < groupings.size(); i++){
						System.out.print(i + ": ");
						for(int j = 0; j < groupings.get(i).size(); j++){
							//System.out.print(groupings.get(i).get(j) + " ");
							String s = groupings.get(i).get(j);

							Bot t= (Bot) bbc.allBots.get(Integer.parseInt(s));
							int tx=(int) map(t.x,0,640,this.leftx,this.rightx);
							int ty=(int) map(t.y,0,480,this.topy,this.bottomy);

							Paint p = new Paint();
							p.setColor(Color.rgb(i*25+30, i*25+25, 200));
							c.drawCircle(tx, ty, 20, p);
						}
						//System.out.println();
					}
				}
			}

			/////////////////////
			for(int i=0; i<bbc.allBots.size(); i ++)
			{
				Bot b= (Bot) bbc.allBots.get(i);

				int bx=(int) map(b.x,0,640,leftx,rightx);
				int by=(int) map(b.y,0,480,topy,bottomy);


				if(simulateVel && simulatedVels!=null)
				{
					//Log.d("","did it get");



					PVector simV = (PVector) simulatedVels.get(i);
					b.x+=simV.x;
					b.y+=simV.y;


					if(touching)
					{
						PVector l = new PVector(bx,by);
						PVector t = new PVector(touchX,touchY);
						PVector d= PVector.sub(t,l);
						d.normalize();
						d.mult(1);

						simV.add(d);	
						simV.limit(2);
					}
					/*
					if(Math.random()<.5)
					{
						b.x+=1;
					}
					else
					{
						b.x-=1;
					}
					if(Math.random()<.5)
					{
						b.y+=1;
					}
					else
					{
						b.y-=1;
					}

					 */

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

				Paint p = new Paint();
				p.setStyle(Style.FILL);
				switch(b.ID)
				{

				case 0: p.setColor(Color.rgb(160,32,240));   break;				
				case 1: p.setColor(Color.RED);  break;
				case 2: p.setColor(Color.rgb(255, 165, 0));  break;
				case 3: p.setColor(Color.YELLOW);  break;
				case 4: p.setColor(Color.GREEN);  break;
				case 5: p.setColor(Color.rgb(0, 206, 209));  break;
				case 6: p.setColor(Color.BLUE);  break;
				case 7: p.setColor(Color.RED);  break;

				default: ;

				}
				c.drawCircle(bx, by, 15f, p);


				if(mode.equals("AvatarMove"))
				{
					c.drawCircle(bx, by, 4f, redPaintHighlight);

				}

				c.drawCircle(bx, by, 2.5f, blackPaint);
				if(b.inspect)
				{
					c.drawCircle(bx, by, 50, redPaintHighlight);
				}

				c.drawLine(bx, by, bx+b.vel.x*100, by+b.vel.y*100, greenPaint);

				int nv =  (int) map(bbc.neighborBound, 0, 640, 0, this.w);
				if(b.vel.x==0)
				{
					c.drawCircle(bx, by, nv, whitePaintStroke);
				}
				else
				{
					c.drawCircle(bx, by, nv, greenPaintHighlight);
				}

				c.drawLine(bx, by, (float) (bx+nv*Math.cos(Math.toRadians(b.camang))),(float) ( by+nv*Math.sin(Math.toRadians(b.camang))), whitePaintStroke);



				/////////////
				b.getNeighbors();
				b.extendedNeighbors=b.getExtendedNeighbors();
				bbc.resetQuery();

				//////////////////////////////////

				PVector p1 = new PVector(b.x,b.y);

				for(int m=0;m<b.neighbors.size();m++)
				{
					Bot b2 = (Bot) b.neighbors.get(m);
					PVector p2 = new PVector(b2.x,b2.y);
					float d = PVector.dist(p1, p2);	
					int b2x=(int) map(b2.x,0,640,leftx,rightx);
					int b2y=(int) map(b2.y,0,480,topy,bottomy);
					if(d< (bbc.neighborBound +20) && d>bbc.neighborBound)
					{
						c.drawLine(bx, by, b2x, b2y, yellowPaint);
					}
					else
					{
						c.drawLine(bx, by, b2x, b2y, redPaintHighlight);
					}




				}


				/*

				// this is for determine extended neigbors
				if(b.ID==0)
				{
					int b1x=(int) map(b.x,0,640,leftx,rightx);
					int b1y=(int) map(b.y,0,480,topy,bottomy);
					c.drawCircle(b1x, b1y, 10, blackPaint); 
					for(int m=0;m<b.extendedNeighbors.size();m++)
					{
						Bot b2 = (Bot) b.extendedNeighbors.get(m);
						PVector p2 = new PVector(b2.x,b2.y);

						int b2x=(int) map(b2.x,0,640,leftx,rightx);
						int b2y=(int) map(b2.y,0,480,topy,bottomy);
						c.drawCircle(b2x, b2y, 10, blackPaint); 

					}
				}
				 */

				/*
				b.numN=0;
				for(int j=0;j<bbc.allBots.size();j++)
				{
					Bot b2 = (Bot) bbc.allBots.get(j);
					PVector p2 = new PVector(b2.x,b2.y);			

					float d = PVector.dist(p1, p2);				
					int b2x=(int) map(b2.x,0,640,leftx,rightx);
					int b2y=(int) map(b2.y,0,480,topy,bottomy);
					if(d< bbc.neighborBound+20   && !b.equals(b2))
					{

						b.numN+=1;
						if(d< (bbc.neighborBound +20) && d>bbc.neighborBound)
						{
							c.drawLine(bx, by, b2x, b2y, yellowPaint);
						}
						else
						{
							c.drawLine(bx, by, b2x, b2y, redPaintHighlight);
						}

					}
				}
				 */

				/////////////////////////////////


				c.drawText("" + b.ID, bx, by+10, whitePaintText);
				c.drawText("" + b.numNeighbors, bx, by+35, blackPaintText);
				c.drawText("ex:" + b.extendedNeighbors.size(), bx, by+70, blackPaintText);

			}

			if(mode.equals("drawn")||mode.equals("Path"))
			{
				if(!thread.addLock)
				{
					PVector p1, p2;

					for(int i=0;i< thread.lines.size()-1;i++)
					{
						p1 = (PVector) thread.lines.get(i);
						c.drawCircle(p1.x, p1.y, 2, blackPaint);
						if(thread.lines.size()>1)
						{
							p2 = (PVector) thread.lines.get(i+1);
						}
						else
						{
							p2=p1;
						}
						if(p2!=null && p1!=null)
						{
							c.drawLine(p1.x, p1.y, p2.x, p2.y, blackPaint);
						}




						p1=p2;
					}
				}
			}


		}

		void fixCursor()
		{

			if(cursor.x<leftx)
			{
				cursor.x=leftx;
			}
			if(cursor.x>rightx)
			{
				cursor.x=rightx;
			}
			if(cursor.y<topy)
			{
				cursor.y=topy;
			}
			if(cursor.y>bottomy)
			{
				cursor.y=bottomy;
			}
		}



	}

	// end arena
	static public final float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		//double dist = Math.sqrt(     Math.pow(this.deltaTouchX,2)+Math.pow(this.deltaTouchY,2)    );
		double dist = Math.sqrt(     Math.pow(this.touchX-this.initialTouchX,2)+Math.pow(this.touchY-this.initialTouchY,2)    );
		Log.d("onlongclick","dist: " + dist);
		if(this.thread.arenaSongmaker)
		{
			//double dist = Math.sqrt(     Math.pow(this.deltaTouchX,2)+Math.pow(this.deltaTouchY,2)    );
			if( dist < this.thread.songMaker.g.szx)
			{

				//open the track menu if clicked on something for long
				/*
				Log.d("onlongclick","it worked: " + dist);
				this.mActivity.doTrackMenu=true;				
				this.mActivity.openOptionsMenu();

				return true;
				 */
			} 
		}
		return false;
	}

	/*
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	// TODO Auto-generated method stub
	return super.onTouchEvent(event);
	}
	 */



	class NumberPick
	{
		public boolean show;
		public long startTime=250;
		public long minTime=25;
		int number;
		Button bup,bdown, pick;

		long bTimer;
		//long startTime=250;
		long bTimeInterval=startTime;


		PVector l;

		NumberPick()
		{
			bup = new Button();
			bup.msg="+";
			bdown = new Button();
			bdown.msg="-";


			//			bup.loc.x=getWidth()/2-bup.sz;
			//			bup.loc.y=getHeight()-bup.sz;
			//			
			//			bdown.loc.x=getWidth()/2+bdown.sz;
			//			bdown.loc.y=getHeight()-bdown.sz;
			l = new PVector();
			l.x = getWidth()/2;
			l.y=getHeight()/2;
			bup.loc.x=l.x+bup.sz;
			bup.loc.y=l.y-bup.sz;

			bdown.loc.x=l.x-bdown.sz;
			bdown.loc.y=l.y-bdown.sz;

			bTimer=System.currentTimeMillis();

			pick = new Button();
			pick.loc.x=l.x;
			pick.loc.y=l.y+ pick.sz;
			pick.msg="pick";


		}

		public void pickit() {
			// TODO Auto-generated method stub
			show=false;
			mActivity.client.sendMessage("controller,"+ 800 + "," + this.number);
			Log.d("sending map","" + this.number);
			mode= mActivity.prevMode;
			Log.d("returning to mode","" + mode);

		}

		void run(Canvas c)
		{


			update();
			render(c);

			bup.run(c);
			bdown.run(c);
			pick.run(c);

		}
		void update()
		{
			if(bup.isPressed)
			{
				if(System.currentTimeMillis()-bTimer>bTimeInterval)
				{
					number++;
					if(bTimeInterval>minTime)
					{
						bTimeInterval-=10;
					}
					bTimer=System.currentTimeMillis();
				}
			}
			if(bdown.isPressed)
			{
				if(System.currentTimeMillis()-bTimer>bTimeInterval)
				{


					if(number>0)
					{
						number--;
					}
					if(number<0)
					{
						number=0;
					}

					if(bTimeInterval>minTime)
					{
						bTimeInterval-=10;
					}
					//bTimer+=bTimeInterval;
					bTimer=System.currentTimeMillis();
				}
			}

			if(pick.isPressed)
			{

			}

			/*
			if(!bup.isPressed && !bdown.isPressed)
			{
				bTimeInterval=starTime;
				System.currentTimeMillis();
			}
			 */
		}
		void render(Canvas c)
		{
			c.drawText("" + number, l.x, l.y, whitePaintText);
		}

		class Button
		{

			Paint pressedColor,notPressedColor;
			String msg;
			PVector loc;
			boolean isPressed;

			int sz=100;

			//long pressedTimer;

			Rect r;

			Button()
			{
				pressedColor = new Paint();
				pressedColor.setColor(Color.RED);
				notPressedColor = new Paint();
				notPressedColor.setColor(Color.WHITE);
				msg="";

				loc = new PVector();

				r = new Rect();
			}

			void run(Canvas c)
			{
				update();
				render(c);

			}


			void update()
			{
				boolean c1=(touchX<loc.x+sz/2 && touchX >loc.x-sz/2);
				boolean c2 = (touchY<loc.y+sz/2 && touchY >loc.y-sz/2);
				if(c1&&c2&&touching)
				{
					isPressed=true;
					//pressedTimer =System.currentTimeMillis();

				}
				else
				{
					isPressed=false;
				}

				r.set((int) (loc.x-sz/2) , (int) (loc.y-sz/2), (int) (loc.x+sz/2), (int)  (loc.y+sz/2));
			}

			void render(Canvas c)
			{
				if(isPressed)
				{
					c.drawRect(r, pressedColor);
				}
				else
				{
					c.drawRect(r, notPressedColor);
				}

				c.drawText(msg, loc.x, loc.y, blackPaintText);

			}
		}

	}


}
