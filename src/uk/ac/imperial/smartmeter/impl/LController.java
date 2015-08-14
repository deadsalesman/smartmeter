package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.UUID;

import uk.ac.imperial.smartmeter.db.ReqsDBManager;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

//LocalController
public class LController {
	private ElectricityGeneration eleGen;
	public ReqsDBManager dbReq;
	private double maxEleConsumption;
	private UserAgent masterUser;
	private ArrayList<ElectricityTicket> unhappyTickets;
	
	
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
	public ArraySet<ElectricityRequirement> getReqs()
	{
		return masterUser.getReqs();
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
		for (ElectricityTicket t : masterUser.getReqTktMap().values()){
			if (t.getStart().compareTo(req.getStartTime())<0)
			{
				if (t.getEnd().compareTo(req.getStartTime())>=0)
				{
					ret.add(t);
				}
			}
			else
			{
				if (t.getStart().compareTo(req.getEndTime())<=0)
				{
					ret.add(t);
				}
			}
		}
		
		return ret;
	}
	public ElectricityTicket findMatchingTicket(ElectricityRequirement req) {
		if (masterUser.getReqTktMap().containsKey(req))
		{
			return masterUser.getReqTktMap().get(req);
		}
		else return null;
	}
	public boolean setTicket(ElectricityTicket t) {
		for (ElectricityRequirement r : masterUser.getReqs())
		{
			if (r.getId().equals(t.reqID.toString()))
			{
				if (masterUser.getReqTktMap().get(r)==null){
				masterUser.getReqTktMap().put(r, t);
				return true;
				}
			}
		}
		return false;
	}
	public ArraySet<ElectricityTicket> getTkts() {
		ArraySet<ElectricityTicket> x = new ArraySet<ElectricityTicket>();
		for (ElectricityTicket t : masterUser.getReqTktMap().values())
		{
			if (t!=null){x.add(t);}
		}
			return x;
	}
	public boolean forceNewTicket(ElectricityTicket t) {
		for (ElectricityRequirement r : masterUser.getReqs())
		{
			if (r.getId().equals(t.reqID.toString()))
			{
				if ((r.getMaxConsumption()<= t.magnitude)&&(r.getDuration()<=t.getDuration()))
				{
					masterUser.getReqTktMap().put(r, t);
					return true;
				}
				else{return false;}
			}
		}
		return false;
	}

	public ElectricityRequirement findMatchingRequirement(ElectricityTicket tkt) {
		for (Entry<ElectricityRequirement, ElectricityTicket> x : masterUser.getReqTktMap().entrySet()) {
			if (tkt.id.toString().equals(x.getValue().id.toString())) {
				return x.getKey();
			}
		}
		return null;
	}
	public Boolean queryUnsatisfiedReqs() {
		Boolean ret = true;
		
		for (ElectricityTicket e : masterUser.getReqTktMap().values())
		{
			ret &= (e!=null);
		}
		
		return ret;
	}
	public ArrayList<ElectricityTicket> getUnhappyTickets() {
		return unhappyTickets;
	}
	public boolean queryUnhappyTickets() {
		unhappyTickets = new ArrayList<ElectricityTicket>();
		double threshold = 1.1; //TODO: set a reasonable value. current is for debug, would normally be lower obviously
		for (Entry<ElectricityRequirement, ElectricityTicket> e : masterUser.getReqTktMap().entrySet())
		{
			if (e.getValue()!=null){
			if (LCServer.calcUtilityNoExtension(e.getValue(),e.getKey()) < threshold)
			{
				unhappyTickets.add(e.getValue());
			}
			}
		}
		return unhappyTickets.size()!=0;
	}
}
