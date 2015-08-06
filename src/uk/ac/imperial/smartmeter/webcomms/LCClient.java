package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
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
	public LCHandler handler;
	private String userId; 
	private String userName;
	
	public LCClient(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, String name,String password) {
		eDCHost = eDCHostName;
		eDCPort = eDCPortNum;
		hLCHost = hLCHostName;
		hLCPort = hLCPortNum;
		userName = name;
		handler = new LCHandler(name,password,0.,0.,0.); //TODO: make not naughty. 
		userId = handler.getId();
	}
	public ArrayList<String> connectEDC(String input) throws IOException {
		ArrayList<String> inputArr = new ArrayList<String>();
		inputArr.add(input);
		return connectEDC(inputArr);
	}

	public ArrayList<String> connectServer(ArrayList<String> input, String host, int port) throws IOException {
		ArrayList<String> ret =  new ArrayList<String>();
		try (Socket kkSocket = new Socket(host, port);
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
			System.err.println("Don't know about host " + host);
			System.exit(1);
		}
		return ret;
	}
	public ArrayList<String> connectClient(ArrayList<String> input,String clientHost, int clientPort) throws IOException {
		return connectServer(input, clientHost, clientPort);
	}
	
	public ArrayList<String> connectEDC(ArrayList<String> input) throws IOException {
		return connectServer(input, eDCHost, eDCPort);
	}
	
	public ArrayList<String> connectHLC(ArrayList<String> input) throws IOException {
		return connectServer(input, hLCHost, hLCPort);
	}
	
	public ArraySet<ElectricityTicket> findCompetingTickets(ElectricityRequirement req)
	{
		return handler.findCompetingTickets(req);
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
	public Boolean GodModeCalcTKTS()
	{
		String inputLine = "CAL,";
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
			int size = splitMsg.size() / 6;
			int offset = 0;
			for (int i = 0; i < size; i++)
			{
				try {
					output.add(new ElectricityTicket(
							df.parse(splitMsg.get(0+offset)),
							df.parse(splitMsg.get(1+offset)),
							Double.parseDouble(splitMsg.get(2+offset)),
							splitMsg.get(3+offset),
							splitMsg.get(4+offset),
							splitMsg.get(5+offset)
							));
				} catch (Exception e) {
					System.out.println("Invalid ticket");
				}
				offset += 6;
			}
			}
		
		} catch (IOException e) {
		}
		
		for (ElectricityTicket t : output)
		{
			handler.setTicket(t);
		}
		return handler.getTkts();
	}
	public Boolean setRequirement(ElectricityRequirement req)
	{
		String inputLine = "REQ," + req.toString();
				
	    ArrayList<String> input = new ArrayList<String>();
	    input.add(inputLine);
	    input.add("END");
	    try {
		   ArrayList<String> ret = connectHLC(input);
		   if (ret.get(0).equals("SUCCESS"))
		   {
			return handler.setRequirement(req);
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

	public Boolean registerUser(Double worth, Double generation, Double economic) {
		String inputLine = "USR," +  handler.getSalt()+ ","+  handler.getHash() + "," + userId + "," +userName +  ","+worth+","+generation+","+economic+",";
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
		return userId;
	}
	public boolean wipe()
	{
		String inputLine = "DEL," + "drop";
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

	public boolean queryUserExists() {
		String inputLine = "XST," + userName;
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectHLC(input);
			return (ret.get(0).equals("SUCCESS"));
		} catch (IOException e) {
		}
		return false;
	}

	public String getRegisteredUUID() {
		String inputLine = "GID," + userName;
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		try {
			ArrayList<String> ret = connectHLC(input);
		    return ret.get(0);
		} catch (IOException e) {
		}
		return null;
	}

	public void setID(UUID fromString) {
		userId = fromString.toString();
	}

	public ArraySet<ElectricityTicket> queryCompeting(String location, int port, ElectricityRequirement req) {
		String inputLine = "ADV," + req.toString();
		ArrayList<String> input = new ArrayList<String>();
		ArraySet<ElectricityTicket> output = new ArraySet<ElectricityTicket>();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		input.add(inputLine);
		input.add("END");
		ArrayList<String> msg;
		try {
			msg = connectClient(input, location, port);
			List<String> splitMsg = Arrays.asList(msg.get(0).split(",[ ]*"));
			if (splitMsg.get(0).equals("SUCCESS")) {
				int size = splitMsg.size() / 6;
				int offset = 0;
				for (int i = 0; i < size; i++) {
					try {
						output.add(new ElectricityTicket(df.parse(splitMsg.get(1 + offset)), df.parse(splitMsg.get(2 + offset)),
								Double.parseDouble(splitMsg.get(3 + offset)), splitMsg.get(4 + offset), splitMsg.get(5 + offset), splitMsg.get(6 + offset)));
					} catch (Exception e) {
						System.out.println("Invalid ticket");
					}
					offset += 6;
				}
			}
		} catch (IOException e1) {
			return null;
		}

		return output;
	}
	public boolean offer(String location, int port, ElectricityTicket tktOld, ElectricityTicket tktNew) {
		String inputLine = "OFR," + tktOld.toString() + tktNew.toString();
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		input.add("END");
		ArrayList<String> msg;
		try {
			msg = connectClient(input, location, port);
			List<String> splitMsg = Arrays.asList(msg.get(0).split(",[ ]*"));
			if (splitMsg.get(0).equals("SUCCESS")) {
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				tktOld = new ElectricityTicket(df.parse(splitMsg.get(1)), df.parse(splitMsg.get(2)),
						Double.parseDouble(splitMsg.get(3)), splitMsg.get(4), splitMsg.get(5), splitMsg.get(6));
				handler.forceNewTicket(tktOld);
				return true;
				
			}
		} catch (IOException e1) {
		} catch (ParseException e1) {
		}
		return false;
	}
}
