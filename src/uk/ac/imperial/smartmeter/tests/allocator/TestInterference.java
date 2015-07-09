package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.imperial.smartmeter.allocator.CalendarQueue;
import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestInterference extends GenericTest {

	@Override
	public boolean doTest() {
		CalendarQueue cal = new CalendarQueue(new EleGenConglomerate(), new Date());
		Date now = new Date();
		int offset = 2;
		int n = 4;
		ElectricityRequirement req = new ElectricityRequirement(
				new Date((long)(now.getTime()+(offset+0.5)*QuantumNode.quanta)),      //offset and a half of a quanta
				new Date((long)(now.getTime()+(offset+n-0.5)*QuantumNode.quanta)));   
		ArrayList<QuantumNode> a = cal.findIntersectingNodes(req);
		
		
		return (a.size()==n);
	}

}
