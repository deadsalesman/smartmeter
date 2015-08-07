package uk.ac.imperial.smartmeter.tests;

public abstract class GenericTest implements testIFace {

	protected boolean success = false;
	protected boolean print = true;
	protected GenericTest()
	{
		try {
		displayResults(doTest());
		} catch(Exception e)
		{
			
		}
	}
	protected GenericTest(Boolean b)
	{
		print = b;
		displayResults(doTest());
	}
	public boolean getResult()
	{
		return success;
	}
	public abstract boolean doTest();
	public boolean displayResults(boolean ans)
	{
		String testId = this.getClass().getName();
		if (print)
		{
		System.out.println((ans ? "PASS_ " : "FAIL_ ") + testId);
		}
		success = ans;
		return ans;
	}

}
