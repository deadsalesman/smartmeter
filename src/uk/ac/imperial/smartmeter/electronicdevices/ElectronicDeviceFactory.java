package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.UUID;

public class ElectronicDeviceFactory {
	
	public static ElectronicConsumerDevice getDevice(Integer type)
	{
		DeviceType x;
		try {
			x = DeviceType.values()[type];
		} catch(ArrayIndexOutOfBoundsException e)
		{
			x = DeviceType.values()[0];
		}
		return getDevice(x.toString());
		
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
		case "DUMMY":
			return new LED(id, initialState);
		case "BATTERY":
			return new Battery(id, initialState);
		case "LED":
			return new LED(id, initialState);
		case "DISHWASHER":
			return new Dishwasher(id, initialState);
		case "STOVE":
			return new Stove(id, initialState);
		case "LIGHT":
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
