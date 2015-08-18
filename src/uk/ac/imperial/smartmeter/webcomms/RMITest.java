package uk.ac.imperial.smartmeter.webcomms;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
public class RMITest {
	public static void main(String[] args)
	{
		try {
			LocateRegistry.createRegistry(1099);
		}
		catch (RemoteException e)
		{
			
		}
		try {
			LCServer srv = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,1099,TicketTestHelper.user1,"");
			srv.RMISetup();
			//while(true)
			{
				try{
					Thread.sleep(100);
				}
				catch(Exception e)
				{}
			}
			srv.client.getMsg("155.198.117.20");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
