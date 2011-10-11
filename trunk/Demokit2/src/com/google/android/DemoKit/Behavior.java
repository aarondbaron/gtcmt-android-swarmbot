package com.google.android.DemoKit;

public class Behavior
{
	private boolean LRMotorSweep;
	private boolean incDec;

	byte step;

	boolean m1, m2;
	boolean m1IncDec, m2IncDec;

	int lowm1, highm1, lowm2 , highm2 , delta;

	//int pos1;// = 180;    // variable to store the servo position 
	//int pos2; //= 180;

	long timer;
	long interval=1000000;  //1 second  -- will change in constructor
	BoeBotController bbc;
	public Behavior(BoeBotController bbc)
	{
		step = (byte) 10;
		interval=interval/8;
		 this.bbc=bbc;
		
	}
	void move2Loc(int x,int y)
	{
		
	}
	void follow(Bot bot)
	{
		
	}
	
	

	void wander()
	{
		//Description..
		//behavior1
		//initially it will move forward
		//then one wheel will decrease to 128 
		// that wheel will then increase back up to 255 
		// the next wheel will decrease to 128
		//then increase to 255.
		//behavior 2
		// when encountering the edge, it must turn appropriately and after angle is correct, or time, it will 
		//go back to behavior1


		//move forward for sometime by default..



		////assuming m1 L m2 R

		//for some time shift wheel power down so bot starts to turn
		if(System.currentTimeMillis()-timer>interval)
		{
			timer+=interval;

			if(m1)
			{
				//increment speed phase Left
				if(m1IncDec)
				{
					if(bbc.getLByte()<highm1)
					{
						bbc.writeL( (byte) (bbc.getLByte()+ (byte)delta)  ); //pos1+=delta
					}
					else
					{
						//time to switch
						m1=false;
						m2=true;

						m1IncDec=false;
					}
				}
				else
				{
					//decrement speed phase Left
					if(bbc.getLByte()>lowm1)
					{
						bbc.writeL((byte) (bbc.getLByte() - (byte)delta) ); // pos1-=delta
					}
					else
					{
						//time to switch
						//m1=false;
						//m2=true;
						m1IncDec=true;
					}
				}
			}


			if(m2)
			{


				if(m2IncDec)
				{
					//increment speed phase RIGHT
					if(bbc.getRByte()>highm2)
					{
						bbc.writeR((byte)( bbc.getRByte()-delta)  ) ; // pos2-=delta;
					}
					else
					{
						//time to switch
						m1=true;
						m2=false;

						m2IncDec=false;
					}
				}
				else
				{
					//decrement speed phase Right
					if(bbc.getRByte()<lowm2)
					{
						bbc.writeR((byte) (bbc.getRByte()+delta));//  pos2+=delta;
					}
					else
					{
						//time to switch
						//m1=true;
						//m2=false;          
						m2IncDec=true;
					}
				}
			}			
		}
		
		// if reach boundary
		if(boundaryReached() )
		{
			int xbound=10;
			if(bbc.myposx < 0 + xbound)
			{
				//get what current angle is
				if(bbc.myangle>90 || bbc.myangle <270)
				{
					//this should probably be rotate until something
					bbc.rotLeft();
				}
				else
				{

					//rotate until some condition
					bbc.rotRight();
				}
			}
		}

	}

	boolean boundaryReached()
	{
		int maxh,maxw;
		maxh=1000;
		maxw=1000;

		if(bbc.myposx <0 )
		{

		}
		if(bbc.myposy<0)
		{

		}
		if(bbc.myposx>maxw)
		{

		}
		if(bbc.myposx>maxh)
		{

		}


		return true;
	}

}
