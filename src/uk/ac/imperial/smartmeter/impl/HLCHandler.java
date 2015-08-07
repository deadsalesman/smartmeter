package uk.ac.imperial.smartmeter.impl;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;

public class HLCHandler {
	private HLController controller;
	
	public HLCHandler()
	{
		controller = new HLController();
	}
	public Boolean queryUserExistence(String userId)
	{
		return controller.queryUserExistence(userId);
	}
	public ArraySet<ElectricityTicket> getTickets(String userId)
	{
		return controller.getTickets(userId);
	}
	public Boolean setRequirement(ElectricityRequirement e)
	{
		return controller.addRequirement(e);
	}
	public Boolean addUserAgent(UserAgent u)
	{
		return controller.addUserAgent(u);
	}
	public Boolean setUserGeneration(String userId, ElectricityGeneration e)
	{
		return controller.setUserGeneration(userId, e);
	}
	public String getUUID(String string) {
		return controller.getUUID(string);
	}
	public Boolean calculateTickets() {
		return controller.calculateTickets();
	}
	public Boolean clearAll(String string) {
		return controller.clearAll(string);
	}
	public Boolean extendTicket(ElectricityTicket tkt, ElectricityRequirement req) {
		return controller.extendTicket(req, tkt);
	}
	public Boolean intensifyTicket(ElectricityTicket tkt, ElectricityRequirement req) {
		return controller.intensifyTicket(req,tkt);
	}
}
