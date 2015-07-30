package uk.ac.imperial.smartmeter.impl;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.User;

public class HLCHandler {
	private HLController controller;
	
	public HLCHandler()
	{
		controller = new HLController();
	}
	public ArraySet<ElectricityTicket> getTickets(User u)
	{
		return controller.getTicket(u);
	}
	public Boolean setRequirement(ElectricityRequirement e)
	{
		return controller.addRequirement(e);
	}
}
