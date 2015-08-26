package uk.ac.imperial.smartmeter.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.smartmeter.db.DevicesDBManager;
import uk.ac.imperial.smartmeter.electronicdevices.DeviceType;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.interfaces.ElectronicDeviceControllerIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;

//ElectronicConsumerDeviceController
public class EDController 
	implements ElectronicDeviceControllerIFace, UniqueIdentifierIFace{
	private ArraySet<ElectronicConsumerDevice> devices;
	private Map<ElectronicConsumerDevice, Integer> pinouts;
	public DevicesDBManager db;
	private UUID id;
	private Set<Integer> availablePins;
	public EDController()
	{
		id = UUID.randomUUID();
		devices = new ArraySet<ElectronicConsumerDevice>();
		db = new DevicesDBManager("jdbc:sqlite:edc.db");
		pinouts = new HashMap<ElectronicConsumerDevice, Integer>();
		availablePins = setAvailablePins();
		pullFromDB();
		setAvailablePins();
	}
	private Set<Integer> setAvailablePins()
	{
		HashSet<Integer> pins = new HashSet<Integer>();
		pins.add(3);
		pins.add(5);
		pins.add(7);
		pins.add(8);
		pins.add(10);
		pins.add(11);
		pins.add(12);
		pins.add(13);
		pins.add(15);
		pins.add(16);
		pins.add(18);
		pins.add(19);
		pins.add(21);
		pins.add(22);
		pins.add(23);
		pins.add(24);
		pins.add(26);
		return pins;
		
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
		for (ElectronicConsumerDevice ed : devices)
		{
			if (ed.getId().equals(deviceID))
			{
				return devices.indexOf(ed);
			}
		}
		
		return -1;
	}
	public ElectronicConsumerDevice getDevice(String deviceID)
	{
		for (ElectronicConsumerDevice ed : devices)
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
			return devices.get(index).getConsumptionEnabled();
		}
		catch (IndexOutOfBoundsException e){
			//System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicConsumerDeviceController");
		}
		return null;
	}

	public Boolean addDevice(ElectronicConsumerDevice e, Integer pin) {
		if (db.insertElement(e)) {
			if (availablePins.contains(pin)) {
				availablePins.remove(pin);
				pinouts.put(e, pin);
				devices.add(e);
				return true;
			}
		}
		return false;
	}
	public Boolean wipe()
	{
		db.wipe();
		devices = new ArraySet<ElectronicConsumerDevice>();
		pinouts = new HashMap<ElectronicConsumerDevice, Integer>();
		availablePins = setAvailablePins();
		return true;
	}
   public Boolean removeDevice(int index)
   {
	   ElectronicConsumerDevice x = devices.get(index);
	   if(db.removeElement(x))
	   {
		   availablePins.add(pinouts.get(x));
		   pinouts.remove(x);
		   return devices.remove(x);
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
			//System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicConsumerDeviceController");
		}
		return null;
	}

	

	@Override
	public Boolean setDeviceState(int index, Boolean newState) {

		try {
			ElectronicConsumerDevice ed = devices.get(index);
			ed.setConsumptionEnabled(newState);
			db.updateDeviceState(ed.hashCode(), newState);
			ProcessBuilder pb = new ProcessBuilder("python", "test1.py", "" + pinouts.get(ed), "" + pinouts.get(ed));
			Process p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			int ret = new Integer(in.readLine()).intValue();
			System.out.println("value is : " + ret);
			return true;
		} catch (IndexOutOfBoundsException e){
			//System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicConsumerDeviceController");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	

	@Override
	public void setDevicesOfType(DeviceType type, Boolean newState) {
		for (ElectronicConsumerDevice device : devices)
		{
			if (device.getType()==type)
			{
				device.setConsumptionEnabled(newState);
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
		for (ElectronicConsumerDevice i : devices)
		{
			db.insertElement(i);
		}
		
	}
	@Override
	public void pullFromDB() {
		ArrayList<ElectronicConsumerDevice>temp_array = db.extractAll();
		for (ElectronicConsumerDevice i : temp_array)
		{
			devices.add(i);
		}
	}
}
