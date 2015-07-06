package uk.ac.imperial.smartmeter.interfaces;

import uk.ac.imperial.smartmeter.res.DeviceType;

public interface ElectronicDeviceControllerIFace {
	public Boolean getDeviceState(int index);
	public DeviceType getDeviceType(int index);
	public void setDeviceState(int index, Boolean newState);
	public void setDevicesOfType(DeviceType type, Boolean newState);
	public Boolean registerSmartMeter(); //Not sure how best to implement this. RMI? Webserver?
	public void pushToDB();
	public void pullFromDB();
}
