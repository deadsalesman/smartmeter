package uk.ac.imperial.smartmeter.autonomous;

import java.rmi.RemoteException;

import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCAdmin;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class LCStandalone {
	public LCServer server;
	public LCAdmin admin;
	Thread s;
	Thread a;
	
	public LCStandalone(int port, String name,Double worth, Double generation, Double economic) throws RemoteException
	{
		initialise(port, name,worth, generation, economic);
		server.setTicketDurationModifiable(true);
		server.setTicketAmplitudeModifiable(true);
	}
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
	public static void main(String[] args)
	{
		try {
			LCStandalone s = new LCStandalone(Integer.parseInt(args[0]),args[1],Double.parseDouble(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4]));
		} catch (NumberFormatException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void wipe() {
		server.client.wipeAll();
	}
	public void printTkts() {

		System.out.println(server.client.handler.getId() + " is printing: ");
		for (ElectricityTicket t : server.client.getTickets())
		{
			System.out.println(t.toString());
		}
	}
	
}
