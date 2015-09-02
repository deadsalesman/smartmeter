package uk.ac.imperial.smartmeter.interfaces;

import java.rmi.RemoteException;
import java.util.UUID;

import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDevice;

public interface EDCServerIFace extends ServerIFace{
	/**
	 * Sets the state of a specific {@link ElectronicDevice}.
	 * @param deviceID The String representation of a {@link UUID} which identifies the specific device
	 * @param newState The new state to be adopted by the  {@link ElectronicDevice}.
	 * @return Success?
	 */
	Boolean setState(String deviceID, Boolean newState) throws RemoteException;
	
	/**
	 * Gets a  {@link ElectronicDevice} from the controller.
	 * @param deviceID
	 * @return the requested  {@link ElectronicDevice}
	 */
	ElectronicDevice getDevice(String deviceID) throws RemoteException;

	/**
	 * Gets the state of a specific  {@link ElectronicDevice}.
	 * @param deviceID The String representation of a {@link UUID} which identifies the specific device
	 * @return true if the  {@link ElectronicDevice} is on
	 */
	Boolean getState(String deviceID) throws RemoteException;

	/**
	 * Removes a  {@link ElectronicDevice} from the controller's storage
	 * @param deviceID The  {@link ElectronicDevice} to be removed.
	 * @return Success?
	 */
	Boolean removeDevice(String deviceID) throws RemoteException;
	
	/**
	 * Removes all information from the controller and its associated database.
	 * @return Success?
	 */
	Boolean wipeEDC() throws RemoteException;

	/**
	 * Adds a new  {@link ElectronicDevice} to the controller's storage.
	 * @param newDevice The new  {@link ElectronicDevice} to be added.
	 * @param pin The GPIO pin that controls this specific device. Only certain values are allowed, given the physical properties of the raspberry pi controlling the devices. No two devices may be controlled by the same pin.
	 * @return Success?
	 */
	Boolean addDevice(ElectronicConsumerDevice newDevice, Integer pin) throws RemoteException;
}
