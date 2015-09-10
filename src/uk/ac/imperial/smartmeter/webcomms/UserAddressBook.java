package uk.ac.imperial.smartmeter.webcomms;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.log.LogCapital;
import uk.ac.imperial.smartmeter.res.Triple;

/**
 * Container class for a set of {@link UserAddress}es.
 * @author bwindo
 *
 */
public class UserAddressBook {
	/**
	 * The identity of the {@link HLCServer} in charge of the node which owns the UserAddressBook.
	 */
	private String HLCiD;
	private Map<String, UserAddress> addresses;
	public UserAddressBook()
	{
		addresses = new HashMap<String, UserAddress>();
	}
	/**
	 * Queries whether a user exists within the address book.
	 * @param id The id of the user to be queried.
	 * @return True if the user exists.
	 */
	public boolean queryUserExists(String id)
	{
		return addresses.containsKey(id);
	}
	/**
	 * Queries whether a user exists within the address book, and writes the associated public key of the user to file using the standard naming convention if the public key exists.
	 * @param id The id of the user to have their public key printed.
	 * @return Success?
	 */
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
	/** 
	 * Adds an address to the address book.
	 * @param id The identity of the new user.
	 * @param location The ip address of the new user.
	 * @param port The port the new user listens on.
	 * @return true if the user is not already in the address book.
	 */
	public boolean newAddress(String id,String location,int port)
	{
		boolean exists = queryUserExists(id);
		if (!exists) {
			addresses.put(id, new UserAddress(id, location, port));
		}
		return !exists;
	}
	/**
	 * Adds an address to the address book.
	 * @param u The address to be added.
	 * @return true if the user is not already in the address book.
	 */
	public boolean addUser(UserAddress u)
	{
		boolean exists = queryUserExists(u.getUserID());
		if (!exists) {
			addresses.put(u.getUserID(), u);
		}
		return !exists;
	}
	/**
	 * Returns the double associated with a specific user to indicate their current level of favour.
	 * @param userId The id of the user in question.
	 * @return The associated rating for their history.
	 */
	public Double getHistory(String userId) {
		if (queryUserExists(userId)) {
			return addresses.get(userId).getHistory();
		}
		return 0.;
	}
	/**
	 * Sets the history of a specific user to indicate their current level of favour.
	 * @param userId The id of the user in question.
	 * @param history The new rating for their history.
	 * @return True if the user exists.
	 */
	public boolean setHistory(String userId, Double history)
	{
		boolean exists = queryUserExists(userId);
		if (exists)
		{
			addresses.get(userId).setHistory(history);
		}
		return exists;
	}
	/**
	 * Adds an address to the address book.
	 * @param entry A Twople representation of the new address as a userid:InetSocketAddress pair.
	 * @return  true if the user is not already in the address book.
	 */
	public boolean addUser(Entry<String, Triple<String,InetSocketAddress, LogCapital>> entry) {
		boolean exists = queryUserExists(entry.getKey());
		if (!exists)
		{
			addresses.put(entry.getKey(),new UserAddress(entry.getKey(),entry.getValue().left,entry.getValue().central.getHostName(),entry.getValue().central.getPort()));
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
