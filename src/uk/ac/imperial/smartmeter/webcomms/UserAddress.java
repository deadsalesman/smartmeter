package uk.ac.imperial.smartmeter.webcomms;

public class UserAddress {
	private String userID;
	private String userAddress;
	private int userPort;
	private Double history;
	private String pubKey;
	
	public UserAddress(String id, String addr, int port, Double hist, String pubkey)
	{
		userID = id;
		userAddress = addr;
		userPort = port;
		history = hist;
		pubKey = pubkey;
	}
	public UserAddress(String id, String addr, int port, Double hist)
	{
		this(id, addr,port, hist, "");
	}
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
