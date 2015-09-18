package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.crypto.KeyPairGen;
import uk.ac.imperial.smartmeter.crypto.SignatureHelper;
import uk.ac.imperial.smartmeter.decisions.DecisionModuleFactory;
import uk.ac.imperial.smartmeter.decisions.DecisionModuleIFace;
import uk.ac.imperial.smartmeter.interfaces.LCServerIFace;
import uk.ac.imperial.smartmeter.log.LogCapital;
import uk.ac.imperial.smartmeter.log.LogTicketTransaction;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Pair;
import uk.ac.imperial.smartmeter.res.TicketTuple;
import uk.ac.imperial.smartmeter.res.Triple;

/**
 * Class that handles remote communication and response for the LocalController.
 * Listens on a port and responds to requests from other clients.
 * @author bwindo
 * @see LCClient
 * @see LCAdmin
 * @see LCStandalone
 */
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
	private String pubKey;
	private String privKey;
	private String passWd;
	private DecisionModuleIFace decisionModule;
	
	public void setTicketDurationModifiable(Boolean t)
	{
		durationModifiable = t;
	}
	public String getPubKey()
	{
		return pubKey;
	}
	/**
	 * Creates a new LCServer object with the given parameters and initialises security settings. Exports the RMI listener to the given port.
	 * @param eDCHostName IP address of the {@link EDCServer}
	 * @param eDCPortNum  Port the {@link EDCServer} listens on.
	 * @param hLCHostName IP address of the {@link HLCServer}
	 * @param hLCPortNum  Port the {@link HLCServer} listens on.
	 * @param ownPort     Port that the LCServer listens on.
	 * @param name        Name of the LCServer.
	 * @param password    Password associated with the LCServer.
	 * @throws RemoteException
	 */
	public LCServer(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, Integer ownPort, String name,String password) throws RemoteException 
	{
		portNum = ownPort;
		client = new LCClient(eDCHostName, eDCPortNum, hLCHostName, hLCPortNum, name, password);
		addresses = new UserAddressBook();
		decisionModule = DecisionModuleFactory.getDecisionModule("Sensible");
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
			Pair<String, String> x = KeyPairGen.genKeySet(client.getId(), password);
			pubKey = x.right;
			privKey = x.left;

			SignatureHelper.printPubKey(client.getId(), pubKey);
			SignatureHelper.printSecKey(client.getId(), privKey);
			
			passWd = password;
			for (Entry<String, Triple<String,InetSocketAddress, LogCapital>> entry : client.getAddresses().entrySet())
			{
				addresses.addUser(entry);
			}
			
		}catch (RemoteException e)
		{
			System.out.println(e.getMessage());
		}
		
	}
	/**
	 * Registers the current user with the remote {@link HLCServer} and prints the public key of the remote server.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 * @param port The port the LCServer is listening on.
	 * @return the pair consisting of the server's id and its public key.
	 */
	public Pair<String,String> registerUser(Double social, Double generation, Double economic, int port) {
		Pair<String, String> ret = client.registerUser(social, generation, economic, pubKey, port);
		SignatureHelper.printPubKey(ret.left, ret.right);
		addresses.setHLCiD(ret.left);
		return ret;
	}
	public Integer getPort()
	{
		return portNum;
	}
	/**
	 * Shuts down the server.
	 */
	public void close()
	{
		active = false;
	}
	/**
	 * Modifies tickets from temporary ones in order to return the original tickets with the correctly altered values.
	 * @param oldtkt
	 * @param newtkt
	 * @param tempOld
	 * @param tempNew
	 */
	private void modifyTickets(ElectricityTicket oldtkt,ElectricityTicket newtkt, ElectricityTicket tempOld, ElectricityTicket tempNew) {
		// TODO Auto-generated method stub
		//change the owners of the tickets around
		//format as string suitable for transfer
		
		newtkt.modifyTimings(tempOld);
		newtkt.modifyID(tempNew);
		oldtkt.modifyTimings(tempNew);
		oldtkt.modifyID(tempOld);
		client.handler.forceNewTicket(oldtkt);
		SignatureHelper.signTicketForNewUser(oldtkt, client.getId(), passWd);
		SignatureHelper.signTicketForNewUser(newtkt, client.getId(), passWd);
		try {
			client.registerTicketTransaction(new LogTicketTransaction(oldtkt.ownerID.toString(), newtkt.ownerID.toString(), newtkt.getId(), new Date()));
			client.registerTicketTransaction(new LogTicketTransaction(newtkt.ownerID.toString(), oldtkt.ownerID.toString(), oldtkt.getId(), new Date()));
		} catch (RemoteException e) {
		}
	}
	public Double calculateTotalUtility()
	{
		Double total = 0.;
		for (Entry<ElectricityRequirement, ElectricityTicket> x : client.handler.getReqTktMap().entrySet())
		{
			try{
			total += x.getKey().getPriority()*calcUtilityNoExtension(x.getValue(), x.getKey());
			}
			catch(Exception e){}
		}
		return total;
	}
	/**
	 * Calculates the utility of a given {@link ElectricityTicket} : {@link ElectricityRequirement} pair.
	 * @param newtkt The ticket under consideration.
	 * @param r The requirement under consideration.
	 * @return The utility of the pairing.
	 */
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
	/**
	 * Calculates the utility associated with a new ticket for a given {@link ElectricityRequirement} and compares that to the utility from the existing ticket if applicable.
	 * If the new utility is not sufficient to swap, the server may attempt to modify the tickets with the consent of the {@link HLCServer} if that option has been previously set.
	 * @param newtkt The new ticket to be considered.
	 * @param r The ElectricityRequirement under consideration.
	 * @param oldtkt The old ticket that was previously found to be satisfactory.
	 * @return The calculated utility for the modified? new ticket.
	 */
	private Double evaluateUtility(ElectricityTicket newtkt, ElectricityRequirement r, ElectricityTicket oldtkt) {
		Double utility = 0.;
		TicketTuple query;
		double duration = (newtkt.getEnd().getTime() - newtkt.getStart().getTime()) / (double)QuantumNode.quanta;
		if (r.getMaxConsumption() <= newtkt.magnitude) {
			if (r.getDuration() <= duration) {
				utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
			} else {
				// ticket is insufficient for this requirement
				if (oldtkt!=null)
				{
				if (durationModifiable)
				{
				query = client.extendImmutableTicket(newtkt, oldtkt, r);
				if ((query!=null)&&(query.success))
				{
				newtkt.clone(query.newTkt);
				oldtkt.clone(query.oldTkt);
				modDur = true;
				utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
				}
				}
				}
			}
		} else {
			if (amplitudeModifiable)
			{
				if (oldtkt!=null)
				{
			query = client.intensifyImmutableTicket(newtkt, oldtkt, r);
			if ((query!=null)&&(query.success))
			{
			newtkt.clone(query.newTkt);
			oldtkt.clone(query.oldTkt);
			modAmp = true;
			utility += LCClient.evalTimeGap(newtkt.getStart(), newtkt.getEnd(), r.getStartTime(), r.getEndTime());
			}
				}
			}
		}

		return utility;
	}
	/**
	 * Initialises the server and binds it to a new thread.
	 */
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
	/**
	 * Registers the client with a given LCServer
	 * @param locationOfB The location of the LCServer
	 * @param portOfB The port listened to by the LCServer
	 * @see LCServer#registerClient(String location, int port, int ownPort, String userId, String ipAddr, String pubKey)
	 * @return Success?
	 */
	public Boolean registerClient(String locationOfB, int portOfB) {
		return client.registerClient(locationOfB, portOfB, portNum,client.handler.getId(),"localhost",pubKey);
	}
	public void setTicketAmplitudeModifiable(Boolean b) {
		amplitudeModifiable = b;
	}
	/**
	 * Stops the server.
	 */
	public void stop() {
		active = false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage(String name, int port) throws RemoteException {
		return "RMI YAY";
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean registerClient(String location, int port, int ownPort,String userId, String ipAddr, String pubKey) {
	UserAddress u = new UserAddress(userId, ipAddr,pubKey, ownPort);
		
		return addresses.addUser(u);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple offer(String location, int port, ElectricityTicket tktDesired, ElectricityTicket tktOffered) {

		String traderId = tktOffered.ownerID.toString();
		Boolean result = false;
        if (addresses.queryUserExists(traderId))
        {
        	modAmp = false;
        	modDur = false;
        	Double oldUtility = 0.;
        	Double newUtility = 0.;
			ElectricityRequirement oldReq = client.handler.findMatchingRequirement(tktDesired);
			ElectricityTicket tempOld = new ElectricityTicket(tktDesired);
			ElectricityTicket tempNew = new ElectricityTicket(tktOffered);
			//if (SignatureHelper.verifyTicket(tktOffered, addresses))
			{
			 oldUtility = evaluateUtility(new ElectricityTicket(tktDesired), oldReq, null); //third parameter not included here for convenience
				                               											   //if it is needed then the old ticket does not satisfy the old requirement which is a systematic failure
			 newUtility = evaluateUtility(new ElectricityTicket(tktOffered), oldReq, new ElectricityTicket(tktDesired));
			result = decisionModule.decideUtility(newUtility, oldUtility,addresses,traderId);
			}
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
				Double del = newUtility - oldUtility;
				try {
					client.setCapital(traderId, del);
					client.setCapital(client.getId(), -del);
					modifyTickets(tktDesired,tktOffered, tempOld, tempNew);
				} catch (RemoteException e) {
				}
			}
        }
        TicketTuple ret = new TicketTuple(tktDesired, tktOffered, result);
        if(ret==null){System.out.println("goofed");}
        return ret;
	}
	/**
	 * {@inheritDoc}
	 */
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
							//System.out.println("MATCH");
							ret.add(et);
						}
					}
				}
		}
		return ret;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple offer(String location, int port, TicketTuple tuple) throws RemoteException {
		return offer(location, port, tuple.newTkt, tuple.oldTkt);
	}
	
	/**
	 * This method changes the type of decision agent used the the {@link LCServer} to make choices regarding whether or not to accept tickets.
	 * @param moduleName A string representing the type of decision agent to use.
	 */
	public void setDecisionModule(String moduleName)
	{
		decisionModule = DecisionModuleFactory.getDecisionModule(moduleName);
	}
}
