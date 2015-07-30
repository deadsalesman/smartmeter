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
import uk.ac.imperial.smartmeter.res.User;

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
			return resultToStr(registerUser(splitMsg));
		}
		case ("CON"):
		{
			return "CON";
		}
		case ("TKT"):
		{
			return getTkts(splitMsg);
		}
		default:
		{
			return "NUL";
		}
		}
	}
	
	private Boolean setGen(List<String> splitMsg) {
		return handler.setUserGeneration(splitMsg.get(1), new ElectricityGeneration(Double.parseDouble(splitMsg.get(2))));
	}
	private Boolean registerUser(List<String> splitMsg) {
		// TODO Verify user
		return handler.addUser(new User(splitMsg.get(1),splitMsg.get(2),splitMsg.get(3)));
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
		ArraySet<ElectricityTicket> tickets = handler.getTickets(new User("",splitMsg.get(1),""));
		
		String ret = "";
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		for (ElectricityTicket et : tickets)
		{
			//this is going to be hacky :(
			ret += df.format(et.start) + "," + df.format(et.end) + "," + et.magnitude + "," + et.ownerID.toString() + "," + et.getId() + ",";
		}
		return "";
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
