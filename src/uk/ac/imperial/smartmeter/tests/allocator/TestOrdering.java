package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;
import java.util.Map;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestOrdering extends GenericTest {

	@Override
	public boolean doTest() {
		UserAgent u = new UserAgent(TicketTestHelper.user1,1.,1.1,1.,1.);
		UserAgent j = new UserAgent(TicketTestHelper.user2,1.1,1.1,1.1,1.);
		UserAgent s = new UserAgent(TicketTestHelper.user3,1.,1.,1.,1.);
		
		TicketTestHelper.bindRequirement(u,1.1, 2.3, 4,3.);
		TicketTestHelper.bindRequirement(j,1.1, 2.3, 4,3.);
		TicketTestHelper.bindRequirement(s,1.1, 2.3, 4,3.);

		ArraySet<UserAgent> m = new ArraySet<UserAgent>();

		m.add(u);
		m.add(j);
		m.add(s);
		

		TicketAllocator alloc = new TicketAllocator(m, new Date(),false);
		
		Map<UserAgent, ArraySet<ElectricityTicket>> x  = alloc.calculateTickets();
		return ((x.get(u).getSize()==0)&&(x.get(j).getSize()==1)&&(x.get(s).getSize()==0));
	}

}
