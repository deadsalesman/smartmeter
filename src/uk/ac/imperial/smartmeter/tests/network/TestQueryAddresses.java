package uk.ac.imperial.smartmeter.tests.network;

import java.net.InetSocketAddress;
import java.util.HashMap;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestQueryAddresses extends GenericTest {

	@Override
	public boolean doTest()  throws Exception{
		LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,9023,TicketTestHelper.user1,"");
		aClient.start();
		int port = 4000;
		aClient.registerUser(1., 2., 3., port);
		HashMap<String, uk.ac.imperial.smartmeter.res.Triple<String,InetSocketAddress, Double>> ret = aClient.client.getAddresses();
		
		Boolean e =  ret.get(aClient.client.getId()).central.getPort()==port;
		aClient.client.wipeAll();
		return e;
	}

}
