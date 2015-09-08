package uk.ac.imperial.smartmeter.log;

import java.rmi.RemoteException;

public interface RegisterTransactionIFace {
	
	/**
	 * Adds a ticket transaction to the central log on the HLCServer
	 * @param log The log to be added.
	 * @return True if successfully added.
	 * @throws RemoteException
	 */
	Boolean registerTicketTransaction(LogTicketTransaction log) throws RemoteException;
	
	/**
	 * Calls the server to write the existing tickets to a .csv file.
	 * @return True if successful.
	 * @throws RemoteException
	 */
	Boolean printTicketTransactions() throws RemoteException;
}
