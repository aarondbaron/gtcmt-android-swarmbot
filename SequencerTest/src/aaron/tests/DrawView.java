package aaron.tests;



import java.util.ArrayList;
import java.util.List;




import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.media.AudioManager;
import android.media.SoundPool;

public class DrawView extends SurfaceView implements OnTouchListener , SurfaceHolder.Callback{
	private static final String TAG = "DrawView";
	DrawViewThread thread;
	BeatTimer beatTimerThread;
	//AndroidAudioDevice audiodev;
	AudioTest atest;

	List<Point> points = new ArrayList<Point>();
	SequenceButton[] sequencer;
	Slider mySlider;

	WaveView wv;

	Paint paint = new Paint();
	Paint paint2 = new Paint();
	Paint paint3 = new Paint();
	Display display ;
	
	private SoundPool _soundPool;
	private int _playbackFile = 0;
	boolean useSample;
	boolean drawPoints;

	public DrawView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);

		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);

		paint2.setColor(Color.BLUE);
		paint2.setAntiAlias(true);
		paint2.setStyle(Paint.Style.STROKE);

		paint3.setColor(Color.RED);
		paint3.setAntiAlias(true);
		paint3.setStyle(Paint.Style.STROKE);
		paint3.setStrokeWidth(9);

		//SurfaceHolder holder = getHolder();
		//holder.addCallback(this);


		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		thread  = new DrawViewThread(holder, context);

		sequencer= new SequenceButton[16];

		beatTimerThread = new BeatTimer();
		
		//audiodev = new AndroidAudioDevice();
		atest=new AudioTest();
		
		//mySlider = new Slider();

		/*
        for(int i = 0 ; i  <sequencer.length; i++)
        {
        	sequencer[i] = new SequenceButton(20+20*i, getHeight()/2 ,20);
        }
		 */
		//thread.start();
		//display = getWindowManager().getDefaultDisplay(); 
		
		_soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 100);
        _playbackFile = _soundPool.load(getContext(), R.raw.explosion2, 0);

	}
	


	@Override
	public void onDraw(Canvas canvas) {
		/*
		canvas.drawColor(Color.BLACK);
		for (Point point : points) {


			canvas.drawCircle(point.x, point.y, 5, paint);

			point.update();

			// Log.d(TAG, "Painting: "+point);
		}
		 */
	}

	public boolean onTouch(View view, MotionEvent event) {
		// if(event.getAction() != MotionEvent.ACTION_DOWN)
		// return super.onTouchEvent(event);
		
		
		if(drawPoints)
		{
		Point point = new Point();
		point.x = event.getX();
		point.y = event.getY();
		point.vx = (float) (Math.random()-.5);
		point.vy = (float) (Math.random()-.5);
		points.add(point);
		invalidate();
		Log.d(TAG, "point: " + point);
		}
		
		
		float x = event.getX();
		float y = event.getY();
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			for(int i=0; i <sequencer.length; i++)
			{
				if(Math.abs(sequencer[i].x-x) < sequencer[i].sz/2 && Math.abs(sequencer[i].y-y) < sequencer[i].sz/2 )
				{
					sequencer[i].on=!sequencer[i].on;
				}

			}
		}
		
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			if(y<mySlider.height + 3*mySlider.sz && y > mySlider.height - 3*mySlider.sz)
			{
				
				mySlider.sliderPos=(int) x;
				beatTimerThread.globalTimeInterval= (long) map(mySlider.sliderPos,0,mySlider.maxWidth,25,500);
			}
		}


		return true;
	}



	class DrawViewThread extends Thread {

		private SurfaceHolder mSurfaceHolder;
		private Handler mHandler;
		private Resources mRes;
		private boolean running;

		DrawViewThread(SurfaceHolder holder, Context context) 
		{

			mSurfaceHolder = holder;
			//mHandler = handler;
			Context mContext = context;
			mRes = context.getResources();

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
			canvas.drawColor(Color.BLACK);
			
			if(drawPoints)
			{
				try 
				{
					for (Point point : points) {


						canvas.drawCircle(point.x, point.y, 5, paint);

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


		}

	}



	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d("surface created", "checking to start ");
		thread.setRunning(true);
		thread.start();

		for(int i = 0 ; i  <sequencer.length; i++)
		{
			sequencer[i] = new SequenceButton(50+50*i, getHeight()/2 ,50);
		}

			
		mySlider = new Slider();
		wv = new WaveView();
		Log.d("beatTimer started", "checking HERE");
		beatTimerThread.setRunning(true);
		beatTimerThread.start();


	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		atest.onStop();
		boolean retry = true;
		thread.setRunning(false);
		beatTimerThread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				beatTimerThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
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

	class SequenceButton
	{
		int x;
		int y;
		int sz;
		boolean on;
		boolean highlight;
		boolean playedOnce;

		SequenceButton(int x, int y, int size)
		{
			this.x=x;
			this.y=y;
			this.sz=size;
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



			if(on)
			{
				c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint);
			}
			else
			{

				c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint2);
			}

			if(highlight)
			{
				c.drawRect(x-sz/2, y+sz/2, x+sz/2, y-sz/2, paint3);
			}

		}




	}
	
	class Slider
	{
		float minVal, maxVal;
		int minWidth,maxWidth, sliderPos, sz, height;
		public Slider()
		{
			minVal=50;
			maxVal=500;
			
			minWidth=0;
			maxWidth=getWidth();
			height = getHeight()/4;
			
			sliderPos = (int) map(beatTimerThread.globalTimeInterval,minVal,maxVal,minWidth,maxWidth);
			//sliderPos = maxWidth/2;
			sz=25;
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
			c.drawLine(minWidth, height, maxWidth, height, paint);
			c.drawCircle(sliderPos, height, sz, paint);
			c.drawText("" + beatTimerThread.globalTimeInterval, sliderPos, height, paint2);
			
			
		}
	}
	
	class WaveView
	{
		public WaveView()
		{
			
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
			for(int i=0; i < atest.device.buffer.length;i++)
			{
				//-32768 ,32767,
				c.drawCircle(i, map(atest.device.buffer[i],-3000 ,3000,getHeight(),getHeight()-getHeight()/4), 1, paint);
			}
		}
	}
	
	
    static public final float map(float value, float istart, float istop, float ostart, float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
      }
}

