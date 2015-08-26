package uk.ac.imperial.smartmeter.electronicdevices;

import java.util.UUID;

import uk.ac.imperial.smartmeter.electricityprofile.BatteryConsumptionProfile;
import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;


public class Battery  implements ElectronicConsumerDevice, ElectronicSupplierDevice {


	/**
	 * 
	 */
	DeviceType type = DeviceType.Battery;
	Boolean charging = false;
	Boolean supplying = false;
	ElectricityGeneration supply;
	ConsumptionProfile demand;
	private UUID id = UUID.randomUUID();
	
	private static final long serialVersionUID = 2442756528890104616L;
	public Battery()
	{
	}
	public Battery(Double consume, Double generate)
	{

		demand = new BatteryConsumptionProfile(1., consume); //arbitrary units of charging
		supply = new ElectricityGeneration(generate);
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
	public double getMaxGenerationRate() {
		return supply.getMaxOutput();
	}

	@Override
	public Boolean getSupplyEnabled() {
		return supplying;
	}

	@Override
	public ElectricityGeneration getSupplyProfile() {
		return supply;
	}

	@Override
	public Boolean getConsumptionEnabled() {
		return charging;
	}

	@Override
	public void setSupplyEnabled(Boolean state) {
		supplying = state;
	}

	@Override
	public void setConsumptionEnabled(Boolean state) {
		charging = state;
	}

	

}
