package uk.ac.imperial.smartmeter.log;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;


/**
 * A class representing a log of a transaction between users.
 * @author bwindo
 *
 */
public class LogTicketTransaction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 254012216679379337L;
	private String userDonor;
	private String userReceiver;
	private String ticketId;
	private Date timeTransaction;
	
	/**
	 * @return A String[] containing appropriate headers for a .csv file representing the ticket's internal data.
	 */
	public LogTicketTransaction(String donor, String receiver, String id, Date timestamp)
	{
		userDonor = donor;
		userReceiver = receiver;
		ticketId = id;
		timeTransaction = timestamp;
	}
	public static String[] getHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		for (Field f : LogTicketTransaction.class.getDeclaredFields())
		{
			headers.add(f.getName());
		}
		return headers.toArray(new String[headers.size()]);
	}
	/**
	 * 
	 * @return A String[] containing the internal data in the form of a string array for use in {@link TicketLogToCSV}
	 */
	public String[] dataToStringArray() {
		ArrayList<String> data = new ArrayList<String>();
		for (Field f : LogTicketTransaction.class.getDeclaredFields())
		{
			try {
				data.add(f.get(this).toString());
			} catch (IllegalArgumentException | IllegalAccessException e) {
			
			}
		}
		
		return data.toArray(new String[data.size()]);
	}

}
