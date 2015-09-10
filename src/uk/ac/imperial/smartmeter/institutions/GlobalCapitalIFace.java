package uk.ac.imperial.smartmeter.institutions;

import java.rmi.RemoteException;

import uk.ac.imperial.smartmeter.interfaces.ServerIFace;

/**
 * The global capital represents the social capital that has been accumulated over all interactions of a given user.
 * @author bwindo
 *
 */
public interface GlobalCapitalIFace extends ServerIFace{
	/**
	 * Attempts to add a specified real number (positive or negative) to the global capital for a given user.
	 * @param userId A string representing the UUID of the user in question.
	 * @param value The number to be added to the existing capital.
	 * @return TODO
	 * @throws RemoteException
	 */
	Boolean setCapital(String userId, Double value) throws RemoteException;
	/**
	 * Attempts to retrieve a current user's global capital value.
	 * @param userId A string representing the UUID of the user in question.
	 * @return The value of the user's current global capital.
	 * @throws RemoteException
	 */
	Double getCapital(String userId) throws RemoteException;
	
	/**
	 * Prints the social capital to .csv files.
	 * @return True if success
	 * @throws RemoteException
	 */
	Boolean printCapital() throws RemoteException;
	
}
