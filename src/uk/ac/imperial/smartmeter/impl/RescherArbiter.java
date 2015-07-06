package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;

public class RescherArbiter {


	private double demandModifier;
	private double equalityModifier;
	private double needsModifier;
	private double productivityModifier;
	private double socialModifier;
	
	RescherArbiter(double d, double e, double n, double p, double s)
	{
		demandModifier = d;
		equalityModifier = e;
		needsModifier = n;
		productivityModifier = p;
		socialModifier = s;
	}
	ArrayList<Integer> demandCanon()
	{
		return null;
	}
	ArrayList<Integer> equalityCanon()
	{
		return null;
	}
	ArrayList<Integer> needsCanon()
	{
		return null;
	}
	ArrayList<Integer> productivityCanon()
	{
		return null;
	}
	ArrayList<Integer> socialCanon()
	{
		return null;
	}
	ArrayList<Integer> getFinalWeighting()
	{
		ArrayList<ArrayList<Integer>> ranks = new ArrayList<ArrayList<Integer>>();
		ranks.add(demandCanon());
		ranks.add(equalityCanon());
		ranks.add(needsCanon());
		ranks.add(productivityCanon());
		ranks.add(socialCanon());
		
		ArrayList<Integer> total = new ArrayList<Integer>();
		for (ArrayList<Integer> a : ranks)
		{
			for (int i = 0; i < a.size(); i++)
			{
				total.set(i, a.get(i) + total.get(i));
			}
		}
		return total;
	}
}
