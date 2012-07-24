package gtcmtswarmbot.mobilecontrol;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class CarillonPlayer {
	Paint blackPaint, yellowPaint, redPaint, greenPaint, whitePaint, blackOutline, bluePaint;

	DrawView v;	

	int NUMNOTES =8;
	//int szx;

	Vector keyVals;

	Vector keys;

	CarillonPlayer(DrawView v)
	{
		this.v = v;

		blackPaint= new Paint();
		yellowPaint = new Paint();
		redPaint= new Paint();
		greenPaint= new Paint();
		whitePaint= new Paint();
		blackOutline = new Paint();
		bluePaint = new Paint();

		greenPaint.setColor(Color.GREEN);
		greenPaint.setAntiAlias(true);
		blackPaint.setColor(Color.BLACK);
		yellowPaint.setColor(Color.YELLOW);
		redPaint.setColor(Color.RED);
		whitePaint.setColor(Color.WHITE);
		blackOutline.setColor(Color.BLACK);
		blackOutline.setStyle(Paint.Style.STROKE);
		blackOutline.setStrokeWidth(2);
		bluePaint.setColor(Color.BLUE);

		int szx=(int) (v.screenWidth/ NUMNOTES)/2;

		keyVals = new Vector();
		for(int i = 0;i<NUMNOTES;i++)
		{
			Boolean b = new Boolean(false);
			keyVals.add(b);
		}
		
		keys = new Vector();
		int x=szx*2;
		for(int i = 0;i<NUMNOTES;i++)
		{
			//keys.add(new Key(x-szx,v.screenHeight/2,x+szx,v.screenHeight));
			keys.add(new Key(x-szx,v.screenHeight/2,x+szx,v.screenHeight,i));
			x+=szx*2 ;
		}


	}

	void run(Canvas c)
	{		 
		update();
		render(c);

		for(int i = 0;i<keys.size();i++)
		{
			Key k = (Key)keys.get(i);
			k.run(c);
		}
	}

	void update()
	{

	}

	void render(Canvas c)
	{
		 
	}

	/*
	float   pointInside(float x )
	{
		//float[] res = new float[2];

		//x=i*szx*2+szx;
		//float i= (x-szx)/(2*szx);
		float i= (x-szx)/2*szx;

		//szy=gethiehgt/2
		//y = szy/4 

		//res[0]=i;
		//res[1]=j;

		return i;
	}
	 */

	class Key
	{
		boolean triggered;
		boolean playOnce;
		String state;

		int x, y;
		int szx;
		int szy;

		int id=0;		

		Key()
		{			
			state = "gate";

			this.szx= (int) (v.screenWidth/ (float)  NUMNOTES)/2;
			this.szy=  v.screenHeight/2;
		}
		Key(int id)
		{			
			state = "";
			state = "gate";
			this.id=id;

			szx=(int) (v.screenWidth/ (float)  NUMNOTES)/2;
			szy= v.screenHeight/2;
		}

		public Key(int i, int j, int k, int w) {
			// TODO Auto-generated constructor stub

			state = "";
			state = "gate";
			this.id= keys.size();

			this.x=i;
			this.y=j;
			this.szx=k;
			this.szy=w;

			this.szx=(int) (v.screenWidth/ (float)  NUMNOTES)/2;
			szy= v.screenHeight/2;

		}
		public Key(int i, int j, int k, int w, int i2) {
			// TODO Auto-generated constructor stub

			state = "";
			state = "gate";
			this.id= i2;

			this.x=i;
			this.y=j;
			this.szx=k;
			this.szy=w;

			szx=(int) (v.screenWidth/ (float)  NUMNOTES)/2;
			szy= v.screenHeight/2;
		}
		void run(Canvas c)
		{
			update();
			render(c);
		}

		void update()
		{		
			 
				boolean c1 = v.touchX>x-szx && v.touchX< x+szx;
				boolean c2 = v.touchY>v.screenHeight/2 && v.touchY<v.screenHeight;
				if(c1&&c2 &&v.touching)
				{
					triggered=true;
				}
				else
				{
					triggered=false;
					playOnce=false;
				}
			 

			if(triggered)
			{
				if(state.equals("gate"))
				{
					if(!playOnce)
					{
						//mActivity.client.sendMessage("controller,"+ 803 ) ;
						if(id<v.bbc.allBots.size())
						{
							v.mActivity.client.sendMessage("controller,"+ 803 + "," + id);
							playOnce=true;
						}
					}
				}

				if(state.equals("continuous"))
				{

				}				
			}		
		}

		void render(Canvas c)
		{
			Rect r = new Rect(x-szx,v.screenHeight/2,x+szx,v.screenHeight);
			if(triggered)
			{
				c.drawRect(r,  greenPaint);
			}
			else
			{
				c.drawRect(r,  redPaint);
			} 
			c.drawRect(r,  blackOutline);
			//x+=szx*2 ;
		}
	}
}
