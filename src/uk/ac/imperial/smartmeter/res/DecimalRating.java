package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;
/**
 * A class implementing a 0-10 scale inclusive.
 * @author bwindo
 *
 */
public class DecimalRating implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4558720313332129703L;
	
	private int value;
	/**
	 * Creates a new {@link DecimalRating} with the given value.
	 * @param i The value in question.
	 */
	public DecimalRating(int i)
	{
		setValue(i);
	}
	public int getValue()
	{
		return value;
	}
	/**
	 * Sets the value to a number within the clamped range of 0-10. If the value is set larger, it is clamped to 10. If smaller, it is clamped to 0.
	 * @param newValue The value to set.
	 */
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
