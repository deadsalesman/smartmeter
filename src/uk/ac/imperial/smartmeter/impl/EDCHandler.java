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
		try {
			return controller.getDeviceState(controller.getDeviceIndex(deviceID));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Boolean setState(String deviceID, Boolean newState) {
		try {
			return controller.setDeviceState(controller.getDeviceIndex(deviceID), newState);
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	public Boolean addDevice(ElectronicDevice newDevice)
	{
		return controller.addDevice(newDevice);
	}
	public Boolean removeDevice(String deviceID)
 {
		try {
			return controller.removeDevice(controller.getDeviceIndex(deviceID));
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	public ElectronicDevice getDevice(String deviceID)
	{
		return controller.getDevice(deviceID);
	}
}
