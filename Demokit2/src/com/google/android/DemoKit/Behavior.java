package com.google.android.DemoKit;

import java.text.DecimalFormat;

import android.os.Handler;
import android.util.Log;

public class Behavior extends Thread
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

	float desiredseparation=50;

	long vmtimer;
	long vmInterval=500;

	long wanderVectorTimer;
	PVector wanderVectorTemp;

	long breath1Timer1;
	long breath1Timer2;


	public long breath2Timer1;

	boolean behaviorRunning=true;


	boolean logger=false;
	//////flags for behaviors
	public boolean orient2Loc;
	public boolean wander;
	public boolean wanderDance;
	public boolean wanderDanceOnce;
	public long wanderDanceTimer;
	public boolean wanderVector;
	public boolean moveVector;
	public boolean move2Loc;
	public boolean separation;
	public boolean alignment;
	public boolean cohesion;
	public boolean followInLine;

	public Handler handler =new Handler();

	public DemoKitActivity mActivity;
	public boolean followMouse;
	public boolean orbitCenter;	
	float orbitDist=200;
	public boolean orbitAvatar;
	public boolean useBeacon;
	public boolean fleeBeacon;
	public boolean orbitInLine;
	public boolean orbitClockwise;
	public String formationType;
	public boolean formation;
	private boolean wto1;
	private boolean wtf1;
	public boolean wanderThenFollow;
	public boolean wanderThenOrbit;
	Bot botTarget;
	Bot wtfilTarget;
	Bot wtoilTarget;
	public boolean wanderThenFollowInLine;
	public boolean wanderThenOrbitInLine;
	public boolean wtfilInitial;
	public boolean wtfilFinalTargetReached;
	public boolean wtoilInitial;
	public boolean wtoilFinalTargetReached;
	
	public boolean moveForward;
	public boolean moveBackward;
	public boolean rotateLeft;
	public boolean rotateRight;
	
	float[] sacWeights;
	private boolean breath1;
	private boolean breath2;

	public Behavior(DemoKitActivity mActivity /*BoeBotController bbc*/)
	{
		this.mActivity=mActivity;

		step = (byte) 10;
		interval=interval/16;
		this.bbc=bbc;		
		int off=20;
		int off2=4;

		lowm1=  128+off2;
		//highm1= 255-off;
		highm1=128+off;

		lowm2 = 128-off2;  //interesting to see what happens if these values were randomized
		//highm2 =  0+off;
		highm2=128-off;

		delta=  1;

		m1=false;
		m2=true;

		desiredVel = new PVector(0,0);

		vmtimer=System.currentTimeMillis();
		wanderVectorTimer =vmtimer;
		wanderVectorTemp = new PVector();
		
		sacWeights = new float[]{1,1,1};
		
	}

	public void run()
	{
		//behaviorRunning=true;
		while(behaviorRunning)
		{
			//Log.d("behavior","running");
			if(orient2Loc)
			{
				//bbc.myBehavior.desiredVelocity.add(orient??);


			}

			if(orient2Loc)
			{
				orient();
			}
			if(wander)
			{

				Log.d("behavior", "wander");
				if(boundaryReached())
				{
					//bbc.myBehavior.fullWander();

					//if they get near a neigbhor?
					if(!initWanderComplete)
					{
						initWander();
						initWanderComplete=true;
					}
					wander();
				}
				else
				{						
					/*
						////////////////////////////////////////
						bbc.stop();
						bbc.myBehavior.initWanderComplete=false;
						//////////////////////////////////////////
					 */

					//slow down
					//do forwardreal once
					if(!bbc.myBehavior.avBoundFwdReal)
					{
						bbc.forwardReal();
						avBoundFwdReal=!avBoundFwdReal;							
					}

					avoidBoundary();	

				}

			}

			if(wanderDance)
			{

				if(!boundaryReached())
				{
					Log.d("behavior", "wanderDance ");
					if(bbc.numNeighbors==0 && !wanderDanceOnce)
					{
						if(!initWanderComplete)
						{
							initWander();
							initWanderComplete=true;

						}
						wander();
						wanderDanceOnce=false;
						bbc.danceSequencer=false;
					}
					else
					{
						Log.d("behavior", "wanderDance " + bbc.numNeighbors);
						initWanderComplete=false;

						if(!wanderDanceOnce)
						{
							bbc.euclidDance();
							wanderDanceOnce=true;
							bbc.danceSequencer=true;
							wanderDanceTimer=System.currentTimeMillis();
						}

						//dance for x seconds
						if(System.currentTimeMillis()-wanderDanceTimer> 1000*10)
						{

							//bbc.euclidDance();//eudance();
							//transtion to next stage of wandering to escape--meaning ignore for a couple seconds
							//for now just stop
							bbc.danceSequencer=false;
							bbc.stop();

						}
						else
						{

						}


					}

				}
				else
				{
					bbc.stop();
					initWanderComplete=false;
				}




			}

			if(wanderVector)
			{
				wanderVector();
			}

			if(wanderThenFollow)
			{
				this.wanderThenFollow();
			}
			if(wanderThenOrbit)
			{
				this.wanderThenOrbit();
			}
			if(wanderThenFollowInLine)
			{
				this.wanderThenFollowInLine();
			}
			if(wanderThenOrbitInLine)
			{
				this.wanderThenOrbitInLine();
			}


			if(move2Loc)
			{
				Log.d("behavior", "move2Loc ");
				moveTo(new PVector(bbc.targetx,bbc.targety));

				/*
				handler.post(new Runnable() {
					@Override
					public void run() {
						bbc.move2locLabel.setText("angle: "+bbc.modDistance+
								"\ncalibrate ang="+bbc.calibrationAngle+
								"\ntargetx="+bbc.targetx+
								"\ntargetx="+bbc.targety);
					}
				});
				 */

			}


			if(useBeacon)
			{
				Log.d("behavior", "useBeacon ");
				desiredVel.add(bbc.myBehavior.separateBeacon());
			}

			if(separation)
			{
				Log.d("behavior","separation " + desiredVel);
				PVector v = bbc.myBehavior.separate();
				v.mult(sacWeights[0]);
				desiredVel.add(v);
			}


			if(alignment)
			{
				Log.d("behavior","alignment " + desiredVel);
				PVector v =bbc.myBehavior.align();
				v.mult(sacWeights[1]);
				desiredVel.add(v);
			}

			if(cohesion)
			{
				Log.d("behavior","cohesion " + desiredVel);
				PVector v =bbc.myBehavior.cohesion();
				v.mult(sacWeights[2]);
				desiredVel.add(v);
			}

			if(formation)
			{
				doFormation();
			}
			if(breath1)
			{
				this.breath1();
			}
			if(breath2)
			{
				this.breath2();
			}
			if(followInLine)
			{
				Log.d("behavior","followInLine " + desiredVel);
				followInLine();				
			}
			if(orbitInLine)
			{
				Log.d("behavior","orbitInLine " + desiredVel);
				orbitInLine();
			}

			if(followMouse)
			{
				Log.d("behavior","followMouse " + desiredVel);
				followMouse();
			}

			if(orbitCenter)
			{
				Log.d("behavior","orbitCenter " + desiredVel);
				orbitCenter();
			}
			if(orbitAvatar)
			{
				Log.d("behavior","orbitAvatar " + desiredVel);
				orbitAvatar();
			}
			
			if(moveForward)
			{
				this.forwardVector();
			}
			if(moveBackward)
			{
				
			}
			if(rotateLeft)
			{
				this.turnLeftVector();
			}
			if(rotateRight)
			{
				this.turnRightVector();
			}


			//Log.d("behavior","" + desiredVel);
			//finally act on velocity
			//bbc.myBehavior.doMove();
			if(bbc!=null)
			{
				if(!bbc.directControl)
				{
					avoidBoundary3();
					doSteer2();
					desiredVel.mult(0f);
				}
			}
		}
	}


	public PVector forwardVector() {
		// TODO Auto-generated method stub
		
 
		int angle1=(int)bbc.camang;
		if(angle1<0)
		{
			angle1=360+angle1;
		}
 
		
		int r=10;
		float xx=(float) (r*Math.cos(Math.toRadians(angle1)));
		float yy= (float) (r*Math.sin(Math.toRadians(angle1)));		
		PVector res = new PVector(xx,yy);
		res.normalize();
		return res;
		
		
	}
	
	private void turnRightVector() {
		// TODO Auto-generated method stub
		
		PVector loc = new PVector(bbc.myposx,bbc.myposy);
		
		float angle1 = bbc.camang;
		if(angle1<0)
		{
			angle1=360+angle1;
		}

		
		
		
		PVector v2 = new PVector();
		float offset=30;
		//v2.x= (float) (perpToTarget.mag()*Math.cos(h-offset));
		//v2.y=(float) (perpToTarget.mag()*Math.sin(h-offset));
		
	}

	private void turnLeftVector() {
		// TODO Auto-generated method stub
		
	}

	public void breath1()
	{
		int w=640/2;
		int h=480/2;

		//stage 1 move in
		if(System.currentTimeMillis()-breath1Timer1<10*1000)
		{
			moveTo(new PVector(w,h));
		}		
		else
		{
			//stage2 move out
			if(System.currentTimeMillis()-breath1Timer1<20*1000)
			{
				this.wanderVector();
			}	
			else
			{
				breath1Timer1=System.currentTimeMillis();
			}
		}

	}

	public void breath2()
	{
		int w=640/2;
		int h=480/2;

		//stage 1 move in
		if(System.currentTimeMillis()-breath2Timer1<10*1000)
		{
			this.orbitDist=300;
			this.orbit(new PVector(w,h),orbitDist,true);

		}		
		else
		{
			//stage2 move out
			if(System.currentTimeMillis()-breath2Timer1<20*1000)
			{
				orbitDist-=.5;

				this.orbit(new PVector(w,h),orbitDist,true);
			}	
			else
			{
				orbitDist=300; 
				this.orbit(new PVector(w,h),orbitDist,true);
				breath2Timer1=System.currentTimeMillis();
			}
		}

	}


	///the end result could possibly have one robot being followed by all
	public void wanderThenFollow()
	{
		int w=640/2;
		int h=480/2;

		boolean followBot;
		Bot botToFollow=null;
		if(!wtf1)
		{

			//this.wanderVector();
			if(bbc.numNeighbors!=0)
			{
				wtf1=true;
			}

		}
		else
		{


			for(int i=0;i<bbc.otherBots.size();i++)
			{
				Bot b = (Bot) bbc.otherBots.get(i);
				if(b.isNeighbor)
				{
					if(botToFollow==null)
					{

						if(b.ID<bbc.ID)
						{
							botToFollow=b;
						}
					}
					else
					{
						if(b.ID<botToFollow.ID)
						{
							botToFollow=b;
						}
					}					

				}	
			}

		}

		if(botToFollow!=null)
		{

			this.follow(botToFollow);				
		}
		else
		{
			this.wanderVector();
		}



	}

	//idea is that it will follow a robot with an id less than itself,
	//but if another robot comes with an id less than itself, but higher than the prev one, then it'll follow that instead.
	public void wanderThenFollowInLine()
	{
		if(bbc.numNeighbors!=0)
		{
			this.wtfilInitial=false;

			if(!wtfilFinalTargetReached)
			{
				for(int i=0;i<bbc.otherBots.size();i++)
				{
					Bot b = (Bot) bbc.otherBots.get(i);
					if(b.isNeighbor)
					{
						if(wtfilTarget==null)
						{
							if(b.ID<bbc.ID)
							{
								wtfilTarget=b;
								
								if(wtfilTarget.ID==bbc.ID-1)
								{
									wtfilFinalTargetReached=true;
									break;
								}
								
							}

							
						}
						else
						{
							if( b.ID<bbc.ID && b.ID>wtfilTarget.ID )
							{
								wtfilTarget=b;
							}

							if(wtfilTarget.ID==bbc.ID-1)
							{
								wtfilFinalTargetReached=true;
								break;
							}
						}					
					}	
				}
			}

		}


		if(this.wtfilInitial)
		{
			this.wanderVector();
		}
		else
		{
			if(this.wtfilTarget!=null)
			{
				this.follow(wtfilTarget);
			}
			else
			{
				this.wanderVector();
			}
		}

	}

	//end result would be everyone following one orbiting robot
	public void wanderThenOrbit()
	{
		int w=640/2;
		int h=480/2;
		boolean followBot;
		Bot botToFollow=null;

		if(bbc.ID!=0)
		{
			if(!wto1)
			{
				//this.wanderVector();
				if(bbc.numNeighbors!=0)
				{
					wto1=true;
				}
			}
			else
			{
				for(int i=0;i<bbc.otherBots.size();i++)
				{
					Bot b = (Bot) bbc.otherBots.get(i);
					if(b.isNeighbor)
					{
						if(botToFollow==null)
						{

							if(b.ID<bbc.ID)
							{
								botToFollow=b;
							}
						}
						else
						{
							if(b.ID<botToFollow.ID)
							{
								botToFollow=b;
							}
						}
					}	
				}



			}

			if(botToFollow!=null)
			{
				this.follow(botToFollow);				
			}
			else
			{
				this.wanderVector();
			}

		}
		else
		{
			// one robot will be orbitting at all times???
			this.orbit(new PVector(w,h));
		}


	}
	
	//idea is that robot ID 0 orbits. and then everyone else will follow it if they come in contact
	// if ID comes along that is less than ID..it will try to follow the correct id.
	public void wanderThenOrbitInLine()
	{
		int w=640/2;
		int h=480/2;
		
		if(bbc.ID==0)
		{
			this.orbit(new PVector(w,h));
		}
		else
		{
			if(bbc.numNeighbors!=0)
			{
				this.wtoilInitial=false;
			}
			
			
			if(!wtoilFinalTargetReached)
			{
				
				for(int i=0;i<bbc.otherBots.size();i++)
				{
					Bot b = (Bot) bbc.otherBots.get(i);
					if(b.isNeighbor)
					{
						if(wtoilTarget==null)
						{
							if(b.ID<bbc.ID)
							{
								wtoilTarget=b;
								
								if(wtoilTarget.ID==bbc.ID-1)
								{
									wtoilFinalTargetReached=true;
									break;
								}
							}

							
						}
						else
						{
							if( b.ID<bbc.ID && b.ID>wtoilTarget.ID )
							{
								wtoilTarget=b;
							}

							if(wtoilTarget.ID==bbc.ID-1)
							{
								wtoilFinalTargetReached=true;
								break;
							}
						}					
					}	
				}
				
			}
			
			
			
			if(this.wtoilInitial)
			{
				this.wanderVector();
			}
			else
			{
				if(this.wtoilTarget!=null)
				{
					this.follow(wtoilTarget);
				}
				else
				{
					this.wanderVector();
				}
			}
			
			
			
		}
		
	}



	public void doFormation() {
		// TODO Auto-generated method stub

		int w=640/2;
		int h=480/2;

		if(formationType=="circle")
		{
			//get position to go to

			//parameters for circle equispaced
			PVector start = new PVector(w, h);
			float r=200;

			float frac = (float)(2*Math.PI) / (bbc.otherBots.size()+1);

			float x = (float) (r*Math.sin( (float)(bbc.ID)*frac  ));
			float y= (float) (-r*Math.cos( (float)(bbc.ID)*frac  ));
			PVector targ = new PVector(start.x+x, start.y+y);

			this.moveTo(targ);
			return;




		}
		if(formationType=="square")
		{
			//parameters for a grid
			int s = bbc.otherBots.size()+1;
			s= (int) Math.round(Math.sqrt(s));

			//PVector start = new PVector(width/4,height/4);

			PVector start = new PVector(w, h);
			int row = bbc.ID/s;
			int col= bbc.ID%s;
			PVector targ= new PVector(start.x+40*row, start.y+40*col);  
			this.moveTo(targ);
			return;

		}
		if(formationType=="horizontal")
		{
			PVector start = new PVector(w, h);
			PVector targ = new PVector(start.x+40*bbc.ID, start.y);

			this.moveTo(targ);
			return;
		}
		if(formationType=="vertical")
		{
			PVector start = new PVector(w, h);
			PVector targ = new PVector(start.x, start.y+40*bbc.ID);

			this.moveTo(targ);
			return;

		}
		if(formationType=="diagonal")
		{
			PVector start = new PVector(w, h);
			PVector targ = new PVector(start.x+40*bbc.ID, start.y+40*bbc.ID);

			this.moveTo(targ);
			return;

		}
		if(formationType=="partialCircle")
		{
			PVector start = new PVector(w, h);
			float r=200;
			float x = (float) (r*Math.sin( (float)bbc.ID/ (float)(2*Math.PI) ));
			float y= (float) (-r*Math.cos( (float)bbc.ID/(float)(2*Math.PI) ));
			PVector targ = new PVector(start.x+x, start.y+y);

			this.moveTo(targ);
			return;


		}


	}



	public void orbitInLine() {
		// TODO Auto-generated method stub

		for(int i=0;i<bbc.otherBots.size();i++)
		{
			Bot b = (Bot) bbc.otherBots.get(i);
			if(b.ID==bbc.ID-1)
			{
				follow(b);
			}
		}

		if(bbc.ID==0)
		{
			orbitCenter();
		}

	}

	public void orbitAvatar() {
		// TODO Auto-generated method stub
		if(bbc.ID!=0)
		{
			if(bbc.currentAvatar!=null)
			{

				this.orbit2(new PVector(bbc.currentAvatar.x, bbc.currentAvatar.y), this.orbitDist, true);
			}
			else
			{

				for(int i=0;i<bbc.otherBots.size();i++)
				{
					Bot b= (Bot) bbc.otherBots.get(i);
					if(b.ID==0)
					{
						bbc.currentAvatar=b;
					}
				}
			}

		}

	}

	public void orbitCenter() {
		// TODO Auto-generated method stub

		PVector target=new PVector(640/2,480/2);
		orbit2(target,orbitDist,orbitClockwise);


	}

	public void followMouse() {
		// TODO Auto-generated method stub

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
			//Log.d("move","reach target:"+bbc.calibrationAngle);
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
			//Log.d("Behavior","rotating" + currentangle+","+bbc.calibrationAngle);
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
				//Log.d("move","reach target:"+bbc.calibrationAngle);
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
				//Log.d("Behavior","rotating" + currentangle+","+bbc.calibrationAngle);
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



	void setFollowInLine(boolean b)
	{
		bbc.mActivity.beatTimer.followInLine=b;

		this.followInLine=b;

	}
	void followInLine()
	{
		for(int i=0;i<bbc.otherBots.size();i++)
		{
			Bot b = (Bot) bbc.otherBots.get(i);
			if(b.ID==bbc.ID-1)
			{
				follow(b);
			}
		}
	}

	void follow(Bot bot)
	{
		PVector p =this.getPointBehindLeader(bot);		
		this.moveTo(p);
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

	public void avoidBoundary2()
	{
		int d=whichBoundaryReached();

		switch(d)
		{
		case 0:
			this.desiredVel.add(new PVector(2,0));
			break;
		case 1:
			this.desiredVel.add(new PVector(0,2));
			break;
		case 2:
			this.desiredVel.add(new PVector(-2,0));
			break;
		case 3:
			this.desiredVel.add(new PVector(0,-2));
			break;

		default:
			;



		}

	}

	public void avoidBoundary3()
	{
		int maxh,maxw;
		maxh=480;
		maxw=640;
		int buff=50;

		boolean bound =false;
		if(bbc.myposx <0 + buff )
		{
			bound=true;
			this.desiredVel.add(new PVector(2,0));
		}
		if(bbc.myposy<0 + buff)
		{
			bound=true;
			this.desiredVel.add(new PVector(0,2));
		}
		if(bbc.myposx>maxw- buff)
		{
			bound=true;
			this.desiredVel.add(new PVector(-2,0));
		}
		if(bbc.myposy>maxh - buff)
		{
			bound=true;
			this.desiredVel.add(new PVector(0,-2));
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



	public void moveTo(PVector p)
	{
		PVector d = new PVector(p.x-bbc.myposx,p.y-bbc.myposy);
		d.limit(1.0f);
		this.desiredVel.add(d);
	}

	public void orbit(PVector p)
	{


	}


	public void orbit2(PVector target, float desiredDistance, boolean clockwise)
	{
		PVector loc = new PVector(bbc.myposx,bbc.myposy);
		//PVector toTarget = steer(target, true);//whether true false i dont know
		PVector toTarget = new PVector(target.x-loc.x,target.y-loc.y);
		float distToTarget=loc.dist(target);

		//now do perpendicular.  
		//a.b=0  or swap x y and negate one

		PVector perpToTarget;
		if (clockwise)
		{
			perpToTarget=new PVector(toTarget.y, -toTarget.x);
		}
		else
		{
			perpToTarget=new PVector(-toTarget.y, toTarget.x);
		}
		perpToTarget.mult(.5f);

		float offset=.2f;
		if (distToTarget<desiredDistance)
		{
			//PVector ev= evade(target, false);
			//perpToTarget.add(ev);

			float h=perpToTarget.heading2D();			
			PVector v2 = new PVector();			

			if(clockwise)
			{
				v2.x= (float) (perpToTarget.mag()*Math.cos(h-offset));
				v2.y=(float) (perpToTarget.mag()*Math.sin(h-offset));
			}
			else
			{
				v2.x= (float) (perpToTarget.mag()*Math.cos(h+offset));
				v2.y=(float) (perpToTarget.mag()*Math.sin(h+offset));
			}

			perpToTarget=v2;


		}
		else
		{
			//PVector t=  steer( target, false) ;
			//t.mult(.2f);
			//perpToTarget.add(t);

			float h=perpToTarget.heading2D();			
			PVector v2 = new PVector();		
			if(clockwise)
			{
				v2.x= (float) (perpToTarget.mag()*Math.cos(h+offset));
				v2.y=(float) (perpToTarget.mag()*Math.sin(h+offset));
			}
			else
			{
				v2.x= (float) (perpToTarget.mag()*Math.cos(h-offset));
				v2.y=(float) (perpToTarget.mag()*Math.sin(h-offset));
			}

			perpToTarget=v2;



		}


		//acc.add(perpToTarget);
		perpToTarget.limit(1.0f);
		this.desiredVel.add(perpToTarget);


	}


	void orbit(PVector target, float desiredDistance, boolean clockwise)
	{
		PVector loc = new PVector(bbc.myposx,bbc.myposy);
		//float tempA=alignment;
		//float tempC=cohesion;
		//alignment=0;
		//cohesion=0;

		//should there be a movementTimer
		//say clockwise
		//if(System.currentTimeMillis()-movementTimer>50)


		PVector toTarget = steer(target, true);//whether true false i dont know
		float distToTarget=loc.dist(target);
		//now do perpendicular.  
		//a.b=0  or swap x y and negate one

		PVector perpToTarget;
		if (clockwise)
		{
			perpToTarget=new PVector(toTarget.y, -toTarget.x);
		}
		else
		{
			perpToTarget=new PVector(-toTarget.y, toTarget.x);
		}
		perpToTarget.mult(.5f);

		if (distToTarget<desiredDistance)
		{
			PVector ev= evade(target, false);
			perpToTarget.add(ev);
		}

		else
		{
			//perpToTarget.mult(2);

			PVector t=  steer( target, false) ;
			t.mult(.2f);
			perpToTarget.add(t);
		}


		//acc.add(perpToTarget);
		this.desiredVel.add(perpToTarget);
		//flock(totalFlock.boids);


		//alignment=tempA;
		//cohesion=tempC;
	}

	//evade 
	PVector evade(PVector target, boolean slowdown) {
		PVector loc = new PVector(bbc.myposx,bbc.myposy);

		float maxspeed=1;
		float maxforce=1;

		PVector steer = new PVector();  // The steering vector
		PVector desired = PVector.sub(target, loc);  // A vector pointing from the location to the target
		float d = desired.mag(); // Distance from the target is the magnitude of the vector
		// If the distance is greater than 0, calc steering (otherwise return zero vector)
		if (d > 0) {
			// Normalize desired
			desired.normalize();
			// Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
			if ((slowdown) && (d < 200.0f)) desired.mult(maxspeed*((200-d)/200.0f)); // This damping is somewhat arbitrary
			else desired.mult(maxspeed);
			// Steering = Desired minus Velocity
			//steer = PVector.sub(desired, vel);
			steer.limit(maxforce);  // Limit to maximum steering force
		} 
		else {
			steer = new PVector(0, 0);
		}
		steer.mult(-1);
		return steer;
	}


	//this did not work exaclty right...
	public void doSteer()
	{
		//look at current position, bearing, velocity
		PVector pos = new PVector(bbc.myposx,bbc.myposy);
		float currentangle = bbc.camang;
		int rbyte=bbc.rbyte;
		int lbyte=bbc.lbyte;

		PVector currentVel = new PVector();

		//one way to do this...estimate what future x shoudl be based on velocity ....
		int x= (int) (bbc.myposx+this.desiredVel.x);
		int y = (int) (bbc.myposy+this.desiredVel.y);

		int diffx=x-bbc.myposx;
		int diffy=y-bbc.myposy;
		//this is the angle we want to rotate to.
		float ang = (float)Math.toDegrees(Math.atan2(diffy,diffx));


		bbc.modDistance=ModularDistance((int) currentangle,(int)( ang ),360);
		int result=ModularDistance2((int)currentangle,(int)( ang ),360);

		//look at desired vel
		PVector v=new PVector(this.desiredVel.x,this.desiredVel.y);	
		v.normalize();

		// now start to change motor speed to adjust to new 
		// -1 means need to turn right
		// 1 means need to turn left
		/*
		 * ///////////////
		rbyte= 128-20;
		writeR(rbyte);
		lbyte= 128+20;
		writeL(lbyte);
		/////////////
		 */
		if(System.currentTimeMillis()-vmtimer>vmInterval)
		{
			bbc.mActivity.client.sendMessage("vel,"+ bbc.mActivity.client.myID + "," + new DecimalFormat("#.##").format(v.x) + "," + new DecimalFormat("#.##").format(v.y)) ;
			vmtimer=System.currentTimeMillis();
		}
		if(bbc.modDistance>10)
		{
			if(result==-1)
			{
				bbc.writeL((int) (128+20*v.mag()));
				bbc.writeR((int) (128-20*v.mag()*.1f));
			}
			else
			{
				bbc.writeL((int) (128+20*v.mag()*.1f));
				bbc.writeR((int) (128-20*v.mag()));
			}
		}
		else
		{
			bbc.forwardReal();
		}

	}


	//currently working do steer.  it's hacky..but its alright. 
	public void doSteer2()
	{
		float currentangle = bbc.camang;
		float ang = (float)Math.toDegrees(Math.atan2(this.desiredVel.y,this.desiredVel.x));

		int angle1=(int)currentangle;
		int angle2=(int)ang;

		if(angle1<0)
		{
			angle1=360+angle1;
		}
		if(angle2<0)
		{
			angle2=360+angle2;
		}	

		bbc.modDistance=ModularDistance(angle1,angle2,360);
		int result=ModularDistance2(angle1,angle2,360);

		//look at desired vel
		PVector v=new PVector(this.desiredVel.x,this.desiredVel.y);	
		v.normalize();//must check this

		// now start to change motor speed to adjust to new 
		// -1 means need to turn right
		// 1 means need to turn left
		/*
		 * ///////////////
		rbyte= 128-20;
		writeR(rbyte);
		lbyte= 128+20;
		writeL(lbyte);
		/////////////
		 */
		if(System.currentTimeMillis()-vmtimer>vmInterval)
		{
			bbc.mActivity.client.sendMessage("vel,"+ bbc.mActivity.client.myID + "," + new DecimalFormat("#.##").format(v.x) + "," + new DecimalFormat("#.##").format(v.y)) ;
			vmtimer=System.currentTimeMillis();
		}
		if(bbc.modDistance>10)
		{
			if(result==-1)
			{
				bbc.writeL((int) (128+20*v.mag()));
				bbc.writeR((int) (128-20*v.mag()*.1f));
			}
			else
			{
				bbc.writeL((int) (128+20*v.mag()*.1f));
				bbc.writeR((int) (128-20*v.mag()));
			}
		}
		else
		{
			bbc.forwardReal();
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

		int extra=5;
		float distBehind=bbc.neighborBound+extra;//was 50

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


	public void setSeparation(boolean b)
	{
		bbc.mActivity.beatTimer.separation=b;		
		this.separation=b;
	}
	public void setAlignment(boolean b)
	{
		bbc.mActivity.beatTimer.alignment=b;
		this.alignment=b;
	}
	public void setCohesion(boolean b)
	{
		bbc.mActivity.beatTimer.cohesion=b;
		this.cohesion=b;
	}

	public void toggleSeparation()
	{
		bbc.mActivity.beatTimer.separation=!bbc.mActivity.beatTimer.separation;		
		this.separation=!this.separation;
	}
	public void toggleAlignment()
	{
		bbc.mActivity.beatTimer.alignment=!bbc.mActivity.beatTimer.alignment;
		this.alignment=!this.alignment;
	}
	public void toggleCohesion()
	{
		bbc.mActivity.beatTimer.cohesion=!bbc.mActivity.beatTimer.cohesion;
		this.cohesion=!this.cohesion;
	}

	///////////////
	//djemble behavior
	//1.  
	////////////////////////

	void wanderVector()
	{
		PVector loc = new PVector(bbc.myposx,bbc.myposy);
		float angle = (float) (bbc.camang *Math.PI/180.0f);
		float m=1.0f;

		if(System.currentTimeMillis()-wanderVectorTimer>2*1000)
		{
			//
			float random = (float) (Math.random()*2 -1 ) * .1f *(float)Math.PI;			
			//float random = 0;			
			wanderVectorTemp = new PVector((float) (m*loc.x*Math.cos(angle+random) ),  (float) (m*loc.y*Math.sin(angle+random) ) );

			//PVector wanderVector = PVector.sub(pt2, loc);
			wanderVectorTemp.limit(1.0f);

			desiredVel.add(wanderVectorTemp);

			wanderVectorTimer = System.currentTimeMillis();

		}
		else
		{
			desiredVel.add(wanderVectorTemp);
		}


	}


	PVector separateBeacon()
	{
		PVector loc = new PVector(bbc.myposx,bbc.myposy);

		/*
		desiredseparation=bbc.neighborBound;
		if (desiredseparation<22.0f)
		{
			desiredseparation=22.0f;
		}
		 */
		PVector steerVec = new PVector(0, 0, 0);
		int count = 0;
		// For every boid in the system, check if it's too close
		for (int i = 0 ; i < bbc.beacons.size(); i++) {
			Beacon b = (Beacon) bbc.beacons.get(i);
			int desiredSep= b.radius;
			PVector otherloc =new PVector (b.x,b.y ) ;
			float d = PVector.dist(loc, otherloc );
			// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
			if ((d > 0) && (d < desiredSep)) {//might need to modify this to take into account the radius of the bot
				// Calculate vector pointing away from neighbor

				this.fleeBeacon=true;
				PVector diff = PVector.sub(loc, otherloc);
				diff.normalize();
				diff.div(d);        // Weight by distance
				steerVec.add(diff);
				count++;            // Keep track of how many        
			}
			else
			{
				this.fleeBeacon=false;
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steerVec.div((float)count);
		}

		// As long as the vector is greater than 0
		if (steerVec.mag() > 0) {
			// Implement Reynolds: Steering = Desired - Velocity
			steerVec.normalize();
			//steer.mult(maxspeed);
			//steer.sub(vel);
			//steer.limit(maxforce);
		}
		return steerVec;

	}

	PVector separate (/*Vector boids*/) 
	{
		PVector loc = new PVector(bbc.myposx,bbc.myposy);

		//float desiredseparation = 25.0f;
		//desiredseparation = 22.0f;  //22 is good minimum
		//float desiredseparation = 60.0f;
		desiredseparation=bbc.neighborBound;
		if (desiredseparation<22.0f)
		{
			desiredseparation=22.0f;
		}
		PVector steerVec = new PVector(0, 0, 0);
		int count = 0;
		// For every boid in the system, check if it's too close
		for (int i = 0 ; i < bbc.otherBots.size(); i++) {
			Bot other = (Bot) bbc.otherBots.get(i);
			PVector otherloc =new PVector (other.x,other.y ) ;
			float d = PVector.dist(loc, otherloc );
			// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
			if ((d > 0) && (d < desiredseparation)) {
				// Calculate vector pointing away from neighbor
				PVector diff = PVector.sub(loc, otherloc);
				diff.normalize();
				diff.div(d);        // Weight by distance
				steerVec.add(diff);
				count++;            // Keep track of how many        
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steerVec.div((float)count);
		}

		// As long as the vector is greater than 0
		if (steerVec.mag() > 0) {
			// Implement Reynolds: Steering = Desired - Velocity
			steerVec.normalize();
			//steer.mult(maxspeed);
			//steer.sub(vel);
			//steer.limit(maxforce);
		}
		return steerVec;
	}



	// Alignment
	// For every nearby boid in the system, calculate the average velocity
	PVector align (/*Vector boids*/) {
		PVector loc = new PVector(bbc.myposx,bbc.myposy);
		float myAng= bbc.camang;
		if(myAng<0)
		{
			myAng=360+myAng;
		}
		
		//float neighbordist = 50.0f;
		float neighbordist = bbc.neighborBound;
		PVector steer = new PVector(0, 0, 0);
		
		int count = 0;
		for (int i = 0 ; i < bbc.otherBots.size(); i++) {
			Bot other = (Bot) bbc.otherBots.get(i);
			PVector otherloc =new PVector (other.x,other.y ) ;
			float otherAng = other.camang;
			if(otherAng<0)
			{
				otherAng=360+otherAng;
			}
			
			float d = PVector.dist(loc, otherloc);
			if ((d > 0) && (d < neighbordist)) {
				
				float r=5;
				float xx=(float) (r*Math.cos(Math.toRadians(otherAng)));
				float yy= (float) (r*Math.sin(Math.toRadians(otherAng)));
				PVector n=new PVector(xx,yy);
				n.normalize();
				steer.add(n);
				
				//steer.add(otherloc);//this was original
				//steer.add(PVector.sub(otherloc,loc));//this was kindof like cohesion
				count++;
			}
		}
		if (count > 0) {
			steer.div((float)count);
		}

		// As long as the vector is greater than 0
		if (steer.mag() > 0) {
			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			//steer.mult(maxspeed);
			//steer.sub(desiredVel);
			//steer.limit(maxforce);
		}
		return steer;
	}

	// Cohesion
	// For the average location (i.e. center) of all nearby boids, calculate steering vector towards that location
	PVector cohesion (/*Vector boids*/) {
		PVector loc = new PVector(bbc.myposx,bbc.myposy);
		//float neighbordist = 50.0f;
		float neighbordist = bbc.neighborBound;
		PVector sum = new PVector(0, 0, 0);   // Start with empty vector to accumulate all locations
		int count = 0;
		for (int i = 0 ; i < bbc.otherBots.size(); i++) {
			Bot other = (Bot) bbc.otherBots.get(i);
			PVector otherloc=new PVector (other.x,other.y ) ;
			float d = PVector.dist(loc, otherloc);
			if ((d > 0) && (d < neighbordist)) {
				sum.add(otherloc); // Add location
				count++;
			}
		}
		if (count > 0) {
			sum.div((float)count);
			return steer(sum, false);  // Steer towards the location
		}
		return sum;
	}

	// A method that calculates a steering vector towards a target
	// Takes a second argument, if true, it slows down as it approaches the target
	PVector steer(PVector target, boolean slowdown) {
		PVector loc = new PVector(bbc.myposx,bbc.myposy);
		//PVector vel = new PVector(0,0);

		PVector steer;  // The steering vector
		PVector desired = PVector.sub(target, loc);  // A vector pointing from the location to the target
		float d = desired.mag(); // Distance from the target is the magnitude of the vector
		// If the distance is greater than 0, calc steering (otherwise return zero vector)
		if (d > 0) {  
			// Normalize desired
			desired.normalize();


			/*
	      // Two options for desired vector magnitude (1 -- based on distance, 2 -- maxspeed)
	      if ((slowdown) && (d < 100.0f)) desired.mult(maxspeed*(d/100.0f)); // This damping is somewhat arbitrary
	      else desired.mult(maxspeed);
			 */


			// Steering = Desired minus Velocity

			steer = PVector.sub(desired, desiredVel); //really shoudln't be desiredVel, but rather your actual vel
			//steer.limit(maxforce);  // Limit to maximum steering force

		} 
		else {
			steer = new PVector(0, 0);
		}
		return steer;
	}

	public void setFollowMouse(boolean b) {
		// TODO Auto-generated method stub

		followMouse=b;

	}

	public void setOrbitCenter(boolean b) {
		// TODO Auto-generated method stub

		orbitCenter=b;

	}

	public void setOrbitAvatar(boolean b) {
		// TODO Auto-generated method stub
		orbitAvatar=b;
	}

	public void toggleUseBeacon() {
		// TODO Auto-generated method stub

		useBeacon=!useBeacon;

	}

	public void setOrbitInLine(boolean b) {
		// TODO Auto-generated method stub
		orbitInLine=b;

	}

	public void setFormation(boolean b) {
		// TODO Auto-generated method stub

		formation=b;
	}

	public void setWanderThenFollow(boolean b) {
		// TODO Auto-generated method stub
		this.wanderThenFollow=b;
		//this.botTarget=null;
	}

	public void setWanderThenOrbit(boolean b) {
		// TODO Auto-generated method stub
		this.wanderThenOrbit=b;

	}

	public void setWanderThenFollowInLine(boolean b) {
		// TODO Auto-generated method stub
		this.wanderThenFollowInLine=b;
		//this.wtfilTarget=null;
		this.wtfilInitial=true;
		this.wtfilFinalTargetReached=false;
		
		if(b==false)
		{
			this.wtfilTarget=null;
		}
	}

	public void setWanderThenOrbitInLine(boolean b) {
		// TODO Auto-generated method stub
		this.wanderThenOrbitInLine=b;
		//this.wtoilTarget=null;
		this.wtoilInitial=true;
		this.wtoilFinalTargetReached=false;
		
		if(b==false)
		{
			this.wtoilTarget=null;
		}

	}

	

	public void setBreath1(boolean b) {
		// TODO Auto-generated method stub
		
		this.breath1=b;
		this.breath1Timer1=System.currentTimeMillis();
		
	}

	public void setBreath2(boolean b) {
		// TODO Auto-generated method stub
		this.breath2=b;
		this.breath2Timer1=System.currentTimeMillis();
	}

}
