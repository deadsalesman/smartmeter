package uk.ac.imperial.smartmeter.webcomms;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;

/**
 * Basic test of the RMI protocol.
 * @deprecated
 * @author bwindo
 *
 */
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

			LCServer srv = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,1098,TicketTestHelper.user1,"");
			@SuppressWarnings("unused")
			EDCServer client = new EDCServer(9002);
			@SuppressWarnings("unused")
			HLCServer hl = new HLCServer(9001);
			
//			boolean redt = srv.client.addDevice(true, 1, UUID.randomUUID().toString(), 5);
			//ElectronicDevice ed = new ElectronicDevice(true, 1, UUID.randomUUID().toString());
			 //Boolean ret = srv.client.addDevice(ed,11);
			 //srv.client.wipeAll();
			System.out.println(srv.client.getMessage("155.198.117.240",1098));
			while(true)
			{
				try{
					Thread.sleep(100);
				}
				catch(Exception e)
				{}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
