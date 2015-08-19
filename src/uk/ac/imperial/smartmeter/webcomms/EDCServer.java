package uk.ac.imperial.smartmeter.webcomms;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import uk.ac.imperial.smartmeter.impl.EDCHandler;
import uk.ac.imperial.smartmeter.interfaces.EDCServerIFace;
import uk.ac.imperial.smartmeter.res.ElectronicDevice;

public class EDCServer implements EDCServerIFace{
	private int portNum;
	public EDCServer(int parseInt) {
		portNum = parseInt;
		handler = new EDCHandler();
		 if (System.getSecurityManager() == null) {
	            System.setSecurityManager(new RMISecurityManager());
	        }
		try {
			LocateRegistry.createRegistry(portNum);
		}
		catch (RemoteException e)
		{
			
		}
		try
		{

			EDCServerIFace stub = (EDCServerIFace) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry(portNum);
			registry.rebind("EDCServer", stub);
		}catch (RemoteException e)
		{
			System.out.println(e.getMessage());
		}
	}

	private EDCHandler handler;
	@Override
	public Boolean addDevice(ElectronicDevice ed, Integer pin) {
		return handler.addDevice(ed, pin);
	}
	@Override
	public Boolean setState(String deviceID, Boolean val) {
		return handler.setState(deviceID, val);
	}
	@Override
	public Boolean getState(String deviceID) {
		return handler.getState(deviceID);
	}
	@Override
	public Boolean removeDevice(String deviceID) {
		return handler.removeDevice(deviceID);
	}
	@Override
	public Boolean wipeEDC() {
		return handler.wipe();
	}
	@Override
	public ElectronicDevice getDevice(String deviceID) {
		return handler.getDevice(deviceID);
	}

}
