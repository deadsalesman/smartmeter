package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import uk.ac.imperial.smartmeter.crypto.KeyPairGen;
import uk.ac.imperial.smartmeter.crypto.SignatureHelper;
import uk.ac.imperial.smartmeter.impl.HLCHandler;
import uk.ac.imperial.smartmeter.institutions.GenericInstitution;
import uk.ac.imperial.smartmeter.institutions.ServerCapitalIFace;
import uk.ac.imperial.smartmeter.log.CapitalLogToCSV;
import uk.ac.imperial.smartmeter.log.LogCapital;
import uk.ac.imperial.smartmeter.log.LogTicketTransaction;
import uk.ac.imperial.smartmeter.log.TicketLogToCSV;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Pair;
import uk.ac.imperial.smartmeter.res.TicketTuple;
import uk.ac.imperial.smartmeter.res.Triple;
import uk.ac.imperial.smartmeter.res.UserAgent;
/**
 * Class that handles High Level control of the network, registering users and allocating tickets.
 * It responds to queries from clients and listens on a given port.
 * @author bwindo
 *
 */
public class HLCServer implements ServerCapitalIFace, Runnable {
	private int portNum;
	private HLCHandler handler;
	private String pubKey;
	private String privKey;
	private String passWd;
	private String id;
	private HashMap<String, Triple<String,InetSocketAddress, LogCapital>> clients;
	private HashMap<String, GenericInstitution> institutions;
	private InetAddress tempAddress;
	private ArrayList<LogTicketTransaction> transactionLog;
	private boolean active = true;
	
	/**
	 * Creates a new HLCServer and initialises security settings. Exports RMI facilities and listens on a given port.
	 * @param parseInt The port RMI listens on.
	 */
	public HLCServer(int parseInt) {
		portNum = parseInt;
		handler = new HLCHandler();
		clients = new HashMap<String, Triple<String,InetSocketAddress, LogCapital>>();
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
			Security.addProvider(new BouncyCastleProvider());
			passWd = "itsa me, the hlc";
			id =  handler.getId();
			Pair<String, String> x = KeyPairGen.genKeySet(id, passWd);
			//System.out.println("id/pass:"+id+" "+passWd);
			pubKey = x.right;
			privKey = x.left;
			handler.setCredentials(passWd, privKey, pubKey);
			ServerCapitalIFace stub = (ServerCapitalIFace) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry(portNum);
			registry.rebind("HLCServer", stub);
			SignatureHelper.printPubKey(id, pubKey);
			SignatureHelper.printSecKey(id, privKey);
			transactionLog = new ArrayList<LogTicketTransaction>();
			//System.out.println(pubKey);
		}catch (RemoteException e)
		{
			System.out.println(e.getMessage());
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashMap<String, Triple<String, InetSocketAddress, LogCapital>> getAddresses(){
		return clients;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple extendMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		Boolean success = handler.extendTicket(tktNew, req, tktOld, true);
		return new TicketTuple(tktNew, tktOld, success);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple extendImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		Boolean success = handler.extendTicket(tktNew, req, tktOld, false);

		return new TicketTuple(tktNew, tktOld, success);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple intensifyMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		Boolean success = handler.intensifyTicket(tktNew, req, tktOld, true);

		return new TicketTuple(tktNew, tktOld, success);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TicketTuple intensifyImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		Boolean success = handler.intensifyTicket(tktNew, req, tktOld, false);
		return new TicketTuple(tktNew, tktOld, success);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRegisteredUUID(String userId) {
		return handler.getUUID(userId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean queryUserExists(String userId) {
		return handler.queryUserExistence(userId);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean setGeneration(String userId, ElectricityGeneration i) {
		return handler.setUserGeneration(userId, i);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean wipeHLC() {
		clients = new HashMap<String, Triple<String,InetSocketAddress, LogCapital>>();
		transactionLog = new ArrayList<LogTicketTransaction>();
		return handler.clearAll();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean setRequirement(ElectricityRequirement req) {
		return handler.setRequirement(req);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean GodModeCalcTKTS() {
		return handler.calculateTickets();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArraySet<ElectricityTicket> getTickets(String user) {
		return handler.getTickets(user);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair<String,String> registerUser(String salt, String hash, String userId, String userName, String foreignPubKey, Double worth, Double generation,
			Double economic, int port) {
		clients.put(userId, new Triple<String, InetSocketAddress, LogCapital>(pubKey,new InetSocketAddress(tempAddress,port),new LogCapital()));
		handler.addUserAgent(new UserAgent(
						salt,
						hash,
						userId,
						userName,
						foreignPubKey,
						worth,
						generation,
						economic
						));
		return new Pair<String, String>(id,pubKey);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPublicKey() throws RemoteException {
		return pubKey;
	}
	/**
	 * Generates a {@link HLCServer} instance.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java HLContNode <int port number>");
			System.exit(1);
		}

		System.setProperty("java.rmi.server.hostname", DefaultTestClient.ipAddr); 
		@SuppressWarnings("unused")
		HLCServer client = new HLCServer(Integer.parseInt(args[0]));

		System.out.println("High Level Server listening on " + args[0]);
		
		while(true)
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean setCapital(String userId, Double value) throws RemoteException {
		try {
		clients.get(userId).right.push(value);
		return true;
		}
		catch (NullPointerException e)
		{
			return false;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getCapital(String userId) throws RemoteException {
		return clients.get(userId).right.getTotal();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean checkUser(String userID, String institutionName) throws RemoteException {
		return institutions.get(institutionName).checkUser(userID, institutionName);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean addUser(String userID, String institutionName) throws RemoteException {
		return institutions.get(institutionName).addUser(userID, institutionName);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean removeUser(String userID, String institutionName) throws RemoteException {
		return institutions.get(institutionName).removeUser(userID, institutionName);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean registerTicketTransaction(LogTicketTransaction log) throws RemoteException {
		return transactionLog.add(log);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer printTicketTransactions() throws RemoteException {
		return TicketLogToCSV.writeLog(transactionLog);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean printCapital() throws RemoteException {
		ArrayList<Pair<String, LogCapital>> logs = new ArrayList<Pair<String, LogCapital>>();
		for (Entry<String, Triple<String, InetSocketAddress, LogCapital>> x : clients.entrySet())
		{
			logs.add(new Pair<String, LogCapital>(x.getKey(),x.getValue().right));
		}
		return CapitalLogToCSV.writeLog(logs);
	}
	@Override
	public void run() {
		try {
			while(active )
			{
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void stop() {
		active = false;
	}
}
