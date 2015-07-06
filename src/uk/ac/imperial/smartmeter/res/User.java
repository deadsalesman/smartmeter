package uk.ac.imperial.smartmeter.res;


import java.util.UUID;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class User implements UniqueIdentifierIFace{
	private UUID id;
	private String name;
	private String hash;
	private String salt = "decentralised";
	
	public User(String saltNew, String passwdHash, String idString, String username)
	{
		name = username;
		salt = saltNew;
		hash = passwdHash;
		id = UUID.fromString(idString);
	}
	User(String username)
	{
		name = username;
		id = UUID.randomUUID();
	}
	User(String username, String password)
	{
		this(username);
		//TODO: make not awful
		hash = Integer.toString(password.hashCode() ^ salt.hashCode()); //supermegainsecure for any value of secure. do properly 
		
	}
	@Override
	public String getId() {
		return id.toString();
	}
	public String getName() {
		return name;
	}
	public boolean verify(String pass)
	{
		return (pass.hashCode() ^ salt.hashCode()) == Integer.parseInt(hash);
		
	}

}
