package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestECDBNodeToggle extends GenericTest {

	@Override
	public boolean doTest() {
		  String t = UUID.randomUUID().toString();
		  LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort, 9003, TicketTestHelper.user1,"");
				aClient.client.addDevice(true, 1, t, 10);
				Boolean state = false;
				for (int i = 0; i < 7; i++)
				{
					state = !state;
					aClient.client.setState(t, state);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// don't sleep, I suppose. Be that way.
					}
				}
				Boolean ret = aClient.client.getState(t)==state;
				aClient.client.wipeAll();
				return ret;
				
	}

}
