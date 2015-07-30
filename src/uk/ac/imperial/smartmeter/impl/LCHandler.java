package uk.ac.imperial.smartmeter.impl;

import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

public class LCHandler {
private LController controller;
	private String id;
	public LCHandler(String userId)
	{
		id = userId;
		controller = new LController(id);
	}
	public Boolean setRequirement(ElectricityRequirement e)
	{
		return controller.addRequirement(e);
	}
}
