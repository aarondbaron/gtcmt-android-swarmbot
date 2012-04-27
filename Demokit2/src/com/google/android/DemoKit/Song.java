package com.google.android.DemoKit;

import java.util.Vector;

public class Song {

	Vector notes;
	Vector measures;
	
	int SEQUENCERLENGTH=48;
	
	Song()
	{
		
		measures = new Vector();
		
		
		
	}
	
	
	
	
	public Measure getMeasure(int i)
	{
		/*
		if(i>=measures.size())
		{
			return (Measure) measures.get(0);
		}
		*/
		
		return (Measure) measures.get(i%measures.size());
		
	}
	
	Vector getMeasuresWithAnyNotes(int[] notesToFind)
	{
		Vector result = new Vector();
		if(notesToFind.length==0)
		{
			return result;
		}
		
		for(int i=0;i<measures.size();i++)
		{
			Measure m = (Measure) measures.get(i);
			for(int k=0;k<notesToFind.length;k++)
			{
				if(m.contains(notesToFind[k]))
				{
					result.add(m);
					break;
				}
			}
		}
		
		return result;
	}
	
	Vector getMeasuresWithTheseNotes(int[] notesToFind)
	{
		Vector result = new Vector();
		if(notesToFind.length==0)
		{
			return result;
		}
		
		for(int i=0;i<measures.size();i++)
		{
			Measure m = (Measure) measures.get(i);
			boolean test=true;
			for(int k=0;k<notesToFind.length;k++)
			{
				if(!m.contains(notesToFind[k]))
				{
					test=false;
					break;
				}
			}
			
			if(test)
			{
				result.add(m);
			}
		}
		
		return result;
	}
	
	
	
}

 class FightSong extends Song
{
	FightSong()
	{
		super();
		
		intro();
		
		idea1();
		
		idea2();
		
		idea3();
		
		idea4();
	}
	
	void intro()
	{
		Measure m1 = new Measure();
		m1.notes[0]=72+12;
		m1.notes[0+12]=72+9;
		m1.notes[0+24]=72+7;
		m1.notes[0+36]=72+4;
		
		measures.add(m1);
		
		Measure m2 = new Measure();
		m2.notes[0]=72+7;
		m2.notes[0+4]=72+9;
		m2.notes[0+8]=72+7;
		m2.notes[0+12]=72+5;
		m2.notes[0+16]=72+4;
		m2.notes[0+20]=72+2;
		m2.notes[0+24]=72;
		m2.notes[0+36]=72+4;
		m2.notes[0+44]=72+2;
		
		measures.add(m2);
	}
	
	void idea1()
	{
		Measure m1 = new Measure();
		m1.notes[0]=72;
		m1.notes[0+8]=72;
		m1.notes[0+12]=72;
		m1.notes[20]=72+2;
		m1.notes[24]=72+4;
		m1.notes[32]=72+4;
		m1.notes[36]=72+4;
		m1.notes[40]=72+2;
		
		m1.notes[44]=72;
		
		measures.add(m1);
		
		Measure m2 = new Measure();
		m2.notes[0]=72+2;
		m2.notes[0+4]=72+4;
		m2.notes[0+8]=72+2;
		m2.notes[0+12]=72;
		m2.notes[0+20]=72-1;
		m2.notes[0+24]=72;
		
		measures.add(m2);
		
		
	}
	
	void idea2()
	{
		Measure m1 = new Measure();
		m1.notes[0]=72+4;
		m1.notes[0+4]=72+4;
		m1.notes[0+8]=72+4;
		m1.notes[0+12]=72+4;
		
		m1.notes[20]=72+5;
		m1.notes[24]=72+7;
		m1.notes[28]=72+7;
		m1.notes[32]=72+7;
		m1.notes[36]=72+7;
		
		m1.notes[44]=72+7;
		
		measures.add(m1);
		
		Measure m2 = new Measure();
		m2.notes[0]=72+7;
		m2.notes[0+4]=72+2;
		m2.notes[0+8]=72+2;
		m2.notes[0+12]=72+2;
		
		m2.notes[0+20]=72+4;
		m2.notes[0+24]=72+2;
		
		m2.notes[0+44]=72+7;
		
		measures.add(m2);
		 
	}
	
	void idea3()
	{
		Measure m1 = new Measure();
		m1.notes[0]=72+9;
		m1.notes[0+8]=72+9;
		m1.notes[0+12]=72+9;
		m1.notes[20]=72+9;
		m1.notes[24]=72+9;
		m1.notes[28]=72+12;
		
		m1.notes[44]=72+9;
		
		measures.add(m1);
		
		Measure m2 = new Measure();
		m2.notes[0]=72+7;
		m2.notes[0+8]=72+7;
		m2.notes[0+12]=72+7;
		m2.notes[20]=72+4;
		m2.notes[24]=72+2;
		
		m2.notes[0+36]=72+4;
		m2.notes[0+44]=72+2;
		
		measures.add(m2);
		
	}
	
	void idea4()
	{
		idea1();
	}
	
	
}
