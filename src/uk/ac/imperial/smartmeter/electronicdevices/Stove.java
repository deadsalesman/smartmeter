package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.UUID;

import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;
import uk.ac.imperial.smartmeter.electricityprofile.StoveConsumptionProfile;

public class Stove implements ElectronicConsumerDevice{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3955694270941594052L;
	DeviceType type = DeviceType.STOVE;
	Boolean active = false;
	ConsumptionProfile demand;
	private UUID id = UUID.randomUUID();
	

	Stove(){
		demand = new StoveConsumptionProfile();
	}
	public Stove(String id2, Boolean initialState) {
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
