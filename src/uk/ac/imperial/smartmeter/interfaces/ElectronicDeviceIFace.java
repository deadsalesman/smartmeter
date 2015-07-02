package uk.ac.imperial.smartmeter.interfaces;


public interface ElectronicDeviceIFace {
	public double getConsumptionRate();
	public Boolean getState();
	public void setState(Boolean newState);
}
