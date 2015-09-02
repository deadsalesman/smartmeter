package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDevice;
import uk.ac.imperial.smartmeter.impl.EDCHandler;
import uk.ac.imperial.smartmeter.interfaces.EDCServerIFace;
/**
 * ElectronicDeviceController server implementation
 * Handles all communications and control of electronic devices.
 * @author Ben Windo
 *
 */
public class EDCServer implements EDCServerIFace{
	private int portNum;
	/**
	 * Creates a new EDCServer and initialises security settings. Exports RMI facilities and listens on a given port.
	 * @param parseInt The port RMI listens on.
	 */
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean addDevice(ElectronicConsumerDevice ed, Integer pin) {
		return handler.addDevice(ed, pin);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean setState(String deviceID, Boolean val) {
		return handler.setState(deviceID, val);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getState(String deviceID) {
		return handler.getState(deviceID);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean removeDevice(String deviceID) {
		return handler.removeDevice(deviceID);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean wipeEDC() {
		return handler.wipe();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElectronicDevice getDevice(String deviceID) {
		return handler.getDevice(deviceID);
	}
	/**
	 * Instantiates an EDCServer for standalone testing
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java EDContNode <int port number>");
			System.exit(1);
		}

		System.setProperty("java.rmi.server.hostname", DefaultTestClient.ipAddr); 
		@SuppressWarnings("unused")
		EDCServer client = new EDCServer(Integer.parseInt(args[0]));

		System.out.println("Device Server listening on : " + args[0]);
		while(true)
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
