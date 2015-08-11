package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class Bulletin {
	private ArrayList<InetSocketAddress> users;
	private Integer index = 0;
	Boolean sociallyAwah = false; //has knowledge of other users
	Bulletin()
	{
	}

	public InetSocketAddress getNextAddress() {
		InetSocketAddress ret = null;
		
		if (sociallyAwah)
		{
			
			ret =  users.get(index);
		}
		index = (index + 1) % users.size();
		return ret;
	}

	public void set(HashMap<String, InetSocketAddress> peers) {
		sociallyAwah = true;
		users = new ArrayList<InetSocketAddress>(peers.values());
		index = 0;
	}

}
