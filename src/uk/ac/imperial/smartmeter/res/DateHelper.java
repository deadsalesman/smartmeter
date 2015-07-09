package uk.ac.imperial.smartmeter.res;

import java.util.Date;

import uk.ac.imperial.smartmeter.allocator.DayNode;
import uk.ac.imperial.smartmeter.allocator.QuantumNode;

public class DateHelper {
//hacky date service
	//java calendars are bad 
	//dates are deprecated
	//this adds extensions to date
	//I am very sorry
	
	public static Date os(Long offset)
	{
		Date now = new Date();
		return new Date(now.getTime()+offset);
	}
	public static Date os(Double offset)
	{
		Date now = new Date();
		return new Date(now.getTime()+(long)(offset*QuantumNode.quanta));
	}
	public static Date dPlus(Date d, Double offset)
	{
		return new Date(d.getTime()+(long)(offset*QuantumNode.quanta));
	}
	public static Date dPlus(Date d, Integer offset)
	{
		return new Date(d.getTime()+(long)(offset*QuantumNode.quanta));
	}
	public static Date incrementDay(Date d, int i)
	{
		return new Date(d.getTime()+i*DayNode.mSecInDay);
	}
}
