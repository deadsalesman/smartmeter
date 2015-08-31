package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
/**
 * Container class for a set of named sockets.
 * {@link Bulletin#sociallyAwah} set to true if values have been updated at any point.
 * @author Ben Windo
 *
 */
public class Bulletin {
	private ArrayList<NamedSocket> users;
	private Integer index = 0;
	Boolean sociallyAwah = false; //has knowledge of other users
	/**
	 * Basic ctor, initialises internal ArrayList
	 */
	Bulletin()
	{
		users = new ArrayList<NamedSocket>();
	}

	/**
	 * Used to iterate through the addresses.
	 * The next address is returned and the internal count of addresses is incremented.
	 * Essentially the same functionality as a cyclical shift register with a single tap
	 * @return InetSocketAddress that is next to be tapped
	 */
	public InetSocketAddress getNextAddress() {
		InetSocketAddress ret = null;
		
		if ((sociallyAwah)&&(users.size()!=0))
		{
			
			ret =  users.get(index).socket;
			index = (index + 1) % users.size();
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
		users.add(e);
	}
	/**
	 * Adds a set of <String, InetSocketAddress> objects to the internal storage
	 * Sets {@link Bulletin#sociallyAwah}
	 * @param peers Objects to be added to storage.
	 */
	public void set(HashMap<String, InetSocketAddress> peers) {
		sociallyAwah = true;
		users = new ArrayList<NamedSocket>();
		for (Entry<String, InetSocketAddress> e : peers.entrySet())
		{
			users.add(new NamedSocket(e.getKey(),e.getValue()));
		}
		index = 0;
	}

}
