package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import uk.ac.imperial.smartmeter.db.ReqsDBManager;
import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.webcomms.LCClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

/**
 * Acts as a central controller for the individual client nodes, coordinating between the High Level controller and the Device Controller.
 * @author bwindo
 * @see LCHandler
 * @see LCClient
 */
public class LController {
	/**
	 * The representation of the user's electricity generation.
	 */
	private ElectricityGeneration eleGen;
	/**
	 * The manager for the local database of {@link ElectricityRequirement}
	 */
	public ReqsDBManager dbReq;
	/**
	 * The {@link UserAgent} that owns the controller.
	 */
	private UserAgent masterUser;
	/**
	 * The set of tickets that have been satisfied, but not to a sufficient quality.
	 */
	private ArrayList<ElectricityTicket> unhappyTickets;
	
	/**
	 * Creates a new controller from scratch, given a username, password, and distributive justice parameters.
	 * @param username The username to be used by the controller to represent the UserAgent.
	 * @param password The password associated with signing tickets and authenticating identity.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 */
	public LController(String username,String password,Double social, Double generation, Double economic)
	{
		dbReq  = new ReqsDBManager("jdbc:sqlite:req.db");
		masterUser = new UserAgent(UUID.randomUUID().toString(), username, password, social, generation, economic);
		pullFromDB();
	}
	/**
	 * Creates a new controller given parameters from a previously instantiated one.
	 * @param salt The salt used for this user.
	 * @param hash A hash of the password and the salt
	 * @param id The UserId for this controller.
	 * @param username The username to be used by the controller to represent the UserAgent.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 */
	public LController(String salt, String hash, String id,String username,Double social, Double generation, Double economic)
	{
		dbReq  = new ReqsDBManager("jdbc:sqlite:req.db");
		masterUser = new UserAgent(salt, hash, id, username, social, generation, economic);
		pullFromDB();
	}
	/**
	 * 
	 * @return Success?
	 */
	public Boolean registerDeviceController() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 
	 * @return all {@link ElectricityRequirement} objects in the controller.
	 */
	public ArraySet<ElectricityRequirement> getReqs()
	{
		return masterUser.getReqs();
	}
	/**
	 * Generates a new {@link ElectricityRequirement} with the properties given.
	 * @param start The start time of the requirement.
	 * @param end The end time of the requirement.
	 * @param prio The priority of the requirement.
	 * @param profileId The identity of the {@link ConsumptionProfile} used by the requirement.
	 * @param amplitude The amplitude of the {@link ConsumptionProfile} used by the requirement.
	 * @return a new {@link ElectricityRequirement} with the above properties
	 */
	public ElectricityRequirement generateRequirement(Date start, Date end, DecimalRating prio, int profileId, double amplitude)
	{
		return new ElectricityRequirement(start, end, prio, profileId, amplitude, masterUser.getId());
	}

	/**
	 * Adds an {@link ElectricityRequirement} to the controller.
	 * @param req The requirement in question.
	 * @return Success?
	 */
	public Boolean addRequirement(ElectricityRequirement req) {
		Boolean success = masterUser.addReq(req);
		if (success) {
			dbReq.insertElement(req);
			return true;
		}
		return false;
	}

	/**
	 * Updates the local database with the {@link ElectricityRequirement} objects stored in the controller data structures.
	 */
	public void pushToDB() {
		for (ElectricityRequirement r : masterUser.getReqs())
		{
			dbReq.insertElement(r);
		}
		
	}
	/**
	 * Updates internal data structures with the {@link ElectricityRequirement} objects stored in the local database.
	 */
	public void pullFromDB() {
		ArrayList<ElectricityRequirement>temp_array = dbReq.extractAll();
		for (ElectricityRequirement r : temp_array)
		{
			if (r.getUserID().equals(masterUser.getId())){
			masterUser.addReq(r);
			}
		}
		
	}
	public String getId()
	{
		return masterUser.getId();
	}
	public ElectricityGeneration getEleGen() {
		return eleGen;
	}

	public boolean setEleGen(ElectricityGeneration eleGen) {
		this.eleGen = eleGen;
		return true;
	}
	public Integer getReqCount() {
		return masterUser.getReqs().getSize();
	}
	public String getSalt() {
		return masterUser.getSalt();
	}
	public String getHash() {
		return masterUser.getHash();
	}
	
