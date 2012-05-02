package gtcmtswarmbot.mobilecontrol;




import java.text.DecimalFormat;
import java.util.Vector;

import gtcmtswarmbot.mobilecontrol.ArenaView.Cursor;
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

public class ArenaView extends SurfaceView implements OnTouchListener , SurfaceHolder.Callback{

	private Paint paint1;
	private Paint paint2;
	private Paint blackpaint;
	private Paint blackpaintText;
	private Paint redPaintHighlight;
	private Paint greenPaint;
	private Paint yellowPaint;
	ArenaViewThread thread;

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

	public ArenaView(Context context, SomeController bbc) {
		//super(context);
		// TODO Auto-generated constructor stub



		super(context);

		mActivity = (DemokitMobileControlActivity) context;

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

		float w=640;
		float h =480;
		
		float factor = w/h;
		
		

		//int woff=10;
		//int hoff=10;
		//thread.arena = new Arena(getWidth()/2, getHeight()/2,getWidth()/2-woff,getHeight()/2-hoff);		
		thread.arena = new Arena(getWidth()/2, getHeight()/2, (int) ((getWidth()/2)/factor) ,(int)( getHeight()/2) );		
		thread.sequencer = new Sequencer(this);
		
		surfCreated=true;
		
		
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
		 

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) 
		{

			float x = event.getX();
			float y = event.getY();
			thread.arena.cursor.x=(int) x;
			thread.arena.cursor.y=(int) y;



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



			if(inspecting)
			{

			}



			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			float x = event.getX();
			float y = event.getY();

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
				mActivity.client.sendMessage("controller,"+ 801 + "," + this.thread.sequencer.getMySequence());

				thread.sequencer.checkInside((int)x, (int)y);
			}

		}



		return false;
	}


	void init(SurfaceHolder holder, Context context) {
		paint1 = new Paint();
		paint2 = new Paint();
		blackpaint = new Paint();
		blackpaintText = new Paint();
		redPaintHighlight = new Paint();
		greenPaint = new Paint();
		yellowPaint = new Paint();

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

		redPaintHighlight.setColor(Color.RED);
		redPaintHighlight.setAntiAlias(true);
		redPaintHighlight.setStyle(Paint.Style.STROKE);

		greenPaint.setColor(Color.GREEN);
		greenPaint.setAntiAlias(true);

		yellowPaint.setColor(Color.YELLOW);
		yellowPaint.setAntiAlias(true);

		thread = new ArenaViewThread(holder, context);
		this.setOnTouchListener(this);
	}



	@Override
	public void onDraw(Canvas canvas) {

		invalidate();
	}

	class ArenaViewThread extends Thread {

		public Sequencer sequencer;
		private SurfaceHolder mSurfaceHolder;
		private Handler mHandler;
		private Resources mRes;
		private boolean running;

		Arena arena;


		Vector lines;
		public boolean showSequencer;
		public boolean addLock;



		ArenaViewThread(SurfaceHolder holder, Context context) 
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
						arena.run(c);
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

			for(int i=0; i<bbc.allBots.size(); i ++)
			{
				Bot b= (Bot) bbc.allBots.get(i);

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
				if(b.inspect)
				{
					c.drawCircle(bx, by, 50, redPaintHighlight);
				}

				c.drawLine(bx, by, bx+b.vel.x*100, by+b.vel.y*100, greenPaint);
				if(b.vel.x==0)
				{
					c.drawCircle(bx, by, 25, redPaintHighlight);
				}
			}

			if(mode.equals("drawn")||mode.equals("Path"))
			{
				if(!thread.addLock)
				{
					PVector p1, p2;

					for(int i=0;i< thread.lines.size()-1;i++)
					{
						p1 = (PVector) thread.lines.get(i);
						c.drawCircle(p1.x, p1.y, 2, blackpaint);
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
							c.drawLine(p1.x, p1.y, p2.x, p2.y, blackpaint);
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


}
