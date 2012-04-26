package com.google.android.DemoKit;

public class Measure
{
	int[] notes;
	int SEQUENCERLENGTH =48;
	Measure()
	{
		notes = new int[SEQUENCERLENGTH];
	}
	
	boolean[] toRhythm()
	{
		boolean[] b= new boolean[SEQUENCERLENGTH];
		
		for(int i=0;i<b.length;i++)
		{
			if(notes[i]!=0)
			{
				b[i]=true;
			}
		}
		return b;
		
	}
	
}