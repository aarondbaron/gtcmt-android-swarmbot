package aaron.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class AudioTest extends Activity implements SensorEventListener
{	    
	Thread t;
	SFXRData sdata;
	int inc;
	boolean threadRunning;
	AndroidAudioDevice device;

	int fs=44100;

	int base =72;

	int[] pset;

	boolean useSFXR=false;

	Handler handler = new Handler();


	TextView myText;

	int div=6;

	//duration in seconds
	float dur=1f/16f;

	int seqlength=48;
	boolean[] seq;
	int seqID=0;
	boolean useSequencer=true;


	////////////////////
	private SensorManager mSensorManager;
	private Sensor mOrientationSensor;
	//data for orientation values filtering (average using a ring buffer)
	static private final int RING_BUFFER_SIZE=10;
	private float[][][] mAnglesRingBuffer;
	private int mNumAngles;
	private int mRingBufferIndex;
	private float[][] mAngles;
	public float azimuth1;
	public float pitch1;
	public float roll1;

	/////////////////

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);                      
		sdata = new SFXRData(1,fs);
		sdata.random((int) (5*Math.random()));
		sdata.resetSample(false);

		pset=pentatonicSet(base);

		threadRunning=true;

		seq= new boolean[seqlength];

		this.setContentView(R.layout.main);

		this.myText = (TextView)this.findViewById(R.id.textView1);
		myText.setText("hwat?");

		mSensorManager = (SensorManager) getSystemService(android.content.Context.SENSOR_SERVICE);
		mOrientationSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//	     
		mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
		// initialize the ring buffer for orientation values
		mNumAngles=0;
		mRingBufferIndex=0;
		mAnglesRingBuffer=new float[RING_BUFFER_SIZE][3][2];
		mAngles=new float[3][2];
		mAngles[0][0]=0;
		mAngles[0][1]=0;
		mAngles[1][0]=0;
		mAngles[1][1]=0;
		mAngles[2][0]=0;
		mAngles[2][1]=0;
		/*
		for(int i=0;i<seq.length;i++)
		{
			if(i%3==0)
			{
				seq[i]=true;
			}
			else
			{
				seq[i]=false;
			}
		}
		 */

		seq = euclidArray(48,seqlength);



		t= new Thread( new Runnable( ) 
		{
			public void run( )
			{        		
				float frequency = 440;
				float increment = (float)(2*Math.PI) * frequency / fs; // angular increment for each sample
				float angle = 0;
				device = new AndroidAudioDevice( fs);
				float samples[] = new float[1024];





				while( true && threadRunning)
				{




					for( int i = 0; i < samples.length; i++ )
					{

						if(useSFXR)
						{
							samples[i]=(float)sdata.synthSample();
							inc++;

							if(inc>secondsToInc(dur))
							{

								int rr = (int) (Math.random()*(pset.length/4) + pset.length/4);
								
								int ind= (int) map(azimuth1,0,360,pset.length/4,pset.length-pset.length/4);
								if(ind<0)
								{
									ind=0;
								}
								if(ind>=pset.length)
								{
									ind=pset.length-1;
								}
								
								int noteNum=(int) (   (pset[rr])  );
								frequency=midiToFreq(noteNum);
								//Log.d("pset rr","pset" + pset[rr]);
								//Log.d("freq","noteNum"+noteNum);

								sdata.freq=frequency;
								sdata.resetSample(false);
								//sdata.random((int)( 8*Math.random() ));
								sdata.random(7);
								sdata.playing_sample=true; 
								inc=0;

							}
						}
						else
						{
							//this is for sine
							//samples[i] = (float)Math.sin( angle );
							//samples[i] = (float) Math.signum(Math.sin( angle ));
							samples[i] = (float) Math.asin(Math.sin(angle));
							
							angle += increment;
							inc++;

							if(useSequencer)
							{

								if(inc>secondsToInc(dur))
								{

									if(seq[seqID])
									{
										
										int mx=(int) (pset.length-pset.length/2);
										int mn=pset.length/7;
										
										int rr = (int) ( ( Math.random()*(mx-mn) )+mn );
										
										//int ind= (int) map(azimuth1,0,360,mn,mx);
										int ind= (int) map(pitch1,90,-90,mn,mx);
										
										
										if(ind<0)
										{
											ind=0;
										}
										if(ind>=pset.length)
										{
											ind=pset.length-1;
										}
										
										int noteNum=(int) (   (pset[ind])  );

										frequency=midiToFreq(noteNum+48);
										//frequency=440;


										angle*=0;
										increment = (float)(2*Math.PI) * frequency / fs;
										inc=0;
										//Log.d("seq on","seq"+seqID);
									}
									else
									{
										inc=0;
										angle=0;
										increment=0;
										frequency=0;
										//Log.d("seq OFF","seq"+seqID);
									}

									seqID++;
									if(seqID>=seqlength)
									{
										seqID=0;
									}

								}
							}
							else
							{
								int rr=pset.length/2;
								int noteNum=(int) (   (pset[rr])  );
								frequency=midiToFreq(noteNum+48);

							}


						}
					}

					device.writeSamples( samples );


				}        	
			}
		} );
		t.start();




	}

	@Override
	public void onStop()
	{
		threadRunning=false;
		device.track.flush();
		device.track.release();
		device.track.stop();
		
		//t.stop();

	}


	@Override
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


	int[] pentatonicSet(int nrange)
	{
		int[] arr0 = new int[nrange/2];
		int[] arr1 = new int[nrange/2];

		arr0[0]= base;
		arr1[0]= base;
		for (int i=0; i < nrange/2-1; i++)
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

	int[] removeDuplicates(int array[]) {  

		HashSet<Integer> hs = new HashSet<Integer>();  

		for (int i =0 ; i < array.length ; i++) {  
			hs.add(array[i]);
		}  

		int new_array[] = new int[hs.size()];  

		Iterator<Integer> itr = hs.iterator();  
		int i =0;  
		while (itr.hasNext ()) {  
			new_array[i]=itr.next();  
			i++;
		}  

		return new_array;
	}  

	int nextPentatonic(int current)
	{
		//assumption...current is already one of the pentatonic
		int dif=current-base;

		int n=0;

		int f=0;

		if (dif>0)
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


		if (dif<0)
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


		if (dif==0)
		{
			return current+2;
		}




		int res = current+f;
		if (res>127)
		{
			res = current;
			//println("over");
		}
		if (res<0)
		{
			res=current;
			//println("neh");
		}
		return res;
	}

	int previousPentatonic(int current)
	{
		if (current==0)
		{
			return base;
		}
		//assumption...current is already one of the pentatonic
		int dif=current-base;

		int n=0;

		int f=0;

		if (dif>0)
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


		if (dif<0)
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


		if (dif==0)
		{
			return current-3;
		}




		int res = current+f;
		if (res>127)
		{
			res = current;
			//println("over");
		}
		if (res<0)
		{
			res=current;
			//println("neh");
		}
		return res;
	}


	float midiToFreq(int a)
	{


		float fp3 = (float) Math.pow(2, (a-base)/12.0f) *261.6255650f;


		return fp3;
	}

	boolean[] euclidArray(int m, int k)
	{
		if (k<m)
			return new boolean[k];

		if (m==0)
			return new boolean[k];

		Vector d[] = new Vector[m];
		for (int i=0; i <m; i++)
		{
			d[i] = new Vector();
			d[i].add("1");
		}

		int dif=k-m;
		//number of zeros

		for (int i=0; i< dif; i++)
		{
			//println(i%m);
			d[i%m].add("0");
		}

		Vector r = new Vector();

		for (int i=0; i< d.length;i++)
		{
			r.addAll(d[i]);
		}


		boolean b[]= new boolean[k];
		for (int i =0; i < r.size(); i++)
		{
			String s = (String) r.get(i);
			//print(s);
			if (s.equals("1"))
			{
				b[i]=true;
			}
		}
		//println();
		return b;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub





		if(event.sensor==mOrientationSensor) {
			if(mNumAngles==RING_BUFFER_SIZE) {
				// subtract oldest vector
				mAngles[0][0]-=mAnglesRingBuffer[mRingBufferIndex][0][0];
				mAngles[0][1]-=mAnglesRingBuffer[mRingBufferIndex][0][1];
				mAngles[1][0]-=mAnglesRingBuffer[mRingBufferIndex][1][0];
				mAngles[1][1]-=mAnglesRingBuffer[mRingBufferIndex][1][1];
				mAngles[2][0]-=mAnglesRingBuffer[mRingBufferIndex][2][0];
				mAngles[2][1]-=mAnglesRingBuffer[mRingBufferIndex][2][1];
			} else {
				mNumAngles++;
			}

			// convert angles into x/y
			mAnglesRingBuffer[mRingBufferIndex][0][0]=(float) Math.cos(Math.toRadians(event.values[0]));
			mAnglesRingBuffer[mRingBufferIndex][0][1]=(float) Math.sin(Math.toRadians(event.values[0]));
			mAnglesRingBuffer[mRingBufferIndex][1][0]=(float) Math.cos(Math.toRadians(event.values[1]));
			mAnglesRingBuffer[mRingBufferIndex][1][1]=(float) Math.sin(Math.toRadians(event.values[1]));
			mAnglesRingBuffer[mRingBufferIndex][2][0]=(float) Math.cos(Math.toRadians(event.values[2]));
			mAnglesRingBuffer[mRingBufferIndex][2][1]=(float) Math.sin(Math.toRadians(event.values[2]));

			// accumulate new x/y vector
			mAngles[0][0]+=mAnglesRingBuffer[mRingBufferIndex][0][0];
			mAngles[0][1]+=mAnglesRingBuffer[mRingBufferIndex][0][1];
			mAngles[1][0]+=mAnglesRingBuffer[mRingBufferIndex][1][0];
			mAngles[1][1]+=mAnglesRingBuffer[mRingBufferIndex][1][1];
			mAngles[2][0]+=mAnglesRingBuffer[mRingBufferIndex][2][0];
			mAngles[2][1]+=mAnglesRingBuffer[mRingBufferIndex][2][1];

			mRingBufferIndex++;
			if(mRingBufferIndex==RING_BUFFER_SIZE) {
				mRingBufferIndex=0;
			}

			// convert back x/y into angles
			float azimuth=(float) Math.toDegrees(Math.atan2((double)mAngles[0][1], (double)mAngles[0][0]));
			float pitch=(float) Math.toDegrees(Math.atan2((double)mAngles[1][1], (double)mAngles[1][0]));
			float roll=(float) Math.toDegrees(Math.atan2((double)mAngles[2][1], (double)mAngles[2][0]));
			//mCompassRenderer.setOrientation(azimuth, pitch, roll);
			if(azimuth<0) azimuth=(360+azimuth)%360;
			//mHeadingView.setText(getString(R.string.heading)+": "+(int)azimuth+"°");
			//azimuthlabel.setText("azimuth:" + azimuth);
			this.azimuth1=azimuth;
			this.pitch1=pitch;
			this.roll1=roll;


			handler.post(new Runnable() {
				@Override
				public void run() {
					if(myText!=null)
					{
						myText.setText("az: " + azimuth1 + "\n pitch:" + pitch1 + "\n roll" + roll1);
					}
				}
			});



		}

	}
	
	
	public float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}


}