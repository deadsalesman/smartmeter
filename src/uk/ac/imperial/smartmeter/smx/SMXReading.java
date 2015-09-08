package uk.ac.imperial.smartmeter.smx;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.imperial.smartmeter.res.Pair;

/**
 * A class representing a reading of the SMX meter. Properties are null if they were not read.
 * @author bwindo
 *
 */
public class SMXReading {
	/**
	 * The reactive power.
	 */
	public Double Q_cons;
	/**
	 * The active power.
	 */
	public Double P_cons;
	/**
	 * The voltage. (probably)
	 */
	public Double U_R;
	/**
	 * The current.
	 */
	public Double I_R;
	
	public Double getReactivePower()
	{
		return Q_cons;
	}
	public Double getActivePower()
	{
		return P_cons;
	}
	public Double getVoltage()
	{
		return U_R;
	}
	public Double getCurrent()
	{
		return I_R;
	}
	/**
	 * Parses a string of the standard SMX format and generates a new {@link SMXReading} from that.
	 * @param readLine A string received from the SMX that contains one and only one representation of an SMXReading.
	 * @return A new SMXReading.
	 */
	public static SMXReading parse(String readLine) {
		String regex = "(\\D_\\D+?=\\d*.?\\d*;)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(readLine);
		  
		ArrayList<String> found = new ArrayList<String>();
		while(matcher.find())
		{
			found.add(readLine.substring(matcher.start(), matcher.end()));
		}
		ArrayList<Pair<String, Double>> elements = new ArrayList<Pair<String, Double>>();
		for (String s : found)
		{

			Pattern antipattern = Pattern.compile("=");
			Matcher antimatcher = antipattern.matcher(s);
			antimatcher.find();
			try{
				String name = s.substring(0, antimatcher.start());
				String value = s.substring(antimatcher.end(), s.length()-1);
				elements.add(new Pair<String,Double>(name,Double.parseDouble(value)));
			}
			catch (NumberFormatException e){
			}
		}
		SMXReading ret = new SMXReading();
		for (Pair<String, Double> pair : elements)
		{
			try {
				SMXReading.class.getField(pair.left).set(ret, pair.right);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				return null;
			}
		}
		return ret;
	}
	
}
