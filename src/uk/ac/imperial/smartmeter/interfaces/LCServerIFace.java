package uk.ac.imperial.smartmeter.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.TicketTuple;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public interface LCServerIFace extends Remote {
	/**
	 * Debug method used to test successful RMI comms.
	 * @param name The ip address of the server.
	 * @param port The port the server listens on.
	 * @return A string given by the server
	 * @throws RemoteException
	 */
	String getMessage(String name, int port) throws RemoteException;
	/**
	 * Registers the client with the receiving {@link LCServer}, adding it to the log of users.
	 * This is mandatory for any further communications to take place.
	 * @param location The ip address of the server.
	 * @param port The port the server listens on.
	 * @param ownPort The port the client listens on.
	 * @param userId The user identity of the client.
	 * @param ipAddr The ip address of the client.
	 * @param pubKey The public key of the client.
	 * @return Success?
	 * @throws RemoteException
	 */
	Boolean registerClient(String location, int port, int ownPort, String userId, String ipAddr, String pubKey) throws RemoteException;

	/**
	 * Offers a ticket in exchange for another ticket.
	 * @param location The ip address of the server.
	 * @param port The port the server listens on.
	 * @param tktOld The offered ticket.
	 * @param tktNew The desired ticket.
	 * @return A {@link TicketTuple} containing the modified tickets.
	 * @throws RemoteException
	 */
	TicketTuple offer(String location, int port, ElectricityTicket tktOld, ElectricityTicket tktNew) throws RemoteException; 
	
	/**
	 * 
	 * @param location The ip address of the server.
	 * @param port The port the server listens on.
	 * @param tuple A {@link TicketTuple} containing the tickets to be modified.
	 * @return A {@link TicketTuple} containing the modified tickets.
	 * @throws RemoteException
	 */
	TicketTuple offer(String location, int port, TicketTuple tuple) throws RemoteException; 
	
	/**
	/**
	 * Finds the electricity tickets that are active at the same time as a given {@link ElectricityRequirement}.
	 * @param req The requirement in question.
	 * @param location The ip address of the server.
	 * @param port The port the server listens on.
	 * @return An {@link ArraySet} of the tickets that conflict with the given requirement.
	 * @throws RemoteException
	 */
	ArraySet<ElectricityTicket> queryCompeting(String location, int port, ElectricityRequirement req) throws RemoteException;
}
