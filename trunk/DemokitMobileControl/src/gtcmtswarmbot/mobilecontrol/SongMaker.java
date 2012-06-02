package gtcmtswarmbot.mobilecontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class SongMaker {


	Paint blackPaint, yellowPaint, redPaint, greenPaint, whitePaint;

	DrawView v;
	
	Grid g;
	boolean trigVal, trigValLock;
	
	SongMaker(DrawView myDrawView)
	{

		this.v = myDrawView;

		blackPaint= new Paint();
		yellowPaint = new Paint();
		redPaint= new Paint();
		greenPaint= new Paint();
		whitePaint= new Paint();

		greenPaint.setColor(Color.GREEN);
		greenPaint.setAntiAlias(true);
		blackPaint.setColor(Color.BLACK);
		yellowPaint.setColor(Color.YELLOW);
		redPaint.setColor(Color.RED);
		whitePaint.setColor(Color.WHITE);


		
		g = new Grid();
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
		int width=500;
		float sz=width/ (float) nx ;
		
		Rect rr;

		boolean[][] gridVals;
		Grid()
		{
			gridVals = new boolean[nx][ny];
			
			width=v.screenWidth;
			sz=width/ (float) nx ;
			
			rr = new Rect();
			//rr.set(left, top, right, bottom);
			
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
					int left = (int) (i*sz-sz/2);
					int top = (int) (j*sz-sz/2);
					int right = (int) (i*sz+sz/2);
					int bottom = (int) (j*sz+sz/2);
					

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
						c.drawRect(rr, redPaint);
					}
				}
			}



		}

		float[] pointInside(PVector p)
		{
			float[] res = new float[2];

			float x = p.x;
			float y= p.y;

			//x=i*sz+sz/2;
			float i= (x-sz/2)/sz;
			//i=i/48;


			//y=j*sz+sz/2;
			float j=(y-sz/2)/sz;
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
			float i= (x-sz/2)/sz;
			//i=i/48;


			//y=j*sz+sz/2;
			float j=(y-sz/2)/sz;
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


}
