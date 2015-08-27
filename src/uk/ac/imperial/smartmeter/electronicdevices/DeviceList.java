package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.electricityprofile.ProfileType;


@SuppressWarnings("unchecked")
public abstract class DeviceList {
	public static final Map<Integer, Class<? extends ElectronicDevice>> profileMap = new HashMap<Integer,Class<? extends ElectronicDevice>>();

	static {

		int i = 0;
		for (ProfileType profile : ProfileType.values())
		{
		try {
			profileMap.put(i, (Class<? extends ElectronicDevice>) Class.forName("uk.ac.imperial.smartmeter.electronicdevices."+ profile.name().substring(0, 1).toUpperCase()+profile.name().substring(1,profile.name().length()).toLowerCase()));
			i++;
		} catch (ClassNotFoundException e) {
		}
		}
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