package com.google.android.DemoKit;

import android.util.Log;

public class Behavior
{
	private boolean LRMotorSweep;
	private boolean incDec;

	byte step;

	boolean m1, m2,middleWait;
	boolean m1IncDec, m2IncDec;
	long middleTimer;

	int lowm1, highm1, lowm2 , highm2 , delta;

	//int pos1;// = 180;    // variable to store the servo position 
	//int pos2; //= 180;

	long timer;
	long interval=1000;  //1 second  -- will change in constructor
	BoeBotController bbc;
	private float tempAzimuth;
	private boolean once;
	private long boundaryTimer;
	private boolean wanderPhase2;
	private boolean wanderPhase1;
	private boolean phase1move;
	private boolean phase2move;
	
	//private float trueAz;
	
	

	public Behavior(BoeBotController bbc)
	{
		step = (byte) 10;
		interval=interval/8;
		this.bbc=bbc;
		
		
		int off=100;
		
		lowm1=  128;
		highm1= 255-off;
		
		lowm2 = 128;
		highm2 =  0+off;
		
		delta=  1;
		
		m1=false;
		m2=true;

	}
	

	
	//this assumes we start out with robot facing in positive x direction and 0 y.
	void move2Loc(int x,int y)
	{
		
		//step one...find the vector 
		int diffx=bbc.myposx-x;
		int diffy=bbc.myposy-y;
		
		//this is the angle we want to rotate to.
		float ang = (float) Math.atan2(diffy,diffx) + (float)Math.PI;
		
		//get angle wrt orienation sensor --azimuth
		float currentangle = bbc.angleAzimuth;
		//bbc.calibrationAngle;
		
		//phase 1
		//rotate to angle.
		
		if(ModularDistance((int) currentangle,(int) ang + (int)bbc.calibrationAngle,360) < 10)
		{
			phase1move=false;
			phase2move=true;
			//bbc.forward();
			bbc.stop();
		}
		else
		{
			bbc.rotLeft();
		}
		//phase 2
		//forward
		/*
		float rad=20;
		if(Math.sqrt( Math.pow((bbc.myposx-x),2) + Math.pow((bbc.myposy-x),2) ) < rad )
		{
			phase1move=false;
			phase2move=false;
			bbc.stop();
		}
		*/
		
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
		
		/*
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
		*/
		wanderPhase1=true;
		wanderPhase2=false;
		
		//middlewait check
		if(middleWait)
		{
			//wait sometime
			if(System.currentTimeMillis()-middleTimer>500)
			{
				middleWait=false;
			}
		}
		
		//checkifnearNeigbhor...
		if(bbc.numNeighbors>0)
		{
			wanderPhase1=false;
			wanderPhase2=false;
			bbc.stop();
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
			//Log.d("Behavior","wanderphase1");
			if(System.currentTimeMillis()-timer>interval && !middleWait)
			{
				timer+=interval;
				Log.d("Behavior","LByte:" + bbc.getLByte() + " RByte:" + bbc.getRByte());

				if(m1)
				{
					Log.d("Behavior","motor1");
					//increment speed phase Left
					if(m1IncDec)
					{
						if(bbc.getLByte()<highm1)
						{
							bbc.writeL(  (bbc.getLByte()+ delta)  ); //pos1+=delta
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
							bbc.writeL( (bbc.getLByte() - delta) ); // pos1-=delta
						}
						else
						{
							//time to switch
							//m1=false;
							//m2=true;
							m1IncDec=true;							
							//time to wait?
							middleTimer=System.currentTimeMillis();
							middleWait=true;
						}
					}
				}


				if(m2)
				{
					Log.d("Behavior","motor2");

					if(m2IncDec)
					{
						//increment speed phase RIGHT
						if(bbc.getRByte()>highm2)
						{
							bbc.writeR( ( bbc.getRByte()-delta)  ) ; // pos2-=delta;
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
							bbc.writeR(  (bbc.getRByte()+delta));//  pos2+=delta;
						}
						else
						{
							//time to switch
							//m1=true;
							//m2=false;          
							m2IncDec=true;
							//time to wait
							middleTimer=System.currentTimeMillis();
							middleWait=true;
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
