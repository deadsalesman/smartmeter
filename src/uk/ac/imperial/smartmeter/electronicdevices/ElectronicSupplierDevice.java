package uk.ac.imperial.smartmeter.electronicdevices;

import uk.ac.imperial.smartmeter.res.ElectricityGeneration;

public interface ElectronicSupplierDevice extends ElectronicDevice {
	public double getMaxGenerationRate();
	public Boolean getSupplyEnabled();
	void setSupplyEnabled(Boolean state);
	public ElectricityGeneration getSupplyProfile();
}
