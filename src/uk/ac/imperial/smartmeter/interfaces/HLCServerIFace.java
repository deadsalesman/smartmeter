package uk.ac.imperial.smartmeter.interfaces;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.UUID;

import uk.ac.imperial.smartmeter.impl.HLController;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.TicketTuple;
import uk.ac.imperial.smartmeter.res.Triple;
import uk.ac.imperial.smartmeter.res.Pair;
import uk.ac.imperial.smartmeter.res.UserAgent;

 public interface HLCServerIFace extends ServerIFace {
	 /**
	  * Gets a log of all the client addresses that have been registered to the HLCServer.
	  * @return a map of userId to a Tuple of publicKey and InetSocketAddress.
	  * @throws RemoteException
	  */
	HashMap<String, Triple<String, InetSocketAddress, Double>> getAddresses() throws RemoteException;

	/**
	 * Attempts to extend a ticket if the ticket duration is less than the requirement duration, changing the underlying node data.
	 * @param req
	 * @param tktNew
	 * @param tktOld
	 * @return a Tuple containing the altered tickets.
	 */
	TicketTuple extendMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) throws RemoteException;
	
	/**
	 * Attempts to extend a ticket if the ticket duration is less than the requirement duration, without changing the underlying node data.
	 * @param req
	 * @param tktNew
	 * @param tktOld
	 * @return a Tuple containing the altered tickets.
	 */
	TicketTuple extendImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) throws RemoteException;
	
	/**
	 * Attempts to intensify a ticket if the ticket amplitude is less than the requirement amplitude, changing the underlying node data.
	 * @param req
	 * @param tktNew
	 * @param tktOld
	 * @return a Tuple containing the altered tickets.
	 */
	TicketTuple intensifyMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) throws RemoteException;

	/**
	 * Attempts to intensify a ticket if the ticket amplitude is less than the requirement amplitude, without changing the underlying node data.
	 * @param req
	 * @param tktNew
	 * @param tktOld
	 * @return a Tuple containing the altered tickets.
	 */
	TicketTuple intensifyImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) throws RemoteException;

	/**
	 * Removes all information from local data structures, including fields and databases.
	 * @return Success?
	 */
	Boolean wipeHLC() throws RemoteException;

	/**
	 * Adds a new user to the internal address book.
	 * @param salt The salt to be cohashed with the password.
	 * @param hash The hash of the password.
	 * @param userId The id of the new UserAgent.
	 * @param userName The username to be used by the controller to represent the UserAgent.
	 * @param pubKey The public key to be associated with the userAgent.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 * @param port The port that the user listens on.
	 * @return A tuple containing the HLCServer's id and publicKey.
	 * @throws RemoteException
	 */
	Pair<String,String> registerUser(String salt, String hash, String userId, String userName, String pubKey, Double social, Double generation, Double economic, int port) throws RemoteException;

	/**
	 * Adds an {@link ElectricityRequirement} to the user whose id matches that of its owner.
	 * @param req The requirement in question.
	 * @return true iff there is a user whose id matches and the addition of the requirement was successful.
	 */
	Boolean setRequirement(ElectricityRequirement req) throws RemoteException;

	/**
	 * Returns an ArraySet of all the {@link ElectricityTicket}s associated with a given user, identified by the String representation of the {@link UserAgent}'s {@link UUID}.
	 * @param userId The id of the user in question.
	 * @return An ArraySet of the tickets of interest.
	 */
	ArraySet<ElectricityTicket> getTickets(String userId) throws RemoteException;

	/**
	 * Attempts to calculate tickets for the currently entered user agents
	 * @return the user agents with their tickets added
	 */
	Boolean GodModeCalcTKTS() throws RemoteException;
	
	/**
	 * Sets the electricity generation of the specified {@link UserAgent} to the specified {@link ElectricityGeneration}
	 * @param userId The representation of the {@link UUID} of the user in question.
	 * @param e The {@link ElectricityGeneration} to be adopted by the user.
	 * @return Success?
	 */
	Boolean setGeneration(String userId, ElectricityGeneration e) throws RemoteException;

	/**
	 * Queries the database for a given user.
	 * @param userId The representation of the {@link UUID} of the user in question.
	 * @return true if the user exists in {@link HLController#agents}.
	 */
	Boolean queryUserExists(String userId) throws RemoteException;

	/**
	 * Gets the UUID registered to a given username.
	 * @param userId The given username.
	 * @return The associated id if it exists, null otherwise.
	 * @throws RemoteException
	 */
	String getRegisteredUUID(String userId) throws RemoteException;
	
	/**
	 * 
	 * @return The public key of the HLCServer
	 * @throws RemoteException
	 */
	String getPublicKey() throws RemoteException;

}
