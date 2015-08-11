package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestECDBNode extends GenericTest {

	@Override
	public boolean doTest(){
		 LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort, 9003, TicketTestHelper.user1,"");
		 Boolean ret = aClient.client.addDevice(true, 1, UUID.randomUUID().toString(),11);
		 aClient.client.wipeEDC();
		 return ret;
	}

}
