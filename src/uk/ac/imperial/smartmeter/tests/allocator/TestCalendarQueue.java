package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;

import uk.ac.imperial.smartmeter.allocator.CalendarQueue;
import uk.ac.imperial.smartmeter.allocator.DayNode;
import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestCalendarQueue extends GenericTest {

	@Override
	public boolean doTest() {
		EleGenConglomerate e = new EleGenConglomerate();
		Date d = new Date();
		CalendarQueue c = new CalendarQueue(e,d);
		DayNode n = new DayNode(e,d);
		DayNode temp = c.push(n);
		for (int i = 0; i < c.getCalendarSize(); i++)
		{
			temp = c.increment();
		}
		return temp.equals(n)&&(!c.findDayNode(n));
	}

}
