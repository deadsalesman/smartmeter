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
import java.util.HashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import uk.ac.imperial.smartmeter.crypto.KeyPairGen;
import uk.ac.imperial.smartmeter.crypto.SignatureHelper;
import uk.ac.imperial.smartmeter.impl.HLCHandler;
import uk.ac.imperial.smartmeter.interfaces.HLCServerIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.TicketTuple;
import uk.ac.imperial.smartmeter.res.Twople;
import uk.ac.imperial.smartmeter.res.UserAgent;
/**
 * Class that handles High Level control of the network, registering users and allocating tickets.
 * It responds to queries from clients and listens on a given port.
 * @author bwindo
 *
 */
public class HLCServer implements HLCServerIFace{
	private int portNum;
	private HLCHandler handler;
	private String pubKey;
	private String privKey;
	private String passWd;
	private String id;
	private HashMap<String, Twople<String,InetSocketAddress>> clients;
	private InetAddress tempAddress;
	/**
	 * Creates a new HLCServer and initialises security settings. Exports RMI facilities and listens on a given port.
	 * @param parseInt The port RMI listens on.
	 */
	public HLCServer(int parseInt) {
		portNum = parseInt;
		handler = new HLCHandler();
		clients = new HashMap<String, Twople<String, InetSocketAddress>>();
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
			Twople<String, String> x = KeyPairGen.genKeySet(id, passWd);
			System.out.println("id/pass:"+id+" "+passWd);
			pubKey = x.right;
			privKey = x.left;
			handler.setCredentials(passWd, privKey, pubKey);
			HLCServerIFace stub = (HLCServerIFace) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry(portNum);
			registry.rebind("HLCServer", stub);
			SignatureHelper.printPubKey(id, pubKey);
			SignatureHelper.printSecKey(id, privKey);
			System.out.println(pubKey);
		}catch (RemoteException e)
		{
			System.out.println(e.getMessage());
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashMap<String, Twople<String, InetSocketAddress>> getAddresses(){
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
		clients = new HashMap<String, Twople<String, InetSocketAddress>>();
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
	public Twople<String,String> registerUser(String salt, String hash, String userId, String userName, String foreignPubKey, Double worth, Double generation,
			Double economic, int port) {
		clients.put(userId, new Twople<String, InetSocketAddress>(pubKey,new InetSocketAddress(tempAddress,port)));
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
		return new Twople<String, String>(id,pubKey);
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
}
