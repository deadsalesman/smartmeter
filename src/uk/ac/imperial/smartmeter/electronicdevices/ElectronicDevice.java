package uk.ac.imperial.smartmeter.electronicdevices;

import java.io.Serializable;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;


public interface ElectronicDevice extends UniqueIdentifierIFace, Serializable{

	public DeviceType getType();
	public String getId();
}