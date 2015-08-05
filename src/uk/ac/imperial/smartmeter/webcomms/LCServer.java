package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;

public class LCServer {
	private int portNum;
	public LCClient client;
	public LCServer(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, int ownPort, String name,String password) {
		portNum = ownPort;
		client = new LCClient(eDCHostName, eDCPortNum, hLCHostName, hLCPortNum, name, password);
	}

	private String recvMsg(String msg) {
		List<String> splitMsg = Arrays.asList(msg.split(",[ ]*"));
		switch (splitMsg.get(0)) {
		case ("ADV"): {
			return findCompetingTickets(splitMsg);
		}
		case ("OFR"): {
			return considerOffer(splitMsg);
		}
		default: {
			return "NUL";
		}
		}
	}

	private String considerOffer(List<String> splitMsg) {

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		try {
			ElectricityTicket newtkt = new ElectricityTicket(
					df.parse(splitMsg.get(1)),
					df.parse(splitMsg.get(2)),
					Double.parseDouble(splitMsg.get(3)),
					splitMsg.get(4)
					);
			ElectricityTicket oldtkt = findOwnTicket(splitMsg.get(5));
			Integer newUtility = evaluateUtility(newtkt);
			Integer oldUtility = evaluateUtility(oldtkt);
			Boolean result = evaluate(newUtility, oldUtility);
			
			if (result)
			{
				return modifyTickets();
			}
			else
			{
				return "FAILURE";
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String modifyTickets() {
		// TODO Auto-generated method stub
		return null;
	}

	private Boolean evaluate(Integer newUtility, Integer oldUtility) {
		// TODO Auto-generated method stub
		return null;
	}

	private Integer evaluateUtility(ElectricityTicket newtkt) {
		// TODO Auto-generated method stub
		return null;
	}

	private ElectricityTicket findOwnTicket(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private String findCompetingTickets(List<String> splitMsg) {

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String ret = "";
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
			ArraySet<ElectricityTicket> tickets = client.findCompetingTickets(req);
			
			if (tickets != null)
			{
				for (ElectricityTicket et : tickets)
				{
					ret += df.format(et.start) + "," + df.format(et.end) + "," + et.magnitude + "," + et.ownerID.toString() + "," + et.getId() + ",";
				}
			}
			else 
			{
				ret = "FAILURE";
			}
		} catch (Exception e) {
		  ret = "FAILURE";
		}
		
		return ret;
	}

	public boolean listen() throws IOException {

		try (ServerSocket serverSocket = new ServerSocket(portNum);
				Socket clientSocket = serverSocket.accept();
				@SuppressWarnings("resource")
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				@SuppressWarnings("resource")
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

			String inputLine, outputLine;

			while ((inputLine = in.readLine()) != null) {
				outputLine = recvMsg(inputLine);
				out.println(outputLine);
				if (outputLine.equals("NUL"))
					return true;
			}
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port " + portNum + " or listening for a connection");
			System.out.println(e.getMessage());
		}
		return false;
	}

}
