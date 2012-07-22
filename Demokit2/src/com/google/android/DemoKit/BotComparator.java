package com.google.android.DemoKit;

import java.util.Comparator;

public class BotComparator implements Comparator{

	String p;

	BotComparator()
	{
		//ID by default
		p="ID";
	}

	void compareBy(String s)
	{
		p=s; 
	}

	public int compare(Object aa,Object bb)
	{
		Bot a= (Bot) aa;
		Bot b = (Bot) bb;

		/*
		if(p == "maxspeed")
		{
			//System.out.println("comparing by " + p);
			if(a.maxspeed>b.maxspeed)
			{
				return 1;
			} 
			else if(a.maxspeed<b.maxspeed)
			{
				return -1;
			}
			else
				return 0;
		}
		*/

		if(p == "ID")
		{
			if(a.ID>b.ID)
			{
				return 1;
			} 
			else if(a.ID<b.ID)
			{
				return -1;
			}
			else
				return 0;
		}

		/*
		if(p == "rd")
		{
			if(a.rd>b.rd)
			{
				return 1;
			} 
			else if(a.rd<b.rd)
			{
				return -1;
			}
			else
				return 0;
		}
		*/

		if(p == "distToMe")
		{
			if(a.distToMe>b.distToMe)
			{
				return 1; 
			}
			else if(a.distToMe<b.distToMe)
			{
				return -1;
			}
			else
			{
				return 0; 
			}

		}
		return 0;
	}

}
