package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;

import uk.ac.imperial.smartmeter.allocator.CalendarQueue;
import uk.ac.imperial.smartmeter.allocator.DayNode;
import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestCalendarSize extends GenericTest {

	@Override
	public boolean doTest() {
		
		CalendarQueue c = new CalendarQueue(new EleGenConglomerate(), new Date());
		
		return((c.getCalendarSize()*c.getDayNodeSize())==(CalendarQueue.daysInCalendar*DayNode.mSecInDay/QuantumNode.quanta));
	}

}
