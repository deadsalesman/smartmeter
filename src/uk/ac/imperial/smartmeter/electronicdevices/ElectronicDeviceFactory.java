package uk.ac.imperial.smartmeter.electronicdevices;

public class ElectronicDeviceFactory {
	
	public ElectronicDeviceIFace getDevice(Integer deviceType)
	{
		return getDevice(DeviceType.values()[deviceType].toString());
		
	}
	public ElectronicDeviceIFace getDevice(String deviceType)
	{
		switch(deviceType)
		{
		case "Battery":
			return new Battery();
		default: 
			return null;
		}
	}
}
