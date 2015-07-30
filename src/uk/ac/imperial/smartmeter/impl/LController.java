package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.imperial.smartmeter.db.ReqsDBManager;
import uk.ac.imperial.smartmeter.interfaces.LocalControllerIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.User;

//LocalController
public class LController implements LocalControllerIFace {
	private ArraySet<ElectricityRequirement> reqList;
	private ElectricityGeneration eleGen;
	public ReqsDBManager dbReq;
	private double maxEleConsumption;
	private User masterUser;
	
	
	public LController(String username)
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
	public Boolean addRequirement(ElectricityRequirement req) {
		Boolean success = reqList.add(req);
		if (success) {
		maxEleConsumption += req.getMaxConsumption();
		}
		return success;
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

	public boolean setEleGen(ElectricityGeneration eleGen) {
		this.eleGen = eleGen;
		return true;
	}
	public double getMaxEleConsumption() {
		return maxEleConsumption;
	}
}