	/**
	 * Finds the electricity tickets that are active at the same time as a given {@link ElectricityRequirement}.
	 * @param req The requirement in question.
	 * @return An {@link ArraySet} of the tickets that conflict with the given requirement.
	 */
	public ArraySet<ElectricityTicket> findCompetingTickets(ElectricityRequirement req) {
		ArraySet<ElectricityTicket> ret = new ArraySet<ElectricityTicket>();
		for (ElectricityTicket t : masterUser.getReqTktMap().values()){
			try{
			if (t.getStart().compareTo(req.getStartTime())<0)
			{
				if (t.getEnd().compareTo(req.getStartTime())>=0)
				{
					ret.add(t);
				}
			}
			else
			{
				if (t.getStart().compareTo(req.getEndTime())<=0)
				{
					ret.add(t);
				}
			}
			}
			catch(NullPointerException n){}
		}
		
		return ret;
	}
	/**
	 * Gets the  {@link ElectricityTicket} object associated with the controller that is allocated to a given {@link ElectricityRequirement}.
	 * @param req The given {@link ElectricityRequirement}.
	 * @return The {@link ElectricityTicket} that matches the given requirement.
	 */
	public ElectricityTicket findMatchingTicket(ElectricityRequirement req) {
		if (masterUser.getReqTktMap().containsKey(req))
		{
			return masterUser.getReqTktMap().get(req);
		}
		else return null;
	}
	/**
	 * Adds a {@link ElectricityTicket} to the controller iff there is an {@link ElectricityRequirement} that it is satisfying.
	 * @param t The ticket to be added.
	 * @return Success?
	 */
	public boolean setTicket(ElectricityTicket t) {
		for (ElectricityRequirement r : masterUser.getReqs())
		{
			if (r.getId().equals(t.reqID.toString()))
			{
				if (masterUser.getReqTktMap().get(r)==null){
				masterUser.getReqTktMap().put(r, t);
				return true;
				}
			}
		}
		return false;
	}
	/**
	 * 
	 * @return all {@link ElectricityTicket} objects in the controller, which are being used to satisfy a {@link ElectricityRequirement}.
	 */
	public ArraySet<ElectricityTicket> getTkts() {
		ArraySet<ElectricityTicket> x = new ArraySet<ElectricityTicket>();
		for (ElectricityTicket t : masterUser.getReqTktMap().values())
		{
			if (t!=null){x.add(t);}
		}
			return x;
	}
	/**
	 * Adds a {@link ElectricityTicket} to the controller iff there is an {@link ElectricityRequirement} that it is satisfying.
	 * @param t The ticket to be added.
	 * @return Success?
	 */
	public boolean forceNewTicket(ElectricityTicket t) {
		for (ElectricityRequirement r : masterUser.getReqs())
		{
			if (r.getId().equals(t.reqID.toString()))
			{
				if ((r.getMaxConsumption()<= t.magnitude)&&(r.getDuration()<=t.getDuration()))
				{
					masterUser.getReqTktMap().put(r, t);
					return true;
				}
				else{return false;}
			}
		}
		return false;
	}
	/**
	 * Gets the  {@link ElectricityRequirement} object associated with the controller that has a given {@link ElectricityTicket} allocated to it.
	 * @param tkt The given {@link ElectricityTicket}.
	 * @return The {@link ElectricityRequirement} that matches the given requirement.
	 */
	public ElectricityRequirement findMatchingRequirement(ElectricityTicket tkt) {
		for (Entry<ElectricityRequirement, ElectricityTicket> x : masterUser.getReqTktMap().entrySet()) {
			try{
			if (tkt.id.toString().equals(x.getValue().id.toString())) {
				return x.getKey();
			}
			}
			catch(NullPointerException e)
			{
				
			}
		}
		return null;
	}
	/**
	 * Queries to see if it has any {@link ElectricityRequirement}s which do not have an associated {@link ElectricityTicket} capable of satisfying their requirements.
	 * @return true iff there are no unsatisfied requirements
	 */
	public synchronized Boolean queryUnsatisfiedReqs() {
		Boolean ret = true;
		try{
		for (ElectricityTicket e : masterUser.getReqTktMap().values())
		{
			ret &= (e!=null);
		}
		}
		catch(ConcurrentModificationException e)
		{
			ret = false;
		}
		return ret;
	}
	/**
	 * 
	 * @return an ArrayList containing all {@link ElectricityTicket} objects that have been marked as not having sufficient utility for the {@link ElectricityRequirement} associated to be fully satisfied.
	 */
	public synchronized ArrayList<ElectricityTicket> getUnhappyTickets() {
		return unhappyTickets;
	}
	/**
	 * Queries whether there are any {@link ElectricityTicket} objects that do not have sufficient utility for the {@link ElectricityRequirement} associated to be fully satisfied.
	 * @return true if there any requirements that are not being sufficiently satisfied.
	 */
	public boolean queryUnhappyTickets() {
		unhappyTickets = new ArrayList<ElectricityTicket>();
		double threshold = 1.1;//0.95; //TODO: set a reasonable value. current is for debug, would normally be lower obviously
		for (Entry<ElectricityRequirement, ElectricityTicket> e : masterUser.getReqTktMap().entrySet())
		{
			if (e.getValue()!=null){
			if (LCServer.calcUtilityNoExtension(e.getValue(),e.getKey()) < threshold)
			{
				unhappyTickets.add(e.getValue());
			}
			}
		}
		return unhappyTickets.size()!=0;
	}
	
	/**
	 * 
	 * @return the map of requirements to tickets.
	 */
	public Map<ElectricityRequirement, ElectricityTicket> getReqTktMap()
	{
		return this.masterUser.getReqTktMap();
	}
}
