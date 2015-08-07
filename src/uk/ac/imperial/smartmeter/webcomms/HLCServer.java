package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

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
	
	public HLCServer(int parseInt) {
		portNum = parseInt;
		handler = new HLCHandler();
	}
	private String recvMsg(String msg)
	{
		List<String> splitMsg = Arrays.asList(msg.split(",[ ]*"));
		switch (splitMsg.get(0)) {
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
	private String modifyTicket(List<String> splitMsg)
	{
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String ret = "FAILURE,";
		ElectricityTicket tkt;
		try {
			tkt = new ElectricityTicket(
					df.parse(splitMsg.get(1)),
					df.parse(splitMsg.get(2)),
					Double.parseDouble(splitMsg.get(3)),
					splitMsg.get(4),
					splitMsg.get(5),
					splitMsg.get(6)
					);
			ElectricityRequirement req = new ElectricityRequirement(
					df.parse(splitMsg.get(7)),
					df.parse(splitMsg.get(8)),
					new DecimalRating(Integer.parseInt(splitMsg.get(9))),
					Integer.parseInt(splitMsg.get(10)),
					Double.parseDouble(splitMsg.get(11)),
					splitMsg.get(12),
					splitMsg.get(13)
					);
			Boolean success = false;
			switch(splitMsg.get(0))
			{
			case("INT"): success = handler.intensifyTicket(tkt,req);
				break;
			case("EXT"): success = handler.extendTicket(tkt, req);
				break;
			}
			if(success)
			{
			ret = "SUCCESS,";
			ret += tkt.toString();
			}
		} catch (Exception e) {
		} 
		return ret;
	}
	private Boolean wipe(List<String> splitMsg) {
		return handler.clearAll(splitMsg.get(1));
	}
	private Boolean calculateTickets() {
		return handler.calculateTickets();
	}
	private String getUID(List<String> splitMsg) {
		return handler.getUUID(splitMsg.get(1));
	}
	private Boolean queryExistence(List<String> splitMsg) {
		return handler.queryUserExistence(splitMsg.get(1));
	}
	private Boolean setGen(List<String> splitMsg) {
		return handler.setUserGeneration(splitMsg.get(1), new ElectricityGeneration(Double.parseDouble(splitMsg.get(2))));
	}
	private Boolean registerUserAgent(List<String> splitMsg) {
		// TODO Verify user
		return handler.addUserAgent(new UserAgent(
				splitMsg.get(1),
				splitMsg.get(2),
				splitMsg.get(3),
				splitMsg.get(4),
				Double.parseDouble(splitMsg.get(5)),
			    Double.parseDouble(splitMsg.get(6)),
				Double.parseDouble(splitMsg.get(7))
						));
	}
	private Boolean addReq(List<String> splitMsg) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		ElectricityRequirement req;
		try {
			req = new ElectricityRequirement(
					df.parse(splitMsg.get(1)),
					df.parse(splitMsg.get(2)),
					new DecimalRating(Integer.parseInt(splitMsg.get(3))),
					Integer.parseInt(splitMsg.get(4)),
					Double.parseDouble(splitMsg.get(5)),
					splitMsg.get(6),
					splitMsg.get(7)
					);
		} catch (Exception e) {
		  return false;
		}
		return handler.setRequirement(req);
	}
	private String getTkts(List<String> splitMsg) {
		ArraySet<ElectricityTicket> tickets = handler.getTickets(splitMsg.get(1));
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
	
	public boolean listen() throws IOException
	{

        try ( 
            ServerSocket serverSocket = new ServerSocket(portNum);
            Socket clientSocket = serverSocket.accept();
            @SuppressWarnings("resource")
			PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            @SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
         
            String inputLine, outputLine;
             
            while ((inputLine = in.readLine()) != null) {
                outputLine = recvMsg(inputLine);
                out.println(outputLine);
                if (outputLine.equals("NUL"))
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
