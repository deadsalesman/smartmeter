package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.impl.HLCHandler;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;

public class HLCServer {
	private int portNum;
	private HLCHandler handler;
	private Map<String, InetSocketAddress> clients;
	private InetAddress tempAddress;
	private Boolean verbose;
	public HLCServer(int parseInt, Boolean verb) {
		portNum = parseInt;
		handler = new HLCHandler();
		clients = new HashMap<String, InetSocketAddress>();
		verbose = verb;
	}
	private String recvMsg(String msg)
	{
		List<String> splitMsg = Arrays.asList(msg.split(",[ ]*"));
		updateSender(splitMsg.get(0));
		switch (splitMsg.get(1)) {
		case ("CAL"):
		{
			return resultToStr(calculateTickets());
		}
		case ("INT"):
		{
			return modifyTicket(splitMsg);
		}
		case ("EXT"):
		{
			return modifyTicket(splitMsg);
		}
		case ("INF"):
		{
			return modifyTicket(splitMsg);
		}
		case ("EXF"):
		{
			return modifyTicket(splitMsg);
		}
		case ("REQ"):
		{
			return resultToStr(addReq(splitMsg));
		}
		case ("GEN"):
		{
			return resultToStr(setGen(splitMsg));
		}
		case ("USR"):
		{
			return resultToStr(registerUserAgent(splitMsg));
		}
		case ("GID"):
		{
			return getUID(splitMsg);
		}
		case ("XST"):
		{
			return resultToStr(queryExistence(splitMsg));
		}
		case ("CON"):
		{
			return "CON";
		}
		case ("ADR"):
		{
			return getAddresses(splitMsg);
		}
		case ("TKT"):
		{
			return getTkts(splitMsg);
		}
		case ("DEL"):
		{
			return resultToStr(wipe(splitMsg));
		}
		default:
		{
			return "NUL";
		}
		}
	}
	private String getAddresses(List<String> splitMsg) {
		String ret = "";
		if(clients.size()!=0)
		{
			ret = "SUCCESS,";
			for (Entry<String, InetSocketAddress> e : clients.entrySet())
			{
				ret += e.getKey() + "," + e.getValue().getHostName() + "," + e.getValue().getPort() + ",";
			}
		}
		return ret;
	}
	private String modifyTicket(List<String> splitMsg)
	{
		String ret = "FAILURE,";
		
		try {
			ElectricityTicket tkt = new ElectricityTicket(
					new Date(Long.parseLong(splitMsg.get(2))),
					new Date(Long.parseLong(splitMsg.get(3))),
					Double.parseDouble(splitMsg.get(4)),
					splitMsg.get(5),
					splitMsg.get(6),
					splitMsg.get(7)
					);
			ElectricityRequirement req = new ElectricityRequirement(
					new Date(Long.parseLong(splitMsg.get(9))),
					new Date(Long.parseLong(splitMsg.get(10))),
					new DecimalRating(Integer.parseInt(splitMsg.get(11))),
					Integer.parseInt(splitMsg.get(12)),
					Double.parseDouble(splitMsg.get(13)),
					splitMsg.get(14),
					splitMsg.get(15)
					);
			ElectricityTicket tktOld = new ElectricityTicket(
					new Date(Long.parseLong(splitMsg.get(17))),
					new Date(Long.parseLong(splitMsg.get(18))),
					Double.parseDouble(splitMsg.get(19)),
					splitMsg.get(20),
					splitMsg.get(21),
					splitMsg.get(22)
					);
			Boolean success = false;
			switch(splitMsg.get(1))
			{
			case("INT"): success = handler.intensifyTicket(tkt,req,tktOld,true);
				break;
			case("INF"): success = handler.intensifyTicket(tkt,req,tktOld,false);
				break;
			case("EXT"): success = handler.extendTicket(tkt, req,tktOld,true);
				break;
			case("EXF"): success = handler.extendTicket(tkt, req,tktOld,false);
				break;
			}
			if(success)
			{
			ret = "SUCCESS,";
			ret += tkt.toString();
			ret += tktOld.toString();
			}
		} catch (Exception e) {
		} 
		return ret;
	}
	private Boolean wipe(List<String> splitMsg) {
		clients = new HashMap<String, InetSocketAddress>();
		return handler.clearAll(splitMsg.get(2));
	}
	private Boolean calculateTickets() {
		return handler.calculateTickets();
	}
	private String getUID(List<String> splitMsg) {
		return handler.getUUID(splitMsg.get(2));
	}
	private Boolean queryExistence(List<String> splitMsg) {
		return handler.queryUserExistence(splitMsg.get(2));
	}
	private Boolean setGen(List<String> splitMsg) {
		return handler.setUserGeneration(splitMsg.get(2), new ElectricityGeneration(Double.parseDouble(splitMsg.get(3))));
	}
	private Boolean registerUserAgent(List<String> splitMsg) {
		// TODO Verify user
		clients.put(splitMsg.get(0), new InetSocketAddress(tempAddress,Integer.parseInt(splitMsg.get(9))));
		return handler.addUserAgent(new UserAgent(
				splitMsg.get(2),
				splitMsg.get(3),
				splitMsg.get(4),
				splitMsg.get(5),
				Double.parseDouble(splitMsg.get(6)),
			    Double.parseDouble(splitMsg.get(7)),
				Double.parseDouble(splitMsg.get(8))
						));
	}
	private Boolean addReq(List<String> splitMsg) {
		ElectricityRequirement req;
		try {
			req = new ElectricityRequirement(
					new Date(Long.parseLong(splitMsg.get(2))),
					new Date(Long.parseLong(splitMsg.get(3))),
					new DecimalRating(Integer.parseInt(splitMsg.get(4))),
					Integer.parseInt(splitMsg.get(5)),
					Double.parseDouble(splitMsg.get(6)),
					splitMsg.get(7),
					splitMsg.get(8)
					);
		} catch (Exception e) {
		  return false;
		}
		return handler.setRequirement(req);
	}
	private String getTkts(List<String> splitMsg) {
		ArraySet<ElectricityTicket> tickets = handler.getTickets(splitMsg.get(0));
		String ret = "";
		if (tickets != null)
		{
			for (ElectricityTicket et : tickets)
			{
				if (et!=null)
				{

					ret += et.toString();
				}
			}
		}
		else 
		{
			ret = "FAILURE";
		}
		
		return ret;
	}
	private String resultToStr(Boolean b)
	{
		return b ? "SUCCESS" : "FAILURE";
	}
	private Boolean updateSender(String user)
	{
		Boolean ret = false;
		if (clients.containsKey(user))
		{
			clients.put(user, new InetSocketAddress(tempAddress,clients.get(user).getPort()));
			return true;
		}
		
		return ret;
		
	}
	public boolean listen() throws IOException
	{

        try ( 
            ServerSocket serverSocket = new ServerSocket(portNum);
            Socket clientSocket = serverSocket.accept();
			PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
         
            String inputLine, outputLine;
             tempAddress = clientSocket.getInetAddress();
            while ((inputLine = in.readLine()) != null) {
                outputLine = recvMsg(inputLine);
                out.println(outputLine);
                if (verbose)
                {
                	System.out.println("Input: " +inputLine);
                	System.out.println("Output: " + outputLine);
                }
                if (outputLine.equals("NUL"))
                	tempAddress = null;
                    return true;
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNum + " or listening for a connection");
            System.out.println(e.getMessage());
        }
        return false;
    }
	 
}
