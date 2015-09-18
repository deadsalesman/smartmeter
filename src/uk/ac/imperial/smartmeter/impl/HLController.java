package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.db.AgentDBManager;
import uk.ac.imperial.smartmeter.db.ReqsDBManager;
import uk.ac.imperial.smartmeter.interfaces.HighLevelControllerIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;

/**
 * Acts as a controller for the overall system, registering users and allocating tickets.
 * @author bwindo
 * @see EDCHandler
 */
public class HLController implements HighLevelControllerIFace, UniqueIdentifierIFace{

	/**
	 * A collection of all the {@link UserAgent} objects that have registered with the controller.
	 */
	private ArraySet<UserAgent> agents;
	/**
	 * The allocator used to distribute {@link ElectricityTicket}s out to the individual {@link ElectricityRequirement}s.
	 */
	public TicketAllocator alloc;
	private UUID id;
	/**
	 * The manager for the local database of {@link ElectricityRequirement}
	 */
	public ReqsDBManager dbReq;
	/**
	 * The manager for the local database of {@link UserAgent}
	 */
	public AgentDBManager dbAgt;
	private String pass = "";
	private String priv = "";
	private String pub = "";
	
	public HLController()
	{
	    agents = new ArraySet<UserAgent>();
		id = UUID.randomUUID();

		dbReq  = new ReqsDBManager ("jdbc:sqlite:req.db");
		dbAgt  = new AgentDBManager("jdbc:sqlite:user.db");
		pullAgtFromDB();
		pullReqsFromDB();
	}
	/**
	 * Attempts to extend a ticket if the ticket duration is less than the requirement duration
	 * @param oldReq
	 * @param newTkt
	 * @param oldTkt
	 * @param mutable Defines whether the underlying nodes can be changed, or whether copies should be made.
	 * @return success?
	 */
	public Boolean extendTicket(ElectricityRequirement oldReq, ElectricityTicket newTkt, ElectricityTicket oldTkt, boolean mutable)
	{
	return alloc.extendTicket(newTkt, oldReq, oldTkt,findMatchingRequirement(newTkt),mutable);
		
	}
	/**
	 * Attempts to intensify a ticket if the ticket amplitude is less than the requirement amplitude
	 * @param newTkt
	 * @param oldReq
	 * @param oldTkt
	 * @param mutable Defines whether the underlying nodes can be changed, or whether copies should be made.
	 * @return success?
	 */
	public Boolean intensifyTicket(ElectricityRequirement oldReq, ElectricityTicket newTkt, ElectricityTicket oldTkt, boolean mutable) {
		return alloc.intensifyTicket(newTkt, oldReq, oldTkt,findMatchingRequirement(newTkt), mutable);
	}
	/**
	 * Adds an {@link ElectricityRequirement} to the user whose id matches that of its owner.
	 * @param e The requirement in question.
	 * @return true iff there is a user whose id matches and the addition of the requirement was successful.
	 */
	public Boolean addRequirement(ElectricityRequirement e) {
		Boolean found = false;
		for (UserAgent u : agents) {
			if (e.getUserID().equals(u.getId())) {
				found = true;
				if (u.addReq(e)) {
					pushReqToDB(e);
				}
			}
		}
		return found;
	}
	/**
	 * Adds a {@link UserAgent} to {@link HLController#agents}
	 * @param u The {@link UserAgent} to be added.
	 * @return Success?
	 */
	public Boolean addUserAgent(UserAgent u) {
		Boolean ret = agents.add(u);
		if (ret) {
			pushAgtToDB(u);
			for(ElectricityRequirement e : u.getReqs())
			{
				pushReqToDB(e);
			}
		}
		return ret;
	}
	/**
	 * Removes all information from local data structures, including fields and databases.
	 * @return Success?
	 */
	public Boolean clearAll()
	{
			agents = new ArraySet<UserAgent> ();
			alloc = new TicketAllocator(agents,new Date(), true, id.toString(), pass);
			return (dbReq.wipe() && dbAgt.wipe());
	}
	/**
	 * Attempts to calculate tickets for the currently entered user agents
	 * @return the user agents with their tickets added
	 */
	public Boolean calculateTickets()
	{
		alloc = new TicketAllocator(agents,new Date(), true, id.toString(), pass);
		alloc.calculateTickets();
		return true;
	}
	/**
	 * Returns an ArraySet of all the {@link ElectricityTicket}s associated with a given user, identified by the String representation of the {@link UserAgent}'s {@link UUID}.
	 * @param userId The id of the user in question.
	 * @return An ArraySet of the tickets of interest.
	 */
	public ArraySet<ElectricityTicket> getTickets(String userId)
	{
		ArraySet<ElectricityTicket> ret = new ArraySet<ElectricityTicket>();
		for (UserAgent a : agents)
		{
			if (a.getId().equals(userId))
			{

				for (ElectricityTicket t : a.getReqTktMap().values())
				{
					if (t!=null)
					{
					ret.add(t);
					}
				}
			}
		}
		return ret;
	}
	@Override
	public String getId() {
		return id.toString();
	}
	public int getAgtCount()
	{
		return agents.size();
	}
	
