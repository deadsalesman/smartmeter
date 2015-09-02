package uk.ac.imperial.smartmeter.res;

import java.util.ArrayList;
import java.util.Date;
/**
 * The class acts as a conglomeration of every {@link ElectricityGeneration} within a node.
 * It is used to find out the overall production of a system when many individual generators exist.
 * @author bwindo
 *
 */
public class EleGenConglomerate {
	private ArrayList<ElectricityGeneration> genList;
	
	public EleGenConglomerate()
	{
		this(new ArrayList<ElectricityGeneration>());
	}
	/**
	 * Creates a new conglomerate from an ArrayList of individual generations.
	 * @param arr The array of existing {@link ElectricityGeneration} elements.
	 */
	public EleGenConglomerate(ArrayList<ElectricityGeneration> arr)
	{
		genList = arr;
	}
	public void addGen(ElectricityGeneration e)
	{
		genList.add(e);
	}
	/**
	 * Calculates the current output from all the {@link ElectricityGeneration} objects that are wrapped in this. 
	 * @return the sum of all the individual outputs at this time.
	 */
	public double getCurrentOutput()
	{
		
		double tally = 0;
		
		for (ElectricityGeneration e : genList)
		{
			tally += e.getCurrentOutput();
		}
		return tally;
	}
	/**
	 * Calculates the output at a given time from all the {@link ElectricityGeneration} objects that are wrapped in this. 
	 * @param d The {@link Date} that marks the time to evaluate the generation outputs.
	 * @return The sum of all the individual outputs at this time.
	 */
	public double getPredictedOutput(Date d)
	{
		double tally = 0;
		
		for (ElectricityGeneration e : genList)
		{
			tally += e.getPredictedOutput(d);
		}
		return tally;
	}
  
  
}
