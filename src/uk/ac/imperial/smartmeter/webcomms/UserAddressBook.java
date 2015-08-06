package uk.ac.imperial.smartmeter.webcomms;

import java.util.HashMap;
import java.util.Map;

public class UserAddressBook {
	private Map<String, UserAddress> addresses;
	public UserAddressBook()
	{
		addresses = new HashMap<String, UserAddress>();
	}
	public boolean queryUserExists(String id)
	{
		return addresses.containsKey(id);
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
}
