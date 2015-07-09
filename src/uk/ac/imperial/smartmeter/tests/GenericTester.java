package uk.ac.imperial.smartmeter.tests;

import java.util.ArrayList;

public abstract class GenericTester {
	protected ArrayList<GenericTest> testLog;

	public abstract Integer main(String[] args);
	private Boolean print = true;
	
	public GenericTester() {
		testLog = new ArrayList<GenericTest>();
	}
	public GenericTester(Boolean b) {
		testLog = new ArrayList<GenericTest>();
		print = b;
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
