package uk.ac.imperial.smartmeter.tests.database;

import uk.ac.imperial.smartmeter.tests.GenericTester;

public class DBTester extends GenericTester{
	
	@Override
	public Integer main()
	{
		testLog.add(new TestECDB());
		testLog.add(new TestRQDB());
		testLog.add(new TestAGTDB());
		return reportLog();
	}
	
	public static void main(String[]args){
	DBTester a = new DBTester();
	a.main();
	}
}

