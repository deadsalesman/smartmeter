package uk.ac.imperial.smartmeter.tests.allocator;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestQuanta extends GenericTest {

	@Override
	public boolean doTest() {
		return (3600000%QuantumNode.quanta==0);
	}

}
