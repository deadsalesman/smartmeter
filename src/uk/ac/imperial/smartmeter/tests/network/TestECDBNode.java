package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LContNode;

public class TestECDBNode extends GenericTest {

	@Override
	public boolean doTest(){
		String[] parameters_lc = {DefaultTestClient.ipAddr, String.valueOf(DefaultTestClient.EDCPort), DefaultTestClient.ipAddr,String.valueOf(DefaultTestClient.HLCPort),UUID.randomUUID().toString(),""};
		try {
			LContNode.main(parameters_lc);
			return LContNode.addDevice(true, 1, UUID.randomUUID().toString());
		} catch (Exception e) {
		}
		
		return false;
	}

}
