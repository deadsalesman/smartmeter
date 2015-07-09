package uk.ac.imperial.smartmeter.res;

import java.util.ArrayList;

public class EleGenConglomerate {
	private ArrayList<ElectricityGeneration> genList;
	
	public EleGenConglomerate()
	{
		this(new ArrayList<ElectricityGeneration>());
	}
	public EleGenConglomerate(ArrayList<ElectricityGeneration> arr)
	{
		genList = arr;
	}
	public void addGen(ElectricityGeneration e)
	{
		genList.add(e);
	}
	public double getCurrentOutput()
	{
		double tally = 0;
		
		for (ElectricityGeneration e : genList)
		{
			tally += e.getCurrentOutput();
		}
		return tally;
	}
	public double getPredictedOutput(double elapsed)
	{
		double tally = 0;
		
		for (ElectricityGeneration e : genList)
		{
			tally += e.getPredictedOutput(elapsed);
		}
		return tally;
	}
  
  
}
