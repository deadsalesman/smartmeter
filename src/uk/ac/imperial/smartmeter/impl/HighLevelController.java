package uk.ac.imperial.smartmeter.impl;

import uk.ac.imperial.smartmeter.db.ReqsDBManager;
import uk.ac.imperial.smartmeter.db.UserDBManager;
import uk.ac.imperial.smartmeter.interfaces.HighLevelControllerIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.User;

public class HighLevelController implements HighLevelControllerIFace{

	private ArraySet<User> usrList;
	private ArraySet<ElectricityRequirement> reqList;
	
	public UserDBManager dbUser;
	public ReqsDBManager dbReq;
	
	public HighLevelController()
	{
		dbUser  = new UserDBManager("jdbc:sqlite:req.db");
		usrList = new ArraySet<User>();
		dbReq  = new ReqsDBManager("jdbc:sqlite:req.db");
		reqList = new ArraySet<ElectricityRequirement>();
	}
   
	public ArraySet<ElectricityTicket> allocateResources()
	{
		
		return null;
		
	}
}
