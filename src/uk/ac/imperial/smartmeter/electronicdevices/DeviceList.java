package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;



@SuppressWarnings("unchecked")
public abstract class DeviceList {
	public static final Map<Integer, Class<? extends ElectronicDevice>> deviceMap = new HashMap<Integer,Class<? extends ElectronicDevice>>();

	static {

		int i = 0;
		for (DeviceType device : DeviceType.values())
		{
		try {
			deviceMap.put(i, (Class<? extends ElectronicDevice>) Class.forName("uk.ac.imperial.smartmeter.electronicdevices."+ device.name().substring(0, 1).toUpperCase()+device.name().substring(1,device.name().length()).toLowerCase()));
			i++;
		} catch (ClassNotFoundException e) {
			System.out.println(device.name() + " does not have an associated device");
		}
		}
	}
	static public int getCode(ElectronicDevice device)
	{
		int ret = -1;
		for (Entry<Integer, Class<? extends ElectronicDevice>> entry : deviceMap.entrySet()) {
	        if (device.getClass().equals(entry.getValue())) {
	            ret = entry.getKey();
	        }
	    }
		return ret;
	}
	static public int getLength()
	{
		return deviceMap.size();
	}
	DeviceList()
	{
		
	}
}