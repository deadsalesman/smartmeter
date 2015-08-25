package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestFavourExchange extends GenericTest {

	@Override
	public boolean doTest()  throws Exception{
		//a will get the ticket in the preferred slot, and then exchange it with b for a favour.
		
		LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,9004,TicketTestHelper.user1,"");
		LCServer bClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,9003,TicketTestHelper.user2,"");
		
		aClient.start();
		bClient.start();
		
		String locationOfB = "localHost";
		int portOfB = 9003;
		aClient.registerUser(0.,10.,0.,aClient.getPort());
		bClient.registerUser(0.,10.,0.,bClient.getPort());
		
		TicketTestHelper.bindRequirement(aClient.client,1.1, 6.3, 4,3.);
		TicketTestHelper.bindRequirement(bClient.client,1.1, 6.3, 4,3.);
		
		aClient.client.GodModeCalcTKTS();
		
		final ArraySet<ElectricityTicket> a = aClient.client.getTickets();
		final ArraySet<ElectricityTicket> b = bClient.client.getTickets();
		UUID aID = UUID.fromString(a.get(0).id.toString());
		UUID bID = UUID.fromString(b.get(0).id.toString());
		

		aClient.registerClient(locationOfB, portOfB);
		ElectricityRequirement req = aClient.client.handler.getReqs().get(0);
		ArraySet<ElectricityTicket> competing = aClient.client.queryCompeting(locationOfB,portOfB, req);
		aClient.client.offer(locationOfB, portOfB, competing.get(0),aClient.client.handler.findMatchingTicket(req));
		
		ArraySet<ElectricityTicket> c = aClient.client.getTickets();
		ArraySet<ElectricityTicket> d = bClient.client.getTickets();
		aClient.client.wipeAll();
		aClient.close();
		bClient.close();
		return ((c.get(0).getId().equals(bID.toString()))&&(d.get(0).getId().equals(aID.toString())));
	}

}
