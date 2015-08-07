package uk.ac.imperial.smartmeter.impl;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;

public class LCHandler {
private LController controller;
	private String id;
	public LCHandler(String username,String password,Double social, Double generation, Double economic)
	{
		controller = new LController(username, password, social, generation, economic);
		id = controller.getId();
	}
	public Boolean setRequirement(ElectricityRequirement e)
	{
		return controller.addRequirement(e);
	}
	public String getSalt(){
		return controller.getSalt();
	}
	public String getHash(){
		return controller.getHash();
	}
	public ArraySet<ElectricityTicket> findCompetingTickets(ElectricityRequirement req)
	{
		return controller.findCompetingTickets(req);
	}
	public ArraySet<ElectricityRequirement> getReqs()
	{
		return controller.getReqs();
	}
	public ElectricityTicket findMatchingTicket(ElectricityRequirement req)
	{
		return controller.findMatchingTicket(req);
	}
	public ElectricityRequirement findMatchingRequirement(ElectricityTicket tkt)
	{
		return controller.findMatchingRequirement(tkt);
	}
	public String getId() {
		return id;
	}
	public boolean setGeneration(ElectricityGeneration e) {
		return controller.setEleGen(e);
	}
	public boolean forceNewTicket(ElectricityTicket t) {
		return controller.forceNewTicket(t);
	}
	public boolean setTicket(ElectricityTicket t) {
		return controller.setTicket(t);
	}
	public ArraySet<ElectricityTicket> getTkts()
	{
		return controller.getTkts();
	}
}
