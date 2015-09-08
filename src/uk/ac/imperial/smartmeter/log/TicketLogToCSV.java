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
	public static Boolean writeLog(ArrayList<LogTicketTransaction> logs)
	{
		try (CSVWriter writer = new CSVWriter(new FileWriter("logout.csv"), ',', CSVWriter.NO_QUOTE_CHARACTER);){
			
			writer.writeNext(LogTicketTransaction.getHeaders());
			for (LogTicketTransaction l : logs)
			{
				writer.writeNext(l.dataToStringArray());
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
