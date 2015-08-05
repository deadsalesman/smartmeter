package uk.ac.imperial.smartmeter.tests.allocator;

import uk.ac.imperial.smartmeter.comparators.productivityComparator;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestPSort extends GenericTest{

	
	TestPSort() {
		super();
	}
    @Override
	public boolean doTest() {
    	ArraySet<UserAgent> l = new ArraySet<UserAgent>();

		l.add(new UserAgent("","",TicketTestHelper.user1,8.,7.,6.,5.));
		l.add(new UserAgent("","",TicketTestHelper.user2,1.,2.,3.,4.));
		l.add(new UserAgent("","",TicketTestHelper.user3,9.,10.,11.,12.));
		
		ArraySet.sort(l, new productivityComparator());
		
		return (l.get(2).getName()==TicketTestHelper.user3)
				&&(l.get(1).getName()==TicketTestHelper.user1)
				&&(l.get(0).getName()==TicketTestHelper.user2);
	}

}
