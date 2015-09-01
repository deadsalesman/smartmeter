package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.UUID;

import uk.ac.imperial.smartmeter.electricityprofile.BatteryConsumptionProfile;
import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;

/**
 * Class to represent a Battery.
 * @author bwindo
 *
 */
public class Battery  implements ElectronicConsumerDevice, ElectronicSupplierDevice {


	/**
	 * 
	 */
	DeviceType type = DeviceType.BATTERY;
	Boolean charging = false;
	Boolean supplying = false;
	ElectricityGeneration supply;
	ConsumptionProfile demand;
	private UUID id = UUID.randomUUID();
	
	private static final long serialVersionUID = 2442756528890104616L;
	/**
	 * Creates a Battery with the default consumption and generation parameters.
	 */
	public Battery()
	{
		demand = new BatteryConsumptionProfile(1., 1.); //arbitrary units of charging
		supply = new ElectricityGeneration(1.);
	}
	/**
	 * Creates a Battery with default consumption and generation parameters, with a custom id and initial state.
	 * @param uID The new id of the battery.
	 * @param initialState The initial state of whether the battery is charging or not.
	 */
	public Battery(String uID, Boolean initialState) {
		this();
		charging = initialState;
		id = UUID.fromString(uID);
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
	public double getMaxGenerationRate() {
		return supply.getMaxOutput();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getSupplyEnabled() {
		return supplying;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElectricityGeneration getSupplyProfile() {
		return supply;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getConsumptionEnabled() {
		return charging;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSupplyEnabled(Boolean state) {
		supplying = state;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConsumptionEnabled(Boolean state) {
		charging = state;
	}

	

}
