package uk.ac.imperial.smartmeter.tests.network;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.LContNode;

public class TestHLCAddReq extends GenericTest {

	@Override
	public boolean doTest() {
		// TODO Auto-generated method stub

		String t = UUID.randomUUID().toString();
	        String[] parameters_lc = {"localHost", "9002", "localHost", "9001",t};
			try {
				LContNode.main(parameters_lc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ElectricityRequirement e = new ElectricityRequirement(
					new Date(),
					new Date(),
					new DecimalRating(2),
					1,
					1,
					LContNode.getUserId(),
					UUID.randomUUID().toString()
					);
			LContNode.registerUser("Password", "User");
			return LContNode.setRequirement(e);
			
	}

}
