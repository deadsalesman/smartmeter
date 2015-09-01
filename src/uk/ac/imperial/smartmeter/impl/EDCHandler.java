package uk.ac.imperial.smartmeter.impl;

import java.util.UUID;

import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDevice;
import uk.ac.imperial.smartmeter.webcomms.EDCServer;
/**
 * Acts as an intermediary for the {@link EDController} and the {@link EDCServer}
 * The controller has public methods that the server does not need to know about, but are useful for testing.
 * Thus, in the absence of proper dependency inversion, this is used to encapsulate the controller.
 * @author bwindo
 *
 */
public class EDCHandler {
	
	private EDController controller;
	public EDCHandler()
	{
		controller = new EDController();
	}
	/**
	 * Gets the state of a specific  {@link ElectronicDevice}.
	 * @param deviceID The String representation of a {@link UUID} which identifies the specific device
	 * @return true if the  {@link ElectronicDevice} is on
	 */
	public Boolean getState(String deviceID)
 {
		try {
			return controller.getDeviceState(controller.getDeviceIndex(deviceID));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	/**
	 * Sets the state of a specific {@link ElectronicDevice}.
	 * @param deviceID The String representation of a {@link UUID} which identifies the specific device
	 * @param newState The new state to be adopted by the  {@link ElectronicDevice}.
	 * @return Success?
	 */
	public Boolean setState(String deviceID, Boolean newState) {
		try {
			return controller.setDeviceState(controller.getDeviceIndex(deviceID), newState);
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	/**
	 * Adds a new  {@link ElectronicDevice} to the controller's storage.
	 * @param newDevice The new  {@link ElectronicDevice} to be added.
	 * @param pin The GPIO pin that controls this specific device. Only certain values are allowed, given the physical properties of the raspberry pi controlling the devices. No two devices may be controlled by the same pin.
	 * @return Success?
	 */
	public Boolean addDevice(ElectronicConsumerDevice newDevice, Integer pin)
	{
		return controller.addDevice(newDevice, pin);
	}
	/**
	 * Removes a  {@link ElectronicDevice} from the controller's storage
	 * @param deviceID The  {@link ElectronicDevice} to be removed.
	 * @return Success?
	 */
	public Boolean removeDevice(String deviceID)
 {
		try {
			return controller.removeDevice(controller.getDeviceIndex(deviceID));
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	/**
	 * Gets a  {@link ElectronicDevice} from the controller.
	 * @param deviceID
	 * @return the requested  {@link ElectronicDevice}
	 */
	public ElectronicDevice getDevice(String deviceID)
	{
		return controller.getDevice(deviceID);
	}
	/**
	 * Removes all information from the controller and its associated database.
	 * @return Success?
	 */
	public Boolean wipe() {
		return controller.wipe();
	}
}
