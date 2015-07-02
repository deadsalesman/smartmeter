package uk.ac.imperial.smartmeter.impl;

import uk.ac.imperial.smartmeter.interfaces.*;
import uk.ac.imperial.smartmeter.res.*;

public class UnbundledSmartMeter implements UnbundledSmartMeterIFace {
	private ElectricityRequirementList eleReqList;
	private ElectricityGeneration eleGen;
	private double maxEleConsumption;
	
	@Override
	public Boolean registerDeviceController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRequirement(ElectricityRequirement req) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ElectricityRequirement getRequirement(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeRequirement(int index) {
		// TODO Auto-generated method stub
		
	}
}
