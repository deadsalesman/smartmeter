package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.UUID;



public abstract class ElectronicDevice implements ElectronicDeviceIFace{
	/**
	 * 
	 */
	private static final long serialVersionUID = -418628052538268179L;
	private DeviceType type;
	private Boolean state;
	private UUID id;
	
	public ElectronicDevice(boolean onoff, String device, String idString)
	{
		state = onoff;
		type = DeviceType.valueOf(device);
		id = UUID.fromString(idString);
	}
	public ElectronicDevice(boolean onoff, int device, String idString)
	{
		state = onoff;
		type = DeviceType.values()[device];
		id = UUID.fromString(idString);
	}
	public ElectronicDevice(boolean onoff, String device)
	{
		state = onoff;
		type = DeviceType.valueOf(device);
		id = UUID.randomUUID();
	}
	
	public ElectronicDevice(boolean onoff, int device)
	{
		state = onoff;
		type = DeviceType.values()[device];
		id = UUID.randomUUID();
	}
	public String getId() {
		return id.toString();
	}

	@Override
	public DeviceType getType() {
		return type;
	}
	
}
