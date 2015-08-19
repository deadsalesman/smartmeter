package uk.ac.imperial.smartmeter.webcomms;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.interfaces.LCServerIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.TicketTuple;

public class LCServer implements Runnable, LCServerIFace{
	private Integer portNum;
	private UserAddressBook addresses;
	public LCClient client;
	private boolean durationModifiable = false;
	private boolean amplitudeModifiable = false;
	private boolean modAmp = false;
	private boolean modDur = false;
	Boolean active = true;
	Thread t;
	private boolean verbose;
	public void setTicketDurationModifiable(Boolean t)
	{
		durationModifiable = t;
	}
	public LCServer(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, Integer ownPort, String name,String password,Boolean loud) throws RemoteException 
	{
		portNum = ownPort;
		client = new LCClient(eDCHostName, eDCPortNum, hLCHostName, hLCPortNum, name, password);
		addresses = new UserAddressBook();
		verbose = loud;
		 if (System.getSecurityManager() == null) {
	            System.setSecurityManager(new RMISecurityManager());
	        }
		try {
			LocateRegistry.createRegistry(portNum);
		}
		catch (RemoteException e)
		{
			
		}
		try
		{

			LCServerIFace stub = (LCServerIFace) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry(portNum);
			registry.rebind("LCServer", stub);
		}catch (RemoteException e)
		{
			System.out.println(e.getMessage());
		}
		
	}
	public LCServer(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, Integer ownPort, String name,String password) throws RemoteException {
		this( eDCHostName,  eDCPortNum,  hLCHostName, hLCPortNum,  ownPort, name, password, false);
	}
	public Integer getPort()
	{
		return portNum;
	}
	public void close()
	{
		active = false;
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
		TicketTuple query;
		double duration = (newtkt.getEnd().getTime() - newtkt.getStart().getTime()) / (double)QuantumNode.quanta;
		if (r.getMaxConsumption() <= newtkt.magnitude) {
			if (r.getDuration() <= duration) {
				utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
			} else {
				// ticket is insufficient for this requirement
				if (durationModifiable)
				{
				query = client.extendImmutableTicket(newtkt, oldtkt, r);
				if (query.success)
				{
				newtkt.clone(query.newTkt);
				oldtkt.clone(query.oldTkt);
				modDur = true;
				utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
				}
				}
			}
		} else {
			if (amplitudeModifiable)
			{
			query = client.intensifyImmutableTicket(newtkt, oldtkt, r);
			if (query.success)
			{
			newtkt.clone(query.newTkt);
			oldtkt.clone(query.oldTkt);
			modAmp = true;
			utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
			}
			}
		}

		return utility;
	}

	public void start() {
		//System.out.println("Client server listening.");
		if (t == null)
	      {
	         t = new Thread (this, client.handler.getId());
	         t.start ();
	      }
	}
	@Override
	public void run() {
			try {
				while(active)
				{
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
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
	@Override
	public String getMessage(String name, int port) throws RemoteException {
		return "RMI YAY";
	}
	@Override
	public Boolean registerClient(String location, int port, int ownPort,String userId, String ipAddr) {
	UserAddress u = new UserAddress(userId, ipAddr,ownPort);
		
		return addresses.addUser(u);
	}
	@Override
	public TicketTuple offer(String location, int port, ElectricityTicket tktDesired, ElectricityTicket tktOffered) {

		String traderId = tktOffered.ownerID.toString();
		Boolean result = false;
        if (addresses.queryUserExists(traderId))
        {
        	modAmp = false;
        	modDur = false;
			ElectricityRequirement oldReq = client.handler.findMatchingRequirement(tktDesired);
			ElectricityTicket tempOld = new ElectricityTicket(tktDesired);
			ElectricityTicket tempNew = new ElectricityTicket(tktOffered);
			Double oldUtility = evaluateUtility(new ElectricityTicket(tktDesired), oldReq, null); //third parameter not included here for convenience
																	   //if it is needed then the old ticket does not satisfy the old requirement which is a systematic failure
			Double newUtility = evaluateUtility(new ElectricityTicket(tktOffered), oldReq, new ElectricityTicket(tktDesired));
			result = decideUtility(newUtility, oldUtility,traderId);
			
			if (result)
			{
				TicketTuple query = new TicketTuple(tempNew, tempOld);
				if (modDur)
				{
				query = client.extendMutableTicket(tempNew, tempOld, oldReq);
				}
				if (modAmp)
				{
				query = client.intensifyMutableTicket(query.newTkt, query.oldTkt, oldReq);
				}
				tempNew = query.newTkt;
				tempOld = query.oldTkt;
				modifyTickets(tktDesired,tktOffered, tempOld, tempNew);
			}
			}
			else
			{
			}
		return new TicketTuple(tktDesired, tktOffered, result);
	}
	@Override
	public ArraySet<ElectricityTicket> queryCompeting(String location, int port, ElectricityRequirement req) {

		ArraySet<ElectricityTicket> ret = new ArraySet<ElectricityTicket>();
		if (addresses.queryUserExists(req.getUserID())) {
			
				ArraySet<ElectricityTicket> tickets = client.findCompetingTickets(req);

				if (tickets != null) {
					for (ElectricityTicket et : tickets) 
					{
						if (et!=null)
						{
							ret.add(et);
						}
					}
				}
		}
		return ret;
	}
	@Override
	public TicketTuple offer(String location, int port, TicketTuple tuple) throws RemoteException {
		return offer(location, port, tuple.newTkt, tuple.oldTkt);
	}

}
