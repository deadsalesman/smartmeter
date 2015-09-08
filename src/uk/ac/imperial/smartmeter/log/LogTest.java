package uk.ac.imperial.smartmeter.log;

import java.util.ArrayList;
import java.util.Date;

public class LogTest {
	public static void main(String[] args)
	{
		String[] x = LogTicketTransaction.getHeaders();
		LogTicketTransaction y = new LogTicketTransaction("hi","ho","silver",new Date());
		ArrayList<LogTicketTransaction> a = new ArrayList<LogTicketTransaction>();
		
		for (int i = 0; i < 1000; i++)
		{
			a.add(new LogTicketTransaction("hi"+i,"ho"+i,"silver"+i,new Date()));
		}
		TicketLogToCSV.writeLog(a);
	}
}
