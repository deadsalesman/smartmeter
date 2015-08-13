package uk.ac.imperial.smartmeter.tests.network;

import uk.ac.imperial.smartmeter.tests.GenericTester;

public class NetworkTester extends GenericTester{

	@Override
	public Integer main()
	{

		testLog.add(new TestECDBNode());
		testLog.add(new TestECDBNodeWriteSetGet());
		testLog.add(new TestECDBNodeRemove());
		testLog.add(new TestECDBNodeToggle());
		testLog.add(new TestHLCAddReq());
		testLog.add(new TestPushGeneration());
		testLog.add(new TestHLCGetTkt());
		testLog.add(new TestConcurrentClients());
		testLog.add(new TestComplexTickets());
		testLog.add(new TestAppendedRequirement());
		testLog.add(new TestGenerousFavour());
		testLog.add(new TestFavourExchange());
		testLog.add(new TestExtendTicket());
		testLog.add(new TestPracticalExtension());
		testLog.add(new TestPracticalIntensify());
		testLog.add(new TestQueryAddresses());
		return reportLog();
	}
	
	public static void main(String[] args)
	{
		NetworkTester a = new NetworkTester();
		a.main();
	}
}
