package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.DayNode;
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
	public boolean queryUnhappyTickets()
	{
		return handler.queryUnhappyTickets();
	}
	public ArrayList<ElectricityTicket> getUnhappyTickets()
	{
		return handler.getUnhappyTickets();
	}
	public ArrayList<String> connectEDC(String input) throws IOException {
		ArrayList<String> inputArr = new ArrayList<String>();
		inputArr.add(input);
		return connectEDC(inputArr);
	}
	public Boolean queryUnsatisfiedReqs()
	{
		return handler.queryUnsatisfiedReqs();
	}
	public ArrayList<String> connectServer(ArrayList<String> input, String host, int port) throws IOException {
		ArrayList<String> ret =  new ArrayList<String>();
		try (Socket kkSocket = new Socket();) {

			kkSocket.connect(new InetSocketAddress(host, port), 1000);
			try (
					PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));)
			{
			for (String s : input) {
				out.println(s);
			}

			String fromServer;
			while ((fromServer = in.readLine()) != null) {
				ret.add(fromServer);
				try {
					Thread.sleep(10); //Hacky solution to RPI being slow. 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println("Server: " + fromServer);
				if (fromServer.equals("NUL"))
					break;
			}
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

	public Boolean addDevice(Boolean state, Integer type, String deviceID, Integer pin)
	{
		String inputLine = formatMessage("ADD" , Boolean.toString(state) ,Integer.toString(type) , deviceID, Integer.toString(pin));
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
		String inputLine = formatMessage("CAL");
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
		String inputLine = formatMessage("TKT");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		ArrayList<String> input = new ArrayList<String>();
		ArraySet<ElectricityTicket> output = new ArraySet<ElectricityTicket>();
		input.add(inputLine);
		end(input);
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
		String inputLine = formatMessage("REQ" , req.toString());
				
	    ArrayList<String> input = new ArrayList<String>();
	    input.add(inputLine);
	    end(input);
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
		String inputLine = formatMessage("SET" ,deviceID , Boolean.toString(val));
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
	public String formatMessage(String ... args)
	{
		String ret = userId + ",";
		for (String s : args)
		{
			ret += s + ",";
		}
		
		return ret;
	}
	public Boolean getState(String deviceID)
	{
		String inputLine = formatMessage("GETS",deviceID);
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
	public ArrayList<String> end(ArrayList<String> s)
	{
		s.add(userId + "," + "END");
		return s;
	}
	public Boolean removeDevice(String deviceID)
	{
		String inputLine = formatMessage("REM",deviceID);
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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

	public Boolean registerUser(Double worth, Double generation, Double economic, int port) {
		String inputLine = formatMessage("USR",  handler.getSalt(), handler.getHash() , userId ,userName ,worth.toString(),generation.toString(),economic.toString(),String.valueOf(port));
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
	public boolean wipeAll()
	{
		return wipeEDC()&&wipeHLC();
	}
	public boolean wipeEDC()
	{
		String inputLine = formatMessage("DEL" , "drop");
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
	public boolean wipeHLC()
	{
		String inputLine = formatMessage("DEL" , "drop");
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
		String inputLine = formatMessage("GEN", userId , String.valueOf(i.getMaxOutput()));
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
		String inputLine = formatMessage("XST",userName);
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
		try {
			ArrayList<String> ret = connectHLC(input);
			return (ret.get(0).equals("SUCCESS"));
		} catch (IOException e) {
		}
		return false;
	}

	public String getRegisteredUUID() {
		String inputLine = formatMessage("GID", userName);
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
		String inputLine = formatMessage("ADV" , req.toString());
		ArrayList<String> input = new ArrayList<String>();
		ArraySet<ElectricityTicket> output = new ArraySet<ElectricityTicket>();
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		input.add(inputLine);
		end(input);
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
		String inputLine = formatMessage("OFR" , tktOld.toString() , tktNew.toString());
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
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
	public Boolean registerClient(String location, int port, int ownPort)
	{
		String inputLine = formatMessage("REG" , userId , "localHost" , String.valueOf(ownPort));
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
		ArrayList<String> msg;
		try {
			msg = connectClient(input, location, port);
			List<String> splitMsg = Arrays.asList(msg.get(0).split(",[ ]*"));
			if (splitMsg.get(0).equals("SUCCESS")) {
				return true;
				
			}
		} catch (IOException e1) {
		} 
		return false;
	}
	public static Double evalTimeGap(Date start1, Date end1, Date start2, Date end2) {
		Double ret = 0.;
		//previous work suggests four hours is a suitable time for the requirement to be useless. This is not accurate e.g. television.
		//However, it is a good starting point. Propose adding a flexibility measure to requirements? Integrating may be tricky.
		double hrsOffset = 4.;
		double msecInHr = DayNode.secInHr*1000;
		
		double dur1 = (end1.getTime()-start1.getTime())/msecInHr;
		double mean1 = ((end1.getTime()+start1.getTime())/(2*msecInHr));
		double dur2 = (end2.getTime()-start2.getTime())/msecInHr;
		double mean2 = ((end2.getTime()+start2.getTime())/(2*msecInHr));
		
		double dst = -Math.abs(mean1-mean2);
		double dsize = Math.abs(dur1-dur2)/2;
		double uncappedUtility = (hrsOffset + dst + dsize) / hrsOffset;
		double cappedUtility = uncappedUtility > 1. ? 1 : uncappedUtility;
		return cappedUtility;
		
	}
	public Boolean modifyTicket(String type,ElectricityTicket tkt, ElectricityRequirement req, ElectricityTicket oldtkt)
	{
		String inputLine = formatMessage(type,  tkt.toString() , req.toString() , oldtkt.toString());
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
		ArrayList<String> msg;
		try {
			msg = connectHLC(input);
			List<String> splitMsg = Arrays.asList(msg.get(0).split(",[ ]*"));
			if (splitMsg.get(0).equals("SUCCESS")) {

				DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				try {
					tkt.start = df.parse(splitMsg.get(1));
					tkt.end   = df.parse(splitMsg.get(2));
					tkt.magnitude = Double.parseDouble(splitMsg.get(3));
					
					oldtkt.start = df.parse(splitMsg.get(7));
					oldtkt.end   = df.parse(splitMsg.get(8));
					oldtkt.magnitude = Double.parseDouble(splitMsg.get(9));
					return true;
				} catch (ParseException e) {
				}
				
			}
		} catch (IOException e1) {
		} 
		return false;
	}

	public Boolean extendTicket(ElectricityTicket tkt, ElectricityRequirement req, ElectricityTicket oldtkt) {
		return modifyTicket("EXT",tkt,req,oldtkt);
	}
	public Boolean intensifyTicket(ElectricityTicket tkt, ElectricityRequirement req, ElectricityTicket oldtkt) {
		return modifyTicket("INT",tkt,req,oldtkt);
	}
	public HashMap<String, InetSocketAddress> getPeers() {
		String inputLine = formatMessage("ADR");
		ArrayList<String> input = new ArrayList<String>();
		input.add(inputLine);
		end(input);
		ArrayList<String> msg;
		HashMap<String, InetSocketAddress> ret = null;
		try {
			msg = connectHLC(input);
			List<String> splitMsg = Arrays.asList(msg.get(0).split(",[ ]*"));
			if (splitMsg.get(0).equals("SUCCESS")) {
				ret = new HashMap<String, InetSocketAddress>();
				int size = splitMsg.size() / 3;
				int offset = 0;
				for (int i = 0; i < size; i++) {
					try {
						ret.put(splitMsg.get(1+offset), new InetSocketAddress(splitMsg.get(2+offset), Integer.parseInt(splitMsg.get(3+offset))));
					} catch (Exception e) {
						System.out.println("Invalid address");
					}
					offset += 3;
				}
			}
		} catch (IOException e1) {
		} 
		return ret;
	}
}
