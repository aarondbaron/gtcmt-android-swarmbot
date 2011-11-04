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
	public boolean phase1move=true;
	public boolean phase2move;
	public boolean orientComplete;
	public boolean initWanderComplete=false;
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
	

	void move()
	{
		move2Loc(bbc.targetx,bbc.targety);
		//phase1move=true;
	}
	
	void orient()
	{		
		orientComplete=orient2Loc(bbc.targetx,bbc.targety);
		
		//MAybe here
		if(orientComplete)
		{
			bbc.orientToLoc(false);
		}	
	}
	
	boolean orient2Loc(int x,int y)
	{
		int diffx=x-bbc.myposx;
		int diffy=y-bbc.myposy;
		//this is the angle we want to rotate to.
		float ang = (float)Math.toDegrees(Math.atan2(diffy,diffx));
		if(ang<0)
			ang+=360;
		bbc.targetangle=ang;
		float currentangle = bbc.angleAzimuth;
		bbc.modDistance=ModularDistance((int) currentangle,(int)( ang + bbc.calibrationAngle),360);
		int result=ModularDistance2((int)currentangle,(int)( ang + bbc.calibrationAngle),360);

		if(bbc.modDistance < 10)
		{
			bbc.stop();
			Log.d("move","reach target:"+bbc.calibrationAngle);
			return true;
		}
		else
		{
			if(result==-1)
			{
				bbc.writeL(130);//Right
				bbc.writeR(130);
			}
			else
			{
				bbc.writeL(127);//Left
				bbc.writeR(127);
			}
			Log.d("Behavior","rotating" + currentangle+","+bbc.calibrationAngle);
			return false;
		}		

	}
	//this assumes we start out with robot facing in positive x direction and 0 y.
	void move2Loc(int x,int y)
	{
		//step one...find the vector 
		int diffx=x-bbc.myposx;
		int diffy=y-bbc.myposy;
		
		//this is the angle we want to rotate to.
		float ang = (float)Math.toDegrees(Math.atan2(diffy,diffx));
				
		if(phase1move)
		{
			if(ang<0)
				ang+=360;
			bbc.targetangle=ang;
			float currentangle = bbc.angleAzimuth;
			bbc.modDistance=ModularDistance((int) currentangle,(int)( ang + bbc.calibrationAngle),360);
			int result=ModularDistance2((int)currentangle,(int)( ang + bbc.calibrationAngle),360);
	
			if(bbc.modDistance < 10)
			{
				phase1move=false;
				phase2move=true;
				bbc.stop();
				Log.d("move","reach target:"+bbc.calibrationAngle);
			}
			else
			{
				if(result==-1)
				{
					bbc.writeL(130);//Right
					bbc.writeR(130);
				}
				else
				{
					bbc.writeL(127);//Left
					bbc.writeR(127);
				}
				Log.d("Behavior","rotating" + currentangle+","+bbc.calibrationAngle);
			}		
		}
		//phase 2
		
		float rad=50;
		if(phase2move)
		{
			if(Math.sqrt( Math.pow((bbc.myposx-x),2) + Math.pow((bbc.myposy-x),2) ) < rad )
			{
				phase1move=true;
				phase2move=false;
				bbc.stop();
			}
			else
			{
//				bbc.writeL(127);
//				bbc.writeR(129);
				bbc.forward();
			}
		}	
	}
	void follow(Bot bot)
	{

	}

	
	void fullWander()
	{
		int boundary=this.whichBoundaryReached();
		int neighbor=bbc.numNeighbors;
		
		if(boundary>-1)
		{
			if(neighbor==0)
			{
				this.orientComplete=this.orient2Loc(320,240);
				if(this.orientComplete)
				{
					if(!initWanderComplete)
					{
						initWander();
						initWanderComplete=true;
					}
					this.wander();
				}
			}
			else
			{
				
			}
		}
		else//not in the boundary
		{
			this.wander();
			initWanderComplete=false;
		}
		
//		
//		if(!this.boundaryReached()&&bbc.numNeighbors==0)
//		{
//			wander();
//			bbc.clearRhythm(bbc.sfxrseq);
//			bbc.clearRhythm(bbc.instrumentseq);
//		}
//		else//Not ok to wander, have neighbors, or at boundary
//		{
//			int w=this.whichBoundaryReached();
//			switch(w)
//			{
//				case -1:	
//					if(bbc.numNeighbors==0)
//						wander();
//					else{
//						bbc.stop();
//						bbc.fillEuclid(bbc.numNeighbors, bbc.instrumentseq);
//						bbc.fillEuclid(bbc.numNeighbors, bbc.sfxrseq);
//					}
//					break;
//				case 0:	
//					//orientComplete=false;
//					this.orient2Loc(320, 240);
//					break;
//				case 1:		
//					this.orient2Loc(320, 240);
//					break;
//				case 2:
//					this.orient2Loc(320, 240);
//					break;
//				case 3:
//					this.orient2Loc(320, 240);
//					break;
//			}
//		}
		
		
	}

	public void initWander()
	{
		bbc.forward();
		middleWait=false;
		//timer=System.currentTimeMillis();
		m1=false;
		m2=true;
		m1IncDec=false;
		m2IncDec=false;
		
	}
	
	public void wander()
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

	
//		
//		//WANDER PHASE 2
//		// if reach boundary
//		if(System.currentTimeMillis()-boundaryTimer>2000 && wanderPhase2)
//		{
//
//			
//
//
//				//record only once
//				if(!once)
//				{
//					tempAzimuth=bbc.angleAzimuth;
//					once=true;
//
//					bbc.rotLeft();//or Right???
//				}
//
//				if(ModularDistance((int) tempAzimuth,(int)bbc.angleAzimuth,360) < 90)
//				{
//					//bbc.stop();
//					bbc.forward();
//					boundaryTimer=System.currentTimeMillis();
//					//need to wait some time before checking boundary again..
//					//then release from boundary phase.
//					wanderPhase2=false;
//					wanderPhase1=true;
//					once=false;
//				}
//			}

	}

	public boolean boundaryReached()
	{
		int maxh,maxw;
		maxh=480;
		maxw=640;
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
	
	
	public int whichBoundaryReached()
	{
		int maxh,maxw;
		maxh=480;
		maxw=640;
		int buff=50;

		boolean bound =false;
		if(bbc.myposx <0 + buff )
		{
			bound=true;
			return 0;
		}
		if(bbc.myposy<0 + buff)
		{
			bound=true;
			return 1;
		}
		if(bbc.myposx>maxw- buff)
		{
			bound=true;
			return 2;
		}
		if(bbc.myposx>maxh - buff)
		{
			bound=true;
			return 3;
		}


		return -1;
	}
	
	

	// Calculates x in modulo m
	public int Mod(int x, int m)
	{
		if (m < 0) m = -m;
		int r = x % m;
		return r<0?r+m:r;
	}
	// Calculates the distance from a to b in modulo m
	public  int ModularDistance(int a, int b, int m)
	{
		return Math.min(Mod(a - b, m), Mod(b - a, m));
	}
	// Calculates the distance from a to b in modulo m
	public  int ModularDistance2(int a, int b, int m)
	{
		if(Mod(a-b,m)<Mod(b-a,m))
			return 1;//Left
		else
			return -1;//Right
	}

}
