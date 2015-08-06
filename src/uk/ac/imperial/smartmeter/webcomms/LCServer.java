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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;

public class LCServer implements Runnable {
	private Integer portNum;
	private Map<String, Double> agentUtilities;
	public LCClient client;
	Thread t;
	public LCServer(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, Integer ownPort, String name,String password) {
		portNum = ownPort;
		client = new LCClient(eDCHostName, eDCPortNum, hLCHostName, hLCPortNum, name, password);
		agentUtilities = new HashMap<String, Double>();
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
			String traderId = splitMsg.get(10);
			ElectricityTicket newtkt = new ElectricityTicket(
					df.parse(splitMsg.get(7)),
					df.parse(splitMsg.get(8)),
					Double.parseDouble(splitMsg.get(9)),
					traderId,
					splitMsg.get(11),
					splitMsg.get(12)
					);
			ElectricityTicket oldtkt = findOwnTicket(splitMsg.get(6));
			Double newUtility = evaluateUtility(newtkt);
			Double oldUtility = evaluateUtility(oldtkt);
			Boolean result = decideUtility(newUtility, oldUtility,splitMsg.get(4));
			
			if (result)
			{
				
				return modifyTickets(oldtkt,newtkt);
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

	private String modifyTickets(ElectricityTicket oldtkt,ElectricityTicket newtkt) {
		// TODO Auto-generated method stub
		//change the owners of the tickets around
		//format as string suitable for transfer
		UUID oldReq = UUID.fromString(newtkt.reqID.toString());
		UUID oldOwner = UUID.fromString(newtkt.ownerID.toString());
		
		newtkt.ownerID = UUID.fromString(oldtkt.ownerID.toString());
		newtkt.reqID = UUID.fromString(oldtkt.reqID.toString());
		
		oldtkt.ownerID = oldOwner;
		oldtkt.reqID = oldReq;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String ret = "SUCCESS,";
		ret += oldtkt.toString();
		client.handler.forceNewTicket(newtkt);
		return ret;
	}

	private Boolean decideUtility(Double newUtility, Double oldUtility, String user) {
		// TODO implement different types of agent
		Double history = 0.;
		Double credit = 0.;
		Double leeway = 0.;
		if (agentUtilities.containsKey(user)) {
			history = agentUtilities.get(user);
		}
		if ((history <= credit) && (newUtility >= (oldUtility + leeway))) {
			agentUtilities.put(user, history + (newUtility - oldUtility));
			return true;
		}
		return false;
	}

	private Double evaluateUtility(ElectricityTicket newtkt) {
		Double utility = 0.;
		double duration;
		duration = (newtkt.end.getTime() - newtkt.start.getTime())/QuantumNode.quanta;
		for (ElectricityRequirement r : client.handler.getReqs())
		{
			if ((r.getDuration() >= duration)&&(r.getMaxConsumption() <= newtkt.magnitude))
				{
				  utility += client.evalTimeGap(newtkt.start, newtkt.end, r.getStartTime(), r.getEndTime());
				}
			else
			{
				//ticket is insufficient for this requirement
			}
		}
		return utility;
	}

	private ElectricityTicket findOwnTicket(String string) {
		return client.getTickets().findFromID(string);
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
				ret = "SUCCESS,";
				for (ElectricityTicket et : tickets)
				{
					ret += et.toString();
				}
			}
			else 
			{
				ret = "FAILURE,";
			}
		} catch (Exception e) {
		  ret = "FAILURE,";
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
	public void start() {
		if (t == null)
	      {
	         t = new Thread (this, client.handler.getId());
	         t.start ();
	      }
	}
	@Override
	public void run() {
		try {
			while(listen()){}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
