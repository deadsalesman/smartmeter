package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;
import java.util.Map;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestTrickyAllocConstantGenerationProfile extends GenericTest {

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
		

		TicketAllocator alloc = new TicketAllocator(m, new Date(),true);
		
		Map<UserAgent, ArraySet<ElectricityTicket>> x  = alloc.calculateTickets();
		

		return (TicketTestHelper.countTickets(x)==10); //this is basically to preserve functionality. it is unlikely to be optimal, and potentially not correct.
	}

}
