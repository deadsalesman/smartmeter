package uk.ac.imperial.smartmeter.tests.network;

import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestExtendTicket extends GenericTest {

	@Override
	public boolean doTest() {
		
		LCServer aClient = new LCServer("localHost", 9002, "localHost", 9001,9009,TicketTestHelper.user1,"");
		
		aClient.start();
		
		String locationOfB = "localHost";
		aClient.client.registerUser(0.,10.,0.);
		
		TicketTestHelper.bindRequirement(aClient.client,6.1, 9.3, 4,3.);
		
		aClient.client.GodModeCalcTKTS();
		
		final ArraySet<ElectricityTicket> a = aClient.client.getTickets();
		

		ElectricityRequirement req = aClient.client.handler.getReqs().get(0);
		req.setStartTime(req.getStartTime(), req.getDuration()*2);
		ElectricityTicket dummy = new ElectricityTicket(new Date(), new Date(), 0., UUID.randomUUID().toString(), UUID.randomUUID().toString());
		aClient.client.extendTicket(a.get(0),req,dummy);
		
		aClient.client.wipe();
		aClient.close();
		return (Math.abs(a.get(0).getDuration()-req.getDuration())<1);
	}

}
