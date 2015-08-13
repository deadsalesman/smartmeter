package uk.ac.imperial.smartmeter.webcomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;

public class LCServer implements Runnable {
	private Integer portNum;
	private UserAddressBook addresses;
	public LCClient client;
	private boolean durationModifiable = false;
	private boolean amplitudeModifiable = false;
	Boolean active = true;
	Thread t;
	private boolean verbose;
	public void setTicketDurationModifiable(Boolean t)
	{
		durationModifiable = t;
	}
	public LCServer(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, Integer ownPort, String name,String password,Boolean loud) {
		portNum = ownPort;
		client = new LCClient(eDCHostName, eDCPortNum, hLCHostName, hLCPortNum, name, password);
		addresses = new UserAddressBook();
		verbose = loud;
	}
	public LCServer(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, Integer ownPort, String name,String password) {
		this( eDCHostName,  eDCPortNum,  hLCHostName, hLCPortNum,  ownPort,  name, password, true);
	}
	public Integer getPort()
	{
		return portNum;
	}
	public void close()
	{
		active = false;
	}
	private String recvMsg(String msg) {
		List<String> splitMsg = Arrays.asList(msg.split(",[ ]*"));
		switch (splitMsg.get(1)) {
		case ("ADV"): {
			return findCompetingTickets(splitMsg);
		}
		case ("OFR"): {
			return considerOffer(splitMsg);
		}
		case ("REG"): {
			return register(splitMsg);
		}
		default: {
			return "NUL";
		}
		}
	}

	private String register(List<String> splitMsg) {
		UserAddress u = new UserAddress(splitMsg.get(2), splitMsg.get(3),Integer.parseInt(splitMsg.get(4)));
		
		if (addresses.addUser(u))
		{
			return "SUCCESS,";
		}
		
		return "FAILURE,";
	}

