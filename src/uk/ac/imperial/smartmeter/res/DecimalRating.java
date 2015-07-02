package uk.ac.imperial.smartmeter.res;

public class DecimalRating {
	private int value;
	
	DecimalRating(int i)
	{
		setValue(i);
	}
	public int getValue()
	{
		return value;
	}
	public void setValue(int newValue)
	{
		if (newValue <= 10)
		{
			if (newValue < 0)
			{
				value = 0;
			}
			value = newValue;
		}
		else {
			value = 10;
		}
	}
}
