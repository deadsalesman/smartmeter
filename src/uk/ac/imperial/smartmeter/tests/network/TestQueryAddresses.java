package uk.ac.imperial.smartmeter.tests.network;

import java.net.InetSocketAddress;
import java.util.HashMap;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestQueryAddresses extends GenericTest {

	@Override
	public boolean doTest() {
		LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,9023,TicketTestHelper.user1,"");
		aClient.start();
		int port = 4000;
		aClient.client.registerUser(1., 2., 3., port);
		HashMap<String, InetSocketAddress> ret = aClient.client.getPeers();
		
		/*for (Entry<String, InetSocketAddress> e : ret.entrySet())
		{
			System.out.println(e.getKey());
			System.out.println(e.getValue());
		}*/
		InetSocketAddress exists = ret.get(aClient.client.getId());
		return ret.get(aClient.client.getId()).getPort()==port;
	}

}
