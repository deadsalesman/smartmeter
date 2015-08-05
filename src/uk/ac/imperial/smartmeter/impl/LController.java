package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.db.ReqsDBManager;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;

//LocalController
public class LController {
	private ElectricityGeneration eleGen;
	public ReqsDBManager dbReq;
	private double maxEleConsumption;
	private UserAgent masterUser;
	
	
	public LController(String username,String password,Double social, Double generation, Double economic)
	{
		dbReq  = new ReqsDBManager("jdbc:sqlite:req.db");
		masterUser = new UserAgent(UUID.randomUUID().toString(), username, password, social, generation, economic);
		pullFromDB();
	}
	public LController(String salt, String hash, String id,String username,Double social, Double generation, Double economic)
	{
		dbReq  = new ReqsDBManager("jdbc:sqlite:req.db");
		masterUser = new UserAgent(salt, hash, id, username, social, generation, economic);
		pullFromDB();
	}
	public Boolean registerDeviceController() {
		// TODO Auto-generated method stub
		return null;
	}
	public ElectricityRequirement generateRequirement(Date start, Date end, DecimalRating prio, int profileId, double amplitude)
	{
		return new ElectricityRequirement(start, end, prio, profileId, amplitude, masterUser.getId());
	}

	public Boolean addRequirement(ElectricityRequirement req) {
		Boolean success = masterUser.addReq(req);
		if (success) {
			maxEleConsumption += req.getMaxConsumption();
			dbReq.insertElement(req);
			return true;
		}
		return false;
	}

	
	public void pushToDB() {
		for (ElectricityRequirement r : masterUser.getReqs())
		{
			dbReq.insertElement(r);
		}
		
	}

	public void pullFromDB() {
		ArrayList<ElectricityRequirement>temp_array = dbReq.extractAll();
		for (ElectricityRequirement r : temp_array)
		{
			if (r.getUserID().equals(masterUser.getId())){
			masterUser.addReq(r);
			}
		}
		updateMaxConsumption();
		
	}
	
	private void updateMaxConsumption()
	{
		maxEleConsumption = 0;
			for (ElectricityRequirement r : masterUser.getReqTktMap().keySet()) {
				maxEleConsumption += r.getMaxConsumption();
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
	public double getMaxEleConsumption() {
		return maxEleConsumption;
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
	public ArraySet<ElectricityTicket> findCompetingTickets(ElectricityRequirement req) {
		ArraySet<ElectricityTicket> ret = new ArraySet<ElectricityTicket>();
		for (ElectricityTicket t : masterUser.getReqTktMap().values())
		if (t.start.before(req.getStartTime())){
			
		}
		else
		{
			if (t.end.after(req.getEndTime()))
			{
		      break;
			}
			else
			{
				ret.add(t);
			}
		}

		return ret;
	}
}
