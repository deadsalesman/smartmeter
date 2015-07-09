package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.RescherArbiter;
import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.db.AgentDBManager;
import uk.ac.imperial.smartmeter.db.ReqsDBManager;
import uk.ac.imperial.smartmeter.db.UserDBManager;
import uk.ac.imperial.smartmeter.interfaces.HighLevelControllerIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.User;

public class HighLevelController implements HighLevelControllerIFace, UniqueIdentifierIFace{

	private ArraySet<User> userList;
	private Map<User,UserAgent> agentList;
	private ArraySet<ElectricityRequirement> reqList;
	//public RescherArbiter arbiter;
	private UUID id;
	
	public UserDBManager dbUser;
	public ReqsDBManager dbReq;
	public AgentDBManager dbAgt;
	
	public HighLevelController()
	{
		dbUser = new UserDBManager ("jdbc:sqlite:user.db");
		dbReq  = new ReqsDBManager ("jdbc:sqlite:req.db");
		dbAgt  = new AgentDBManager("jdbc:sqlite:user.db");
		userList = new ArraySet<User>();
		agentList = new HashMap<User,UserAgent>();
		reqList = new ArraySet<ElectricityRequirement>();
		//arbiter = new RescherArbiter();
		id = UUID.randomUUID();
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
    public ArraySet<UserAgent> generateAgentSet()
    {
    	ArraySet<UserAgent> ret = new ArraySet<UserAgent>();
    	for (UserAgent u : agentList.values())
    	{
    		ret.add(u);
    	}
    	return ret;
    }
	public ArraySet<ElectricityTicket> allocateResources()
	{
		
		return null;
		
	}
	
	public User getUser(int index)
	{
		return userList.get(index);
	}
	public void updateAgentList(User u)
	{
		agentList.put(u, new UserAgent(u));
	}
	public void addUserAgent(UserAgent u)
	{
		agentList.put(u.getUser(), u);
		userList.add(u.getUser());
	}
	public void addUser(User u)
	{
		userList.add(u);
		updateAgentList(u);
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
			userList.add(u);
			updateAgentList(u);
		}
	}
	public void pushUsrToDB() {
		for (User u : userList)
		{
			dbUser.insertElement(u);
		}
		
	}
	public void pullAgtFromDB() {
		ArrayList<UserAgent>temp_array = dbAgt.extractAll();
		for (UserAgent u : temp_array)
		{
			userList.add(u.getUser());
			agentList.put(u.getUser(),u);
		}
	}
	public void pushAgtToDB() {
		for (UserAgent u : agentList.values())
		{
			dbAgt.insertElement(u);
		}
		
	}
}
