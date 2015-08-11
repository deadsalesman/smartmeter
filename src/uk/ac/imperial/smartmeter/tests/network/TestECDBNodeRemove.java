package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestECDBNodeRemove extends GenericTest {

	@Override
	public boolean doTest() {
		 String t = UUID.randomUUID().toString();
		 LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,
					DefaultTestClient.HLCPort, 9003, TicketTestHelper.user1, "");
				aClient.client.addDevice(true, 1, t,3);
				Boolean temp1 = aClient.client.getState(t);
				aClient.client.removeDevice(t);
				Boolean temp2 = aClient.client.getState(t);
				Boolean temp3 = aClient.client.addDevice(false, 1, t,3);
				aClient.client.wipeAll();
				return (temp1 &&(temp2==null) && temp3);
	}

}
