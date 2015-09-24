package uk.ac.imperial.smartmeter.allocator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.UserAgent;

public class RescherArbiter {


	private Double demandModifier;
	private Double equalityModifier;
	private Double needsModifier;
	private Double productivityModifier;
	private Double socialModifier;
	private ArrayList<Double> weightings;
	/**
	 * Default ctor, used in testing. 
	 * All parameters set to unity.
	 */
	public RescherArbiter()
	{
		this(1.,1.,0.1,1.,1.);
	}
	/**
	 * Initialises the RescherArbiter with the specified weightings for each of
	 * the distributive justice attributes considered
	 * @param d Weighting for demand.
	 * @param e Weighting for equality.
	 * @param n Weighting for needs.
	 * @param p Weighting for productivity.
	 * @param s Weighting for social worth.
	 */
	public RescherArbiter(Double d, Double e, Double n, Double p, Double s)
	{
		demandModifier = d;
		equalityModifier = e;
		needsModifier = n;
		productivityModifier = p;
		socialModifier = s;
		
		weightings = new ArrayList<Double>();
		weightings.add(demandModifier);
		weightings.add(equalityModifier);
		weightings.add(needsModifier);
		weightings.add(productivityModifier);
		weightings.add(socialModifier);
	}
	/**
	 * Initialises an ArrayList<Integer> of the specified size.
	 * @param size
	 * @return ArrayList of size
	 */
	private ArrayList<Integer> getValidIntArray(int size)
	{
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int i = 0; i < size; i++)
		{
			ret.add(-1);
		}
		
		return ret;
	}
	/**
	 * Initialises an ArrayList<Double> of the specified size.
	 * @param size
	 * @return ArrayList of size.
	 */
	private ArrayList<Double> getValidDoubleArray(int size)
	{
		ArrayList<Double> ret = new ArrayList<Double>();
		for (int i = 0; i < size; i++)
		{
			ret.add(0.);
		}
		
		return ret;
	}
	/**
	 * Orders the set of UserAgents according to the specified comparator.
	 * 
	 * @param users The users to be sorted.
	 * @param canon The comparator used.
	 * @return ArrayList of the relative positions of the sorted elements.
	 *         where ret[i] = position of i in the sorted array
	 */
	public ArrayList<Integer> evaluateCanon(ArraySet<UserAgent> users, Comparator<UserAgent> canon)
	{
		ArrayList<Integer> ret = getValidIntArray(users.getSize());
		ArraySet<UserAgent> temp = new ArraySet<UserAgent>();
		temp.addAll(users);
		ArraySet.sort(temp, canon);
		for (int i = 0; i < users.getSize(); i++)
		{
			for (int j = 0; j < temp.getSize(); j++)
			{
				if (users.get(i).getId().equals(temp.get(j).getId())){
					ret.set(i,j);
				}
			}
		}
		return ret;
	}
	/**
	 * @deprecated
	 * @param m
	 * @return The normalised array.
	 */
	@SuppressWarnings("unused")
	private Map<UserAgent, Double> normaliseWeighting(Map<UserAgent,Double> m)
	{
		Double totalEnergy = 0.;
		Double totalWeight = 0.;
		Double scalingFactor = 1.;

		for (Entry<UserAgent, Double> e : m.entrySet()) {
			totalWeight = 0.;
			for (ElectricityRequirement r : e.getKey().getReqTktMap().keySet()) {
				totalEnergy += r.getMaxConsumption() * r.getDuration();
			}
			totalWeight += e.getValue();
		}
		scalingFactor = totalEnergy / totalWeight;
		for (Entry<UserAgent, Double> e : m.entrySet()) {
			e.setValue(e.getValue() * scalingFactor);
		}

		return m;
	}
	/**
	 * Maps the user agents to a double representing their consolidated rankings
	 * according to the Rescher criteria
	 * @param users The users to be weighted.
	 * @return A map of users to their weights.
	 */
	public Map<UserAgent, Double> getWeighting(ArraySet<UserAgent> users)
	{
		Map<UserAgent, Double> ret = new HashMap<UserAgent, Double>();
		/*
		ArrayList<ArrayList<Integer>> ranks = new ArrayList<ArrayList<Integer>>();
		
		ranks.add(evaluateCanon(users, new demandComparator()));
		ranks.add(evaluateCanon(users, new equalityComparator()));
		ranks.add(evaluateCanon(users, new needsComparator()));
		ranks.add(evaluateCanon(users, new productivityComparator()));
		ranks.add(evaluateCanon(users, new socialComparator()));
		
		ArrayList<Double> total = getValidDoubleArray(users.getSize());
		for (ArrayList<Integer> a : ranks)
		{
			for (int i = 0; i < a.size(); i++)
			{
				total.set(i, 1 + a.get(i) + weightings.get(ranks.indexOf(a))*total.get(i));
			}
		}
		*/
		for (UserAgent x : users)
		{
			Double tally = 0.;
			tally+= x.getMaxPower()*demandModifier;
			tally+= x.getAverageAllocation()*equalityModifier;
			for (ElectricityRequirement e : x.getReqs())
			{
				tally+= e.getMaxConsumption()*needsModifier;
			}
			tally+= x.getEconomicPower()*productivityModifier;
			tally+= x.getSocialWorth()*socialModifier;

			ret.put(x, tally);
			x.weight = tally;
		}
		/*
		for (int i = 0; i < users.getSize(); i++) {
			ret.put(users.get(i), total.get(i));
			users.get(i).weight = total.get(i);
		}
		*/
		return ret;
		
	}
}
