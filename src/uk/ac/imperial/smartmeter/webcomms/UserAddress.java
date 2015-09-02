package uk.ac.imperial.smartmeter.webcomms;
/**
 * A class to represent the communication details involved with an individual user.
 * @author bwindo
 *
 */
public class UserAddress {
	private String userID;
	/**
	 * The ip address of the user.
	 */
	private String userAddress;
	/**
	 * The port of the user.
	 */
	private int userPort;
	/** 
	 * The current rating of the user in terms of generosity. If the user is very generous, this will be high. Negative indicates the user has received many favours from the owner of this address.
	 */
	private Double history;
	/**
	 * The public key associated with the user.
	 */
	private String pubKey;
	
	/**
	 * Creates a new object from the parameters given.
	 * @param id The id of the user.
	 * @param addr The ip address of the user. 
	 * @param port The port of the user.
	 * @param hist The current rating of the user in terms of generosity.
	 * @param pubkey The public key associated with the user.
	 */
	public UserAddress(String id, String addr, int port, Double hist, String pubkey)
	{
		userID = id;
		userAddress = addr;
		userPort = port;
		history = hist;
		pubKey = pubkey;
	}
	/**
	 * Creates a new object from the given parameters, the history is given as 0 indicating no previous interactions.
	 * @param id The id of the user.
	 * @param addr The ip address of the user. 
	 * @param port The port of the user.
	 * @param pubkey The public key associated with the user.
	 */
	public UserAddress(String id, String addr, String pubkey, int port)
	{
		this(id, addr, port,0.,pubkey);
	}
	/**
	 * Creates a new object from the given parameters, the public key is given as empty.
	 * @param id The id of the user.
	 * @param addr The ip address of the user. 
	 * @param port The port of the user.
	 * @param hist The current rating of the user in terms of generosity.
	 */
	public UserAddress(String id, String addr, int port, Double hist)
	{
		this(id, addr,port, hist, "");
	}
	/**
	 * Creates a new object from the given parameters, the public key is given as empty and the user has no previous transactions.
	 * @param id The id of the user.
	 * @param addr The ip address of the user. 
	 * @param port The port of the user.
	 */
	public UserAddress(String id, String addr, int port)
	{
		this(id, addr, port, 0.);
	}
	public int getUserPort() {
		return userPort;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public String getUserID() {
		return userID;
	}

	public Double getHistory() {
		return history;
	}

	public void setHistory(Double history) {
		this.history = history;
	}
	public String getPubKey() {
		return pubKey;
	}
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

}
