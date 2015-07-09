package uk.ac.imperial.smartmeter.tests.database;

import uk.ac.imperial.smartmeter.tests.GenericTester;

public class DBTester extends GenericTester{
	
	public Integer main(String[] args)
	{
		testLog.add(new TestECDB());
		testLog.add(new TestRQDB());
		testLog.add(new TestUSRDB());
		testLog.add(new TestAGTDB());
		return reportLog();
	}
}

