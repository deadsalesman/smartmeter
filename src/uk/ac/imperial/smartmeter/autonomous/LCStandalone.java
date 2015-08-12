package uk.ac.imperial.smartmeter.autonomous;

import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCAdmin;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class LCStandalone {
	LCServer server;
	LCAdmin admin;
	Thread s;
	Thread a;
	
	public LCStandalone(int port, String name,Double worth, Double generation, Double economic)
	{
		initialise(port, name,worth, generation, economic);
	}
	public void stop()
	{
		server.stop();

		admin.stop();
		server.client.unjamServer();
		try {
			s.join();
			a.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void initialise(int port, String name, Double worth, Double generation, Double economic)
	{
		server = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,port,name,"");
		admin = new LCAdmin(server.client);
		server.client.registerUser(worth, generation, economic, port);
		s = new Thread(server);
		a = new Thread(admin);
		s.start();
		a.start();
	}
	public static void main(String[] args)
	{
		LCStandalone s = new LCStandalone(Integer.parseInt(args[0]),args[1],Double.parseDouble(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4]));
	}
	public void wipe() {
		server.client.wipeAll();
	}
	
}
