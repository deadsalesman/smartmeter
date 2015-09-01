package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.UUID;

import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;
import uk.ac.imperial.smartmeter.electricityprofile.LedConsumptionProfile;

public class Led implements ElectronicConsumerDevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1369515486831127720L;
	DeviceType type = DeviceType.LED;
	Boolean active = false;
	ConsumptionProfile demand;
	private UUID id = UUID.randomUUID();
	

	Led(){
		demand = new LedConsumptionProfile();
	}
	public Led(String id2, Boolean initialState) {
		this();
		active = initialState;
		id = UUID.fromString(id2);
	}
	@Override
	public DeviceType getType() {
		return type;
	}

	@Override
	public String getId() {
		return id.toString();
	}

	@Override
	public double getMaxConsumptionRate() {
		return demand.getMaxConsumption();
	}

	@Override
	public ConsumptionProfile getProfile() {
		return demand;
	}

	@Override
	public Boolean getConsumptionEnabled() {
		return active;
	}

	@Override
	public void setConsumptionEnabled(Boolean state) {
		active = state;
		
	}

}