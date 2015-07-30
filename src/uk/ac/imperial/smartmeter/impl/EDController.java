package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;
import java.util.UUID;

import uk.ac.imperial.smartmeter.db.DevicesDBManager;
import uk.ac.imperial.smartmeter.interfaces.ElectronicDeviceControllerIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DeviceType;
import uk.ac.imperial.smartmeter.res.ElectronicDevice;

//ElectronicDeviceController
public class EDController 
	implements ElectronicDeviceControllerIFace, UniqueIdentifierIFace{
	private ArraySet<ElectronicDevice> devices;
	public DevicesDBManager db;
	private UUID id;
	
	public EDController()
	{
		id = UUID.randomUUID();
		devices = new ArraySet<ElectronicDevice>();
		db = new DevicesDBManager("jdbc:sqlite:edc.db");
		pullFromDB();
	}
	public String getId()
	{
		return id.toString();
	}
	public int getDeviceCount()
	{
		return devices.size();
	}
	public int getDeviceIndex(String deviceID)
	{
		for (ElectronicDevice ed : devices)
		{
			if (ed.getId().equals(deviceID))
			{
				return devices.indexOf(ed);
			}
		}
		
		return -1;
	}
	public ElectronicDevice getDevice(String deviceID)
	{
		for (ElectronicDevice ed : devices)
		{
			if (ed.getId().equals(deviceID))
			{
				return ed;
			}
		}
		
		return null;
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
   public Boolean addDevice(ElectronicDevice e)
   {
	   if(db.insertElement(e))
	   {
		   return devices.add(e);
	   }
	   else 
	   {
		   return false;
	   }
   }
   public Boolean removeDevice(int index)
   {
	   if(db.removeElement(devices.get(index)))
	   {
		   return devices.remove(devices.get(index));
	   }
	   else 
	   {
		   return false;
	   }
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
	public Boolean setDeviceState(int index, Boolean newState) {

		try {
			devices.get(index).setState(newState);
			db.updateDeviceState(devices.get(index).hashCode(), newState);
			return true;
		}
		catch (IndexOutOfBoundsException e){
			System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicDeviceController");
			return false;
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
			db.insertElement(i);
		}
		
	}
	@Override
	public void pullFromDB() {
		ArrayList<ElectronicDevice>temp_array = db.extractAll();
		for (ElectronicDevice i : temp_array)
		{
			devices.add(i);
		}
	}
}
