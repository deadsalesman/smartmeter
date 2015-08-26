package uk.ac.imperial.smartmeter.interfaces;

import java.rmi.RemoteException;

import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDevice;

public interface EDCServerIFace extends ServerIFace{
	Boolean setState(String deviceID, Boolean val) throws RemoteException;

	ElectronicDevice getDevice(String deviceID) throws RemoteException;

	Boolean getState(String deviceID) throws RemoteException;

	Boolean removeDevice(String deviceID) throws RemoteException;

	Boolean wipeEDC() throws RemoteException;

	Boolean addDevice(ElectronicConsumerDevice ed, Integer pin) throws RemoteException;
}
