package com.google.android.DemoKit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;

public class AudioTest
{	    
	Thread t;
	SFXRData sdata;
	int inc;
	boolean threadRunning;
	AndroidAudioDevice device;

	int base =72;
	int[] pSet;
	int fs=44100;

	float frequency=440;
	float angle=0;
	float increment;

	public boolean useSFXR=false;
	public boolean useSequencer=true;

	BoeBotController bbc;

	public AudioTest()
	{

		sdata = new SFXRData(1,fs);
		sdata.random((int) (5*Math.random()));
		sdata.resetSample(false);

		threadRunning=true;
		pSet=pentatonicSet(base);

		t= new Thread( new Runnable( ) 
		{


			public void run( )
			{        		
				frequency = 440;
				increment = (float)(2*Math.PI) * frequency / fs; // angular increment for each sample
				angle = 0;
				device = new AndroidAudioDevice( fs );
				float samples[] = new float[1024];

				while( true && threadRunning)
				{
					for( int i = 0; i < samples.length; i++ )
					{


						if(useSFXR)
						{
							samples[i]=(float)sdata.synthSample();
						}
						else
						{
							float ang1=0;;
							if(bbc!=null){
								ang1=bbc.camang;
								if(ang1<0)
								{
									ang1=360+ang1;
								}
							}



							int ch=1;
							switch(ch)
							{
							case 0: //sine
							samples[i] = (float)Math.sin( angle );
							break;
							case 1: //square
								samples[i] = (float) Math.signum(Math.sin( angle ));
								break;
							case 2: //triangle?
								samples[i] = (float) Math.asin(Math.sin(angle));
								break;
							case 3: //dunno
								samples[i] = (float) Math.sin(angle*angle);
								break;
							case 4: //dunno
								samples[i] = (float) (Math.sin(angle+Math.sin(angle)));
								break;
							case 5: //dunno
								samples[i] = (float) (Math.sin(angle+frequency*Math.sin(angle)));
								break;
							case 6: //dunno
								float fd=frequency/map(ang1,-90,90,-1,1);
								float fm=frequency;
								float thing=fd/fm;
								samples[i] = (float) (Math.sin(angle+(thing)*Math.sin(angle))) ;
								break;
							case 7: //dunno
								float fd3=frequency/map(ang1,-90,90,-1,1);
								float fm3=frequency;
								float thing3=fd3/fm3;
								samples[i] = (float) (Math.signum( Math.sin(angle+(thing3)*Math.sin(angle)))  );
								break;
							case 8: //dunno
								float fd4=frequency/map(ang1,-90,90,-1,1);
								float fm4=frequency;
								float thing4=fd4/fm4;
								samples[i] = (float) (Math.asin( Math.sin(angle+(thing4)*Math.sin(angle)))  );
								//samples[i] = (float) (  ( Math.sin(angle+(thing4)*Math.signum(Math.sin(angle))))  );
								break;

							case 9: 
								float fd5=frequency/map(ang1,-90,90,-1,1);
								float fm5=frequency;
								float thing5=fd5/fm5;

								float amp=ang1;

								samples[i] = (float) ( Math.sin(angle+(thing5)*amp*Math.sin(angle)) ) ;

								break;

							case 10: //dunno

								float fd2=frequency/ang1;
								float fm2=frequency;
								float thing2=fd2/fm2;
								samples[i] = (float) (Math.sin(angle+thing2*Math.sin(angle)));
								break;

							default: ;
							}

							angle += increment;
							inc++;

						}
						//inc++;
					}

					device.writeSamples( samples );

					//this was one way to make repetitions
					//if(inc>44100/8)
					//{
					/*
					 * this was for sine increasing
					 */
					/*
            	   frequency+=40;
            	   increment=(float)(2*Math.PI) * frequency / 44100;
            	   inc=0;
					 */            	   

					/*
            	   sdata.resetSample(false);
            	   sdata.random((int)( 7*Math.random() ));
            	   sdata.playing_sample=true; 
            	   inc=0;
					 */  
					//}


				} 	
			}
		} );
		t.start();
	}

	public void replayRandom()
	{
		sdata.resetSample(false);
		sdata.random((int)( 7*Math.random() ));
		sdata.playing_sample=true; 
	}

	public void replay()
	{
		sdata.resetSample(false);
		sdata.playing_sample=true; 
	}

	public void randomSound()
	{
		sdata.random((int)( 7*Math.random() ));

	}

	public void soundType(int a)
	{
		sdata.random(a);   
	}

	public void setFreq(float f)
	{
		sdata.freq=f;
	}
	public void randomPentFreq()
	{
		int f=0;
		sdata.freq=f;
	}

	public void setIncrement(float increment)
	{
		this.increment=increment;
	}
	public void properIncrement()
	{

		this.increment=(float)(2*Math.PI) * frequency / fs; ;
	}
	public void setAngle(float angle)
	{
		this.angle=angle;
	}
	public void setFrequency(float frequency)
	{
		this.frequency=frequency;
	}
	
