package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;
import java.util.Map;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestSufficientGeneration extends GenericTest {

	@Override
	public boolean doTest() {
		UserAgent u = new UserAgent(TicketTestHelper.user1,8.,3.,6.,5.);
		
		TicketTestHelper.bindRequirement(u,1.1, 2.3, 4,3.);

		ArraySet<UserAgent> m = new ArraySet<UserAgent>();

		m.add(u);
		

		TicketAllocator alloc = new TicketAllocator(m, new Date(),false);
		
		Map<UserAgent, ArraySet<ElectricityTicket>> x  = alloc.calculateTickets();
		
		return (TicketTestHelper.countTickets(x)==1);
	}

}