package uk.ac.imperial.smartmeter.tests.network;

import java.io.IOException;
import java.util.UUID;

import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.LContNode;

public class TestPushGeneration extends GenericTest {

	@Override
	public boolean doTest() {
		String t = UUID.randomUUID().toString();
        String[] parameters_lc = {"localHost", "9002", "localHost", "9001",t};
		try {
			LContNode.main(parameters_lc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LContNode.registerUser("Password", "User");
		return LContNode.setGeneration(new ElectricityGeneration(10.));
		
	}

}
