package uk.ac.imperial.smartmeter.electronicdevices;

import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;

/**
 * Represents an {@link ElectronicDevice} that can extract energy from the grid.
 * @author bwindo
 *
 */
public interface ElectronicConsumerDevice extends ElectronicDevice {
	/**
	 * Gets the maximum instantaneous consumption of the device.
	 * @return The maximum consumption.
	 */
	public double getMaxConsumptionRate();
	/**
	 * Gets whether the device is currently consuming electricity.
	 * @return The state of the device.
	 */
	public Boolean getConsumptionEnabled();
	/**
	 * Sets the device to consume electricity or not.
	 * @param state The value the device's state should take.
	 */
	public void setConsumptionEnabled(Boolean state);
	/**
	 * Gets the current {@link ConsumptionProfile} of the device.
	 * @return The device's demand profile.
	 */
	public ConsumptionProfile getProfile();
}
