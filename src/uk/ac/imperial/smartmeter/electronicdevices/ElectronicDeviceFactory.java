package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.UUID;

public class ElectronicDeviceFactory {
	
	public static ElectronicConsumerDevice getDevice(Integer deviceType)
	{
		return getDevice(DeviceType.values()[deviceType].toString());
		
	}
	public static ElectronicConsumerDevice getDevice(int deviceType, String id, Boolean initialState)
	{
		return getDevice(DeviceType.values()[deviceType].toString(),id, initialState);
	}
	public static ElectronicConsumerDevice getDevice(String deviceType, String id, Boolean initialState)
	{
		if (id == null)
		{
			id = UUID.randomUUID().toString();
		}
		switch(deviceType)
		{
		case "Battery":
			return new Battery(id, initialState);
		case "LED":
			return new LED(id, initialState);
		case "Dishwasher":
			return new Dishwasher(id, initialState);
		case "Stove":
			return new Stove(id, initialState);
		case "Light":
			return new Light(id, initialState);
		default: 
			return null;
		}
	}
	public static ElectronicConsumerDevice getDevice(String deviceType)
	{
		return getDevice(deviceType, null, false);
	}
}
