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

//HighLevelController
public class HLController implements HighLevelControllerIFace, UniqueIdentifierIFace{

	private ArraySet<UserAgent> agents;
	public TicketAllocator alloc;
	private UUID id;
	
	public ReqsDBManager dbReq;
	public AgentDBManager dbAgt;
	
	public HLController()
	{
		
	    agents = new ArraySet<UserAgent>();
		id = UUID.randomUUID();

		dbReq  = new ReqsDBManager ("jdbc:sqlite:req.db");
		dbAgt  = new AgentDBManager("jdbc:sqlite:user.db");
		pullAgtFromDB();
		pullReqsFromDB();
	}
	public Boolean extendTicket(ElectricityRequirement e, ElectricityTicket t, ElectricityTicket tktOld, boolean mutable)
	{
	return alloc.extendTicket(t, e, tktOld,findMatchingRequirement(t),mutable);
		
	}
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
	public Boolean clearAll()
	{
			agents = new ArraySet<UserAgent> ();
			alloc = new TicketAllocator(agents,new Date(), true);
			return (dbReq.wipe() && dbAgt.wipe());
	}
	public Boolean calculateTickets()
	{
		alloc = new TicketAllocator(agents,new Date(), true);
		alloc.calculateTickets();
		return true;
	}
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

	public void pushReqToDB(ElectricityRequirement req) {

		dbReq.insertElement(req);

	}

	public void pushReqsToDB() {
		for (UserAgent u : agents)
		{
				for (ElectricityRequirement r : u.getReqs())
				{
					pushReqToDB(r);
				}
			}
	}

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

	public void pushAgtToDB(UserAgent u) {

		dbAgt.insertElement(u);

	}

	public void pushAgtsToDB() {
		for (UserAgent u : agents) {
			pushAgtToDB(u);
		}

	}
	
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
	public ElectricityRequirement findMatchingRequirement(ElectricityTicket tkt) {
		for (UserAgent u:agents)
		{
		for (Entry<ElectricityRequirement, ElectricityTicket> x : u.getReqTktMap().entrySet()) {
			if (tkt.id.toString().equals(x.getValue().id.toString())) {
				return x.getKey();
			}
		}
		}
		return null;
	}
	public Boolean intensifyTicket(ElectricityRequirement req, ElectricityTicket tkt, ElectricityTicket tktOld) {
			return alloc.intensifyTicket(tkt, req,findMatchingRequirement(tkt), tktOld,findMatchingRequirement(tktOld));
	}
}
