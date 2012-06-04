package gtcmtswarmbot.mobilecontrol;

import gtcmtswarmbot.mobilecontrol.enums.ControllerCode;
import android.util.Log;

public class HandleStuffThread extends Thread{
	
	DemokitMobileControlActivity mActivity;
	
	long timer;
	long interval;
	int count;
	
	 
	HandleStuffThread(DemokitMobileControlActivity mActivity)
	{
		this.mActivity=mActivity;
		timer=System.currentTimeMillis();
		interval=1000/8;
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
				
				String s = this.mActivity.drawView.bbc.patternToString(b);
				Log.d("","count: " + count + ", " + s);
				//
				this.mActivity.client.sendMessage("controller,"+ ControllerCode.SETSEQUENCE.getCode() + "," + count+ "," + s );
				Log.d("sending patterns","pattern " + count);
				
				timer=System.currentTimeMillis();				
				count++;
			}
			
		}
		
		
	}
	
	

}
