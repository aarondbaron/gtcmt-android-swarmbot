package com.google.android.DemoKit;

import java.util.Vector;

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

	boolean contains(int note)
	{
		boolean b=false;
		for(int i=0;i<notes.length;i++)
		{
			if(notes[i]==note)
			{
				return true;				
			}
		}
		return b;
	}
	
	int numNotesOfType(int note)
	{
		int num=0;
		for(int i=0;i<notes.length;i++)
		{
			if(notes[i]==note)
			{
				num++;				
			}
		}
		
		return num;
	}
	
	int numNotes()
	{
		int num=0;
		for(int i=0;i<notes.length;i++)
		{
			if(notes[i]!=0)
			{
				num++;				
			}
		}
		
		return num;
	}



}