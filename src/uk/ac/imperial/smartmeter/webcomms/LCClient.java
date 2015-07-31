package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import uk.ac.imperial.smartmeter.impl.LCHandler;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;

public class LCClient{
	private String eDCHost;
	private int eDCPort;
	private String hLCHost;
	private int hLCPort;
	private static LCHandler handler;
	private String userId; 
	private String userName;
	
	
	public LCClient(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, String name) {
		eDCHost = eDCHostName;
		eDCPort = eDCPortNum;
		hLCHost = hLCHostName;
		hLCPort = hLCPortNum;
		userName = name;
		userId = UUID.randomUUID().toString();
		handler = new LCHandler(userId.toString());
		
		//why not register the user here?
	}

	public ArrayList<String> connectEDC(String input) throws IOException {
		ArrayList<String> inputArr = new ArrayList<String>();
		inputArr.add(input);
		return connectEDC(inputArr);
	}
	public ArrayList<String> connectEDC(ArrayList<String> input) throws IOException {
		ArrayList<String> ret =  new ArrayList<String>();
		try (Socket kkSocket = new Socket(eDCHost, eDCPort);
				PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));) {
			for (String s : input) {
				out.println(s);
			}

			String fromServer;
			while ((fromServer = in.readLine()) != null) {
				ret.add(fromServer);
				//System.out.println("Server: " + fromServer);
				if (fromServer.equals("NUL"))
					break;
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + eDCHost);
			System.exit(1);
		}
		return ret;
	}

	public ArrayList<String> connectHLC(ArrayList<String> input) throws IOException {
		ArrayList<String> ret =  new ArrayList<String>();
		try (Socket kkSocket = new Socket(hLCHost, hLCPort);
				PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));) {
			for (String s : input) {
				out.println(s);
			}

			String fromServer;
			while ((fromServer = in.readLine()) != null) {
				ret.add(fromServer);
				System.out.println("Server: " + fromServer);
				if (fromServer.equals("NUL"))
					break;
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hLCHost);
			System.exit(1);
		}
		return ret;
	}
	

	public Boolean addDevice(Boolean state, Integer type, String deviceID)
	{
		String inputLine = "ADD," + Boolean.toString(state) + "," + Integer.toString(type) + "," + deviceID;
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectEDC(input);
			if (ret.get(0).equals("SUCCESS"))
			{
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}
	public ArraySet<ElectricityTicket> getTickets()
	{
		String inputLine = "TKT," +  userId;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		ArrayList<String> input = new ArrayList<String>();
		ArraySet<ElectricityTicket> output = new ArraySet<ElectricityTicket>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> msg = connectHLC(input);
			if (!msg.get(0).equals("FAILURE"))
			{
			List<String> splitMsg = Arrays.asList(msg.get(0).split(",[ ]*"));
			int size = splitMsg.size() / 5;
			int offset = 0;
			for (int i = 0; i < size; i++)
			{
				try {
					output.add(new ElectricityTicket(
							df.parse(splitMsg.get(0+offset)),
							df.parse(splitMsg.get(1+offset)),
							Double.parseDouble(splitMsg.get(2+offset)),
							splitMsg.get(3+offset),
							splitMsg.get(4+offset)
							));
				} catch (Exception e) {
					System.out.println("Invalid ticket");
				}
				offset += size;
			}
			}
		
		} catch (IOException e) {
		}
		return output;
	}
	public Boolean setRequirement(ElectricityRequirement req)
	{

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String inputLine = "REQ," +
				df.format(req.getStartTime()) + "," +
				df.format(req.getEndTime()) + "," +
				req.getPriority() + "," +
				req.getProfileCode() + "," +
				req.getMaxConsumption() + "," +
				req.getUserID() + "," +
				req.getId()
				;
	    ArrayList<String> input = new ArrayList<String>();
	    input.add(inputLine);
	    input.add("END");
	    try {
		   ArrayList<String> ret = connectHLC(input);
		   if (ret.get(0).equals("SUCCESS"))
		   {
		   	return true;
		   }
	    } catch (IOException e) {
	}
	return false;
	}
	public Boolean setState(String deviceID, Boolean val)
	{
		String inputLine = "SET," + deviceID + "," + Boolean.toString(val);
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectEDC(input);
			if (ret.get(0).equals("SUCCESS"))
			{
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}
	public Boolean getState(String deviceID)
	{
		String inputLine = "GETS," + deviceID;
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectEDC(input);
			switch(ret.get(0))
			{
			case "TRUE":  return true;
			case "FALSE": return false;
			case "NULL":  return null; 
			}
			return Boolean.parseBoolean(ret.get(0));
		} catch (IOException e) {
		}
		return null;
	}
	public Boolean removeDevice(String deviceID)
	{
		String inputLine = "REM," + deviceID;
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectEDC(input);
			if (ret.get(0).equals("SUCCESS"))
			{
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}

	public Boolean registerUser(String passWd, String userName) {
		String inputLine = "USR," + userName + "," + userId + "," + passWd;
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectHLC(input);
			if (ret.get(0).equals("SUCCESS"))
			{
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}

	public String getId() {
		return handler.getId();
	}

	public boolean setGeneration(ElectricityGeneration i) {
		String inputLine = "GEN," + userId + "," + i.getMaxOutput();
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectHLC(input);
			if (ret.get(0).equals("SUCCESS"))
			{
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}
}
