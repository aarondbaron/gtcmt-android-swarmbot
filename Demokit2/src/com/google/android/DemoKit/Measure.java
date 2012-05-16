package com.google.android.DemoKit;

import java.lang.reflect.Array;
import java.util.LinkedHashSet;
import java.util.Vector;

public class Measure
{
	int[] notes;
	int SEQUENCERLENGTH =48;
	Measure()
	{
		notes = new int[SEQUENCERLENGTH];
	}
	
	Measure(Measure m)
	{
		notes = new int[m.SEQUENCERLENGTH];
		
		for(int i=0;i< SEQUENCERLENGTH ; i++)
		{
			notes[i]=m.notes[i];
		}
		
		
		
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
	
	boolean[] toBellRhythm(int nnn)
	{
		boolean[] b= new boolean[SEQUENCERLENGTH];

		for(int i=0;i<b.length;i++)
		{
			if(notes[i]==nnn)
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
	
	Vector findNote (int note)
	{
		//int[] nn=null;
		
		Vector n = new Vector();
		
		for(int i=0;i<notes.length;i++)
		{
			if(notes[i]==note)
			{
				n.add(i);			
			}
		
		}
		return n;
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
	
	void transpose(int n)
	{
		for(int i=0;i<notes.length;i++)
		{
			notes[i]+=n;
		}		
	}
	
	void reverse()
	{
		for(int i = 0; i < notes.length/2; i++)
		{
		    int temp = notes[i];
		    notes[i] = notes[notes.length - i - 1];
		    notes[notes.length - i - 1] = temp;
		}

	
	}
	
	
	void shiftRight()
	{
		//roll[number-1].pressed=roll[0].pressed;
		int temp = notes[notes.length-1];
		for (int i=notes.length-1; i>0;i--)
		{
			notes[i]=notes[i-1];
		}

		notes[0] = temp;
	}

	void shiftLeft( )
	{
		//roll[number-1].pressed=roll[0].pressed;
		int temp = notes[0];
		for (int i=0; i<notes.length-1;i++)
		{
			notes[i]=notes[i+1];
		}

		notes[notes.length - 1] = temp;
	}
	
	
	//Object clone()
	{
		
	}
	
	LinkedHashSet uniqueNotes()
	{
		 
		
		LinkedHashSet vv = new LinkedHashSet();
		
		for(int i=0;i<notes.length;i++)
		{
			if(notes[i]!=0)
		      {
		        Integer n = new Integer(notes[i]);
		        vv.add(n);
		      }	  
		}
	   
		
		return vv;
	}
	
	
	                  
	



}