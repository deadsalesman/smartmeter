package uk.ac.imperial.smartmeter.impl;

import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

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
	public String getId() {
		return id;
	}
	public boolean setGeneration(ElectricityGeneration e) {
		return controller.setEleGen(e);
	}
}
