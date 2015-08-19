package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestGenerousFavour extends GenericTest {

	@Override
	public boolean doTest()  throws Exception{
		LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,9005,TicketTestHelper.user1,"");
		LCServer bClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,9006,TicketTestHelper.user2,"");
		
		aClient.start();
		bClient.start();
		
		String locationOfB = "localHost";
		int portOfB = 9006;
		String locationOfA = "localHost";
		int portOfA = 9005;
		aClient.client.registerUser(0.,0.,0.,aClient.getPort());
		bClient.client.registerUser(0.,3.,0.,bClient.getPort());
		
		TicketTestHelper.bindRequirement(aClient.client,1.1, 2.3, 4,3.);
		TicketTestHelper.bindRequirement(bClient.client,1.1, 2.3, 4,3.);
		
		aClient.client.GodModeCalcTKTS();
		
		final ArraySet<ElectricityTicket> a = aClient.client.getTickets();
		final ArraySet<ElectricityTicket> b = bClient.client.getTickets();
		UUID aID = UUID.fromString(a.get(0).id.toString());
		UUID bID = UUID.fromString(b.get(0).id.toString());
		
		aClient.registerClient(locationOfB, portOfB);
		bClient.registerClient(locationOfA, portOfA);
		
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
