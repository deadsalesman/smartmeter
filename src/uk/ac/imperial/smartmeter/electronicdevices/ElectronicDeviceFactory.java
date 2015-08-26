package uk.ac.imperial.smartmeter.electronicdevices;

public class ElectronicDeviceFactory {
	
	public ElectronicDevice getDevice(Integer deviceType)
	{
		return getDevice(DeviceType.values()[deviceType].toString());
		
	}
	public ElectronicDevice getDevice(String deviceType)
	{
		switch(deviceType)
		{
		case "Battery":
			return new Battery(false, 0);
		default: 
			return null;
		}
	}
}
