package uk.ac.imperial.smartmeter.tests.allocator;

import uk.ac.imperial.smartmeter.tests.GenericTester;

public class AllocatorTester extends GenericTester{


	@Override
	public Integer main()
	{
		testLog.add(new TestDSort());
		testLog.add(new TestESort());
		testLog.add(new TestNSort());
		testLog.add(new TestPSort());
		testLog.add(new TestSSort());
		testLog.add(new TestWeightingConsistency());
		testLog.add(new TestNDayNodes());
		testLog.add(new TestDayNodeDiff());
		testLog.add(new TestQuanta());
		testLog.add(new TestCalendarSize());
		testLog.add(new TestCalendarQueue());
		testLog.add(new TestInterference());
		testLog.add(new TestSufficientGeneration());
		testLog.add(new TestNotExclusive());
		testLog.add(new TestMutuallyExclusive());
		testLog.add(new TestInsufficientGeneration());
		testLog.add(new TestOrdering());
		testLog.add(new TestUserPriority());
		testLog.add(new TestRearrangeMutex());
		testLog.add(new TestTrickyAllocConstantGenerationProfile());
		
		return reportLog();
	}

	public static void main(String[]args){
	AllocatorTester a = new AllocatorTester();
	a.main();
	}
}
