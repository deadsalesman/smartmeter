package uk.ac.imperial.smartmeter.institutions;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import uk.ac.imperial.smartmeter.interfaces.HLCServerIFace;
import uk.ac.imperial.smartmeter.webcomms.HLCServer;

public class GenericInstitution implements InstitutionIFace, GlobalCapitalIFace {

	/**
	 * The unique identifier for the institution.
	 */
	String name;
	/**
	 * The location of the HLCServer used to query global capital.
	 */
	String hLCHost;
	/**
	 * The port of the HLCServer used to query global capital.
	 */
	Integer hLCPort;
	/**
	 * A list of the members of the institution.
	 */
	ArrayList<String> members;
	/**
	 * The threshold the members of the institution must be below in order to remain in the institution.
	 */
	Double threshold;
	/**
	 * Generates a new institution with the specified parameters.
	 * @param HLCHost The ip address of the HLCServer.
	 * @param HLCPort The port the HLCServer listens on.
	 * @param thresh  The social capital threshold for inclusion in the institution.
	 */
	GenericInstitution(String HLCHost, Integer HLCPort, Double thresh)
	{
		hLCHost = HLCHost;
		hLCPort = HLCPort;
		threshold = thresh;
	}
	/**
	 * Generates a new institution with the specified parameters.
	 * @param HLCHost The ip address of the HLCServer.
	 * @param HLCPort The port the HLCServer listens on.
	*/
	GenericInstitution(String HLCHost, Integer HLCPort)
	{
		this(HLCHost, HLCPort, 10.);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean checkUser(String userID, String institutionName) throws RemoteException {
		if (institutionName.equals(name))
		{
		if (checkSufficientCapital(userID))
		{
			return true;
		}
		else
		{
			removeUser(userID, institutionName);
		}
		}

		return false;
	}
	/**
	 * Checks to see if the user has sufficient global capital to remain in the institution.
	 * @param userID The user in question.
	 * @return true if the global capital value of the user is greater than the threshold.
	 * @throws RemoteException
	 */
	private Boolean checkSufficientCapital(String userID) throws RemoteException
	{
		return (getCapital(userID) <= threshold);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean addUser(String userID, String institutionName) throws RemoteException {
		if (institutionName.equals(name))
		{
		if (checkSufficientCapital(userID))
		{
			members.add(userID);
			return true;
		}
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean removeUser(String userID, String institutionName) throws RemoteException {
		if (institutionName.equals(name))
		{
		for (String s : members)
		{
			if (s.equals(userID))
			{
				members.remove(s);
				return true;
			}
		}
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean setCapital(String userId, Double value) throws RemoteException {
		return lookupHLCCapital().setCapital(userId, value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getCapital(String userId) throws RemoteException {
		return lookupHLCCapital().getCapital(userId);
	}
	
	/**
	 * Looks up the registry hosted at a remote {@link HLCServer}.
	 * @return A {@link HLCServerIFace} from the remote registry, or null if this was not possible.
	 */
	private GlobalCapitalIFace lookupHLCCapital()
	{
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(hLCHost,hLCPort);
			return (GlobalCapitalIFace) registry.lookup("HLCServer");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		};
		return null;
	}

}
