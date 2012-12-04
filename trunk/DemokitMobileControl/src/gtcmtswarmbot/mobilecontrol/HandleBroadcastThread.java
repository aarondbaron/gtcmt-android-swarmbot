package gtcmtswarmbot.mobilecontrol;

import gtcmtswarmbot.mobilecontrol.enums.ControllerCode;
import android.util.Log;

public class HandleBroadcastThread extends Thread{
	
	DemokitMobileControlActivity mActivity;
	
	long timer;
	long interval;
	int count;
	
	CMeasure m;
	 
	HandleBroadcastThread(DemokitMobileControlActivity mActivity)
	{
		this.mActivity=mActivity;
		timer=System.currentTimeMillis();
		interval=1000/8;
		m = mActivity.sc.myComposition.currentMeasure;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean[][] vals = this.mActivity.drawView.thread.songMaker.g.gridVals;
		
		while(count<vals.length)
		{
			if(System.currentTimeMillis()-timer>interval)
			{
				//boolean[][] vals = this.mActivity.drawView.thread.songMaker.g.gridVals;
				
				boolean[] b = vals[count];
				
				if(b.length== this.mActivity.drawView.thread.songMaker.TRUELENGTH/2)
				{
					boolean[] b2 = new boolean[b.length*2];
					
					for(int i=0;i<b.length;i++)
					{
						b2[i*2]=b[i];
					}
					
					b=b2;
				}
				
				String s = this.mActivity.drawView.bbc.patternToString(b);
				Log.d("","count: " + count + ", " + s);
				//
				this.mActivity.client.sendMessage("controller,"+ ControllerCode.SENDCMEASURE.getCode() + "," + m.ID+ "," + count+ "," + s );
				Log.d("sending CMeasure","pattern " + count);
				
				timer=System.currentTimeMillis();				
				count++;
			}
			
		}
		
		
	}
	
	

}
