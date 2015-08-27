package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public abstract class DeviceList {
	public static final Map<Integer, Class<? extends ElectronicDevice>> profileMap = new HashMap<Integer,Class<? extends ElectronicDevice>>();

	static {
		profileMap.put(0, Battery.class);
		profileMap.put(1, Light.class);
		profileMap.put(2, LED.class);
		profileMap.put(3, Dishwasher.class);
		profileMap.put(4, Stove.class);
		profileMap.put(5, Battery.class);
	}
	static public int getCode(ElectronicDevice device)
	{
		int ret = -1;
		for (Entry<Integer, Class<? extends ElectronicDevice>> entry : profileMap.entrySet()) {
	        if (device.getClass().equals(entry.getValue())) {
	            ret = entry.getKey();
	        }
	    }
		return ret;
	}
	static public int getLength()
	{
		return profileMap.size();
	}
	DeviceList()
	{
		
	}
}