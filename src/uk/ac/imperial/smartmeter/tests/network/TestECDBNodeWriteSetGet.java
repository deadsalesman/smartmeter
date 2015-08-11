package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestECDBNodeWriteSetGet extends GenericTest {

	@Override
	public boolean doTest() {
		String t = UUID.randomUUID().toString();
		LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,
				DefaultTestClient.HLCPort, 9003, TicketTestHelper.user1, "");
		aClient.client.addDevice(true, 1, t, 3);
		aClient.client.setState(t, false);
		return (aClient.client.getState(t) == false);
	}

}
