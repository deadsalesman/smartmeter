package uk.ac.imperial.smartmeter.interfaces;

import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.HashMap;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.TicketTuple;

 public interface HLCServerIFace extends ServerIFace {
	HashMap<String, InetSocketAddress> getAddresses() throws RemoteException;

	TicketTuple extendMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) throws RemoteException;

	TicketTuple extendImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) throws RemoteException;

	TicketTuple intensifyMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) throws RemoteException;

	TicketTuple intensifyImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) throws RemoteException;

	Boolean wipeHLC() throws RemoteException;

	Boolean registerUser(String salt, String hash, String userId, String userName, String pubKey, Double worth, Double generation, Double economic, int port) throws RemoteException;

	Boolean setRequirement(ElectricityRequirement req) throws RemoteException;

	ArraySet<ElectricityTicket> getTickets(String user) throws RemoteException;

	Boolean GodModeCalcTKTS() throws RemoteException;

	Boolean setGeneration(String userId, ElectricityGeneration i) throws RemoteException;

	Boolean queryUserExists(String userId) throws RemoteException;

	String getRegisteredUUID(String userId) throws RemoteException;
}
