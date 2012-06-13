package gtcmtswarmbot.mobilecontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class SongMaker {


	Paint blackPaint, yellowPaint, redPaint, greenPaint, whitePaint, blackOutline, bluePaint;

	DrawView v;

	Grid g;
	boolean trigVal, trigValLock;
	Scroll scroll;
	
	int TRUELENGTH=48;

	SongMaker(DrawView myDrawView)
	{

		this.v = myDrawView;

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
		
		bluePaint.setColor(Color.BLUE);



		g = new Grid();
		
		scroll= new Scroll();
	}

	void run(Canvas c)
	{
		g.run(c);
		update();
		render(c);
	}

	void update()
	{

	}

	void render(Canvas c)
	{

	}



	class Grid
	{
		int nx=48;
		int ny=8;
		int width,height;
		float sz=width/ (float) nx ;
		float szx;
		float szy;
		Rect rr;
		
		int factor;
		
		float offx,offy;

		boolean[][] gridVals;
		Grid()
		{
			gridVals = new boolean[ny][nx];

			width=v.screenWidth;
			height=v.screenHeight;
			//sz=width/ (float) nx ;
			szx=width/ (float) nx ;
			
			szy=height/ (float) ny;
			szy=szy/2;
			
			
			rr = new Rect();
			//rr.set(left, top, right, bottom);
			
			
			offx= szx/2;
			offy= szy/2;
			
			factor = TRUELENGTH/nx;

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

			for (int i=0;i<gridVals.length;i++)
			{


				for (int j = 0;j<gridVals[i].length; j++)
				{
					//Log.d("songmaker","ij " + i + "," + j);
					/*
					int left = (int) (i*sz-sz/2);
					int top = (int) (j*sz-sz/2);
					int right = (int) (i*sz+sz/2);
					int bottom = (int) (j*sz+sz/2);
					 */				
					int left = (int) (j*szx-szx/2 + offx);
					int top = (int) (i*szy-szy/2 + offy);
					int right = (int) (j*szx+szx/2 + offx);
					int bottom = (int) (i*szy+szy/2 + offy);


					if (gridVals[i][j])
					{
						//fill(0, 255, 0);
						//stroke(0);

						//Rect r = new Rect((int) (i*sz-sz/2), (int)(j*sz-sz/2),(int)(i*sz+sz/2),(int)(j*sz+sz/2) );


						rr.set(left, top, right, bottom);
						c.drawRect(rr, greenPaint);
						//rect(i*sz+sz/2, j*sz+sz/2, sz, sz);
						//Log.d("songmaker","ij " + i + "," + j);
					}
					else
					{
						//fill(255);
						//stroke(0);
						//rect(i*sz+sz/2, j*sz+sz/2, sz, sz);
						//Rect r = new Rect((int) (i*sz-sz/2), (int)(j*sz-sz/2),(int)(i*sz+sz/2),(int)(j*sz+sz/2) );


						rr.set(left, top, right, bottom);
						c.drawRect(rr, whitePaint);
						//Log.d("songmaker","ij " + i + "," + j);
					}
					//float[] ff =  pointInside(new PVector(v.touchX, v.touchY));
					float[] ff =  pointInside(v.touchX, v.touchY);

					if ( Math.round( ff[0]) ==i && Math.round( ff[1])==j)
					{
						//fill(255, 0, 0);
						//rect(i*sz+sz/2, j*sz+sz/2, sz, sz);
						//Rect r = new Rect((int) (i*sz-sz/2), (int)(j*sz-sz/2),(int)(i*sz+sz/2),(int)(j*sz+sz/2) );

						rr.set(left, top, right, bottom);
						c.drawRect(rr, bluePaint);
					}
					
					if( (j*factor)  ==v.bbc.currentIndex)
					{
						rr.set(left, top, right, bottom);
						c.drawRect(rr, redPaint);
					}
					
					rr.set(left, top, right, bottom);
					c.drawRect(rr, blackOutline);
				}
			}



		}

		float[] pointInside(PVector p)
		{
			float[] res = new float[2];

			float x = p.x;
			float y= p.y;

			//x=i*sz+sz/2;
			//float i= (x-sz/2)/sz;
			float i= (y-szy/2-offy)/szy;
			//i=i/48;


			//y=j*sz+sz/2;
			//float j=(y-sz/2)/sz;
			float j=(x-szx/2-offx)/szx;
			//j=j/8;

			res[0]=i;
			res[1]=j;

			if (i<nx&&i>0&&j>0&&j<ny)
			{

				//println("i: " + res[0] + "  j: " + res[1]);
			}

			return res;
		}

		float[] pointInside(float x, float y)
		{
			float[] res = new float[2];



			//x=i*sz+sz/2;
			//float i= (x-sz/2)/sz;
			//float i= (x-szx/2)/szx;
			float i= (y-szy/2)/szy;
			//i=i/48;


			//y=j*sz+sz/2;
			//float j=(y-sz/2)/sz;
			//float j=(y-szy/2)/szy;
			float j=(x-szx/2)/szx;
			//j=j/8;

			res[0]=i;
			res[1]=j;

			if (i<nx&&i>0&&j>0&&j<ny)
			{

				//println("i: " + res[0] + "  j: " + res[1]);
			}

			return res;
		}


		void setGridCell(int i, int j, boolean value)
		{
			gridVals[i][j]=value;
		}
	}

	class Button
	{
		Button()
		{

		}
	}
	
	class Scroll
	{
		
		int offset=30;
		
		Scroll()
		{
			
		}
		
		void run()
		{
			
		}
		
		void update()
		{
			
		}
		
		void render()
		{
			
		}
	}
	
	
	int[] toIntArray()
	{
		int[] result = new int[this.TRUELENGTH];
		
		int factor = this.TRUELENGTH/this.g.gridVals.length;
		
		for (int i=0;i<this.g.gridVals.length;i++)
		{
			
			

			
			for (int j = 0;j<this.g.gridVals[i].length; j++)
			{
			   
				int noteVal = 72+j;
				if(this.g.gridVals[i][j])
				{
					
				}
			
			}
		}
		
		return result;
	}
	
	


}