	private String considerOffer(List<String> splitMsg) {

		String traderId = splitMsg.get(0);
        if (addresses.queryUserExists(traderId))
        {
		try {
			ElectricityTicket newtkt = new ElectricityTicket(
					new Date(Long.parseLong(splitMsg.get(9))),
					new Date(Long.parseLong(splitMsg.get(10))),
					Double.parseDouble(splitMsg.get(11)),
					traderId,
					splitMsg.get(13),
					splitMsg.get(14)
					);
			ElectricityTicket oldtkt = findOwnTicket(splitMsg.get(7));
			if (oldtkt!= null)
			{
			ElectricityRequirement oldReq = client.handler.findMatchingRequirement(oldtkt);
			ElectricityTicket tempOld = new ElectricityTicket(oldtkt);
			ElectricityTicket tempNew = new ElectricityTicket(newtkt);
			Double oldUtility = evaluateUtility(new ElectricityTicket(oldtkt), oldReq, null); //third parameter not included here for convenience
																	   //if it is needed then the old ticket does not satisfy the old requirement which is a systematic failure
			Double newUtility = evaluateUtility(tempNew, oldReq, tempOld);
			Boolean result = decideUtility(newUtility, oldUtility,splitMsg.get(5));
			
			if (result)
			{
				
				return modifyTickets(oldtkt,newtkt, tempOld, tempNew);
			}
			}
			else
			{
				return "FAILURE";
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
		return "FAILURE";
	}

	private String modifyTickets(ElectricityTicket oldtkt,ElectricityTicket newtkt, ElectricityTicket tempOld, ElectricityTicket tempNew) {
		// TODO Auto-generated method stub
		//change the owners of the tickets around
		//format as string suitable for transfer
		newtkt.modifyTimings(tempOld);
		newtkt.modifyID(tempNew);
		oldtkt.modifyTimings(tempNew);
		oldtkt.modifyID(tempOld);
		String ret = "SUCCESS,";
		ret += newtkt.toString();
		client.handler.forceNewTicket(oldtkt);
		return ret;
	}

	private Boolean decideUtility(Double newUtility, Double oldUtility, String user) {
		// TODO implement different types of agent
		Double history = 0.;
		Double credit = 0.;
		Double leeway = 0.2;
		history = addresses.getHistory(user);
		
		if ((history <= credit) && (Math.abs(newUtility-oldUtility) <= leeway)) {
			addresses.setHistory(user,history + (newUtility - oldUtility));
			return true;
		}
		return false;
	}
	public static Double calcUtilityNoExtension(ElectricityTicket newtkt, ElectricityRequirement r)
			{
		Double utility = 0.;
		double duration;
		duration = (newtkt.getEnd().getTime() - newtkt.getStart().getTime()) / (double)QuantumNode.quanta;
		if (r.getMaxConsumption() <= newtkt.magnitude) {
			if (r.getDuration() <= duration) {
				utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
			}
		}
		return utility;
			}
	private Double evaluateUtility(ElectricityTicket newtkt, ElectricityRequirement r, ElectricityTicket oldtkt) {
		Double utility = 0.;
		double duration = (newtkt.getEnd().getTime() - newtkt.getStart().getTime()) / (double)QuantumNode.quanta;
		if (r.getMaxConsumption() <= newtkt.magnitude) {
			if (r.getDuration() <= duration) {
				utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
			} else {
				// ticket is insufficient for this requirement
				if (durationModifiable)
				{
				client.extendTicket(newtkt, r, oldtkt);
				utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
				}
			}
		} else {
			if (amplitudeModifiable)
			{
			client.intensifyTicket(newtkt, r, oldtkt);
			utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
			}
		}

		return utility;
	}

	private ElectricityTicket findOwnTicket(String string) {
		return client.getTickets().findFromID(string);
	}

	private String findCompetingTickets(List<String> splitMsg) {

		String ret = "FAILURE";
		if (addresses.queryUserExists(splitMsg.get(7))) {
			ElectricityRequirement req;
			try {
				req = new ElectricityRequirement(
						new Date(Long.parseLong(splitMsg.get(2))),
						new Date(Long.parseLong(splitMsg.get(3))),
						new DecimalRating(Integer.parseInt(splitMsg.get(4))),
						Integer.parseInt(splitMsg.get(5)),
						Double.parseDouble(splitMsg.get(6)),
						splitMsg.get(7),
						splitMsg.get(8));
				ArraySet<ElectricityTicket> tickets = client.findCompetingTickets(req);

				if (tickets != null) {
					ret = "SUCCESS,";
					for (ElectricityTicket et : tickets) {
						ret += et.toString();
					}
				}
			} catch (Exception e) {
			}
		}
		return ret;
	}

	public boolean listen() throws IOException {
		while(active){
		try (ServerSocket serverSocket = new ServerSocket(portNum);){
		serverSocket.setSoTimeout(3000);
				try(
				Socket clientSocket = serverSocket.accept();
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

			String inputLine, outputLine;

			while (((inputLine = in.readLine()) != null)&&active) {
				outputLine = recvMsg(inputLine);
				out.println(outputLine);

                if (verbose)
                {
                	System.out.println("Input: " +inputLine);
                	System.out.println("Output: " + outputLine);
                }
				if (outputLine.equals("NUL"))
					return true;
			}
				}
		} catch (IOException e) {
			//System.out.println("Exception caught when trying to listen on port " + portNum + " or listening for a connection");
			//System.out.println(e.getMessage());
		}
		}
		return false;
	}
	public void start() {
		System.out.println("Client server listening.");
		if (t == null)
	      {
	         t = new Thread (this, client.handler.getId());
	         t.start ();
	      }
	}
	@Override
	public void run() {
			try {
				while(listen()&&active)
				{
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public Boolean registerClient(String locationOfB, int portOfB) {
		return client.registerClient(locationOfB, portOfB, portNum);
	}
	public void setTicketAmplitudeModifiable(Boolean b) {
		amplitudeModifiable = b;
	}
	public void stop() {
		active = false;
	}

}
