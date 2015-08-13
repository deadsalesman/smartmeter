package uk.ac.imperial.smartmeter.tests.autonomous;

import uk.ac.imperial.smartmeter.tests.GenericTester;

public class AutonTester extends GenericTester {

	@Override
	public Integer main() {
		testLog.add(new TestObservableFlow());
		return reportLog();
	}
	
	public static void main(String[]args){
	AutonTester a = new AutonTester();
	a.main();
	}

}
