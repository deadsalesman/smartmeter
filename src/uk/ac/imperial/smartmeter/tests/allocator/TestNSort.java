package uk.ac.imperial.smartmeter.tests.allocator;

import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.comparators.needsComparator;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.User;
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

		m.add(new UserAgent(new User("Uma Thurman"),8.,7.,j,6.,5.));
		m.add(new UserAgent(new User("John Travolta"),1.,2.,k,3.,4.));
		m.add(new UserAgent(new User("Samuel Jackson"),9.,10.,l,11.,12.));
		
		ArraySet.sort(m, new needsComparator());
		
		return (m.get(0).getUser().getName()=="Samuel Jackson")
				&&(m.get(2).getUser().getName()=="Uma Thurman")
				&&(m.get(1).getUser().getName()=="John Travolta");
    }
}
