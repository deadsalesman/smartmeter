package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;

import uk.ac.imperial.smartmeter.allocator.DayNode;
import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestDayNodeDiff extends GenericTest {
	@Override
	public boolean doTest() {
		 Date start = new Date();
		 DayNode d = new DayNode(start, 2,new EleGenConglomerate());
	     DayNode e = new DayNode(start, 3,new EleGenConglomerate());
	     
	     
		return e.getStartTime().equals(d.getEndTime());
	}

}
