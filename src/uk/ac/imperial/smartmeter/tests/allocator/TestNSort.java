package uk.ac.imperial.smartmeter.tests.allocator;

import uk.ac.imperial.smartmeter.comparators.needsComparator;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestNSort extends GenericTest{

	
	TestNSort() {
		super();
	}
    @Override
	public boolean doTest() {
		ElectricityRequirement j = new ElectricityRequirement(1.);
		ElectricityRequirement k = new ElectricityRequirement(2.);
		ElectricityRequirement l = new ElectricityRequirement(3.);
		
		ArraySet<UserAgent> m = new ArraySet<UserAgent>();

		m.add(new UserAgent("","","",TicketTestHelper.user1,8.,7.,6.,5.,j));
		m.add(new UserAgent("","","",TicketTestHelper.user2,1.,2.,3.,4.,k));
		m.add(new UserAgent("","","",TicketTestHelper.user3,9.,10.,11.,12.,l));
		
		ArraySet.sort(m, new needsComparator());
		
		return (m.get(2).getName()==TicketTestHelper.user3)
				&&(m.get(1).getName()==TicketTestHelper.user2)
				&&(m.get(0).getName()==TicketTestHelper.user1);
    }
}