	public void setNote(int n)
	{
		this.angle=0;
		this.frequency=midiToFreq(n);
	}

	public void setFrequencyRP()
	{
		int n = (int) (Math.random()*pSet.length);
		this.frequency=midiToFreq(pSet[n]);
	}

	public void setFrequencyAP()
	{
		float angle1=bbc.camang;
		if(angle1<0)
		{
			angle1=360+angle1;
		}

		int n = (int) bbc.map(angle1, 0, 360, 0+pSet.length/3+pSet.length/6, pSet.length-pSet.length/6);
		this.frequency=midiToFreq(pSet[n]);
	}



	public void onStop()
	{
		threadRunning=false;
		device.track.flush();
		device.track.release();
		//device.track.stop();


	}

	public void onDestroy()
	{

		/*try {
		t.join();
		threadRunning=false;
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		 */
		threadRunning=false;
		device.track.flush();
		device.track.release();
		//device.track.stop();

		threadRunning=false;
		device.track.stop();
		//t.stop();

	}



	int secondsToInc(float s)
	{
		int i= (int) (fs*s);
		return i;
	}


	//////////////////////////////////////////////////////////////
	int[] pentatonicSet(int nrange)
	{
		int[] arr0 = new int[nrange/2];
		int[] arr1 = new int[nrange/2];

		arr0[0]= base;
		arr1[0]= base;
		for(int i=0; i < nrange/2-1; i++)
		{

			arr0[i+1]=nextPentatonic(arr0[i]);
			arr1[i+1]= previousPentatonic(arr1[i]);
		}

		int[] result = Arrays.copyOf(arr0, arr0.length + arr1.length);
		System.arraycopy(arr1, 0, result, arr0.length, arr1.length);

		result=removeDuplicates(result);
		Arrays.sort(result);
		return result;
	}

	int nextPentatonic(int current)
	{
		//assumption...current is already one of the pentatonic
		int dif=current-base;

		int n=0;

		int f=0;

		if(dif>0)
		{
			n=dif%12;

			switch(n)
			{
			case 0: 
				f= 2; 
				break;
			case 2: 
				f=2; 
				break;
			case 4: 
				f=3; 
				break;
			case 7: 
				f=2; 
				break;
			case 9: 
				f=3 ;
				break;
			default: 
				//println("next pentatonic . c: " + current + " n: " + n);
			}
		}


		if(dif<0)
		{
			////

			n= -(-dif)%12;

			switch(n)
			{

			case 0: 
				f= 2; 
				break;


			case -3: 
				f= 3; 
				break;
			case -5: 
				f= 2 ; 
				break;
			case -8: 
				f= 3; 
				break;
			case -10: 
				f= +2; 
				break;
			default: 
				//println(" next  pentatonic . c: " + current + " n: " + n);
			}
		}


		if(dif==0)
		{
			return current+2;
		}




		int res = current+f;
		if(res>127)
		{
			res = current;
			//println("over");
		}
		if(res<0)
		{
			res=current;
			//println("neh");
		}
		return res;
	}

	int previousPentatonic(int current)
	{
		if(current==0)
		{
			return base;
		}
		//assumption...current is already one of the pentatonic
		int dif=current-base;

		int n=0;

		int f=0;

		if(dif>0)
		{
			n=dif%12;

			switch(n)
			{
			case 0: 
				f= -3; 
				break;
			case 2: 
				f=-2; 
				break;
			case 4: 
				f=-2; 
				break;
			case 7: 
				f=-3; 
				break;
			case  9: 
				f= -2 ;
				break;
			default: 
				//println("prev pentatonic . c: " + current + " n: " + n);
			}
		}


		if(dif<0)
		{
			n= -(-dif)%12;

			switch(n)
			{
			case 0: 
				f= -3; 
				break;

			case -3: 
				f= -2; 
				break;
			case -5: 
				f=-3; 
				break;
			case -8: 
				f=-2; 
				break;
			case -10: 
				f= -2; 
				break;
			default: 
				//println("prev pentatonic . c: " + current + " n: " + n);
			}
		}


		if(dif==0)
		{
			return current-3;
		}




		int res = current+f;
		if(res>127)
		{
			res = current;
			//println("over");
		}
		if(res<0)
		{
			res=current;
			//println("neh");
		}
		return res;
	}

	int[] removeDuplicates(int array[]) {  

		HashSet<Integer> hs = new HashSet<Integer>();  

		for(int i =0 ; i < array.length ; i++) {  
			hs.add(array[i]);
		}  

		int new_array[] = new int[hs.size()];  

		Iterator<Integer> itr = hs.iterator();  
		int i =0;  
		while(itr.hasNext()) {  
			new_array[i]=itr.next();  
			i++;
		}  

		return new_array;
	}  


	float midiToFreq(int a)
	{


		float fp3 = (float) Math.pow(2, (a-base)/12.0f) *261.6255650f;


		return fp3;
	}
	////////////////////////////////////////////////////////
	public float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}


}