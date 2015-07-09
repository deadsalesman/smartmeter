package uk.ac.imperial.smartmeter.tests.allocator;

import uk.ac.imperial.smartmeter.allocator.DayNode;
import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestNDayNodes extends GenericTest{

	
	TestNDayNodes() {
		super();
	}
    @Override
	public boolean doTest() {
     DayNode d = new DayNode(2,new EleGenConglomerate());
     
     return (d.getSize() == 24*60*60*1000/QuantumNode.quanta);
     
	}

}
