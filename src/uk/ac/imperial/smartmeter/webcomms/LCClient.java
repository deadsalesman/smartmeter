package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.DayNode;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDeviceFactory;
import uk.ac.imperial.smartmeter.impl.LCHandler;
import uk.ac.imperial.smartmeter.institutions.GlobalCapitalIFace;
import uk.ac.imperial.smartmeter.institutions.InstitutionIFace;
import uk.ac.imperial.smartmeter.institutions.ServerCapitalIFace;
import uk.ac.imperial.smartmeter.interfaces.EDCServerIFace;
import uk.ac.imperial.smartmeter.interfaces.HLCServerIFace;
import uk.ac.imperial.smartmeter.interfaces.LCServerIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.log.LogCapital;
import uk.ac.imperial.smartmeter.log.LogTicketTransaction;
import uk.ac.imperial.smartmeter.log.RegisterTransactionIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Pair;
import uk.ac.imperial.smartmeter.res.TicketTuple;
import uk.ac.imperial.smartmeter.res.Triple;

/**
 * Acts as a central controller for the individual client nodes, coordinating between the High Level controller and the Device Controller.
 * @author bwindo
 *
 */
public class LCClient implements LCServerIFace, ServerCapitalIFace, EDCServerIFace, UniqueIdentifierIFace, InstitutionIFace {
	private String eDCHost;
	private int eDCPort;
	private String hLCHost;
	private int hLCPort;
	public LCHandler handler;
	private String userId; 
	private String userName;
	protected ArraySet<ElectricityRequirement> newReqs;
	public LCClient(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, String name,String password) {
		eDCHost = eDCHostName;
		eDCPort = eDCPortNum;
		hLCHost = hLCHostName;
		hLCPort = hLCPortNum;
		userName = name;
		handler = new LCHandler(name,password,0.,0.,0.); //TODO: make not naughty. 
		newReqs = new ArraySet<ElectricityRequirement>();
		userId = handler.getId();
		 if (System.getSecurityManager() == null) {
	            System.setSecurityManager(new RMISecurityManager());
	        }
	}
	/**
	 * Queries the controller whether there are any {@link ElectricityTicket} objects that do not have sufficient utility for the {@link ElectricityRequirement} associated to be fully satisfied.
	 * @return true if there any requirements that are not being sufficiently satisfied.
	 */
	public boolean queryUnhappyTickets()
	{
		return handler.queryUnhappyTickets();
	}
	/**
	 * 
	 * @return an ArrayList containing all {@link ElectricityTicket} objects that have been marked as not having sufficient utility for the {@link ElectricityRequirement} associated to be fully satisfied.
	 */
	public synchronized ArrayList<ElectricityTicket> getUnhappyTickets()
	{
		return handler.getUnhappyTickets();
	}
	/**
	 * Queries the controller to see if it has any {@link ElectricityRequirement}s which do not have an associated {@link ElectricityTicket} capable of satisfying their requirements.
	 * @return true iff there are no unsatisfied requirements
	 */
	public Boolean queryUnsatisfiedReqs()
	{
		return handler.queryUnsatisfiedReqs();
	}
	/**
	 * Finds the electricity tickets that are active at the same time as a given {@link ElectricityRequirement}.
	 * @param req The requirement in question.
	 * @return An {@link ArraySet} of the tickets that conflict with the given requirement.
	 */
	public synchronized ArraySet<ElectricityTicket> findCompetingTickets(ElectricityRequirement req)
	{
		return handler.findCompetingTickets(req);
	}
	/**
	 * Constructs a new {@link ElectronicConsumerDevice} via the factory, then calls:
	 * {@link LCClient#addDevice(ElectronicConsumerDevice ed, Integer pin)} 
	 */
	public Boolean addDevice(Boolean state, Integer type, String deviceID, Integer pin)
	{
		return addDevice(ElectronicDeviceFactory.getDevice(type,deviceID,state),pin);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean addDevice(ElectronicConsumerDevice ed, Integer pin)
	{
		try {
			return lookupEDCServer().addDevice(ed, pin);
		} catch (RemoteException e) {
			return false;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean GodModeCalcTKTS()
	{
		try {
			return lookupHLCServer().GodModeCalcTKTS();
		} catch (RemoteException e) {
			return false;
		}
	}
	/**
	 * Gets all tickets associated with the user associated with the invoking client.
	 * @return  An ArraySet of all the tickets associated with the owner of the invoking client.
	 * @see LCClient#getTickets(String user)
	 */
	public synchronized ArraySet<ElectricityTicket> getTickets()
	{
		try {
		ArraySet<ElectricityTicket> output = lookupHLCServer().getTickets(userId);
		for (ElectricityTicket t : output)
		{
			handler.setTicket(t);
		}
		return handler.getTkts();
		} catch(RemoteException e)
		{
			
		}
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized ArraySet<ElectricityTicket> getTickets(String user)
	{
		try {
			return lookupHLCServer().getTickets(user);
		} catch (RemoteException e) {
			return null;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean setRequirement(ElectricityRequirement req)
	{
		try {
			if (lookupHLCServer().setRequirement(req))
			{
				newReqs.add(req);
				return handler.setRequirement(req);
			}
		} catch (RemoteException e) {
		}

		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean setState(String deviceID, Boolean val)
	{
		try {
			return lookupEDCServer().setState(deviceID, val);
		} catch (RemoteException e) {
			return false;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getState(String deviceID)
	{
		try {
			return lookupEDCServer().getState(deviceID);
		} catch (RemoteException e) {
			return false;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean removeDevice(String deviceID)
	{
		try {
			return lookupEDCServer().removeDevice(deviceID);
		} catch (RemoteException e) {
			return false;
		}
	}
	/**
	 * Registers the current user with the remote server, treating the public key as empty Inadvisable to invoke. 
	 * @see LCClient#registerUser(String salt, String hash, String userId, String userName, String pubKey, Double worth, Double generation, Double economic, int port)
	 */
	public Pair<String,String> registerUser(Double worth, Double generation, Double economic,int port) {
		return registerUser(worth, generation, economic, "", port);
	}
	/**
	 * Registers the current user with the remote server. 
	 * @see LCClient#registerUser(String salt, String hash, String userId, String userName, String pubKey, Double worth, Double generation,
			Double economic, int port)
	 */
	public Pair<String,String> registerUser(Double worth, Double generation, Double economic, String pubKey, int port) {
		try {
			return lookupHLCServer().registerUser(handler.getSalt(),handler.getHash(),userId, userName, pubKey, worth, generation, economic, port);
		} catch (RemoteException e) {
			return null;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return userId;
	}
	public boolean wipeAll()
	{
		Boolean h = wipeHLC();
		Boolean e = wipeEDC();
		return e&&h;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean wipeEDC()
	{
		try {
			return lookupEDCServer().wipeEDC();
		} catch (RemoteException e) {
			return false;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean wipeHLC()
	{
		try {
			return lookupHLCServer().wipeHLC();
		} catch (RemoteException e) {
			return false;
		}
	}
	/**
	 * Sets the {@link ElectricityGeneration} of the user invoking this method to the specified value.
	 * @see LCClient#setGeneration(String userId, ElectricityGeneration i)
	 */
	public Boolean setGeneration(ElectricityGeneration i) {
		return setGeneration(userId, i);
	}
	/**
	 * Queries the remote database for the user invoking this method.
	 * @see LCClient#queryUserExists(String userId)
	 */
	public Boolean queryUserExists() {
		return queryUserExists(userId);
	}
	/**
	 * Gets the {@link UUID} registered to the user invoking this method.
	 * @see LCClient#getRegisteredUUID(String userId)
	 */
	public String getRegisteredUUID() {
		return getRegisteredUUID(userId);
	}
	/**
	 * Sets the {@link UUID} registered to the user invoking this method.
	 * @param fromString The UUID to set the userID to.
	 */
	public void setID(UUID fromString) {
		userId = fromString.toString();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArraySet<ElectricityTicket> queryCompeting(String location, int port, ElectricityRequirement req) {
		try {
			return lookupLCServer(location,port).queryCompeting(location, port, req);
		} catch (RemoteException | NullPointerException n) {
			return null;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized TicketTuple offer(String location, int port, ElectricityTicket tktDesired, ElectricityTicket tktOffered) {
		TicketTuple ret = null;
		try {
			ret = lookupLCServer(location,port).offer(location, port, tktDesired, tktOffered);
			tktDesired.clone(ret.newTkt);
			tktOffered.clone(ret.oldTkt);
		} catch (RemoteException e) {
		}
		return ret;
	}
	/**
	 * Evaluates the utility of two time intervals. The utility is one if they are identical, and decreases the more different they are.
	 * @param start1 The start time of the first interval.
	 * @param end1 The end time of the first interval.
	 * @param start2 The start time of the second interval.
	 * @param end2 The end time of the second interval.
	 * @return A number between 0 and 1 inclusive representing the utility provided by the two timeslots.
	 */
	public static Double evalTimeGap(Date start1, Date end1, Date start2, Date end2) {
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

	/**
	 * Looks up the registry hosted at a remote client.
	 * @param name The ip address of the remote client. 
	 * @param port The port of the registry at the remote client.
	 * @return A {@link LCServerIFace} from the remote registry, or null if this was not possible.
	 */
	private LCServerIFace lookupLCServer(String name, int port)
	{
		//return (LCServerIFace)lookupServer(name,port, "LCServer");
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(name, port);
			return (LCServerIFace) registry.lookup("LCServer");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		};
		return null;
	}
	/**
	 * Looks up the registry hosted at a remote {@link HLCServer}.
	 * @return A {@link HLCServerIFace} from the remote registry, or null if this was not possible.
	 */
	private HLCServerIFace lookupHLCServer()
	{
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(hLCHost,hLCPort);
			return (HLCServerIFace) registry.lookup("HLCServer");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		};
		return null;
	}
	/**
	 * Looks up the registry hosted at a remote {@link HLCServer}.
	 * @return A {@link RegisterTransactionIFace} from the remote registry, or null if this was not possible.
	 */
	private RegisterTransactionIFace lookupTransactionServer()
	{
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(hLCHost,hLCPort);
			return (RegisterTransactionIFace) registry.lookup("HLCServer");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		};
		return null;
	}
	/**
	 * Looks up the registry hosted at a remote {@link HLCServer}.
	 * @return A {@link GlobalCapitalIFace} from the remote registry, or null if this was not possible.
	 */
	private GlobalCapitalIFace lookupCapitalServer()
	{
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(hLCHost,hLCPort);
			return (GlobalCapitalIFace) registry.lookup("HLCServer");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		};
		return null;
	}
	/**
	 * Looks up the registry hosted at a remote {@link HLCServer}.
	 * @return A {@link InstitutionIFace} from the remote registry, or null if this was not possible.
	 */
	private InstitutionIFace lookupInstitution()
	{
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(hLCHost,hLCPort);
			return (InstitutionIFace) registry.lookup("HLCServer");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		};
		return null;
	}
	/**
	 * Looks up the registry hosted at a remote {@link EDCServer}.
	 * @return A {@link EDCServerIFace} from the remote registry, or null if this was not possible.
	 */
	private EDCServerIFace lookupEDCServer()
	{
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(eDCHost,eDCPort);
			return (EDCServerIFace) registry.lookup("EDCServer");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		};
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage(String name, int port) throws RemoteException {
		String ret = "";
		ret = lookupLCServer(name, port).getMessage(name, port);
		System.out.println(ret);
		return ret;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashMap<String, Triple<String, InetSocketAddress, LogCapital>> getAddresses(){
		try {
			return lookupHLCServer().getAddresses();
		} catch (RemoteException e) {
			return null;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple extendImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		TicketTuple ret = null;
		try {
			ret = lookupHLCServer().extendImmutableTicket(tktNew, tktOld, req);
			tktNew.clone(ret.newTkt);
			tktOld.clone(ret.oldTkt);
		} catch (RemoteException e) {
		}
		return ret;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple intensifyMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		TicketTuple ret = null;
		try {
			ret = lookupHLCServer().intensifyMutableTicket(tktNew, tktOld, req);
			tktNew.clone(ret.newTkt);
			tktOld.clone(ret.oldTkt);
		} catch (RemoteException e) {
		}
		return ret;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple intensifyImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		TicketTuple ret = null;
		try {
			ret = lookupHLCServer().intensifyImmutableTicket(tktNew, tktOld, req);
			tktNew.clone(ret.newTkt);
			tktOld.clone(ret.oldTkt);
		} catch (RemoteException e) {
		}
		return ret;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple extendMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		TicketTuple ret = null;
		try {
			ret = lookupHLCServer().extendMutableTicket(tktNew, tktOld, req);
			tktNew.clone(ret.newTkt);
			tktOld.clone(ret.oldTkt);
		} catch (RemoteException e) {
		}
		return ret;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair<String,String> registerUser(String salt, String hash, String userId, String userName, String pubKey, Double worth, Double generation,
			Double economic, int port) {
		try {
			return lookupHLCServer().registerUser(salt, hash, userId, userName, pubKey, worth, generation, economic, port);
		} catch (RemoteException e) {
			return null;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean setGeneration(String userId, ElectricityGeneration i) {
		try {
			return lookupHLCServer().setGeneration(userId, i);
		} catch (RemoteException e) {
			return false;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean queryUserExists(String userId) {
		try {
			return lookupHLCServer().queryUserExists(userId);
		} catch (RemoteException e) {
			return false;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRegisteredUUID(String userId) {
		try {
			return lookupHLCServer().getRegisteredUUID(userId);
		} catch (RemoteException e) {
			return null;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean registerClient(String location, int port, int ownPort, String userId, String ipAddr, String pubKey) {
		try {
			return lookupLCServer(location, port).registerClient(location, port, ownPort, userId, ipAddr, pubKey);
		} catch (RemoteException e) {
			return false;
		}
	}
	
	/**
	 * Registers the client invoking this with the designated LCServer, adding it to the log of users. This is mandatory for further negotiations to take place.
	 * Assumes the ip address can be given simply as "localhost".
	 * @see LCClient#registerClient(String location, int port, int ownPort, String userId, String ipAddr, String pubKey)
	 * @return Success?
	 */
	public Boolean registerClient(String location, int port, int ownPort, String pubKey)
	{
		return registerClient(location,port,ownPort,userId,"localhost", pubKey);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElectronicDevice getDevice(String deviceID) {
		try {
			return lookupEDCServer().getDevice(deviceID);
		} catch (RemoteException e) {
			return null;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple offer(String location, int port, TicketTuple tuple) throws RemoteException {
		return offer(location, port, tuple.newTkt, tuple.oldTkt);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPublicKey() throws RemoteException {
		try {
			return lookupHLCServer().getPublicKey();
		} catch (RemoteException e) {
			return null;
		}
	}
	/**
	 * Instantiates a new LCClient.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		if (args.length != 6) {
			System.err.println("Usage: java LContNode <host name> <port number> <host name> <port number> <username> <password>");
			System.exit(1);
		}
		@SuppressWarnings("unused")
		LCClient client = new LCClient(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), args[4],args[5]); // TODO

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean setCapital(String userId, Double value) throws RemoteException {
		return lookupCapitalServer().setCapital(userId, value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getCapital(String userId) throws RemoteException {
		return lookupCapitalServer().getCapital(userId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean checkUser(String userID, String institutionName) throws RemoteException {
		return lookupInstitution().checkUser(userID, institutionName);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean addUser(String userID, String institutionName) throws RemoteException {
		return lookupInstitution().addUser(userID, institutionName);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean removeUser(String userID, String institutionName) throws RemoteException {
		return lookupInstitution().removeUser(userID, institutionName);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean registerTicketTransaction(LogTicketTransaction log) throws RemoteException {
		return lookupTransactionServer().registerTicketTransaction(log);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean printTicketTransactions() throws RemoteException {
		return lookupTransactionServer().printTicketTransactions();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean printCapital() throws RemoteException {
		return lookupCapitalServer().printCapital();
	}
}
