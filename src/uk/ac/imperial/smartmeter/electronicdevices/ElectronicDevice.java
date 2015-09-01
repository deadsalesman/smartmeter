package uk.ac.imperial.smartmeter.electronicdevices;

import java.io.Serializable;

import uk.ac.imperial.smartmeter.impl.EDController;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

/**
 * Represents an ElectronicDevice, to be stored in the ElectronicDeviceController.
 * @author bwindo
 * @see EDController
 */
public interface ElectronicDevice extends UniqueIdentifierIFace, Serializable{
 
	/**
	 * Gets the type of device represented by this object.
	 * @return The type of device.
	 */
	public DeviceType getType();
	/**
	 * Gets the id of the device represented by this object.
	 * @return The id of the device.
	 */
	public String getId();
}