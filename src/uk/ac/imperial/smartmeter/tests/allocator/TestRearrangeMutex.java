package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;
import java.util.Map;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestRearrangeMutex extends GenericTest {

	@Override
	public boolean doTest() {
		UserAgent u = new UserAgent(TicketTestHelper.uma,8.,1.,6.,5.);
		UserAgent j = new UserAgent(TicketTestHelper.john,1.,1.,3.,4.);
		UserAgent s = new UserAgent(TicketTestHelper.sam,8.,1.,9.,7.);
		
		TicketTestHelper.bindRequirement(u,1.1, 6.3, 4,3.);
		TicketTestHelper.bindRequirement(j,3.1,4.3, 7, 3);
		TicketTestHelper.bindRequirement(j,2.1, 6.3, 3, 3);
		TicketTestHelper.bindRequirement(s,3.1, 8.3, 4, 3);

		ArraySet<UserAgent> m = new ArraySet<UserAgent>();

		m.add(u);
		m.add(j);
		m.add(s);
		

		TicketAllocator alloc = new TicketAllocator(m, new Date(),true);
		
		Map<UserAgent, ArraySet<ElectricityTicket>> x  = alloc.calculateTickets();
		TicketTestHelper.printTickets(x,new Date());
		return (TicketTestHelper.countTickets(x)==4);
	}

}
