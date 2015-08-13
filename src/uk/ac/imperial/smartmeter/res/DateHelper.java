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
	public static Date init;
	public static Date clone(Date d)
	{
		return new Date(d.getTime());
	}
	public static Date os(Long offset)
	{
		initialise();
		return new Date(init.getTime()+offset);
	}
	public static Date os(Double offset)
	{
		initialise();
		return new Date(init.getTime()+(long)(offset*QuantumNode.quanta));
	}
	public static Date dPlus(Date d, Double offset)
	{
		long off = (long)(offset*QuantumNode.quanta);
		long time =d.getTime() + off;
		Date n = new Date(time);
		return n;
	}
	public static Date dPlus(Date d, Integer offset)
	{
		return new Date(d.getTime()+(long)(offset*QuantumNode.quanta));
	}
	public static Date incrementDay(Date d, int i)
	{
		return new Date(d.getTime()+i*DayNode.mSecInDay);
	}
	public static Date incrementDay(Date d, Integer i) {
		return new Date(d.getTime()+i*DayNode.mSecInDay);
	}
	public static Date incrementDay(Integer i) {
		return DateHelper.incrementDay(new Date(), i);
	}
	public static Date incrementHour(Date d, int i)
	{
		return new Date(d.getTime()+i*DayNode.mSecInDay/24);
	}
	public static Date incrementHour(Date d, Integer i) {
		return new Date(d.getTime()+i*DayNode.mSecInDay/24);
	}
	public static Date incrementHour(Integer i) {
		return DateHelper.incrementHour(new Date(), i);
	}
	public static void initialise() {
		init = new Date();
	}
}
