package uk.ac.imperial.smartmeter.log;

import java.util.ArrayList;
import java.util.Date;

/**
 * @deprecated
 * Test for csv capabilities.
 * @author bwindo
 *
 */
public class LogTest {
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		LogTicketTransaction y = new LogTicketTransaction("hi","ho","silver",new Date());
		String[] x = y.getHeaders();
		ArrayList<LogTicketTransaction> a = new ArrayList<LogTicketTransaction>();
		
		for (int i = 0; i < 1000; i++)
		{
			a.add(new LogTicketTransaction("hi"+i,"ho"+i,"silver"+i,new Date()));
		}
		TicketLogToCSV.writeLog(a);
	}
}
