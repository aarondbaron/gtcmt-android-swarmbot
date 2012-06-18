package com.google.android.DemoKit;

public class NeighborThread extends Thread{

	BoeBotController bbc;
	NeighborThread(BoeBotController bbc)
	{
		this.bbc=bbc;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true)
		{
			
			
			if(System.currentTimeMillis()-bbc.extendedNeighborsTimer>250)
			{
				
				for(int i =0 ; i < bbc.otherBots.size();i++)
				{
					Bot b = bbc.otherBots.get(i);
					b.getNeighbors();
				}
				
				Bot b = new Bot(bbc.myposx,bbc.myposy,bbc);
				b.neighbors=bbc.myNeighbors;
				bbc.myExtendedNeighbors=b.getExtendedNeighbors();
				
				//this.myExtendedNeighbors=getExtendedNeighbors();
				bbc.resetQuery();
				//Log.d("bbc","extended neighbors");
				bbc.extendedNeighborsTimer+=250;
			}
			
			
			
		}
		
	}

}
