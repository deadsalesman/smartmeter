package uk.ac.imperial.smartmeter.res;


import java.util.UUID;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
/**
 * @deprecated
 * @author bwindo
 *
 */
public class User implements UniqueIdentifierIFace{
	private UUID id;
	private String name;
	private String hash = "";
	private String salt = "decentralised";
	//DEPRECATED: 
	/*public User(String saltNew, String passwdHash, String idString, String username)
	{
		name = username;
		salt = saltNew;
		hash = passwdHash;
		id = UUID.fromString(idString);
	}
	public User(String username)
	{
		name = username;
		id = UUID.randomUUID();
	}
	public User(String username, String password)
	{
		this(username);
		//TODO: make not awful
		hash = Integer.toString(password.hashCode() ^ salt.hashCode()); //supermegainsecure for any value of secure. do properly 
		
	}
	public User(String username, String uID, String password)
	{
		name = username;
		id = UUID.fromString(uID);
		//TODO: make not awful
		hash = Integer.toString(password.hashCode() ^ salt.hashCode()); //supermegainsecure for any value of secure. do properly 
		
	}
	*/
	@Override
	public String getId() {
		return id.toString();
	}
	public String getName() {
		return name;
	}
	public String getHash() {
		return hash;
	}
	public String getSalt() {
		return salt;
	}
	public boolean verify(String pass)
	{
		return (pass.hashCode() ^ salt.hashCode()) == Integer.parseInt(hash);
		
	}

}
