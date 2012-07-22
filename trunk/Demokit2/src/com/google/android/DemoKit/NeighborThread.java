package com.google.android.DemoKit;


import java.util.Collections;


public class NeighborThread extends Thread{

	BoeBotController bbc;

	String inter;
	NeighborThread(BoeBotController bbc)
	{
		this.bbc=bbc;
		inter = "";
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

		while(true)
		{


			if(System.currentTimeMillis()-bbc.extendedNeighborsTimer>250)
			{
				
				bbc.allowedToCheck=false;
				
				if(bbc.cn)
				{
					bbc.CN();
					bbc.cn=false;
				}
				
				
				bbc.numberOfNeigbhors();


				for(int i =0 ; i < bbc.otherBots.size();i++)
				{
					Bot b = bbc.otherBots.get(i);
					b.getNeighbors();
				}

				Bot b = new Bot(bbc.myposx,bbc.myposy,bbc);
				b.ID=-99;
				b.neighbors=bbc.myNeighbors;
				 
				bbc.myExtendedNeighbors=b.getExtendedNeighbors();

				for(int i=0;i<bbc.myExtendedNeighbors.size();i++)
				{
					Bot k = bbc.myExtendedNeighbors.get(i);
					if(k.ID==bbc.ID)
					{
						bbc.myExtendedNeighbors.remove(k);
						//bbc.p=true;
					}


					if(!bbc.rfv.drawFace)
					{
						inter ="isz:" + bbc.myExtendedNeighbors.size() + ":-:";
						this.inter+=k.ID + ",";
					}
				}

				bbc.myExtendedNeighbors.remove(b);



				//this.myExtendedNeighbors=getExtendedNeighbors();
				bbc.resetQuery();
				//Log.d("bbc","extended neighbors");
				bbc.extendedNeighborsTimer+=250;

				bbc.allowedToCheck=true;

				BotComparator bc = new BotComparator(); 
				bc.compareBy("ID");
				Collections.sort(bbc.myExtendedNeighbors, bc);
			}



		}

	}

	public void sortBy(String s)
	{
		BotComparator bc = new BotComparator(); 
		bc.compareBy(s);   
		Collections.sort(bbc.otherBots, bc);
		Collections.sort(bbc.myNeighbors, bc);
		//Collections.sort(bbc.myExtendedNeighbors, bc);
	}

}
