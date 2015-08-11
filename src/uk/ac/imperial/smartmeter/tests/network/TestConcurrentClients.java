package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class TestConcurrentClients extends GenericTest {

	@Override
	public boolean doTest() {
		String a = UUID.randomUUID().toString();
		LCClient b = new LCClient(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,a,"");
		String c = UUID.randomUUID().toString();
		LCClient d = new LCClient(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,c,"");
		
		try {
			b.addDevice(true, 1, a, 3);
			d.addDevice(true, 1, c, 5);
			b.setState(a, false);
			d.setState(c, false);
			return ((b.getState(a)==false) && (d.getState(c)==false));
			
		} catch (Exception e) {
			return false;
		}
	}

}
