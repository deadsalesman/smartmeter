package uk.ac.imperial.smartmeter.interfaces;

import java.rmi.RemoteException;

import uk.ac.imperial.smartmeter.res.ElectronicDevice;

public interface EDCServerIFace extends ServerIFace{
	Boolean addDevice(ElectronicDevice ed, Integer pin) throws RemoteException;

	Boolean setState(String deviceID, Boolean val) throws RemoteException;

	ElectronicDevice getDevice(String deviceID) throws RemoteException;

	Boolean getState(String deviceID) throws RemoteException;

	Boolean removeDevice(String deviceID) throws RemoteException;

	Boolean wipeEDC() throws RemoteException;
}
