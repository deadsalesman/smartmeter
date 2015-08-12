package uk.ac.imperial.smartmeter.tests.autonomous;

import uk.ac.imperial.smartmeter.tests.GenericTester;

public class AutonTester extends GenericTester {

	@Override
	public Integer main(String[] args) {
		//testLog.add(new TestObservableFlow());
		return reportLog();
	}

}
