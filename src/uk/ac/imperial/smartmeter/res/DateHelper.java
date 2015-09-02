package uk.ac.imperial.smartmeter.res;

import java.util.Date;

import uk.ac.imperial.smartmeter.allocator.DayNode;
import uk.ac.imperial.smartmeter.allocator.QuantumNode;
/** 
 * Utility class to make up for some deficits in the existing implementation of Java Date. 
 * Calendars were not used as they have too much overhead and complexity.
 * @author bwindo
 * @see Date
 */
public class DateHelper {
	public static Date init;
	/**
	 * Creates a new {@link Date} based on a previously existing one.
	 * @param d The Date to copy from.
	 * @return The new {@link Date}
	 */
	public static Date clone(Date d)
	{
		return new Date(d.getTime());
	}
	/**
	 * Creates a {@link Date} offset from the current time by a given value.
	 * @param offset The value of the offset in milliseconds.
	 * @return The new {@link Date}
	 */
	public static Date os(Long offset)
	{
		initialise();
		return new Date(init.getTime()+offset);
	}
	/**
	 * Creates a {@link Date} offset from the current time by a given value.
	 * @param offset The value of the offset in {@link QuantumNode#quanta} units.
	 * @return The new {@link Date}
	 */
	public static Date os(Double offset)
	{
		initialise();
		return new Date(init.getTime()+(long)(offset*QuantumNode.quanta));
	}
	/**
	 * Creates a {@link Date} offset from a given date by a given value.
	 * @param d The Date object to have the offset added to.
	 * @param offset The offset from the given date in milliseconds.
	 * @return The new Date.
	 */
	public static Date dPlus(Date d, Double offset)
	{
		long off = (long)(offset*QuantumNode.quanta);
		long time =d.getTime() + off;
		Date n = new Date(time);
		return n;
	}
	/**
	 * Creates a {@link Date} offset from a given date by a given value.
	 * @param d The Date object to have the offset added to.
	 * @param offset The offset from the given date in {@link QuantumNode#quanta} units.
	 * @return The new Date.
	 */
	public static Date dPlus(Date d, Integer offset)
	{
		return new Date(d.getTime()+(long)(offset*QuantumNode.quanta));
	}
	/**
	 * Creates a {link Date} a certain number of 24 hour periods after the given date.
	 * @param d The date to be offset.
	 * @param i The number of 24 hour periods to offset.
	 * @return The new Date.
	 */
	public static Date incrementDay(Date d, int i)
	{
		return new Date(d.getTime()+i*DayNode.mSecInDay);
	}
	/**
	 * Creates a {link Date} a certain number of 24 hour periods after the given date.
	 * @param d The date to be offset.
	 * @param i The number of 24 hour periods to offset.
	 * @return The new Date.
	 */
	public static Date incrementDay(Date d, Integer i) {
		return new Date(d.getTime()+i*DayNode.mSecInDay);
	}
	/**
	 * Creates a {link Date} a certain number of 24 hour periods after the current date.
	 * @param i The number of 24 hour periods to offset.
	 * @return The new Date.
	 */
	public static Date incrementDay(Integer i) {
		return DateHelper.incrementDay(new Date(), i);
	}
	/**
	 * Creates a {link Date} a certain number of 1 hour periods after the given date.
	 * @param d The date to be offset.
	 * @param i The number of 1 hour periods to offset.
	 * @return The new Date.
	 */
	public static Date incrementHour(Date d, int i)
	{
		return new Date(d.getTime()+i*DayNode.mSecInDay/24);
	}
	/**
	 * Creates a {link Date} a certain number of 1 hour periods after the given date.
	 * @param d The date to be offset.
	 * @param i The number of 1 hour periods to offset.
	 * @return The new Date.
	 */
	public static Date incrementHour(Date d, Integer i) {
		return new Date(d.getTime()+i*DayNode.mSecInDay/24);
	}
	/**
	 * Creates a {link Date} a certain number of 1 hour periods after the current date.
	 * @param i The number of 1 hour periods to offset.
	 * @return The new Date.
	 */
	public static Date incrementHour(Integer i) {
		return DateHelper.incrementHour(new Date(), i);
	}
	/**
	 * Sets the current time.
	 */
	public static void initialise() {
		init = new Date();
	}
}
