package uk.ac.imperial.smartmeter.webcomms;

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
import uk.ac.imperial.smartmeter.interfaces.EDCServerIFace;
import uk.ac.imperial.smartmeter.interfaces.HLCServerIFace;
import uk.ac.imperial.smartmeter.interfaces.LCServerIFace;
import uk.ac.imperial.smartmeter.interfaces.ServerIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.TicketTuple;
import uk.ac.imperial.smartmeter.res.Twople;

/**
 * imaclass
 * @author bwindo
 *
 */
public class LCClient implements LCServerIFace, HLCServerIFace, EDCServerIFace {
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
	public boolean queryUnhappyTickets()
	{
		return handler.queryUnhappyTickets();
	}
	public ArrayList<ElectricityTicket> getUnhappyTickets()
	{
		return handler.getUnhappyTickets();
	}
	public Boolean queryUnsatisfiedReqs()
	{
		return handler.queryUnsatisfiedReqs();
	}

	public ArraySet<ElectricityTicket> findCompetingTickets(ElectricityRequirement req)
	{
		return handler.findCompetingTickets(req);
	}
	public Boolean addDevice(Boolean state, Integer type, String deviceID, Integer pin)
	{
		return addDevice(ElectronicDeviceFactory.getDevice(type,deviceID,state),pin);
	}
	@Override
	public Boolean addDevice(ElectronicConsumerDevice ed, Integer pin)
	{
		try {
			return lookupEDCServer().addDevice(ed, pin);
		} catch (RemoteException e) {
			return false;
		}
	}
	public Boolean GodModeCalcTKTS()
	{
		try {
			return lookupHLCServer().GodModeCalcTKTS();
		} catch (RemoteException e) {
			return false;
		}
	}
	public ArraySet<ElectricityTicket> getTickets()
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
	public ArraySet<ElectricityTicket> getTickets(String user)
	{
		try {
			return lookupHLCServer().getTickets(user);
		} catch (RemoteException e) {
			return null;
		}
	}
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
	public Boolean setState(String deviceID, Boolean val)
	{
		try {
			return lookupEDCServer().setState(deviceID, val);
		} catch (RemoteException e) {
			return false;
		}
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
		try {
			return lookupEDCServer().getState(deviceID);
		} catch (RemoteException e) {
			return false;
		}
	}
	public Boolean removeDevice(String deviceID)
	{
		try {
			return lookupEDCServer().removeDevice(deviceID);
		} catch (RemoteException e) {
			return false;
		}
	}
	public Twople<String,String> registerUser(Double worth, Double generation, Double economic,int port) {
		return registerUser(worth, generation, economic, "", port);
	}
		
	public Twople<String,String> registerUser(Double worth, Double generation, Double economic, String pubKey, int port) {
		try {
			return lookupHLCServer().registerUser(handler.getSalt(),handler.getHash(),userId, userName, pubKey, worth, generation, economic, port);
		} catch (RemoteException e) {
			return null;
		}
	}

	public String getId() {
		return userId;
	}
	public boolean wipeAll()
	{
		Boolean h = wipeHLC();
		Boolean e = wipeEDC();
		return e&&h;
	}
	public Boolean wipeEDC()
	{
		try {
			return lookupEDCServer().wipeEDC();
		} catch (RemoteException e) {
			return false;
		}
	}
	public Boolean wipeHLC()
	{
		try {
			return lookupHLCServer().wipeHLC();
		} catch (RemoteException e) {
			return false;
		}
	}
	public Boolean setGeneration(ElectricityGeneration i) {
		return setGeneration(userId, i);
	}

	public Boolean queryUserExists() {
		return queryUserExists(userId);
	}

	public String getRegisteredUUID() {
		return getRegisteredUUID(userId);
	}

	public void setID(UUID fromString) {
		userId = fromString.toString();
	}

	public ArraySet<ElectricityTicket> queryCompeting(String location, int port, ElectricityRequirement req) {
		try {
			return lookupLCServer(location,port).queryCompeting(location, port, req);
		} catch (RemoteException | NullPointerException n) {
			return null;
		}
	}
	@Override
	public TicketTuple offer(String location, int port, ElectricityTicket tktDesired, ElectricityTicket tktOffered) {
		TicketTuple ret = null;
		try {
			ret = lookupLCServer(location,port).offer(location, port, tktDesired, tktOffered);
			tktDesired.clone(ret.newTkt);
			tktOffered.clone(ret.oldTkt);
		} catch (RemoteException e) {
		}
		return ret;
	}
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

	private ServerIFace lookupServer(String name, int port, String registered)
	{
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(name, port);
			return (ServerIFace) registry.lookup(registered);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		};
		return null;
	}
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
	private EDCServerIFace lookupEDCServer()
	{
		//return (EDCServerIFace)lookupServer(eDCHost,eDCPort, "EDCServer");

		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(eDCHost,eDCPort);
			return (EDCServerIFace) registry.lookup("EDCServer");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		};
		return null;
	}
	@Override
	public String getMessage(String name, int port) throws RemoteException {
		String ret = "";
		ret = lookupLCServer(name, port).getMessage(name, port);
		System.out.println(ret);
		return ret;
	}
	@Override
	public HashMap<String, Twople<String, InetSocketAddress>> getAddresses(){
		try {
			return lookupHLCServer().getAddresses();
		} catch (RemoteException e) {
			return null;
		}
	}
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
	@Override
	public Twople<String,String> registerUser(String salt, String hash, String userId, String userName, String pubKey, Double worth, Double generation,
			Double economic, int port) {
		try {
			return lookupHLCServer().registerUser(salt, hash, userId, userName, pubKey, worth, generation, economic, port);
		} catch (RemoteException e) {
			return null;
		}
	}
	@Override
	public Boolean setGeneration(String userId, ElectricityGeneration i) {
		try {
			return lookupHLCServer().setGeneration(userId, i);
		} catch (RemoteException e) {
			return false;
		}
	}
	@Override
	public Boolean queryUserExists(String userId) {
		try {
			return lookupHLCServer().queryUserExists(userId);
		} catch (RemoteException e) {
			return false;
		}
	}
	@Override
	public String getRegisteredUUID(String userId) {
		try {
			return lookupHLCServer().getRegisteredUUID(userId);
		} catch (RemoteException e) {
			return null;
		}
	}
	@Override
	public Boolean registerClient(String location, int port, int ownPort, String userId, String ipAddr, String pubKey) {
		try {
			return lookupLCServer(location, port).registerClient(location, port, ownPort, userId, ipAddr, pubKey);
		} catch (RemoteException e) {
			return false;
		}
	}
	

	public Boolean registerClient(String location, int port, int ownPort, String pubKey)
	{
		return registerClient(location,port,ownPort,userId,"localhost", pubKey);
	}
	@Override
	public ElectronicDevice getDevice(String deviceID) {
		try {
			return lookupEDCServer().getDevice(deviceID);
		} catch (RemoteException e) {
			return null;
		}
	}
	@Override
	public TicketTuple offer(String location, int port, TicketTuple tuple) throws RemoteException {
		return offer(location, port, tuple.newTkt, tuple.oldTkt);
	}
	@Override
	public String getPublicKey() throws RemoteException {
		try {
			return lookupHLCServer().getPublicKey();
		} catch (RemoteException e) {
			return null;
		}
	}
}
