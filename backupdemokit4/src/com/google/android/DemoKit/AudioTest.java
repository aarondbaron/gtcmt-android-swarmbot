package com.google.android.DemoKit;

import android.app.Activity;
import android.os.Bundle;

public class AudioTest
{	    
	Thread t;
	SFXRData sdata;
	int inc;
	boolean threadRunning;
	AndroidAudioDevice device;
	
   public AudioTest()
   {
	   
      sdata = new SFXRData(1);
      sdata.random((int) (5*Math.random()));
      sdata.resetSample(false);
      
      threadRunning=true;
      
      t= new Thread( new Runnable( ) 
      {
         public void run( )
         {        		
            float frequency = 440;
            float increment = (float)(2*Math.PI) * frequency / 44100; // angular increment for each sample
            float angle = 0;
            device = new AndroidAudioDevice( );
            float samples[] = new float[1024];
 
            while( true && threadRunning)
            {
               for( int i = 0; i < samples.length; i++ )
               {
            	   //this is for sine
            	   /*
                  samples[i] = (float)Math.sin( angle );
                  angle += increment;
                  inc++;
                  */
                  
                  samples[i]=(float)sdata.synthSample();
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
}