package gtcmtswarmbot.mobilecontrol;


 

import java.util.Vector;

public class Composition {


	String turn;

	boolean humanInterrupted;

	Vector<CMeasure> measures;

	CMeasure currentMeasure;
	
	int base =72;
	
	int marker=0;

	Composition()
	{
		measures = new Vector();
	}


	///warning
	CMeasure getPreviousMeasure()
	{
		CMeasure m;

		m=measures.get(measures.size()-2);
		
		currentMeasure=m;

		return m;
	}
	
	//warning
	public CMeasure getNextMeasure() {
		// TODO Auto-generated method stub
		CMeasure m;

		m=measures.get(measures.size()-2);

		return m;
	}



	void makeSuggestion()
	{

		CMeasure previousMeasure = getPreviousMeasure();
	}

	void modifyHumanInput()
	{

	}

	void createMeasure()
	{

		currentMeasure = new CMeasure();
	}

	void clearMeasure()
	{
		for(int i=0;i<currentMeasure.notes.length;i++)
		{
			currentMeasure.notes[i].clear();

		}

	}

	void getMeasureFromHuman()
	{


	}
	public CMeasure getMeasure(int i) {
		// TODO Auto-generated method stub
		
		return this.measures.get(i%measures.size());
	}
	
	
	void addMeasure(CMeasure m)
	{
		measures.add(m);
	}
	
	
	void removeMeasure()
	{
		if(measures.size()!=0)
		{
			measures.remove(measures.size()-1);
		}
		
	}
	void removeMeasure(int i)
	{
		if(i<measures.size())
		{
			measures.remove(i);
		}
	}
	void removeMeasure(CMeasure m)
	{
		measures.remove(m);
	}


	public CMeasure newMeasure() {
		// TODO Auto-generated method stub
		
		CMeasure c = new CMeasure();
		measures.add(c);
		
		marker= measures.size()-1;
		currentMeasure=measures.get(marker);
		
		return c;
	}


	public void deleteMeasure() {
		// TODO Auto-generated method stub
		measures.remove(currentMeasure);
		
	}
 
	public void nextMeasure()
	{
		marker++;
		marker=marker%measures.size();
		currentMeasure=measures.get(marker);
	}
	
	public void previousMeasure()
	{
		marker--;
		if(marker<0)
		{
			marker=measures.size()-1;
		}
		currentMeasure=measures.get(marker);
	}
	
	
	public int getCurrentMeasureID()
	{
		return currentMeasure.ID;
	}
	
	
	


}