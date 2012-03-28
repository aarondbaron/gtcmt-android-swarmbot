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

	public boolean useSFXR=true;

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
				float frequency = 440;
				float increment = (float)(2*Math.PI) * frequency / fs; // angular increment for each sample
				float angle = 0;
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

							int ch=0;
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
	////////////////////////////////////////////////////////



}