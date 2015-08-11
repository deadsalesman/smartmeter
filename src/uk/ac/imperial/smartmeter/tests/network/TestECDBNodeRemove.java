package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LContNode;

public class TestECDBNodeRemove extends GenericTest {

	@Override
	public boolean doTest() {
		 String t = UUID.randomUUID().toString();
	        String[] parameters_lc = {DefaultTestClient.ipAddr, String.valueOf(DefaultTestClient.EDCPort), DefaultTestClient.ipAddr,String.valueOf(DefaultTestClient.HLCPort),UUID.randomUUID().toString(),""};
			try {
				LContNode.main(parameters_lc);
				LContNode.addDevice(true, 1, t,3);
				Boolean temp1 = LContNode.getState(t);
				LContNode.removeDevice(t);
				Boolean temp2 = LContNode.getState(t);
				return ((temp1 != null) &&(temp2==null));
				
			} catch (Exception e) {
				return false;
			}
	}

}
