package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestPushGeneration extends GenericTest {

	@Override
	public boolean doTest() {
		String t = UUID.randomUUID().toString();
		LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,8999,t,"");
		aClient.client.registerUser(0.,0.,0.,8000);
		Boolean ret = aClient.client.setGeneration(new ElectricityGeneration(10.));
		aClient.client.wipeAll();
		return ret;
		
	}

}
