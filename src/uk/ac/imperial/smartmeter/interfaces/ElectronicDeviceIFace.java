package uk.ac.imperial.smartmeter.interfaces;

import uk.ac.imperial.smartmeter.res.DeviceType;


public interface ElectronicDeviceIFace {
	public double getConsumptionRate();
	public DeviceType getType();
	public Boolean getState();
	public void setState(Boolean newState);
}
