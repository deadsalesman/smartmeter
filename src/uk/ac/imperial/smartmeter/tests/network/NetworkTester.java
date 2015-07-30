package uk.ac.imperial.smartmeter.tests.network;

import uk.ac.imperial.smartmeter.tests.GenericTester;

public class NetworkTester extends GenericTester{

	public Integer main(String[] args)
	{
		testLog.add(new TestECDBNode());
		testLog.add(new TestECDBNodeWriteSetGet());
		testLog.add(new TestECDBNodeReadWrite());
		testLog.add(new TestECDBNodeRemove());
		return reportLog();
	}
}
