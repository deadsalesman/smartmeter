package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestUserPriority extends GenericTest {

	@Override
	public boolean doTest() {
		UserAgent u = new UserAgent("","",TicketTestHelper.user1,8.,10.,6.,5.);
		
		ElectricityRequirement e = TicketTestHelper.bindRequirement(u,1.1, 2.3, 9,5.);
		TicketTestHelper.bindRequirement(u,1.1, 2.3, 7, 6);
		TicketTestHelper.bindRequirement(u,1.1, 2.3, 3, 7);
		TicketTestHelper.bindRequirement(u,1.1, 2.3, 4, 8);

		ArraySet<UserAgent> m = new ArraySet<UserAgent>();

		m.add(u);
		

		TicketAllocator alloc = new TicketAllocator(m, new Date(),false);
		
		ArraySet<UserAgent> x  = alloc.calculateTickets();
		try{
			return (u.getReqTktMap().get(e)!=null);
		}
		catch(NullPointerException ex)
		{
			return false;
		}
	}

}
