package uk.ac.imperial.smartmeter.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.TicketTuple;

public interface LCServerIFace extends Remote {
	String getMessage(String name, int port) throws RemoteException;

	Boolean registerClient(String location, int port, int ownPort, String userId, String ipAddr) throws RemoteException;

	TicketTuple offer(String location, int port, ElectricityTicket tktOld, ElectricityTicket tktNew) throws RemoteException; 
	TicketTuple offer(String location, int port, TicketTuple tuple) throws RemoteException; 

	ArraySet<ElectricityTicket> queryCompeting(String location, int port, ElectricityRequirement req) throws RemoteException;
}
