package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.db.AgentDBManager;
import uk.ac.imperial.smartmeter.db.ReqsDBManager;
import uk.ac.imperial.smartmeter.db.UserDBManager;
import uk.ac.imperial.smartmeter.interfaces.HighLevelControllerIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.User;

//HighLevelController
public class HLController implements HighLevelControllerIFace, UniqueIdentifierIFace{

	private ArraySet<User> userList;
	private Map<User,UserAgent> agentList;
	private ArraySet<ElectricityRequirement> reqList;
	public TicketAllocator alloc;
	private UUID id;
	
	public UserDBManager dbUser;
	public ReqsDBManager dbReq;
	public AgentDBManager dbAgt;
	
	public HLController()
	{
		dbUser = new UserDBManager ("jdbc:sqlite:user.db");
		dbReq  = new ReqsDBManager ("jdbc:sqlite:req.db");
		dbAgt  = new AgentDBManager("jdbc:sqlite:user.db");
		userList = new ArraySet<User>();
		agentList = new HashMap<User,UserAgent>();
		reqList = new ArraySet<ElectricityRequirement>();
		alloc = new TicketAllocator(agentList.values(), new Date(), true);
		id = UUID.randomUUID();
		pullAgtFromDB();
		pullUsrFromDB();
		pullReqsFromDB();
	}

	public void extractRequirements()
	{
		for (UserAgent u : agentList.values())
		{
			for (ElectricityRequirement r : u.getReqs())
			{
				reqList.add(r);
			}
		}
	}
	public Boolean addRequirement(ElectricityRequirement e)
	{
		Boolean found = false;
		User owner  = null;
		for (User u : userList)
		{
			if (e.getUserID().equals(u.getId()))
			{found = true; owner = u;}
		}
		if (found)
		{
			agentList.get(owner).addReq(e);
			reqList.add(e);
		}
		return found;
	}
	public ArraySet<ElectricityTicket> getTicket(User u)
	{
		for (User e : userList)
		{
			if (u.getId().equals(e.getId())){u=e;}
		}
		alloc = new TicketAllocator(agentList.values(), new Date(), true);
		alloc.calculateTickets();
		return alloc.getTicketsOfUser(agentList.get(u));
	}
    public ArraySet<UserAgent> generateAgentSet()
    {
    	ArraySet<UserAgent> ret = new ArraySet<UserAgent>();
    	for (UserAgent u : agentList.values())
    	{
    		ret.add(u);
    	}
    	return ret;
    }
	
	public User getUser(int index)
	{
		return userList.get(index);
	}
	public void updateAgentList(User u)
	{
		UserAgent newAgt = new UserAgent(u);
		agentList.put(u, newAgt);
		pushAgtToDB(newAgt);
	}
	public void addUserAgent(UserAgent u)
	{
		agentList.put(u.getUser(), u);
		userList.add(u.getUser());
		pushAgtToDB(u);
		pushUsrToDB(u.getUser());
		for (ElectricityRequirement req : u.getReqs())
		{
			pushReqToDB(req);
		}
	}
	public boolean addUser(User u)
	{
		boolean ret = userList.add(u);
		pushUsrToDB(u);
		updateAgentList(u);
		return ret;
	}
	@Override
	public String getId() {
		return id.toString();
	}
	public int getAgtCount()
	{
		return agentList.size();
	}
	public int getUsrCount()
	{
		return userList.size();
	}
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

	public Boolean setUserGeneration(String userId, ElectricityGeneration e) {
		for (UserAgent u: agentList.values())
		{
			if(u.getUser().getId().equals(userId))
			{
				u.setGeneratedPower(e);
				return true;
			}
		}
		return false;
	}
}
