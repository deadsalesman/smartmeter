package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;

import uk.ac.imperial.smartmeter.db.DevicesDBManager;
import uk.ac.imperial.smartmeter.interfaces.ElectronicDeviceControllerIFace;
import uk.ac.imperial.smartmeter.res.DeviceType;


public class ElectronicDeviceController implements ElectronicDeviceControllerIFace {
	private ArrayList<ElectronicDevice> devices;
	private DevicesDBManager db;
	
	@Override
	public void addDevice(ElectronicDevice dev) {
		boolean exists = false;
		for (ElectronicDevice ed : devices)
		{
			exists |= dev.getId()==ed.getId();
		}
		if (!exists) {
			devices.add(dev);

		}
	}

	@Override
	public void removeDevice(int index) {
		try {
		devices.remove(index);
		}
		catch (IndexOutOfBoundsException e){
			System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicDeviceController");
		}
	}

	@Override
	public Boolean getDeviceState(int index) {
		
		try {
			return devices.get(index).getState();
		}
		catch (IndexOutOfBoundsException e){
			System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicDeviceController");
		}
		return null;
	}

	

	@Override
	public DeviceType getDeviceType(int index) {

		try {
			return devices.get(index).getType();
		}
		catch (IndexOutOfBoundsException e){
			System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicDeviceController");
		}
		return null;
	}

	

	@Override
	public void setDeviceState(int index, Boolean newState) {

		try {
			devices.get(index).setState(newState);
		}
		catch (IndexOutOfBoundsException e){
			System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicDeviceController");
		}
	}

	

	@Override
	public void setDevicesOfType(DeviceType type, Boolean newState) {
		for (ElectronicDevice device : devices)
		{
			if (device.getType()==type)
			{
				device.setState(newState);
			}
		}
	}

	@Override
	public Boolean registerSmartMeter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void pushToDB() {
		for (ElectronicDevice ed : devices)
		{
			db.insertDevice(ed);
		}
	}

	@Override
	public void pullFromDB() {
		ArrayList<ElectronicDevice>temp_array = db.extractAllDevices();
		for (ElectronicDevice ed : temp_array)
		{
			devices.add(ed);
		}
	}
}
