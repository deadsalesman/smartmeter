package uk.ac.imperial.smartmeter.impl;

import uk.ac.imperial.smartmeter.interfaces.*;
import uk.ac.imperial.smartmeter.res.DeviceType;


public class ElectronicDevice implements ElectronicDeviceIFace{
	public DeviceType type;
	private Boolean state;
	@Override
	public double getConsumptionRate() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Boolean getState() {
		return state;
	}
	@Override
	public void setState(Boolean newState) {
		state = newState;
	}
	
}
