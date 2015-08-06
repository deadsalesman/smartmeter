package uk.ac.imperial.smartmeter.tests.network;

import uk.ac.imperial.smartmeter.tests.GenericTester;

public class NetworkTester extends GenericTester{

	public Integer main(String[] args)
	{

		testLog.add(new TestECDBNode());
		testLog.add(new TestECDBNodeWriteSetGet());
		testLog.add(new TestECDBNodeRemove());
		testLog.add(new TestHLCAddReq());
		testLog.add(new TestPushGeneration());
		testLog.add(new TestHLCGetTkt());
		testLog.add(new TestConcurrentClients());
		testLog.add(new TestComplexTickets());
		testLog.add(new TestAppendedRequirement());
		testLog.add(new TestGenerousFavour());
		testLog.add(new TestFavourExchange());
		return reportLog();
	}
}
