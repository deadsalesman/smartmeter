package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.res.DateHelper;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestHLCAddReq extends GenericTest {

	@Override
	public boolean doTest() throws Exception{

			LCServer elsie = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort, 9003, TicketTestHelper.user1,"");
			ElectricityRequirement e = new ElectricityRequirement(
					DateHelper.os(0.),
					DateHelper.os(10.),
					new DecimalRating(2),
					1,
					1,
					elsie.client.getId(),
					UUID.randomUUID().toString()
					);
			
			elsie.registerUser(0.,0.,0.,8000);
			Boolean ret = elsie.client.setRequirement(e);
			
			elsie.client.wipeAll();
			return ret;
			
	}

}
