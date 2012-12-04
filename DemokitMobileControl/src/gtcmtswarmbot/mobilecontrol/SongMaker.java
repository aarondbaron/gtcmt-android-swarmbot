package gtcmtswarmbot.mobilecontrol;



import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class SongMaker {


	Paint blackPaint, yellowPaint, redPaint, greenPaint, whitePaint, blackOutline,blackOutline2, bluePaint, whitePaintText;

	DrawView v;

	Grid g;
	boolean trigVal, trigValLock;
	Scroll scroll;

	int TRUELENGTH=48;

	CarillonPlayer cp;

	Composition comp;
	
	
	String state;



	SongMaker(DrawView myDrawView)
	{

		this.v = myDrawView;

		blackPaint= new Paint();
		yellowPaint = new Paint();
		redPaint= new Paint();
		greenPaint= new Paint();
		whitePaint= new Paint();
		whitePaintText = new Paint();
		blackOutline = new Paint();
		blackOutline2 = new Paint();
		bluePaint = new Paint();

		greenPaint.setColor(Color.GREEN);
		greenPaint.setAntiAlias(true);
		blackPaint.setColor(Color.BLACK);
		yellowPaint.setColor(Color.YELLOW);
		redPaint.setColor(Color.RED);
		whitePaint.setColor(Color.WHITE);
		whitePaintText.setColor(Color.WHITE);
		blackOutline.setColor(Color.BLACK);
		blackOutline.setStyle(Paint.Style.STROKE);
		
		blackOutline2.setColor(Color.BLACK);
		blackOutline2.setStyle(Paint.Style.STROKE);
		blackOutline2.setStrokeWidth(4);

		bluePaint.setColor(Color.BLUE);
		bluePaint.setStyle(Paint.Style.STROKE);
		bluePaint.setStrokeWidth(4);


		whitePaintText.setTextSize(20);

		g = new Grid();

		scroll= new Scroll();

		cp = new CarillonPlayer(v);

		this.comp=v.bbc.myComposition;
		
		//state = "lock";
		state = "release";
	}

	void run(Canvas c)
	{
		g.run(c);
		update();
		render(c);
		
		Paint p = new Paint();
		p.setTextSize(12);
		p.setColor(Color.WHITE);
		for(int i=0;i<g.gridVals[0].length;i++)
		{
			c.drawText("" + (i+1), g.szx*i, v.screenHeight/2+18, p);
			//j*szx-szx/2
		}

		cp.run(c);

		compStatsRender(  c);
	}

	void update()
	{

	}

	void render(Canvas c)
	{

	}

	void compStatsRender(Canvas c)
	{
		//"NumMeasures:" + comp.measures.size() + 
		if(comp!=null)
		{

			if(comp.currentMeasure!=null)
			{
				c.drawText("Current Measure:" + (comp.currentMeasure.ID+1) + "/" + comp.measures.size(), 0, v.screenHeight-10, whitePaintText);
				if(comp.currentMeasure.ID%2==0)
				{
					//humans turn
					c.drawText("Human's Turn"  , v.screenWidth/2, v.screenHeight-10, whitePaintText);


				}
				else
				{
					//robot's turn
					c.drawText("Robots' Turn"  , v.screenWidth/2, v.screenHeight-10, whitePaintText);

				}
			}
		}
	}



	class Grid
	{
		int nx=TRUELENGTH;
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

					


					if(v.bbc.loopMeasure)
					{
						if( (j*factor)  ==v.bbc.currentIndex)
						{
							rr.set(left, top, right, bottom);
							c.drawRect(rr, redPaint);
						}
					}

					rr.set(left, top, right, bottom);
					
					if(j%12==0)
					{
						c.drawRect(rr, blackOutline2);
					}
					else
					{
						c.drawRect(rr, blackOutline);
					}
					
					if ( Math.round( ff[0]) ==i && Math.round( ff[1])==j)
					{
						//fill(255, 0, 0);
						//rect(i*sz+sz/2, j*sz+sz/2, sz, sz);
						//Rect r = new Rect((int) (i*sz-sz/2), (int)(j*sz-sz/2),(int)(i*sz+sz/2),(int)(j*sz+sz/2) );

						rr.set(left, top, right, bottom);
						c.drawRect(rr, bluePaint);
						
						
						if(v.touching)
						{
							Paint p = new Paint();
							p.setTextSize(40);
							p.setColor(Color.WHITE);
							c.drawText("Index:" + (i+1) + " | " + (j+1), v.screenWidth/2-100, v.screenHeight-70, p);
						}
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


		void clear(){
			for (int i=0;i<gridVals.length;i++)
			{
				for (int j = 0;j<gridVals[i].length; j++)
				{
					gridVals[i][j]=false;
				}
			}
		}


		//get grid val


		//
		void gridToCMeasure()
		{

			///go through each index first, then put the columns into a vector
			for (int j = 0;j< nx; j++)
			{
				Vector<Integer> vv = new Vector<Integer>();
				for (int i=0;i<ny;i++)
				{
					if(gridVals[i][j])
					{
						vv.add(new Integer(i + comp.base));
					}
				}

				comp.currentMeasure.notes[j]=vv;



			}


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



	void displayMeasure(int k)
	{
		this.g.clear();
		CMeasure m = this.comp.getMeasure(k);
		//make this one the currentMeasure always
		this.comp.currentMeasure =m;
		for(int i =0 ; i< m.SEQUENCERLENGTH;i++)
		{

			if(m.SEQUENCERLENGTH==this.TRUELENGTH)
			{
				Vector<Integer> v = m.notes[i];
				for(int j = 0; j < v.size();j++)
				{
					Integer vv = v.get(j);				
					int t = vv.intValue()%g.gridVals.length;				
					//Log.d("values", "t" + t + ", i" + i);				
					this.g.setGridCell(t,i,true);
				}		
			}

			if(m.SEQUENCERLENGTH==this.TRUELENGTH/2)
			{
				Vector<Integer> v = m.notes[i];
				for(int j = 0; j < v.size();j++)
				{
					Integer vv = v.get(j);				
					int t = vv.intValue()%g.gridVals.length;				
					//Log.d("values", "t" + t + ", i" + i);				
					this.g.setGridCell(t,i*2,true);
				}	
			}
		}
	}

	void displayMeasure(CMeasure m)
	{
		this.g.clear();
		//make this one the currentMeasure always
		this.comp.currentMeasure =m;
		for(int i =0 ; i< m.SEQUENCERLENGTH;i++)
		{

			if(m.SEQUENCERLENGTH==this.TRUELENGTH)
			{
				Vector<Integer> v = m.notes[i];
				for(int j = 0; j < v.size();j++)
				{
					Integer vv = v.get(j);				
					int t = vv.intValue()%g.gridVals.length;				
					//Log.d("values", "t" + t + ", i" + i);				
					this.g.setGridCell(t,i,true);
				}		
			}

			if(m.SEQUENCERLENGTH==this.TRUELENGTH/2)
			{
				Vector<Integer> v = m.notes[i];
				for(int j = 0; j < v.size();j++)
				{
					Integer vv = v.get(j);				
					int t = vv.intValue()%g.gridVals.length;				
					//Log.d("values", "t" + t + ", i" + i);				
					this.g.setGridCell(t,i*2,true);
				}	
			}
		}

	}

	public void nextMeasure() {
		// TODO Auto-generated method stub

		//this.comp.

		//displayMeasure();

		this.comp.nextMeasure();

		this.displayMeasure(this.comp.currentMeasure);

	}

	public void previousMeasure() {
		// TODO Auto-generated method stub


		this.comp.previousMeasure();

		this.displayMeasure(this.comp.currentMeasure);

	}



	public void broadcastComposition()
	{

	}

	public void broadcastCMeasure()
	{

	}


}
