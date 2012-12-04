package gtcmtswarmbot.mobilecontrol;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

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
		
		int off=20;

		Key()
		{			
			state = "gate";

			this.szx= (int) (v.screenWidth/ (float)  NUMNOTES)/2;
			this.szy=  3*v.screenHeight/4;
		}
		Key(int id)
		{			
			state = "";
			state = "gate";
			this.id=id;

			szx=(int) (v.screenWidth/ (float)  NUMNOTES)/2;
			szy= 3*v.screenHeight/4;
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
			szy= 3*v.screenHeight/4;

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
			szy= 3*v.screenHeight/4;
		}
		void run(Canvas c)
		{
			update();
			render2(c);
		}

		void update()
		{		

			boolean c1 = v.touchX>x-szx && v.touchX< x+szx;
			//boolean c2 = v.touchY>v.screenHeight/2 && v.touchY<v.screenHeight;
			boolean c2 = v.touchY>(v.screenHeight/2+off) && v.touchY<szy;
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
							//v.mActivity.client.sendMessage("controller,"+ 803 + "," + id);
							
							// fix the index
							int ind = v.bbc.currentIndex;
							int every3 = ind/ (3);
							int every3mod = ind%(3);
							
							if(every3mod==1)
							{
								ind= (every3*3)%v.bbc.SEQUENCERLENGTH;
							}
							if(every3mod==2)
							{
								ind= ((every3+1)*3) %v.bbc.SEQUENCERLENGTH;								
							}
							
							
							
							v.mActivity.client.sendMessage("controller,"+ 803 + "," + id + "," + ind);
							playOnce=true;


							if(v.bbc.loopMeasure)
							{
								int vvv=ind;
								Log.d("id " + id," genind" + vvv);
								v.thread.songMaker.g.gridVals[id][vvv]=true;
								v.thread.songMaker.g.gridToCMeasure();
							}
							v.mActivity.writeToFile("key hit: " + id + "loopmeasure" + v.bbc.loopMeasure);
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
			//Rect r = new Rect(x-szx,v.screenHeight/2,x+szx,v.screenHeight);
			Rect r = new Rect(x-szx,v.screenHeight/2+off,x+szx,szy);
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

		void render2(Canvas c)
		{
			Rect r = new Rect(x-szx,v.screenHeight/2+off,x+szx,szy);
			if(triggered)
			{
				c.drawRect(r,  whitePaint);
			}
			else
			{
				Paint p = new Paint();
				switch(id)
				{

				case 0: p.setColor(Color.rgb(160,32,240)); c.drawRect(r,  p); break;				
				case 1: p.setColor(Color.RED);c.drawRect(r,  p); break;
				case 2: p.setColor(Color.rgb(255, 165, 0));c.drawRect(r,  p); break;
				case 3: p.setColor(Color.YELLOW);c.drawRect(r,  p); break;
				case 4: p.setColor(Color.GREEN);c.drawRect(r,  p); break;
				case 5: p.setColor(Color.rgb(0, 206, 209));c.drawRect(r,  p); break;
				case 6: p.setColor(Color.BLUE);c.drawRect(r,  p); break;
				case 7: p.setColor(Color.RED);c.drawRect(r,  p); break;

				default: ;

				}


			} 
			c.drawRect(r,  blackOutline);
		}
	}
}
