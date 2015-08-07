package uk.ac.imperial.smartmeter.tests;

public abstract class GenericTest implements testIFace {

	protected boolean success = false;
	protected boolean print = true;
	protected String testId;
	protected GenericTest()
	{
      this (true);
	}
	protected GenericTest(Boolean b)
	{
		testId = this.getClass().getName();
		print = b;
		try {
		displayResults(doTest());
		} catch (Exception e)
		{
		System.out.println("EXCEPTION THROWN IN " + testId);
		}
	}
	public boolean getResult()
	{
		return success;
	}
	public abstract boolean doTest();
	public boolean displayResults(boolean ans)
	{
		if (print)
		{
		System.out.println((ans ? "PASS_ " : "FAIL_ ") + testId);
		}
		success = ans;
		return ans;
		
	}
}
