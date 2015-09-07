package uk.ac.imperial.smartmeter.institutions;

import java.rmi.RemoteException;

import uk.ac.imperial.smartmeter.interfaces.ServerIFace;

/**
 * An institution represents a collective body that may be joined by individual users to promote cohesion between those agents.
 * 
 * @author bwindo
 *
 */
public interface InstitutionIFace extends ServerIFace{
	/**
	 * Verifies if a given user is a member of the institution.
	 * @param userID The String form of the UUID representation of the user.
	 * @param institutionName The name of the institution
	 * @return true if the user is a member of the institution.
	 * @throws RemoteException
	 */
	public  Boolean checkUser(String userID, String institutionName) throws RemoteException;
	/**
	 * Attempts to add a user to the institution.
	 * @param userID The String form of the UUID representation of the user.
	 * @param institutionName The name of the institution
	 * @return true if the user's global capital is sufficient and they have not previously been removed from the institution.
	 * @throws RemoteException
	 */
	public  Boolean addUser(String userID, String institutionName) throws RemoteException;
	/**
	 * Attempts to remove a user from the institution.
	 * @param userID The String form of the UUID representation of the user.
	 * @param institutionName The name of the institution
	 * @return true if the user was a member of the institution beforehand, false otherwise.
	 * @throws RemoteException
	 */
	public Boolean removeUser(String userID, String institutionName) throws RemoteException;
}
