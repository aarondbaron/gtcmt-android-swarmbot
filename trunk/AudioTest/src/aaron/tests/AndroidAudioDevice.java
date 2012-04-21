package aaron.tests;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AndroidAudioDevice
{
   AudioTrack track;
   short[] buffer = new short[1024];
   
   short[] buffer2;
   
   int fs;
 
   public AndroidAudioDevice(int fs )
   {
	   this.fs=fs;
	   buffer2=new short[fs];
	   
	   //sine
	   for(int i=0;i<buffer2.length;i++)
	   {
		   buffer2[i]=(short) Math.sin(i);
	   }
	   
      int minSize =AudioTrack.getMinBufferSize( fs, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT );        
      track = new AudioTrack( AudioManager.STREAM_MUSIC, fs, 
                                        AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 
                                        minSize, AudioTrack.MODE_STREAM);
      track.play();        
   }	   
 
   public void writeSamples(float[] samples) 
   {	
      fillBuffer( samples );
      track.write( buffer, 0, samples.length );
   }
 
   private void fillBuffer( float[] samples )
   {
      if( buffer.length < samples.length )
         buffer = new short[samples.length];
 
      for( int i = 0; i < samples.length; i++ )
         buffer[i] = (short)(samples[i] * Short.MAX_VALUE);;
   }		
}