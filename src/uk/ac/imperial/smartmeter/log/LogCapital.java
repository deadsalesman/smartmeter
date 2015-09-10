package uk.ac.imperial.smartmeter.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import uk.ac.imperial.smartmeter.res.Pair;

/**
 * This class acts a log of all the social capital that has been accrued (or lost) by a particular user.
 * @author bwindo
 *
 */
public class LogCapital implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2138181373641445504L;
	public Boolean modified;
	private ArrayList<Pair<Date, Double>> log;
	private Double currentTotal;
	public LogCapital()
	{
		log = new ArrayList<Pair<Date, Double>>();
		log.add(new Pair<Date, Double>(new Date(),0.));
		currentTotal = 0.;
		modified = false;
	}
	/**
	 * Adds a new entry to the log of all modifications. Sets modified to true, to prevent accidental misreading of a cached value.
	 * @param value The delta to add into the log.
	 * @return true if successful.
	 */
	public Boolean push(Double value)
	{
		modified = true;
		return log.add(new Pair<Date, Double>(new Date(), value));
	}
	/**
	 * 
	 * @return the total sum of all the deltas that have been associated with this user.
	 */
	public Double getTotal()
	{
		if (modified)
		{
			return calculateTotal();
		}
		else
		{
			return currentTotal;
		}
	}
	/**
	 * Sums all the deltas entered in the system
	 * @return The current total.
	 */
	private Double calculateTotal() {
		modified = false;
		Double total = 0.;
		for (Pair<Date, Double> x : log)
		{
			total += x.right;
		}
		currentTotal = total;
		return total;
	}
	
	public ArrayList<String[]> dataToStringArray() {
		ArrayList<String[]> ret = new ArrayList<String[]>();
		for (Pair<Date, Double> p : log)
		{
			ret.add(new String[]{p.left.toString(),p.right.toString()});
		}
		return ret;
	}
	
	public String[] getHeaders() {
		return new String[]{"delta","date"};
	}
}
