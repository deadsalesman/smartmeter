package uk.ac.imperial.smartmeter.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LCServerIFace extends Remote {
	public String getMessage() throws RemoteException;
}
