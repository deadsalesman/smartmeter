package uk.ac.imperial.smartmeter.tests.allocator;

import uk.ac.imperial.smartmeter.allocator.DayNode;
import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestDayNodeDiff extends GenericTest {

	@Override
	public boolean doTest() {
		 DayNode d = new DayNode(2,new EleGenConglomerate());
	     DayNode e = new DayNode(3,new EleGenConglomerate());
	     
	     
		return e.getStartTime().equals(d.getEndTime());
	}

}
