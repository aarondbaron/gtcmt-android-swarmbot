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

	public long fullwanderboundarytimer;
	private boolean timeoutofboundary;
	public long fullwanderplaywithneighbortimer;
	private boolean playwithneighborfirsttime;
	private long danceTimer;


	boolean avBoundFwdReal;
	boolean boundOk;
	long avoidBoundTimer;
	
	PVector desiredVel;

	public Behavior(BoeBotController bbc)
	{
		step = (byte) 10;
		interval=interval/16;
		this.bbc=bbc;		
		int off=20;
		int off2=4;

		lowm1=  128+off2;
		//highm1= 255-off;
		highm1=128+off;

		lowm2 = 128-off2;  //iteresting to see what happens if these values were randomized
		//highm2 =  0+off;
		highm2=128-off;

		delta=  1;

		m1=false;
		m2=true;

		desiredVel = new PVector();
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
		//float currentangle = bbc.angleAzimuth;
		float currentangle = bbc.camang;

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
				bbc.writeL(128+7);//Right
				bbc.writeR(128+7);
			}
			else
			{
				bbc.writeL(128-7);//Left
				bbc.writeR(128-7);
			}
			Log.d("Behavior","rotating" + currentangle+","+bbc.calibrationAngle);
			return false;
		}		

	}
	//this assumes we start out with robot facing in positive x direction and 0 y...if not using camang
	void move2Loc(int x,int y)
	{
		//step one...find the vector 
		int diffx=x-bbc.myposx;
		int diffy=y-bbc.myposy;

		//this is the angle we want to rotate to.
		float ang = (float)Math.toDegrees(Math.atan2(diffy,diffx));

		if(ang<0)
			ang+=360;
		bbc.targetangle=ang;
		//float currentangle = bbc.angleAzimuth;
		float currentangle = bbc.camang;
		bbc.modDistance=ModularDistance((int) currentangle,(int)( ang + bbc.calibrationAngle),360);
		int result=ModularDistance2((int)currentangle,(int)( ang + bbc.calibrationAngle),360);

		double distToTarget = Math.sqrt( Math.pow((bbc.myposx-x),2) + Math.pow((bbc.myposy-x),2) );

		if(phase1move)
		{


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
					bbc.writeL(128+7);//Right
					bbc.writeR(128+7);
				}
				else
				{
					bbc.writeL(128-7);//Left
					bbc.writeR(128-7);
				}
				Log.d("Behavior","rotating" + currentangle+","+bbc.calibrationAngle);
			}		
		}
		//phase 2

		float rad=50;
		if(phase2move)
		{

			if(distToTarget < rad )
			{
				phase1move=true;
				phase2move=false;
				bbc.stop();
			}
			else
			{
				if(bbc.modDistance>15)
				{
					phase1move=true;
					phase2move=false;
				}
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
			fullwanderboundarytimer=System.currentTimeMillis();
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
			if(System.currentTimeMillis()-fullwanderboundarytimer>4000)
			{
				timeoutofboundary=true;//remember to use this later 
				if(bbc.numNeighbors!=0)
				{
					bbc.stop();
					bbc.fillRhythm(bbc.numNeighbors, bbc.instrumentseq);
					bbc.fillRhythm(bbc.numNeighbors, bbc.sfxrseq);
					if(!playwithneighborfirsttime)
					{
						playwithneighborfirsttime=true;
						fullwanderplaywithneighbortimer=System.currentTimeMillis();
					}
				}
				else
				{
					this.wander();
					bbc.fillRhythm(1, bbc.instrumentseq);
					bbc.fillRhythm(1, bbc.sfxrseq);
					initWanderComplete=false;
				}

			}
			else
			{

			}


			/*
			if(bbc.numNeighbors!=0)
			{

			}
			else
			{

			}
			this.wander();
			initWanderComplete=false;
			 */
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
		//bbc.forward();
		bbc.writeL(148);
		bbc.writeR(108);
		middleWait=false;
		timer=System.currentTimeMillis();
		m1=true;
		m2=false;
		m1IncDec=false;
		m2IncDec=false;

		this.avBoundFwdReal=false;



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
			if(System.currentTimeMillis()-middleTimer>250)
			{
				middleWait=false;
				Log.d("wander wait", "middlewait ended");
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

					//increment speed phase Left
					if(m1IncDec)
					{
						Log.d("Behavior","increment speed phase left");
						if(bbc.getLByte()<highm1)
						{
							bbc.writeL(  (bbc.getLByte()+ delta)  ); //pos1+=delta
						}
						else
						{
							Log.d("BehaviorSwitch","speedhaseleftswitch");
							//time to switch
							m1=false;
							m2=true;

							m1IncDec=false;
						}
					}
					else
					{
						//decrement speed phase Left
						Log.d("Behavior","decrement speed phase left");
						if(bbc.getLByte()>lowm1)
						{
							bbc.writeL( (bbc.getLByte() - delta) ); // pos1-=delta
						}
						else
						{
							Log.d("BehaviorSwitch","speedhaseleftswitch");
							//time to switch
							//m1=false;
							//m2=true;
							m1IncDec=true;							


							//time to wait?
							middleTimer=System.currentTimeMillis();///this isn't actually middle here
							middleWait=true;
							Log.d("wander wait", "middlewait started");

						}
					}
				}


				if(m2)
				{


					if(m2IncDec)
					{
						//increment speed phase RIGHT
						Log.d("Behavior","increment spheed phase right");
						if(bbc.getRByte()>highm2)
						{
							bbc.writeR( ( bbc.getRByte()-delta)  ) ; // pos2-=delta;
						}
						else
						{
							//time to switch
							Log.d("BehaviorSwitch","increment spheed phase right");
							m1=true;
							m2=false;

							m2IncDec=false;
						}
					}
					else
					{
						//decrement speed phase Right
						Log.d("Behavior","decrement spheed phase right");
						if(bbc.getRByte()<lowm2)
						{
							bbc.writeR(  (bbc.getRByte()+delta));//  pos2+=delta;
						}
						else
						{
							//time to switch
							Log.d("BehaviorSwitch","increment spheed phase right");
							//m1=true;
							//m2=false;          
							m2IncDec=true;

							//time to wait?
							middleTimer=System.currentTimeMillis();//this shoudlnt be here but up above in stead.
							middleWait=true;
							Log.d("wander wait", "middlewait started");

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
		if(bbc.myposy>maxh - buff)
		{
			bound=true;
		}


		return bound;
	}

	public void avoidBoundary()
	{
		boolean checkFlag=false;
		long tempTimer=System.currentTimeMillis();

		if(boundaryReached())
		{
			//under assumption that we always move forward
			PVector v = new PVector();
			v.x=bbc.vxs[bbc.vxyindex];
			v.y=bbc.vxs[bbc.vxyindex];
			PVector loc = new PVector(bbc.myposx,bbc.myposy);
			float ang=bbc.camang;

			/*
			float a=0;
			for(int i=0;i<bbc.aest.length;i++)
			{
				a+=bbc.aest[i];

			}
			a =a /(float)bbc.aest.length;
			 */
			float a=ang;
			//if()
			int w = whichBoundaryReached();
			boolean cond1,cond2;
			int wiggle=10;
			switch(w)
			{
			case 0: 

				cond1 = a>180 && a< 270+wiggle;
				cond2 = a>90-wiggle && a<180;
				if(cond1)
				{
					bbc.rotRight();//?
				}

				if(cond2)
				{
					bbc.rotLeft();//?
				}

				if( !(  cond1||cond2 ) )
				{
					this.boundOk=true;
					this.avoidBoundTimer=System.currentTimeMillis();
					bbc.forward();
				}
				break;

			case 1:

				cond1=a>270 && ( a< 360  || a<0+wiggle);
				cond2= a>180 -wiggle && a<270;
				if(cond1)
				{
					bbc.rotRight();//?
				}

				if(cond2)
				{
					bbc.rotLeft();//?
				}

				if( !(  cond1||cond2 ) )
				{
					this.boundOk=true;
					this.avoidBoundTimer=System.currentTimeMillis();
					bbc.forward();
				}
				break;

			case 2: 

				cond1=a>0 && a< 90+wiggle;
				cond2= a>270 -wiggle && a<360;
				if(cond1)
				{
					bbc.rotRight();//?
				}

				if(cond2)
				{
					bbc.rotLeft();//?
				}

				if( !(  cond1||cond2 ) )
				{
					this.boundOk=true;
					this.avoidBoundTimer=System.currentTimeMillis();
					bbc.forward();
				}
				break;

			case 3:
				cond1=a>90 && a< 180 +wiggle;
				cond2= (a>0 || a>360-wiggle) && a<90;
				if(cond1)
				{
					bbc.rotRight();//?
				}

				if(cond2)
				{
					bbc.rotLeft();//?
				}

				if( !(  cond1||cond2 ) )
				{
					this.boundOk=true;
					this.avoidBoundTimer=System.currentTimeMillis();
					bbc.forward();
				}
				break;
			default: ;

			}
			//bbc.backward();
			checkFlag=true;

		}
		else
		{

		}

		if(checkFlag)
		{
			if(System.currentTimeMillis()-tempTimer>1000)
			{
				checkFlag=false;
				bbc.forward();
			}
		}

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
		if(bbc.myposy>maxh - buff)
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

	//this was a 
	public void steer(float str, float dir)
	{
		if(str<=.01)
		{		
			bbc.writeR(128);
			bbc.writeL(128);
			return;
		}

		bbc.writeR(0);
		bbc.writeL(0);

	}

	public void doSteer()
	{
		//look at current position, bearing, velocity
		
		float currentAngle = bbc.camang;
		
		//look at desired vel
		PVector v=this.desiredVel;		
		
		// now start to change motor speed to adjust to new 
		if(currentAngle<v.heading2D())
		{
			//increase motor speed??
		}
		else
		{
			//decrease motorspeed??
		}
		

	}

	public void doMove()
	{
		float maxspeed = 2;
		PVector vel = new PVector();
		PVector acc = new PVector();
		PVector loc = new PVector();

		vel.add(acc);
		// Limit speed
		vel.limit(maxspeed);
		loc.add(vel);///will replace this with doVelocity..which corresponds to boe bot controls
	}

	public void doVel(PVector vel, PVector loc, float angle)//given where you are and orientation, and your desired? velocity..make this work 
	{
		float vmag= vel.mag();
		PVector temp = new PVector(loc.x+vel.x, loc.y+vel.y );
		float vang= vel.angleBetween(temp, loc);

		float dif = vang-angle;

		//imagine 90 deg is wher we want.. and we are at 269 (-91)
		// we turn right maximally..meaning that right wheel is full throttle, left is full throttle other direction
		// as the angle decreases, the left wheel starts to slow down to 128 and then when it is 0 it should be full throttle in same direction


		if(dif>0)
		{
			//turn left "faster" depending on how far away and a how;
			int m=440;
			m = (int)bbc.map(dif,0,180,255/*??*/,128);
			bbc.writeL(m);
			bbc.writeR(0);//??
		}
		else
		{
			//turn right


			int m=440;
			m = (int)bbc.map(dif,0,-180,0/*??*/,128);
			bbc.writeR(m);
			bbc.writeL(255);//??
		}

	}

	public void timedDance(long t)
	{
		if(System.currentTimeMillis()-danceTimer<t)
		{
			bbc.danceSequencer=true;
			//bbc.euclidDance();
		}
		else
		{
			bbc.danceSequencer=false;
		}

	}

	public void seek(PVector target)
	{

	}

	PVector getPointBehindLeader(Bot target)
	{
		PVector tloc= new PVector(target.x, target.y);   
		PVector behind = new PVector(tloc.x, tloc.y);

		float distBehind=50;

		//float theta = target.vel.heading2D() + radians(90);

		/// how can you get this  must transmit?????
		//PVector nVel = new PVector(-target.vel.x, -target.vel.y);
		//in the meantime..
		PVector nVel = new PVector(0, 0);
		nVel.normalize();

		behind.x += distBehind*nVel.x;
		behind.y += distBehind*nVel.y;

		return behind;
	}



	///////////////
	//djemble behavior
	//1.  

}
