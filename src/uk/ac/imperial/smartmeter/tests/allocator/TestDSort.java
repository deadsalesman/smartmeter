package uk.ac.imperial.smartmeter.tests.allocator;

import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.comparators.demandComparator;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.User;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestDSort extends GenericTest{

	
	TestDSort() {
		super();
	}
    @Override
	public boolean doTest() {
		ArraySet<UserAgent> l = new ArraySet<UserAgent>();

		l.add(new UserAgent(new User("Uma Thurman"),8.,7.,6.,5.));
		l.add(new UserAgent(new User("John Travolta"),1.,2.,3.,4.));
		l.add(new UserAgent(new User("Samuel Jackson"),9.,10.,11.,12.));
		
		ArraySet.sort(l, new demandComparator());
		
		return (l.get(2).getUser().getName()=="Samuel Jackson")
				&&(l.get(1).getUser().getName()=="Uma Thurman")
				&&(l.get(0).getUser().getName()=="John Travolta");
	}

}
