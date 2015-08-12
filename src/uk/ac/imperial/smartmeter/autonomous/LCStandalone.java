package uk.ac.imperial.smartmeter.autonomous;

import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCAdmin;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class LCStandalone {
	LCServer server;
	LCAdmin admin;
	
	public LCStandalone(int port, String name,Double worth, Double generation, Double economic)
	{
		initialise(port, name,worth, generation, economic);
	}
	public void stop()
	{
		server.stop();
		admin.stop();
		server.client.unjamServer();
	}
	public void initialise(int port, String name, Double worth, Double generation, Double economic)
	{
		server = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,port,name,"");
		admin = new LCAdmin(server.client);
		server.client.registerUser(worth, generation, economic, port);
		new Thread(server).start();
		new Thread(admin).start();
	}
	public static void main(String[] args)
	{
		LCStandalone s = new LCStandalone(Integer.parseInt(args[0]),args[1],Double.parseDouble(args[2]),Double.parseDouble(args[3]),Double.parseDouble(args[4]));
	}
	public void wipe() {
		server.client.wipeAll();
	}
	
}
