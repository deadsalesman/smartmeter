package uk.ac.imperial.smartmeter.res;

import java.util.UUID;

import uk.ac.imperial.smartmeter.interfaces.ElectronicDeviceIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;


public class ElectronicDevice implements ElectronicDeviceIFace, UniqueIdentifierIFace{
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
	@Override
	public double getConsumptionRate() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Boolean getState() {
		return state;
	}
	@Override
	public void setState(Boolean newState) {
		state = newState;
	}

	public String getId() {
		return id.toString();
	}

	@Override
	public DeviceType getType() {
		return type;
	}
	
}
