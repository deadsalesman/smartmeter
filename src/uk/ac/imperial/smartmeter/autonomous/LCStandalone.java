package uk.ac.imperial.smartmeter.autonomous;

import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCAdmin;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class LCStandalone {
	LCServer server;
	LCAdmin admin;
	
	public LCStandalone(int port, String name)
	{
		initialise(port, name);
	}
	public void stop()
	{
		server.stop();
		admin.stop();
	}
	public void initialise(int port, String name)
	{
		server = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,port,name,"");
		admin = new LCAdmin(server.client);

		new Thread(server).start();
		new Thread(admin).start();
	}
	public static void main(String[] args)
	{
		LCStandalone s = new LCStandalone(Integer.parseInt(args[0]),args[1]);
	}
	
}
