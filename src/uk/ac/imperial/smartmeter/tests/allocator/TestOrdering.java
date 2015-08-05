package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestOrdering extends GenericTest {

	@Override
	public boolean doTest() {
		UserAgent u = new UserAgent("","",TicketTestHelper.user1,1.,1.1,1.,1.);
		UserAgent j = new UserAgent("","",TicketTestHelper.user2,1.1,1.1,1.1,1.);
		UserAgent s = new UserAgent("","",TicketTestHelper.user3,1.,1.,1.,1.);
		
		TicketTestHelper.bindRequirement(u,1.1, 2.3, 4,3.);
		TicketTestHelper.bindRequirement(j,1.1, 2.3, 4,3.);
		TicketTestHelper.bindRequirement(s,1.1, 2.3, 4,3.);

		ArraySet<UserAgent> m = new ArraySet<UserAgent>();

		m.add(u);
		m.add(j);
		m.add(s);
		

		TicketAllocator alloc = new TicketAllocator(m, new Date(),false);
		
		ArraySet<UserAgent> x  = alloc.calculateTickets();
		return ((x.findFromID(u.getId()).countTkts()==0)&&(x.findFromID(j.getId()).countTkts()==1)&&(x.findFromID(s.getId()).countTkts()==0));
	}

}
