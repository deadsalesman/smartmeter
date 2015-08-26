package uk.ac.imperial.smartmeter.electronicdevices;

import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;

public class Light implements ElectronicConsumerDevice{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4762861591914874965L;

	@Override
	public DeviceType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setState(Boolean newState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getMaxConsumptionRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ConsumptionProfile getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

}
