package com.google.android.DemoKit;

 

import java.util.Vector;

public class CMeasure {
	
	Vector[] notes;
	int SEQUENCERLENGTH =48;

	CMeasureStats myStats;
	public int ID;

	CMeasure()
	{

		notes = new Vector[48];

		for(int i=0;i<notes.length;i++)
		{
			notes[i]= new Vector<Integer>();
		}

		myStats=new CMeasureStats();

	}

	CMeasure( CMeasure m)
	{
		notes = new Vector[m.SEQUENCERLENGTH];

		for(int i=0;i< SEQUENCERLENGTH ; i++)
		{
			notes[i]=m.notes[i];
		}
	}

	void clearMeasure()
	{
		for(int i=0;i< SEQUENCERLENGTH ; i++)
		{
		 notes[i].clear();
		}
	}
	
	void reverseMeasure()
	{
		int left  = 0;          // index of leftmost element
		int right = notes.length-1; // index of rightmost element

		while (left < right) {
			// exchange the left and right elements
			Vector temp = notes[left]; 
			notes[left]  = notes[right]; 
			notes[right] = temp;

			// move the bounds toward the center
			left++;
			right--;
		}

	}

	CMeasure getReversedMeasure()
	{
		CMeasure m = new CMeasure(this);
		m.reverseMeasure();
		return m; 
	}
	
	int numberOfNotesOfType(int note)
	{
		int num=0;
		
		for(int i=0;i<notes.length;i++)
		{
			
			Vector n = notes[i];
			
			if(n.contains(new Integer(note) ) )
			{
				num++;			
			}
		}
		
		
		return num;
	}
	
	boolean contains(int note)
	{
		boolean b=false;
		Integer nn=new Integer(note);
		for(int i=0;i<notes.length;i++)
		{
			
			Vector n = notes[i];
			
			if(n.contains(nn ) )
			{
				return true;				
			}
		}
		return b;
	}
	
	//return the indices where note is found
	Vector findNote (int note)
	{

		Vector n = new Vector();
		Integer nn=new Integer(note);
		for(int i=0;i<notes.length;i++)
		{
			if(n.contains(nn ) )
			{
				n.add(i);			
			}
		}
		return n;
	}
	
	boolean[] getLine(int n)
	{
		boolean[] arr = new boolean[this.SEQUENCERLENGTH];		
		for(int i=0;i<notes.length;i++)
		{
			if(notes[i].contains(new Integer(n)))
			{
				arr[i]=true;
			}
		}
		
		return arr;
	}
	boolean[] getLineDumb(int id)
	{
		
		boolean[] arr = new boolean[this.SEQUENCERLENGTH];		
		Vector<Integer> test = notes[id];		
		for(int i=0;i<test.size();i++)
		{
			Integer vv = test.get(i);
			int tt = vv.intValue();
			if(tt>=0 && tt<arr.length)
			{
				arr[tt]=true;
			}
		}

		return arr;
		
	}
	
	boolean[][] toGrid()
	{
		boolean[][] gg= new boolean[8][this.SEQUENCERLENGTH];
		
		for(int i =0 ; i< SEQUENCERLENGTH;i++)
		{		
				Vector<Integer> v = this.notes[i];
				for(int j = 0; j < v.size();j++)
				{
					Integer vv = v.get(j);				
					int t = vv.intValue()%gg.length;				
					//Log.d("values", "t" + t + ", i" + i);				
					gg[t][i]=true;
				}			
		}
		
		return gg;
	}
	
	
	class CMeasureStats
	{

		CMeasureStats()
		{

		}
	}
	
	

}
