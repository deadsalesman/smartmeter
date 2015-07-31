package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class TestConcurrentClients extends GenericTest {

	@Override
	public boolean doTest() {
		String a = UUID.randomUUID().toString();
		LCClient b = new LCClient("localHost", 9002, "localHost", 9001,a);
		String c = UUID.randomUUID().toString();
		LCClient d = new LCClient("localHost", 9002, "localHost", 9001,c);
		
		try {
			b.addDevice(true, 1, a);
			d.addDevice(true, 1, c);
			b.setState(a, false);
			d.setState(c, false);
			return ((b.getState(a)==false) && (d.getState(c)==false));
			
		} catch (Exception e) {
			return false;
		}
	}

}
