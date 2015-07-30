package uk.ac.imperial.smartmeter.impl;

import uk.ac.imperial.smartmeter.res.ElectronicDevice;

public class EDCHandler {
	
	private EDController controller;
	public EDCHandler()
	{
		controller = new EDController();
	}
	public Boolean getState(String deviceID)
	{
		return controller.getDeviceState(controller.getDeviceIndex(deviceID));
	}
	public Boolean setState(String deviceID, Boolean newState)
	{
		return controller.setDeviceState(controller.getDeviceIndex(deviceID), newState);
	}
	public Boolean addDevice(ElectronicDevice newDevice)
	{
		return controller.addDevice(newDevice);
	}
	public Boolean removeDevice(String deviceID)
	{
		return controller.removeDevice(controller.getDeviceIndex(deviceID));
	}
	public ElectronicDevice getDevice(String deviceID)
	{
		return controller.getDevice(deviceID);
	}
}
