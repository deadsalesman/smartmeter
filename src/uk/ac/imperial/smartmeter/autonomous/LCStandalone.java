package uk.ac.imperial.smartmeter.autonomous;

import java.rmi.RemoteException;

import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCAdmin;
import uk.ac.imperial.smartmeter.webcomms.LCServer;
/**
 * Standalone implementation of the LocalController
 * Has a LCAdmin for autonomous actions
 * Has a LCServer to respond and instigate to remote agents
 * @author Ben Windo
 * @see LCServer
 * @see LCAdmin
 */
public class LCStandalone {
	public LCServer server;
	public LCAdmin admin;
	Thread s;
	Thread a;
	/**
	 * Standard ctor that initialises the standalone and composite classes.
	 * @param port The port the server listens on.
	 * @param name The name of the server.
	 * @param worth The social worth of the server.
	 * @param generation The generated electricity amplitude of the server.
	 * @param economic The economic worth of the server. 
	 * @throws RemoteException
	 */
	public LCStandalone(int port, String name,Double worth, Double generation, Double economic) throws RemoteException
	{
		initialise(port, name,worth, generation, economic);
		server.setTicketDurationModifiable(true);
		server.setTicketAmplitudeModifiable(true);
	}
	/**
	 * Attempts to stop the standalone, waits for the constituent threads hosting admin and server to rejoin.
	 */
	public void stop()
	{
		server.stop();

		admin.stop();
		try {
			s.join();
			a.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Initialises the server and admin components.
	 * Also registers the server with the central HLC coordinator.
	 * 
	 * @param port The port the server listens on.
	 * @param name The name of the server.
	 * @param worth The social worth of the server.
	 * @param generation The generated electricity amplitude of the server.
	 * @param economic The economic worth of the server. 
	 * @throws RemoteException
	 * @throws InterruptedException 
	 */
	public void initialise(int port, String name, Double worth, Double generation, Double economic) throws RemoteException
	{
		
		server = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,port,name,"");
		admin = new LCAdmin(server.client,port,server.getPubKey());
		server.registerUser(worth, generation, economic, port);
		s = new Thread(server);
		a = new Thread(admin);
		s.start();
		a.start();
	}
	/**
	 * Creates a new LCStandalone.
	 * @param args Basic parameters for the server/admin.
	 */
	public static void main(String[] args)
	{
		try {
			@SuppressWarnings("unused")
			LCStandalone s = new LCStandalone(Integer.parseInt(args[0]),args[1],Double.parseDouble(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4]));
		} catch (NumberFormatException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Clears all data from the EDC, HLC, and Client.
	 */
	public void wipe() {
		server.client.wipeAll();
	}
	/**
	 * Prints tickets to system out. Debug only.
	 * @deprecated
	 */
	public void printTkts() {

		System.out.println(server.client.handler.getId() + " is printing: ");
		for (ElectricityTicket t : server.client.getTickets())
		{
			System.out.println(t.toString());
		}
	}
	
}
