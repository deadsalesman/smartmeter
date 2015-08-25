package uk.ac.imperial.smartmeter.webcomms;

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
import uk.ac.imperial.smartmeter.crypto.PGPKeyGen;
import uk.ac.imperial.smartmeter.impl.HLCHandler;
import uk.ac.imperial.smartmeter.interfaces.HLCServerIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.TicketTuple;
import uk.ac.imperial.smartmeter.res.Twople;
import uk.ac.imperial.smartmeter.res.UserAgent;

public class HLCServer implements HLCServerIFace{
	private int portNum;
	private HLCHandler handler;
	private String pubKey;
	private String privKey;
	private String passWd;
	private HashMap<String, Twople<String,InetSocketAddress>> clients;
	private InetAddress tempAddress;
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
			Twople<String, String> x = KeyPairGen.genKeySet(handler.getId(), passWd);
			pubKey = x.right;
			privKey = x.left;
			handler.setCredentials(passWd, privKey, pubKey);
			HLCServerIFace stub = (HLCServerIFace) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry(portNum);
			registry.rebind("HLCServer", stub);
			PGPKeyGen.printPubKey(handler.getId(), pubKey);
			PGPKeyGen.printSecKey(handler.getId(), privKey);
		}catch (RemoteException e)
		{
			System.out.println(e.getMessage());
		}
	}
	@Override
	public HashMap<String, Twople<String, InetSocketAddress>> getAddresses(){
		return clients;
	}
	@Override
	public TicketTuple extendMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		Boolean success = handler.extendTicket(tktNew, req, tktOld, true);
		return new TicketTuple(tktNew, tktOld, success);
	}

	@Override
	public TicketTuple extendImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		Boolean success = handler.extendTicket(tktNew, req, tktOld, false);

		return new TicketTuple(tktNew, tktOld, success);
	}

	@Override
	public TicketTuple intensifyMutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		Boolean success = handler.intensifyTicket(tktNew, req, tktOld, true);

		return new TicketTuple(tktNew, tktOld, success);
	}

	@Override
	public TicketTuple intensifyImmutableTicket(ElectricityTicket tktNew, ElectricityTicket tktOld, ElectricityRequirement req) {
		Boolean success = handler.intensifyTicket(tktNew, req, tktOld, false);
		return new TicketTuple(tktNew, tktOld, success);
	}
	@Override
	public String getRegisteredUUID(String userId) {
		return handler.getUUID(userId);
	}
	@Override
	public Boolean queryUserExists(String userId) {
		return handler.queryUserExistence(userId);
	}
	@Override
	public Boolean setGeneration(String userId, ElectricityGeneration i) {
		return handler.setUserGeneration(userId, i);
	}
	@Override
	public Boolean wipeHLC() {
		clients = new HashMap<String, Twople<String, InetSocketAddress>>();
		return handler.clearAll();
	}
	@Override
	public Boolean setRequirement(ElectricityRequirement req) {
		return handler.setRequirement(req);
	}
	@Override
	public Boolean GodModeCalcTKTS() {
		return handler.calculateTickets();
	}
	@Override
	public ArraySet<ElectricityTicket> getTickets(String user) {
		return handler.getTickets(user);
	}
	@Override
	public Twople<String,String> registerUser(String salt, String hash, String userId, String userName, String pubKey, Double worth, Double generation,
			Double economic, int port) {
		clients.put(userId, new Twople<String, InetSocketAddress>(pubKey,new InetSocketAddress(tempAddress,port)));
		handler.addUserAgent(new UserAgent(
						salt,
						hash,
						userId,
						userName,
						pubKey,
						worth,
						generation,
						economic
						));
		return new Twople<String, String>(handler.getId(),pubKey);
	}
	@Override
	public String getPublicKey() throws RemoteException {
		return pubKey;
	}
	 
}
