package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import uk.ac.imperial.smartmeter.res.BulletinProbabilityGenerator;
import uk.ac.imperial.smartmeter.res.Pair;
/**
 * Container class for a set of named sockets.
 * {@link Bulletin#sociallyAwah} set to true if values have been updated at any point.
 * @author Ben Windo
 *
 */
public class Bulletin {
	private ArrayList<Pair<NamedSocket,Integer>> users;
	private Integer index = 0;
	Boolean sociallyAwah = false; //has knowledge of other users
	/**
	 * Basic ctor, initialises internal ArrayList
	 */
	Bulletin()
	{
		users = new ArrayList<Pair<NamedSocket,Integer>>();
	}
 
	/**
	 * Used to iterate through the addresses.
	 * The next address is returned and the internal count of addresses is incremented.
	 * Essentially the same functionality as a cyclical shift register with a single tap
	 * @return {@link InetSocketAddress} that is next to be tapped
	 */
	public Pair<NamedSocket, Integer> getNextAddress() {
		Pair<NamedSocket, Integer> ret = null;
		try{
		BulletinProbabilityGenerator bpg = new BulletinProbabilityGenerator();
		ArrayList<Pair<NamedSocket, Double>> probabilities = bpg.generateProbabilities(users);
		
		Random rnd = new Random();
		Double roll = rnd.nextDouble();
		Double total = 0.;
		for (int i = 0; i < probabilities.size(); i++)
		{
			total += probabilities.get(i).right;
			if (total >= roll)
			{
				ret = users.get(i);
			}
		}
		}
		catch(NullPointerException e)
		{
			
		}
		return ret;
	}
	/**
	 * Adds a NamedSocket to the internal list.
	 * Does not set {@link Bulletin#sociallyAwah}
	 * @param e The NamedSocket in question.
	 */
	public void add(NamedSocket e)
	{
		users.add(new Pair<NamedSocket, Integer>(e,0));
	}
	/**
	 * Adds a set of <String, InetSocketAddress> objects to the internal storage
	 * Sets {@link Bulletin#sociallyAwah}
	 * @param peers Objects to be added to storage.
	 */
	public void set(HashMap<String, InetSocketAddress> peers) {
		sociallyAwah = true;
		users = new ArrayList<Pair<NamedSocket,Integer>>();
		for (Entry<String, InetSocketAddress> e : peers.entrySet())
		{
			users.add(new Pair<NamedSocket, Integer>(new NamedSocket(e.getKey(),e.getValue()),0));
		}
		index = 0;
	}
	
	/**
	 * Updates the utility for a given user, used internally to calculate the next user to consider.
	 * @param id A string representing the Id of the user to be considered.
	 * @param d A change in the user's social capital value.
	 */
	public void setUtility(Pair<NamedSocket, Integer> subject, Integer d)
	{
		subject.right += d;
	}

}
