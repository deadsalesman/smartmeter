package uk.ac.imperial.smartmeter.impl;

import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
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
	
	/*public UserDBManager dbUser;
	public ReqsDBManager dbReq;
	public AgentDBManager dbAgt;*/
	
	public HLController()
	{
		
	    agents = new ArraySet<UserAgent>();
		alloc = new TicketAllocator(null, new Date(), true);
		id = UUID.randomUUID();
		/*
		dbUser = new UserDBManager ("jdbc:sqlite:user.db");
		dbReq  = new ReqsDBManager ("jdbc:sqlite:req.db");
		dbAgt  = new AgentDBManager("jdbc:sqlite:user.db");
		pullAgtFromDB();
		pullUsrFromDB();
		pullReqsFromDB();*/
	}

	public Boolean addRequirement(ElectricityRequirement e) {
		Boolean found = false;
		for (UserAgent u : agents) {
			if (e.getUserID().equals(u.getId())) {
				found = true;
				if (u.addReq(e)) {
					//pushReqToDB(e);
				}
			}
		}
		return found;
	}

	public Boolean addUserAgent(UserAgent u) {
		Boolean ret = agents.add(u);
		if (ret) {
			//pushAgtToDB(u);
			for(ElectricityRequirement e : u.getReqs().keySet())
			{
				//pushReqToDB(e);
			}
		}
		return ret;
	}
	public ArraySet<ElectricityTicket> getTickets(String userId)
	{
		ArraySet<ElectricityTicket> ret = new ArraySet<ElectricityTicket>();
		for (UserAgent a : agents)
		{
			if (a.getId().equals(userId))
			{

				for (ElectricityTicket t : a.getReqs().values())
				{
					ret.add(t);
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
	/*
	public void pullUsrFromDB() {
		ArrayList<User>temp_array = dbUser.extractAll();
		for (User u : temp_array)
		{
			if(userList.add(u))
			{
			updateAgentList(u);
			}
			else
			{
				userList.forceAdd(u);
			}
		}
	}
	public void pushUsrsToDB() {
		for (User u : userList)
		{
			pushUsrToDB(u);
		}
		
	}
	public void pushUsrToDB(User u) {
		
			dbUser.insertElement(u);
		
	}
	public void pullReqsFromDB() {
		ArrayList<ElectricityRequirement>temp_array = dbReq.extractAll();
		for (ElectricityRequirement e : temp_array)
		{
			reqList.add(e);
			//Add to agents: ? !! Cancer code detected. O(agent*requirements)
			for (UserAgent u : agentList.values())
			{
				if (u.getUser().getId().equals(e.getUserID()))
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
		for (ElectricityRequirement u : reqList) {
			pushReqToDB(u);
		}

	}

	public void pullAgtFromDB() {
		ArrayList<UserAgent> temp_array = dbAgt.extractAll();
		for (UserAgent u : temp_array) {
			if(userList.add(u.getUser()))
			{
			agentList.put(u.getUser(), u);
			}
		}
	}

	public void pushAgtToDB(UserAgent u) {

		dbAgt.insertElement(u);

	}

	public void pushAgtsToDB() {
		for (UserAgent u : agentList.values()) {
			pushAgtToDB(u);
		}

	}
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
}
