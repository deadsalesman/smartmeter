package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;
import java.util.Map;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestUserPriority extends GenericTest {

	@Override
	public boolean doTest() {
		UserAgent u = new UserAgent(TicketTestHelper.uma,8.,10.,6.,5.);
		
		ElectricityRequirement e = TicketTestHelper.bindRequirement(u,1.1, 2.3, 9,5.);
		TicketTestHelper.bindRequirement(u,1.1, 2.3, 7, 6);
		TicketTestHelper.bindRequirement(u,1.1, 2.3, 3, 7);
		TicketTestHelper.bindRequirement(u,1.1, 2.3, 4, 8);

		ArraySet<UserAgent> m = new ArraySet<UserAgent>();

		m.add(u);
		

		TicketAllocator alloc = new TicketAllocator(m, new Date(),false);
		
		Map<UserAgent, ArraySet<ElectricityTicket>> x  = alloc.calculateTickets();
		
		return (x.get(u).get(0).magnitude==e.getMaxConsumption());
	}

}
