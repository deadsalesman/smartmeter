package uk.ac.imperial.smartmeter.electronicdevices;

import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;

public interface ElectronicConsumerDevice extends ElectronicDeviceIFace {
	public double getMaxConsumptionRate();
	public Boolean getConsumptionEnabled();
	public void setConsumptionEnabled(Boolean state);
	public ConsumptionProfile getProfile();
}
