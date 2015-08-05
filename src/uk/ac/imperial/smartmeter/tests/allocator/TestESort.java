package uk.ac.imperial.smartmeter.tests.allocator;

import uk.ac.imperial.smartmeter.comparators.equalityComparator;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestESort extends GenericTest{

	
	TestESort() {
		super();
	}
    @Override
	public boolean doTest() {

		ArraySet<UserAgent> l = new ArraySet<UserAgent>();

		l.add(new UserAgent("","","Uma Thurman",8.,7.,6.,5.));
		l.add(new UserAgent("","","John Travolta",1.,2.,3.,4.));
		l.add(new UserAgent("","","Samuel Jackson",9.,10.,11.,12.));
		
		ArraySet.sort(l, new equalityComparator());
		
		return (l.get(0).getName()=="Samuel Jackson")
				&&(l.get(1).getName()=="Uma Thurman")
				&&(l.get(2).getName()=="John Travolta");
	}

}
