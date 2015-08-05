package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.res.DateHelper;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class TestHLCAddReq extends GenericTest {

	@Override
	public boolean doTest() {
		// TODO Auto-generated method stub

		String t = UUID.randomUUID().toString();

			LCClient elsie = new LCClient("localHost", 9002, "localHost", 9001,t,"");
			ElectricityRequirement e = new ElectricityRequirement(
					DateHelper.os(0.),
					DateHelper.os(10.),
					new DecimalRating(2),
					1,
					1,
					elsie.getId(),
					UUID.randomUUID().toString()
					);
			
			elsie.registerUser(0.,0.,0.);
			return elsie.setRequirement(e);
			
	}

}
