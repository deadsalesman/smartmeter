package uk.ac.imperial.smartmeter.interfaces;

import uk.ac.imperial.smartmeter.res.*;

public interface LocalControllerIFace {
	public Boolean registerDeviceController(); //Not sure how best to implement this. RMI? Webserver?
	public void addRequirement(ElectricityRequirement req);
	public ElectricityRequirement getRequirement(int index);
	public void removeRequirement(int index);
	public void main(String[] args);
	public void pushToDB();
	public void pullFromDB();
}
