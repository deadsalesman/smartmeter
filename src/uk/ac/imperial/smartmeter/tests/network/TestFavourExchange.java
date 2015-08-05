package uk.ac.imperial.smartmeter.tests.network;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestFavourExchange extends GenericTest {

	@Override
	public boolean doTest() {
		//a will get the ticket in the preferred slot, and then exchange it with b for a favour.
		
		LCServer aClient = new LCServer("localHost", 9002, "localHost", 9001,9004,TicketTestHelper.user1,"");
		LCServer bClient = new LCServer("localHost", 9002, "localHost", 9001,9003,TicketTestHelper.user2,"");
		String locationOfB = "localHost";
		String portOfB = "9003";
		aClient.client.registerUser(0.,10.,0.);
		bClient.client.registerUser(0.,10.,0.);
		
		TicketTestHelper.bindRequirement(aClient.client,1.1, 6.3, 4,3.);
		TicketTestHelper.bindRequirement(bClient.client,1.1, 6.3, 4,3.);
		
		aClient.client.GodModeCalcTKTS();
		
		ArraySet<ElectricityTicket> a = aClient.client.getTickets();
		ArraySet<ElectricityTicket> b = bClient.client.getTickets();
		
		aClient.client.queryReq(locationOfB,portOfB);
		
		ArraySet<ElectricityTicket> c = aClient.client.getTickets();
		ArraySet<ElectricityTicket> d = bClient.client.getTickets();
		
		return ((c.get(0).getId().equals(b.get(0).getId()))&&(d.get(0).getId().equals(a.get(0).getId())));
	}

}
