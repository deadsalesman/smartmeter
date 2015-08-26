package uk.ac.imperial.smartmeter.webcomms;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.res.Twople;

public class UserAddressBook {
	private String HLCiD;
	private Map<String, UserAddress> addresses;
	public UserAddressBook()
	{
		addresses = new HashMap<String, UserAddress>();
	}
	public boolean queryUserExists(String id)
	{
		return addresses.containsKey(id);
	}
	public boolean findAndPrintPubKey(String id)
	{
		UserAddress x = addresses.get(id);
		if (x!=null)
		{
		try {
			FileOutputStream fOut = new FileOutputStream(id+"_pub.bpg");
			for (byte b: x.getPubKey().getBytes("UTF-8"))
			{
				fOut.write(b);
			}
			fOut.close();
			return true;
		} catch (IOException e) {
		}
		}
		return false;
	}
	public boolean newAddress(String id,String location,int port)
	{
		boolean exists = queryUserExists(id);
		if (!exists) {
			addresses.put(id, new UserAddress(id, location, port));
		}
		return !exists;
	}
	public boolean addUser(UserAddress u)
	{
		boolean exists = queryUserExists(u.getUserID());
		if (!exists) {
			addresses.put(u.getUserID(), u);
		}
		return !exists;
	}
	public Double getHistory(String userId) {
		if (queryUserExists(userId)) {
			return addresses.get(userId).getHistory();
		}
		return 0.;
	}
	public boolean setHistory(String userId, Double history)
	{
		boolean exists = queryUserExists(userId);
		if (exists)
		{
			addresses.get(userId).setHistory(history);
		}
		return exists;
	}
	public boolean addUser(Entry<String, Twople<String, InetSocketAddress>> entry) {
		boolean exists = queryUserExists(entry.getKey());
		if (!exists)
		{
			addresses.put(entry.getKey(),new UserAddress(entry.getKey(),entry.getValue().left,entry.getValue().right.getHostName(),entry.getValue().right.getPort()));
		}
		return exists;
	}
	public String getHLCiD() {
		return HLCiD;
	}
	public void setHLCiD(String hLCiD) {
		HLCiD = hLCiD;
	}
}
