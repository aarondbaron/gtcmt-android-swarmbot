package gtcmtswarmbot.mobilecontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Sequencer {

	boolean[] seq;
	PVector loc;
	ArenaView v;
	
	Paint p;
	int sz=60;
	
 	Sequencer(ArenaView v)
	{
 		
 		
		this.v=v;
		seq = new boolean[48];
		loc = new PVector(sz,v.getHeight()-v.getHeight()/2);
		
		p = new Paint();
		p.setColor(Color.RED);
		p.setAntiAlias(true);
		p.setStyle(Paint.Style.STROKE);
		
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
 			c.drawRect(r, p);
 			
 			
 			if(i%12==0)
 			{
 				//yinc++;
 				//xinc=0;
 			}
 			
 		}
 	}
 	
}
