package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.imperial.smartmeter.db.ReqsDBManager;
import uk.ac.imperial.smartmeter.db.UserDBManager;
import uk.ac.imperial.smartmeter.interfaces.*;
import uk.ac.imperial.smartmeter.res.*;

public class LocalController implements LocalControllerIFace {
	private ArraySet<ElectricityRequirement> reqList;
	private ElectricityGeneration eleGen;
	public ReqsDBManager dbReq;
	private double maxEleConsumption;
	private User masterUser;
	
	
	public LocalController(String username)
	{
		dbReq  = new ReqsDBManager("jdbc:sqlite:req.db");
		reqList = new ArraySet<ElectricityRequirement>();
		masterUser = new User(username);
	}
	@Override
	public Boolean registerDeviceController() {
		// TODO Auto-generated method stub
		return null;
	}
	public int getReqCount()
	{
		return reqList.size();
	}
	public ElectricityRequirement generateRequirement(Date start, Date end, DecimalRating prio, int profileId, double amplitude)
	{
		return new ElectricityRequirement(start, end, prio, profileId, amplitude, masterUser.getId());
	}
	@Override
	public void addRequirement(ElectricityRequirement req) {
		reqList.add(req);
		maxEleConsumption += req.getMaxConsumption();
	}

	@Override
	public ElectricityRequirement getRequirement(int index) {
		return reqList.get(index);
	}
	public void setUser(User u)
	{
		
	}
	@Override
	public void removeRequirement(int index) {
		try{
		  double rMaxConsumption = getRequirement(index).getMaxConsumption();
		  reqList.remove(index);
		  setMaxEleConsumption(getMaxEleConsumption() - rMaxConsumption);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			
		}
		
	}

	private void setMaxEleConsumption(double d) {
		maxEleConsumption = d;
	}
	@Override
	public void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

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
	private void updateMaxConsumption()
	{
		maxEleConsumption = 0;
		if (reqList != null)
 {
			for (ElectricityRequirement r : reqList) {
				maxEleConsumption += r.getMaxConsumption();
			}
		}
	}
	public ElectricityGeneration getEleGen() {
		return eleGen;
	}

	public void setEleGen(ElectricityGeneration eleGen) {
		this.eleGen = eleGen;
	}
	public double getMaxEleConsumption() {
		return maxEleConsumption;
	}
}
