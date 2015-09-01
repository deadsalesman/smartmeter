package uk.ac.imperial.smartmeter.electronicdevices;

import uk.ac.imperial.smartmeter.res.ElectricityGeneration;

/**
 * Represents an {@link ElectronicDevice} that can provide electricity to the grid.
 * @author bwindo
 *
 */
public interface ElectronicSupplierDevice extends ElectronicDevice {
	/**
	 * Gets the maximum instantaneous generation of the device.
	 * @return The maximum generation.
	 */
	public double getMaxGenerationRate();

	/**
	 * Gets whether the device is currently supplying electricity.
	 * @return The state of the device.
	 */
	public Boolean getSupplyEnabled();
	/**
	 * Sets the device to supply electricity or not.
	 * @param state The value the device's state should take.
	 */
	void setSupplyEnabled(Boolean state);
	/**
	 * Gets the current {@link ElectricityGeneration} of the device.
	 * @return The device's supply profile.
	 */
	public ElectricityGeneration getSupplyProfile();
}
