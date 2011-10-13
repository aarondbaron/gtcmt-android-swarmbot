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
	private float tempAzimuth;
	private boolean once;
	private long boundaryTimer;
	private boolean wanderPhase2;
	private boolean wanderPhase1;
	private boolean phase1move;
	private boolean phase2move;

	public Behavior(BoeBotController bbc)
	{
		step = (byte) 10;
		interval=interval/8;
		this.bbc=bbc;
		
		

	}
	void move2Loc(int x,int y)
	{
		
		//step one...find the vector 
		int diffx=bbc.myposx-x;
		int diffy=bbc.myposy-y;
		
		float ang = (float)Math.atan2(diffy,diffx);
		
		//get angle wrt orienation sensor --azimuth
		float currentangle = bbc.angleAzimuth;
		
		// 
		float angleneeded=0;
		
		//phase 1
		//rotate to angle.
		if(ModularDistance((int) currentangle,(int)angleneeded,360) < 10)
		{
			phase1move=false;
			phase2move=true;
		}
		//phase 2
		//forward
		float rad=20;
		if(Math.sqrt( Math.pow((bbc.myposx-x),2) + Math.pow((bbc.myposy-x),2) ) < rad )
		{
			phase1move=true;
			phase2move=false;
		}
		
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
		
		if(boundaryReached() )
		{
			
			wanderPhase1=false;
			wanderPhase2=true;
		}
		else
		{
			wanderPhase1=true;
			wanderPhase2=false;
		}
		

		//for some time shift wheel power down so bot starts to turn
		//WANDER PHASE 1
		if(wanderPhase1)
		{
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
		}

	
		
		//WANDER PHASE 2
		// if reach boundary
		if(System.currentTimeMillis()-boundaryTimer>2000 && wanderPhase2)
		{

			


				//record only once
				if(!once)
				{
					tempAzimuth=bbc.angleAzimuth;
					once=true;

					bbc.rotLeft();//or Right???
				}

				if(ModularDistance((int) tempAzimuth,(int)bbc.angleAzimuth,360) < 90)
				{
					//bbc.stop();
					bbc.forward();
					boundaryTimer=System.currentTimeMillis();
					//need to wait some time before checking boundary again..
					//then release from boundary phase.
					wanderPhase2=false;
					wanderPhase1=true;
					once=false;
				}
			}


		

	}

	boolean boundaryReached()
	{
		int maxh,maxw;
		maxh=1000;
		maxw=1000;
		int buff=50;

		boolean bound =false;
		if(bbc.myposx <0 + buff )
		{
			bound=true;
		}
		if(bbc.myposy<0 + buff)
		{
			bound=true;
		}
		if(bbc.myposx>maxw- buff)
		{
			bound=true;
		}
		if(bbc.myposx>maxh - buff)
		{
			bound=true;
		}


		return bound;
	}



	// Calculates x in modulo m
	public int Mod(int x, int m)
	{
		if (m < 0) m = -m;
		int r = x % m;
		return r < 0 ? r + m : r;
	}
	// Calculates the distance from a to b in modulo m
	public  int ModularDistance(int a, int b, int m)
	{
		return Math.min(Mod(a - b, m), Mod(b - a, m));
	}

}
