package uk.ac.imperial.smartmeter.tests.network;

import java.util.ArrayList;
import java.util.UUID;

import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.LContNode;

public class TestECDBNodeWriteSetGet extends GenericTest {

	@Override
	public boolean doTest() {
		  ArrayList<String> test = new ArrayList<String>();
	        String t = UUID.randomUUID().toString();
	        String[] parameters_lc = {"localHost", "9002", "localHost", "9001"};
			try {
				LContNode.main(parameters_lc);
				LContNode.addDevice(true, 1, t);
				return (LContNode.getState(t)==true);
				
			} catch (Exception e) {
				return false;
			}
	}

}
