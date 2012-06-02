package gtcmtswarmbot.mobilecontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Sequencer {

	boolean[] seq;
	PVector loc;
	DrawView v;

	Paint p,p2,p3;
	int sz=60;

	Sequencer(DrawView v)
	{


		this.v=v;
		seq = new boolean[48];
		loc = new PVector(sz,v.getHeight()-v.getHeight()/2);

		p = new Paint();
		p.setColor(Color.RED);
		p.setAntiAlias(true);
		p.setStyle(Paint.Style.STROKE);
		
		p2 = new Paint();
		p2.setColor(Color.GREEN);
		p2.setAntiAlias(true);
		//p2.setStyle(Paint.Style.STROKE);
		
		p3 = new Paint();
		p3.setColor(Color.YELLOW);
		p3.setAntiAlias(true);
		//p3.setStyle(Paint.Style.STROKE);
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
		int col=0;
		int row=0;
		for(int i = 0 ; i <seq.length;i++)
		{
			row=i%12;
			col=i/12;

			//Rect r = new Rect(leftx, topy, rightx, bottomy);

			Rect r = new Rect((int)(loc.x-sz/2+sz*row), (int)(loc.y-sz/2 +col*sz), (int)( loc.x+sz/2 +sz*row ),(int) loc.y+sz/2  +col*sz);
			if(seq[i])
			{
				c.drawRect(r, p2);
			}
			else
			{
				c.drawRect(r, p);
			}
			
			if(i==v.bbc.currentIndex)
			{
				c.drawRect(r, p3);
			}
			


			if(i%12==0)
			{
				//yinc++;
				//xinc=0;
			}

		}
	}

	String patternToString(boolean[] b)
	{
		String s = "";

		for(int i=0; i < b.length; i++)
		{
			if(b[i])
			{
				s += "1";
			}
			else
			{
				s+="0";
			}
		}

		return s;
	}

	String getMySequence()
	{
		return patternToString(seq);
	}



	public void checkInside(int x, int y) {
		// TODO Auto-generated method stub


		for(int i=0;i <seq.length;i++)
		{
			int row=i%12;
			int col=i/12;
			
			int leftx=	(int)(loc.x-sz/2+sz*row);
			int topy = (int)(loc.y-sz/2 +col*sz);
			int rightx=(int)( loc.x+sz/2 +sz*row );
			int bottomy= (int) (loc.y+sz/2  +col*sz);
			
			if(x > leftx && x < rightx && y>topy && y<bottomy)
			{
				seq[i]=!seq[i];
			}
		}
	}

}
