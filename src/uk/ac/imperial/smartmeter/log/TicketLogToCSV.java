package uk.ac.imperial.smartmeter.log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVWriter;

/**
 * This class takes a log of ticket transactions and prints it to a .csv file for later analysis.
 * @author bwindo
 *
 */
public class TicketLogToCSV {
	/**
	 * Writes the given transactions to file.
	 * @param logs An ArrayList of the {@link LogTicketTransaction}s to be written.
	 * @return 
	 */
	public static Integer writeLog(ArrayList<LogTicketTransaction> logs)
	{
		Integer total = -1;
		try (CSVWriter writer = new CSVWriter(new FileWriter("./tickets/logout.csv"), ',', CSVWriter.NO_QUOTE_CHARACTER);){
			LogTicketTransaction dummy = new LogTicketTransaction(null, null, null, null);
			writer.writeNext(dummy.getHeaders());
			total = 0;
			for (LogTicketTransaction l : logs)
			{
				writer.writeNext(l.dataToStringArray());
				total += 1;
			}
		} catch (IOException e) {
		}
		return total;
	}
}
