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
		m1.notes[0+24]=72+6;
		m1.notes[0+36]=72+4;
		
		measures.add(m1);
		
		Measure m2 = new Measure();
		m2.notes[0]=72+6;
		m2.notes[0+4]=72+8;
		m2.notes[0+8]=72+6;
		m2.notes[0+12]=72+4;
		m2.notes[0+16]=72+3;
		m2.notes[0+20]=72+3;
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
		m1.notes[20]=72;
		m1.notes[24]=72;
		m1.notes[32]=72;
		m1.notes[36]=72;
		m1.notes[40]=72;
		
		m1.notes[44]=72;
		
		measures.add(m1);
		
		Measure m2 = new Measure();
		m2.notes[0]=72;
		m2.notes[0+4]=72;
		m2.notes[0+8]=72;
		m2.notes[0+12]=72;
		m2.notes[0+20]=72;
		m2.notes[0+24]=72;
		
		measures.add(m2);
		
		
	}
	
	void idea2()
	{
		Measure m1 = new Measure();
		m1.notes[0]=72;
		m1.notes[0+4]=72;
		m1.notes[0+8]=72;
		m1.notes[0+12]=72;
		
		m1.notes[20]=72;
		m1.notes[24]=72;
		m1.notes[28]=72;
		m1.notes[32]=72;
		m1.notes[36]=72;
		
		m1.notes[40]=72;
		
		measures.add(m1);
		
		Measure m2 = new Measure();
		m2.notes[0]=72;
		m2.notes[0+4]=72;
		m2.notes[0+8]=72;
		m2.notes[0+12]=72;
		
		m2.notes[0+20]=72;
		m2.notes[0+24]=72;
		
		m2.notes[0+44]=72;
		 
	}
	
	void idea3()
	{
		Measure m1 = new Measure();
		m1.notes[0]=72;
		m1.notes[0+8]=72;
		m1.notes[0+12]=72;
		m1.notes[20]=72;
		m1.notes[24]=72;
		m1.notes[28]=72;
		
		m1.notes[44]=72;
		
		Measure m2 = new Measure();
		m1.notes[0]=72;
		m1.notes[0+8]=72;
		m1.notes[0+12]=72;
		m1.notes[20]=72;
		m1.notes[24]=72;
		
		m2.notes[0+36]=72;
		m2.notes[0+44]=72;
		
	}
	
	void idea4()
	{
		idea1();
	}
	
	
}
