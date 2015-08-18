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
import uk.ac.imperial.smartmeter.impl.LCHandler;
import uk.ac.imperial.smartmeter.interfaces.EDCServerIFace;
import uk.ac.imperial.smartmeter.interfaces.HLCServerIFace;
import uk.ac.imperial.smartmeter.interfaces.LCServerIFace;
import uk.ac.imperial.smartmeter.interfaces.ServerIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.ElectronicDevice;

public class LCClient implements LCServerIFace, HLCServerIFace, EDCServerIFace {
	private String eDCHost;
	private int eDCPort;
	private String hLCHost;
	private int hLCPort;
	public LCHandler handler;
	private String userId; 
	private String userName;
	
	public LCClient(String eDCHostName, int eDCPortNum, String hLCHostName,int hLCPortNum, String name,String password) {
		eDCHost = eDCHostName;
		eDCPort = eDCPortNum;
		hLCHost = hLCHostName;
		hLCPort = hLCPortNum;
		userName = name;
		handler = new LCHandler(name,password,0.,0.,0.); //TODO: make not naughty. 
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
		return addDevice(new ElectronicDevice(state,type,deviceID),pin);
	}
	@Override
	public Boolean addDevice(ElectronicDevice ed, Integer pin)
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
			return lookupHLCServer().setRequirement(req);
		} catch (RemoteException e) {
			return false;
		}
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
	public Boolean registerUser(Double worth, Double generation, Double economic, int port) {
		try {
			return lookupHLCServer().registerUser(handler.getSalt(),handler.getHash(),userId, userName, worth, generation, economic, port);
		} catch (RemoteException e) {
			return false;
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
		} catch (RemoteException e) {
			return null;
		}
	}
	@Override
	public Boolean offer(String location, int port, ElectricityTicket tktOld, ElectricityTicket tktNew) {
		try {
			return lookupLCServer(location,port).offer(location, port, tktOld, tktNew);
		} catch (RemoteException e) {
			return false;
		}
	}
	public Boolean registerClient(String location, int port, int ownPort)
	{
		return registerClient(location,port,ownPort,userId,"localhost");
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
		return (LCServerIFace)lookupServer(name,port, "LCServer");
	}
	private HLCServerIFace lookupHLCServer()
	{
		return (HLCServerIFace)lookupServer(hLCHost,hLCPort, "HLCServer");
	}
	private EDCServerIFace lookupEDCServer()
	{
		return (EDCServerIFace)lookupServer(eDCHost,eDCPort, "EDCServer");
	}
	@Override
	public String getMessage(String name, int port) throws RemoteException {
		String ret = "";
		ret = lookupLCServer(name, port).getMessage(name, port);
		System.out.println(ret);
		return ret;
	}
	@Override
	public HashMap<String, InetSocketAddress> getAddresses(){
		try {
			return lookupHLCServer().getAddresses();
		} catch (RemoteException e) {
			return null;
		}
	}
	@Override
	public Boolean extendImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		try {
			return lookupHLCServer().extendImmutableTicket(tktNew, tktOld, req);
		} catch (RemoteException e) {
			return false;
		}
	}
	@Override
	public Boolean intensifyMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		try {
			return lookupHLCServer().intensifyMutableTicket(tktNew, tktOld, req);
		} catch (RemoteException e) {
			return false;
		}
	}
	@Override
	public Boolean intensifyImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		try {
			return lookupHLCServer().intensifyImmutableTicket(tktNew, tktOld, req);
		} catch (RemoteException e) {
			return false;
		}
	}
	@Override
	public Boolean extendMutableTicket(ElectricityTicket tktNew, ElectricityRequirement req, ElectricityTicket tktOld) {
		try {
			return lookupHLCServer().extendMutableTicket(tktNew, req, tktOld);
		} catch (RemoteException e) {
			return false;
		}
	}
	@Override
	public Boolean registerUser(String salt, String hash, String userId, String userName, Double worth, Double generation,
			Double economic, int port) {
		try {
			return lookupHLCServer().registerUser(salt, hash, userId, userName, worth, generation, economic, port);
		} catch (RemoteException e) {
			return false;
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
	public Boolean registerClient(String location, int port, int ownPort, String userId, String ipAddr) {
		try {
			return lookupLCServer(location, ownPort).registerClient(location, port, ownPort, userId, ipAddr);
		} catch (RemoteException e) {
			return false;
		}
	}
	@Override
	public ElectronicDevice getDevice(String deviceID) {
		try {
			return lookupEDCServer().getDevice(deviceID);
		} catch (RemoteException e) {
			return null;
		}
	}
}
