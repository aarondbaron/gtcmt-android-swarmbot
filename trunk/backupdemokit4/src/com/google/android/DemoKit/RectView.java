package com.google.android.DemoKit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RectView extends View {

	Paint paint;
	public RectView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public RectView(Context context, AttributeSet attrs)
    {
    	super(context,attrs);
    	init();
    }
	
	
	public RectView(Context context, AttributeSet attrs, int defStyle)
    {
    	super(context,attrs, defStyle);
    	init();
    }

	private void init() {
		// TODO Auto-generated method stub
		 paint = new Paint();
		 paint.setColor(Color.BLACK);


	}
	
	
	@Override
    public void onDraw(Canvas canvas) {
            canvas.drawLine(0, 0, 20, 20, paint);
            canvas.drawLine(20, 0, 0, 20, paint);
    }


}
