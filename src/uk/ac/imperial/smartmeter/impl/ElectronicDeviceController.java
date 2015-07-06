package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.UUID;

import uk.ac.imperial.smartmeter.db.DevicesDBManager;
import uk.ac.imperial.smartmeter.interfaces.ElectronicDeviceControllerIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DeviceType;


public class ElectronicDeviceController 
	implements ElectronicDeviceControllerIFace, UniqueIdentifierIFace{
	private ArraySet<ElectronicDevice> devices;
	public DevicesDBManager db;
	private UUID id;
	
	public ElectronicDeviceController()
	{
		id = UUID.randomUUID();
		devices = new ArraySet<ElectronicDevice>();
		db = new DevicesDBManager("jdbc:sqlite:edc.db");
	}
	public String getId()
	{
		return id.toString();
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
   public void addDevice(ElectronicDevice e)
   {
	   devices.add(e);
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
			db.updateDeviceState(devices.get(index).hashCode(), newState);
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
				db.updateDeviceState(device.getId().hashCode(), newState);
			}
		}
	}

	@Override
	public Boolean registerSmartMeter() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	protected void finalize() throws Throwable
	{
		pushToDB();
		super.finalize();
	}
	
	@Override
	public void pushToDB() {
		for (ElectronicDevice i : devices)
		{
			db.insertDevice(i);
		}
		
	}
	@Override
	public void pullFromDB() {
		ArrayList<ElectronicDevice>temp_array = db.extractAllDevices();
		for (ElectronicDevice i : temp_array)
		{
			devices.add(i);
		}
	}
}
