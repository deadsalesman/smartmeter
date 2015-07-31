package uk.ac.imperial.smartmeter.tests.network;

import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class TestComplexTickets extends GenericTest {

	@Override
	public boolean doTest() {
		UserAgent a = new UserAgent(TicketTestHelper.user1,2.,2.,6.,5.);
		UserAgent b = new UserAgent(TicketTestHelper.user2,1.,1.,3.,4.);
		UserAgent c = new UserAgent(TicketTestHelper.user3,13.,1.,9.,3.);
		UserAgent d = new UserAgent(TicketTestHelper.user4,4.,4.,2.,5.);
		UserAgent e = new UserAgent(TicketTestHelper.user5,1.,1.,5.,4.);
		UserAgent f = new UserAgent(TicketTestHelper.user6,5.,1.,4.,4.);	
		
		TicketTestHelper.bindRequirement(a,1.1, 6.3, 4,3.);
		TicketTestHelper.bindRequirement(a,1.1, 6.3, 4,3.);
		TicketTestHelper.bindRequirement(a,1.1, 6.3, 4,3.);
		
		TicketTestHelper.bindRequirement(b,3.1,4.3, 7, 2.);
		TicketTestHelper.bindRequirement(b,2.1, 6.3, 3, 1.);
		TicketTestHelper.bindRequirement(b,3.1,4.3, 7, 3);
		TicketTestHelper.bindRequirement(b,2.1, 6.3, 3, 3);
		
		TicketTestHelper.bindRequirement(c,3.1, 8.3, 4, 8.);
		TicketTestHelper.bindRequirement(c,3.1, 8.3, 4, 3);
		
		TicketTestHelper.bindRequirement(d,2.1, 6.3, 3, 3);
		TicketTestHelper.bindRequirement(d,3.1,4.3, 7, 3);
		
		TicketTestHelper.bindRequirement(e,3.1, 8.3, 4, 3);
		
		TicketTestHelper.bindRequirement(f,1.1, 6.3, 4,3.);
		TicketTestHelper.bindRequirement(f,3.1,4.3, 7, 3);
		TicketTestHelper.bindRequirement(f,2.1, 6.3, 3, 3);
		TicketTestHelper.bindRequirement(f,3.1, 8.3, 4, 3);

		ArraySet<UserAgent> m = new ArraySet<UserAgent>();

		m.add(a);
		m.add(b);
		m.add(c);
		m.add(d);
		m.add(e);
		m.add(f);
		
		LCClient aClient = new LCClient("localHost", 9002, "localHost", 9001,a.getId());
		LCClient bClient = new LCClient("localHost", 9002, "localHost", 9001,b.getId());
		LCClient cClient = new LCClient("localHost", 9002, "localHost", 9001,c.getId());
		LCClient dClient = new LCClient("localHost", 9002, "localHost", 9001,d.getId());
		LCClient eClient = new LCClient("localHost", 9002, "localHost", 9001,e.getId());
		LCClient fClient = new LCClient("localHost", 9002, "localHost", 9001,f.getId());
		
		aClient.registerUser("a", a.getUser().getName());
		//aClient.pushData(a); //TODO
		for (ElectricityRequirement req : a.getReqs())
		{
			aClient.setRequirement(req);
		}
		
		ArraySet<ElectricityTicket> tktsA = aClient.getTickets();
		return false;
	}

}
