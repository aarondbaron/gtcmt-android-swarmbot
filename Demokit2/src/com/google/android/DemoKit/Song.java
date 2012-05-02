package com.google.android.DemoKit;

import java.util.Vector;

public class Song {

	//Vector notes;
	Vector measures;
	
	int SEQUENCERLENGTH=48;
	
	Song()
	{
		
		measures = new Vector();
		
		
		
	}
	
	
	
	public int numNotes()
	{
		return 0;
	}
	
	public int numMeasures()
	{
		return measures.size();
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
 
 
 class OldMacDonald extends Song
 {
	 int base =72+7;
	 OldMacDonald()
	 {
		 super();
		idea1(); 
		idea2();
		idea3();
		idea1();
		
	 }
	 
	 
	 void idea1()
	 {
	
		 Measure m1 = new Measure();
		 m1.notes[0]=base;
		 m1.notes[6]=base;
		 m1.notes[12]=base;
		 m1.notes[18]=base-5;
		 m1.notes[24]=base-3;
		 m1.notes[30]=base-3;
		 m1.notes[36]=base-5;
		 
		 measures.add(m1);
		 
		 Measure m2 = new Measure();
		 m2.notes[0]=base+4;
		 m2.notes[6]=base+4;
		 m2.notes[12]=base+2;
		 m2.notes[18]=base+2;
		 m2.notes[24]=base;
		 
		 m2.notes[42]=base-5;
		 
		 measures.add(m2);
		 
		 
	 }
	 
	 void idea2()
	 {
		 //
		 Measure m1 = new Measure();
		 m1.notes[0]=base;
		 m1.notes[6]=base;
		 m1.notes[12]=base;
		 m1.notes[18]=base-5;
		 m1.notes[24]=base-3;
		 m1.notes[30]=base-3;
		 m1.notes[36]=base-5;
		 
		 measures.add(m1);
		 
		 Measure m2 = new Measure();
		 m2.notes[0]=base+4;
		 m2.notes[6]=base+4;
		 m2.notes[12]=base+2;
		 m2.notes[18]=base+2;
		 m2.notes[24]=base;
		 
		 m2.notes[42]=base-5;
		 m2.notes[45]=base-5;
		 
		 measures.add(m2);
	 }
	 
	 void idea3()
	 {
		 Measure m1 = new Measure();
		 m1.notes[0]=base;
		 m1.notes[6]=base;
		 m1.notes[12]=base;
		 m1.notes[18]=base-5;
		 m1.notes[21]=base-5;
		 
		 m1.notes[24]=base;
		 m1.notes[30]=base;
		 m1.notes[36]=base;
		 
		 measures.add(m1);
		 
		 Measure m2 = new Measure();
		 m2.notes[0]=base;
		 m2.notes[3]=base;
		 m2.notes[6]=base;
		 
		 m2.notes[12]=base;
		 m2.notes[15]=base;
		 m2.notes[18]=base;
		 
		 m2.notes[24]=base;
		 m2.notes[27]=base;
		 m2.notes[30]=base;
		 m2.notes[33]=base;
		 m2.notes[36]=base;
		 m2.notes[42]=base;
		 
		 measures.add(m2);
		 
	 }

 }
 
 class Joyful extends Song
 {
	 int base=72;
	 Joyful()
	 {
		 super();
		 idea1();
		 idea2();
		 idea3();
		 idea2();
	 }
	 
	 void idea1()
	 {
		 Measure m1 = new Measure();
		 m1.notes[0]=base+4;
		 m1.notes[6]=base+4;
		 m1.notes[12]=base+5;
		 m1.notes[18]=base+7;
		 m1.notes[24]=base+7;
		 m1.notes[30]=base+5;
		 m1.notes[36]=base+4;
		 m1.notes[42]=base+2;
		 measures.add(m1);
		 
		 Measure m2= new Measure();
		 m2.notes[0]=base;
		 m2.notes[6]=base;
		 m2.notes[12]=base+2;
		 m2.notes[18]=base+4;
		 m2.notes[24]=base+4;
		 m2.notes[33] = base+2;
		 m2.notes[36]=base+2;
		 measures.add(m2);
		 
		 
	 }
	 
	 void idea2()
	 {
		 Measure m1 = new Measure();
		 m1.notes[0]=base+4;
		 m1.notes[6]=base+4;
		 m1.notes[12]=base+5;
		 m1.notes[18]=base+7;
		 m1.notes[24]=base+7;
		 m1.notes[30]=base+5;
		 m1.notes[36]=base+4;
		 m1.notes[42]=base+2;
		 measures.add(m1);
		 
		 Measure m2= new Measure();
		 m2.notes[0]=base;
		 m2.notes[6]=base;
		 m2.notes[12]=base+2;
		 m2.notes[18]=base+4;
		 m2.notes[24]=base+2;
		 m2.notes[33] = base;
		 m2.notes[36]=base;
		 measures.add(m2);
	 }
	 
	 void idea3()
	 {
		 Measure m1 = new Measure();
		 m1.notes[0]=base+2;
		 m1.notes[6]=base+2;
		 m1.notes[12]=base+4;
		 m1.notes[18]=base;
		 m1.notes[24]=base+2;
		 m1.notes[30]=base+4;
		 m1.notes[33]=base+5;
		 m1.notes[36]=base+4;
		 m1.notes[42]=base;
		 measures.add(m1);
		 
		 Measure m2= new Measure();
		 m2.notes[0]=base+2;
		 m2.notes[6]=base+4;
		 m2.notes[9]=base+5;
		 m2.notes[12]=base+4;
		 m2.notes[18]=base+2;
		 m2.notes[24] = base;
		 m2.notes[30]=base+2;
		 m2.notes[36]=base-5;
		 measures.add(m2);
	 }
	 
	 
 }
 
 class LionSleeps extends Song
 {
	 int base =72+7;
	 LionSleeps()
	 {
		 
		 super();
		 idea1();
		 idea2();
		 idea3();
		 idea4();
		 
		 idea5();
		 idea6();
		 
		 idea5();
		 idea6();
		 
	 }
	 
	 void idea1()
	 {	 //in the jungle the mighty jungle
		 Measure m1 = new Measure();
		 m1.notes[0]=base;
		 m1.notes[12]=base+2;
		 m1.notes[18]=base+4;
		 m1.notes[30]=base+2;
		 m1.notes[42]=base+4;
		 measures.add(m1);
		 
		 Measure m2 = new Measure();
		 
		 m2.notes[0]=base+5;
		 m2.notes[12]=base+4;
		 m2.notes[18]=base+2;
		 m2.notes[30]=base;
		 m2.notes[42]=base+2;
		 measures.add(m2);
		 
		 
	 }
	 
	 
	 void idea2()
	 {	 //the lion sleeps tonight
		 Measure m1 = new Measure();
		 m1.notes[0]=base+4;
		 m1.notes[12]=base+2;
		 m1.notes[18]=base;
		 m1.notes[34]=base+4;
		 m1.notes[40]=base+2;
		 for(int i=40;i<m1.notes.length;i++)
		 {
			 m1.notes[i]=base+2;
		 }
		 measures.add(m1);
		 
		 Measure m2 = new Measure();
		 m2.notes[0]=base+2;
		 for(int i=0;i<m2.notes.length;i++)
		 {
			 if(i==24)
			 {
				 break;
			 }
			 m2.notes[i]=base+2;
		 }
		 measures.add(m2);
	 }
	 
	 void idea3()
	 { // (high) in the jungle the mighty jungle
		 Measure m1 = new Measure();
		 m1.notes[0]=base+7;
		 m1.notes[12]=base+4;
		 m1.notes[18]=base+2;
		 m1.notes[30]=base+4;
		 m1.notes[42]=base+7;
		 measures.add(m1);
		 
		 
		 Measure m2 = new Measure();
		 m2.notes[0]=base+5;
		 m2.notes[12]=base+4;
		 m2.notes[18]=base+2;
		 m2.notes[30]=base;
		 m2.notes[42]=base+2;
		 measures.add(m2);
		 
		 
	 }
	 
	 void idea4()
	 {
		 //the lion sleeps tonight
		 idea2();
	 }
	 void idea5()
	 {
		 ///long notes oooweeeeeeeeee
		 Measure m1 = new Measure();
		 for(int i=0;i<m1.notes.length;i++)
		 {
			 m1.notes[i] = base+7;
			 /*
			 if(i==48)
			 {
				 break;
			 }
			 */
		 
		 }		 
		 
		 measures.add(m1);
		 
		 
		 Measure m2 = new Measure();
		 
		 m2.notes[0]=base+7;
		 m2.notes[12]=base+7;
		 
		 m2.notes[3]=base+5;
		 m2.notes[9]=base+7;
		 m2.notes[15]=base+5;
		 
		 m2.notes[12]=base+2;
		  
		 
		 measures.add(m2);
		 
		 
	 }
	 
	 void idea6()
	 {
		 //ah wee um bum ba way.
		 
		 
		 Measure m1 = new Measure();
		 m1.notes[12]=base+4;
		 
		 m1.notes[12]=base-5;
		 m1.notes[12]=base-5;
		 m1.notes[12]=base-5;
		 
		 m1.notes[12]=base-5;
		 measures.add(m1);
		 
		 Measure m2 = new Measure();
		 m2.notes[12]=base-5;
		 m2.notes[12]=base-5; 
		 m2.notes[12]=base-5;
		 m2.notes[12]=base-5;
		 m2.notes[12]=base-5;
		 
		 measures.add(m2);
		 
		 
		 
	 }
	 
	 
 }
 
 class Tetris extends Song
 {
	 Tetris()
	 {
		 super();
		 idea1();
	 }
	 
	 void idea1()
	 {
		 
	 }
	 
 }
 
 
