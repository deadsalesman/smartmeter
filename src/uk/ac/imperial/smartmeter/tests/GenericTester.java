package uk.ac.imperial.smartmeter.tests;

import java.util.ArrayList;

import uk.ac.imperial.smartmeter.res.DateHelper;

public abstract class GenericTester {
	protected ArrayList<GenericTest> testLog;

	public abstract Integer main();
	private Boolean print = true;
	
	public GenericTester() {
		this(true);
	}
	public GenericTester(Boolean b) {
		testLog = new ArrayList<GenericTest>();
		print = b;
		DateHelper.initialise();
	}

	protected Integer reportLog() {
		int count = 0;
		for (GenericTest g : testLog) {
			count += g.success ? 0 : 1;
		}
		if (print) {
			if (count == 0) {
				System.out.println("All tests passed in "
						+ this.getClass().getName());
			} else {
				System.out.println(count + " tests failed in "
						+ this.getClass().getName());
			}
		}
		return count;
	}
}
