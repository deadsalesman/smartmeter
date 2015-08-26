package uk.ac.imperial.smartmeter.electronicdevices;

import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;

public class Dishwasher implements ElectronicConsumerDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6906758019033560805L;

	@Override
	public DeviceType getType() {
		return this.getClass().toString();
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
