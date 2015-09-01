package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.UUID;

import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;
import uk.ac.imperial.smartmeter.electricityprofile.DishwasherConsumptionProfile;

/**
 * Class to represent a Dishwasher.
 * @author bwindo
 *
 */
public class Dishwasher implements ElectronicConsumerDevice {

	/**
	 * 
	 */

	private static final long serialVersionUID = -6906758019033560805L;
	DeviceType type = DeviceType.DISHWASHER;
	Boolean active = false;
	ConsumptionProfile demand;
	private UUID id = UUID.randomUUID();
	

	Dishwasher(){
		demand = new DishwasherConsumptionProfile();
	}
	public Dishwasher(String id2, Boolean initialState) {
		this();
		active = initialState;
		id = UUID.fromString(id2);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceType getType() {
		return type;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return id.toString();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getMaxConsumptionRate() {
		return demand.getMaxConsumption();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConsumptionProfile getProfile() {
		return demand;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getConsumptionEnabled() {
		return active;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConsumptionEnabled(Boolean state) {
		active = state;
		
	}

}
