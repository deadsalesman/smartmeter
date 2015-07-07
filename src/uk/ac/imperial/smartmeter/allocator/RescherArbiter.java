package uk.ac.imperial.smartmeter.allocator;

import java.util.ArrayList;
import uk.ac.imperial.smartmeter.res.ArraySet;

public class RescherArbiter {


	private Double demandModifier;
	private Double equalityModifier;
	private Double needsModifier;
	private Double productivityModifier;
	private Double socialModifier;
	private ArrayList<Double> weightings;
	
	RescherArbiter(Double d, Double e, Double n, Double p, Double s)
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
	ArrayList<Integer> demandCanon(ArraySet<UserAgent> users)
	{
		ArraySet.sort(users, new demandComparator());
		return null;
	}
	ArrayList<Integer> equalityCanon(ArraySet<UserAgent> users)
	{
		return null;
	}
	ArrayList<Integer> needsCanon(ArraySet<UserAgent> users)
	{
		return null;
	}
	ArrayList<Integer> productivityCanon(ArraySet<UserAgent> users)
	{
		return null;
	}
	ArrayList<Integer> socialCanon(ArraySet<UserAgent> users)
	{
		return null;
	}
	ArrayList<Double> getFinalWeighting(ArraySet<UserAgent> users)
	{
		ArrayList<ArrayList<Integer>> ranks = new ArrayList<ArrayList<Integer>>();
		ranks.add(demandCanon(users));
		ranks.add(equalityCanon(users));
		ranks.add(needsCanon(users));
		ranks.add(productivityCanon(users));
		ranks.add(socialCanon(users));
		
		ArrayList<Double> total = new ArrayList<Double>();
		for (ArrayList<Integer> a : ranks)
		{
			for (int i = 0; i < a.size(); i++)
			{
				total.set(i, a.get(i) + weightings.get(i)*total.get(i));
			}
		}
		return total;
	}
}
