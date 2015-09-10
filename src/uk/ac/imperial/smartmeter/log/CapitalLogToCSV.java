package uk.ac.imperial.smartmeter.log;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import uk.ac.imperial.smartmeter.res.Pair;

import com.opencsv.CSVWriter;

/**
 * This class takes a log of social capital updates and prints it to a .csv file for later analysis.
 * @author bwindo
 *
 */
public class CapitalLogToCSV {
	/**
	 * Writes the given transactions to file.
	 * @param logs An ArrayList of the {@link LogCapital}s to be written.
	 * @return 
	 */
	public static Boolean writeLog(ArrayList<Pair<String, LogCapital>> logs)
	{
		for (Pair<String, LogCapital> l : logs)
		{
			try (CSVWriter writer = new CSVWriter(new FileWriter("./capital/" +l.left+".csv"), ',', CSVWriter.NO_QUOTE_CHARACTER);){
				writer.writeNext(l.right.getHeaders());
				writer.writeAll(l.right.dataToStringArray());
			} catch (IOException e) {
				return false;
			}
		}
		return true;
		
	}
}
