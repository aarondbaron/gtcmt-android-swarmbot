package com.google.android.DemoKit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class RectView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	Paint paint1,paint2;
	private SurfaceHolder       mHolder;
	
	public BoeBotController bbc;
	
	public RectView(Context context) {
		super(context);
		mHolder = getHolder();
        mHolder.addCallback(this);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public RectView(Context context, AttributeSet attrs)
    {
    	super(context,attrs);
    	mHolder = getHolder();
        mHolder.addCallback(this);
    	init();
    }
	
	
	public RectView(Context context, AttributeSet attrs, int defStyle)
    {
    	super(context,attrs, defStyle);
    	mHolder = getHolder();
        mHolder.addCallback(this);
    	init();
    }

	private void init() {
		// TODO Auto-generated method stub
		 paint1 = new Paint();
		 paint1.setColor(Color.WHITE);
		 paint1.setStrokeWidth(2);
		 paint1.setStyle(Paint.Style.FILL);

		 paint2 = new Paint();
		 paint2.setColor(Color.WHITE);
		 paint2.setStrokeWidth(2);
		 paint2.setStyle(Paint.Style.STROKE);
		 
		 this.setBackgroundColor(Color.BLUE);
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
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}


}
