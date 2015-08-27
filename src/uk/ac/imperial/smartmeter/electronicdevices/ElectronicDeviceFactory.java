package uk.ac.imperial.smartmeter.electronicdevices;

import java.lang.reflect.Constructor;
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
		
		DeviceType x = DeviceType.valueOf(deviceType);
		try{

			String name = "uk.ac.imperial.smartmeter.electronicdevices." + x.name().substring(0, 1).toUpperCase()+x.name().substring(1,x.name().length()).toLowerCase();
			@SuppressWarnings("unchecked")
			Constructor<ElectronicConsumerDevice> c = (Constructor<ElectronicConsumerDevice>) Class.forName(
					name
					).getConstructor(String.class, Boolean.class);
			ElectronicConsumerDevice foo = c.newInstance(id, initialState);
			return foo;
		} catch (Exception e)
		{
			return new Uniform(id, initialState);
		}
	}
	public static ElectronicConsumerDevice getDevice(String deviceType)
	{
		return getDevice(deviceType, null, false);
	}
}