	/**
	 * Updates internal data structures with the {@link ElectricityRequirement} objects stored in the local database.
	 */
	public void pullReqsFromDB() {
		ArrayList<ElectricityRequirement>temp_array = dbReq.extractAll();
		for (ElectricityRequirement e : temp_array)
		{
			//Add to agents: ? !! Cancer code detected. O(agent*requirements)
			for (UserAgent u : agents)
			{
				if (u.getId().equals(e.getUserID()))
				{
					u.addReq(e);
				}
			}
		}
	}
	/**
	 * Updates the local database with an {@link ElectricityRequirement} object.
	 */
	public void pushReqToDB(ElectricityRequirement req) {

		dbReq.insertElement(req);

	}
	/**
	 * Updates the local database with the {@link ElectricityRequirement} objects stored in the controller data structures.
	 */
	public void pushReqsToDB() {
		for (UserAgent u : agents)
		{
				for (ElectricityRequirement r : u.getReqs())
				{
					pushReqToDB(r);
				}
			}
	}
	/**
	 * Updates internal data structures with the {@link UserAgent} objects stored in the local database.
	 */
	public void pullAgtFromDB() {
		ArrayList<UserAgent> temp_array = dbAgt.extractAll();
		for (UserAgent u : temp_array) {
			Boolean inStorage = false;
			for (UserAgent a : agents)
			{
				if (a.getId().equals(u.getId())){
					inStorage = true;
					break;
				}
			}
			if (!inStorage)
			{
			agents.add(u);
			}
		}
	}
	
	/**
	 * Updates the local database with an {@link UserAgent} object.
	 */
	public void pushAgtToDB(UserAgent u) {

		dbAgt.insertElement(u);

	}
	/**
	 * Updates the local database with the {@link UserAgent} objects stored in the controller data structures.
	 */
	public void pushAgtsToDB() {
		for (UserAgent u : agents) {
			pushAgtToDB(u);
		}

	}
	/**
	 * Sets the electricity generation of the specified {@link UserAgent} to the specified {@link ElectricityGeneration}
	 * @param userId The representation of the {@link UUID} of the user in question.
	 * @param e The {@link ElectricityGeneration} to be adopted by the user.
	 * @return Success?
	 */
	public Boolean setUserGeneration(String userId, ElectricityGeneration e) {
		for (UserAgent u: agents)
		{
			if(u.getId().equals(userId))
			{
				u.setGeneratedPower(e);
				return true;
			}
		}
		return false;
	}
	/**
	 * Queries the database for a given user.
	 * @param userId The representation of the {@link UUID} of the user in question.
	 * @return true if the user exists in {@link HLController#agents}.
	 */
	public Boolean queryUserExistence(String userId) {
		Boolean ret = false;
		for (UserAgent u : agents)
		{
			if (u.getName().equals(userId))
			{
				ret = true;
				break;
			}
		}

		return ret;
	}
	/**
	 * Returns the String form of the {@link UUID} associated with a given user name.
	 * @param userName The username in question.
	 * @return the UUID associated with the given username.
	 */
	public String getUUID(String userName) {
		for (UserAgent u : agents)
		{
			if (u.getName().equals(userName))
			{
				return u.getId();
			}
		}
        return null;
	}
	/**
	 * Returns the {@link ElectricityRequirement} which matches a given {@link ElectricityTicket}. This may be owned by any user in {@link HLController#agents}.
	 * @param tkt The {@link ElectricityTicket} in question.
	 * @return the matching {@link ElectricityRequirement} if it exists, else null.
	 */
	public ElectricityRequirement findMatchingRequirement(ElectricityTicket tkt) {
		try{
		for (UserAgent u:agents)
		{
		for (Entry<ElectricityRequirement, ElectricityTicket> x : u.getReqTktMap().entrySet()) {
			if (tkt.id.toString().equals(x.getValue().id.toString())) {
				return x.getKey();
			}
		}
		}
		}
		catch (NullPointerException e)
		{
			
		}
		return null;
	}
	/**
	 * Sets the essential credentials of the server, used to sign tickets and prove identity.
	 * @param passWd The password used to access the server's private key.
	 * @param privKey The secret key of the server.
	 * @param pubKey The public key of the server.
	 */
	public void setCredentials(String passWd, String privKey, String pubKey) {
		pass = passWd;
		priv = privKey;
		pub = pubKey;
	}
}
