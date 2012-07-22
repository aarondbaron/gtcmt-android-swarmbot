package gtcmtswarmbot.mobilecontrol;




import java.text.DecimalFormat;
import java.util.Vector;

import gtcmtswarmbot.mobilecontrol.DrawView.Cursor;
import gtcmtswarmbot.mobilecontrol.enums.Mapping;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

	int screenWidth;
	int screenHeight;


	boolean touching;
	int touchX,touchY;
	int prevTouchX,prevTouchY;
	int deltaTouchX,deltaTouchY;
	int initialTouchX,initialTouchY;

	int selectedI,selectedJ;
	long longTouchTimer;

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

		//doTest();


	}

	void doTest()
	{
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
			Bot testBot = new Bot();
			testBot.x=(int) (Math.random()*640);
			testBot.y=(int) (Math.random()*480);
			testBot.ID=i;
			testBot.camang=(float) ((  2*Math.random()-1  ) *180 );
			bbc.allBots.add(testBot);
		}

		simulateVel=true;
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
				this.thread.songMaker.trigValLock=false;
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
				int xx = (int) map(thread.arena.cursor.x,thread.arena.leftx,thread.arena.rightx,0,640);

				int yy = (int) map(thread.arena.cursor.y,thread.arena.topy,thread.arena.bottomy,0,480);

				mActivity.client.sendMessage("controller,"+ 9988) ;
			}
			if(this.mode.equals("AvatarMove"))
			{
				int xx = (int) map(thread.arena.cursor.x,thread.arena.leftx,thread.arena.rightx,0,640);

				int yy = (int) map(thread.arena.cursor.y,thread.arena.topy,thread.arena.bottomy,0,480);

				mActivity.client.sendMessage("controller,"+ 9987) ;
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
			}

			//bbc.numberOfNeigbhors();

			for(int i=0; i<bbc.allBots.size(); i ++)
			{
				Bot b= (Bot) bbc.allBots.get(i);

				if(simulateVel)
				{

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

					if(b.x<0)
					{
						b.x+=1;
					}
					if(b.y<0)
					{
						b.y+=1;
					}
					if(b.x>640)
					{
						b.x-=1;
					}
					if(b.y>480)
					{
						b.y-=1;
					}

				}

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



				//////////////////////////////////

				PVector p1 = new PVector(b.x,b.y);
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

				/////////////////////////////////


				c.drawText("" + b.ID, bx, by+10, whitePaintText);
				c.drawText("" + b.numN, bx, by+35, blackPaintText);


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
				Log.d("onlongclick","it worked: " + dist);
				//this.mActivity.makeTrackMenu();
				
				//this.mActivity.openContextMenu(this);
				this.mActivity.doTrackMenu=true;				
				this.mActivity.openOptionsMenu();

				return true;
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



}