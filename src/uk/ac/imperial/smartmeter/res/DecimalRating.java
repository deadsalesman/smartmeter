package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;

public class DecimalRating implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4558720313332129703L;
	
	private int value;
	
	public DecimalRating(int i)
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
