package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Bulletin {
	private ArrayList<NamedSocket> users;
	private Integer index = 0;
	Boolean sociallyAwah = false; //has knowledge of other users
	Bulletin()
	{
		users = new ArrayList<NamedSocket>();
	}

	public InetSocketAddress getNextAddress() {
		InetSocketAddress ret = null;
		
		if ((sociallyAwah)&&(users!=null))
		{
			
			ret =  users.get(index).socket;
			index = (index + 1) % users.size();
		}
		return ret;
	}
	public void add(NamedSocket e)
	{
		users.add(e);
	}
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
