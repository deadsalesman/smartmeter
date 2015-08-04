package uk.ac.imperial.smartmeter.impl;

import java.util.Date;

import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.UserAgent;

//LocalController
public class LController {
	private ElectricityGeneration eleGen;
	//public ReqsDBManager dbReq;
	private double maxEleConsumption;
	private UserAgent masterUser;
	
	
	public LController(String username)
	{
		//dbReq  = new ReqsDBManager("jdbc:sqlite:req.db");
		masterUser = new UserAgent(username, username, username, username, maxEleConsumption, maxEleConsumption, maxEleConsumption, maxEleConsumption, null);
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
		}
		return success;
	}

	private void setMaxEleConsumption(double d) {
		maxEleConsumption = d;
	}
	
/*
	@Override
	public void pushToDB() {
		for (ElectricityRequirement r : reqList)
		{
			dbReq.insertElement(r);
		}
		
	}

	@Override
	public void pullFromDB() {
		ArrayList<ElectricityRequirement>temp_array = dbReq.extractAll();
		for (ElectricityRequirement r : temp_array)
		{
			reqList.add(r);
		}
		updateMaxConsumption();
		
	}
	*/
	private void updateMaxConsumption()
	{
		maxEleConsumption = 0;
			for (ElectricityRequirement r : masterUser.getReqs().keySet()) {
				maxEleConsumption += r.getMaxConsumption();
			}
		
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
}
