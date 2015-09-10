package uk.ac.imperial.smartmeter.smx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.imperial.smartmeter.res.Pair;

/**
 * This class sends queries via sockets to the local SMX, receiving information about current consumption.
 * @author Ben Windo
 *
 */
public class SMXHandler {

	private String host;
	private Integer port;
	

	public SMXHandler(String hostIP, Integer hostPort)
	{
		host = hostIP;
		port = hostPort;
	}
	
	/**
	 * Queries the state of an existing smart meter. 
	 * @param smartMeterId The id of the smart meter. An integer between 0 and 2 inclusive.
	 * @return The current state of that smart meter.
	 */
	public SMXReading getData(Integer smartMeterId)
	{
			return null;
	}
	
	/**
	 * Asks the SMX for all available information on one of the smart meters.
	 * @param smartMeterId The id of the smart meter in question.
	 * @return A string that may be sent on the the SMX as a query.
	 */
	public String generateHTTPRequest(Integer ...smartMeterId)
	{
		String request = "get ";
		for (Integer i : smartMeterId)
		{
			String processedInt = padToTwoDigits(i);
			if (processedInt!=null)
			{
				request += "   Consumator_" + processedInt + " ";
				for (Field f : SMXReading.class.getDeclaredFields())
				{
					request += f.getName() + "=;";
				}
			}
			
		}
		return request;
	}
	/**
	 * Takes an integer between zero inclusive and 100 exclusive and pads it to two digits.
	 * @param smartMeterId
	 * @return String representation of the given integer, or null if invalid input;
	 */
	private String padToTwoDigits(Integer smartMeterId) {
		if ((smartMeterId < 10)&&(smartMeterId >= 0))
		{
			return "0"+smartMeterId.toString();
		}
		else if ((smartMeterId < 100)&&(smartMeterId >= 0))
		{
			return smartMeterId.toString();
		}
		else
		{
			return null;
		}
	}
	/**
	 * Sends a given request to the smartmeter, returning a new {@link SMXReading} array.
	 * @param request The HTTPRequest for the SMX.
	 * @return A new {@link SMXReading} array representing the response of the SMX.
	 */
	public SMXReading[] sendRequest(String request)
	{
		try (Socket kkSocket = new Socket();) {

			kkSocket.connect(new InetSocketAddress(host, port), 1000);
			try (
					PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));)
			{
			out.println(request);
			return parseSMXInput(in.readLine());
			}
		} catch (IOException e) {
			System.err.println("Don't know about host " + host);
			System.exit(1);
		}
		return null;
	}
	
	/**
	 * Parses an input string from the SMX, extracting the SMXReadings from that object.
	 * @param input The string from the SMX.
	 * @return An array of all the SMXReadings from the input string.
	 */
	public SMXReading[] parseSMXInput(String input) {
		String regex = "(Consumator_\\d\\d\\s+(\\D_\\D+?=\\d*.?\\d*;)+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		ArrayList<String> found = new ArrayList<String>();
		while (matcher.find()) {
			found.add(input.substring(matcher.start(), matcher.end()));
		}
		ArrayList<SMXReading> readings = new ArrayList<SMXReading>();

		for (String s : found) {
			readings.add(parse(s));
		}
		return readings.toArray(new SMXReading[readings.size()]);
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
