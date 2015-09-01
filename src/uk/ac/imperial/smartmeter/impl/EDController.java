package uk.ac.imperial.smartmeter.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.smartmeter.db.DevicesDBManager;
import uk.ac.imperial.smartmeter.electronicdevices.DeviceType;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDevice;
import uk.ac.imperial.smartmeter.interfaces.ElectronicDeviceControllerIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
/**
 * Acts as a controller for the individual device controllers, setting the physical states of hardware.
 * @author bwindo
 * @see EDCHandler
 */
public class EDController 
	implements ElectronicDeviceControllerIFace, UniqueIdentifierIFace{
	/**
	 * A representation of all the devices controlled by this controller
	 */
	private ArraySet<ElectronicConsumerDevice> devices;
	/**
	 * A map of the devices controlled to the physical pins that they are mapped to.
	 */
	private Map<ElectronicConsumerDevice, Integer> pinouts;
	/**
	 * The manager for the local database of {@link ElectronicConsumerDevice}
	 */
	public DevicesDBManager db;
	private UUID id;
	/**
	 * A set of the physical GPIO pins that are available for use.
	 */
	private Set<Integer> availablePins;
	public EDController()
	{
		id = UUID.randomUUID();
		devices = new ArraySet<ElectronicConsumerDevice>();
		db = new DevicesDBManager("jdbc:sqlite:edc.db");
		pinouts = new HashMap<ElectronicConsumerDevice, Integer>();
		availablePins = setAvailablePins();
		pullFromDB();
		setAvailablePins();
	}
	/**
	 * Creates a Set<Integer> which contains the integer values corresponding to the numbers of the Raspberry Pi pins that can be used for General Purpose Input/Output.
	 * @return the available pins before any devices have been allocated.
	 */
	private Set<Integer> setAvailablePins()
	{
		HashSet<Integer> pins = new HashSet<Integer>();
		pins.add(3);
		pins.add(5);
		pins.add(7);
		pins.add(8);
		pins.add(10);
		pins.add(11);
		pins.add(12);
		pins.add(13);
		pins.add(15);
		pins.add(16);
		pins.add(18);
		pins.add(19);
		pins.add(21);
		pins.add(22);
		pins.add(23);
		pins.add(24);
		pins.add(26);
		return pins;
		
	}
	public String getId()
	{
		return id.toString();
	}
	public int getDeviceCount()
	{
		return devices.size();
	}
	/**
	 * Returns the index of a given device, if it in storage.
	 * @param deviceID The given device's id.
	 * @return the integer index of the device, -1 if it is not present.
	 */
	public int getDeviceIndex(String deviceID)
	{
		for (ElectronicConsumerDevice ed : devices)
		{
			if (ed.getId().equals(deviceID))
			{
				return devices.indexOf(ed);
			}
		}
		
		return -1;
	}
	/**
	 * Gets a  {@link ElectronicDevice} from the controller.
	 * @param deviceID
	 * @return the requested  {@link ElectronicDevice}
	 */
	public ElectronicConsumerDevice getDevice(String deviceID)
	{
		for (ElectronicConsumerDevice ed : devices)
		{
			if (ed.getId().equals(deviceID))
			{
				return ed;
			}
		}
		
		return null;
	}
	/**
	 * Gets the state of a specific  {@link ElectronicDevice}.
	 * @param index The String representation of a {@link UUID} which identifies the specific device
	 * @return true if the  {@link ElectronicDevice} is on
	 */
	@Override
	public Boolean getDeviceState(int index) {
		
		try {
			return devices.get(index).getConsumptionEnabled();
		}
		catch (IndexOutOfBoundsException e){
			//System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicConsumerDeviceController");
		}
		return null;
	}
	/**
	 * Adds a new  {@link ElectronicDevice} to the controller's storage.
	 * @param e The new  {@link ElectronicDevice} to be added.
	 * @param pin The GPIO pin that controls this specific device. Only certain values are allowed, given the physical properties of the raspberry pi controlling the devices. No two devices may be controlled by the same pin.
	 * @return Success?
	 */
	public Boolean addDevice(ElectronicConsumerDevice e, Integer pin) {
		if (db.insertElement(e)) {
			if (availablePins.contains(pin)) {
				availablePins.remove(pin);
				pinouts.put(e, pin);
				devices.add(e);
				return true;
			}
		}
		return false;
	}
	/**
	 * Removes all information from the controller and its associated database.
	 * @return Success?
	 */
	public Boolean wipe()
	{
		db.wipe();
		devices = new ArraySet<ElectronicConsumerDevice>();
		pinouts = new HashMap<ElectronicConsumerDevice, Integer>();
		availablePins = setAvailablePins();
		return true;
	}
	/**
	 * Removes a  {@link ElectronicDevice} from the controller's storage
	 * @param index The index of the {@link ElectronicDevice} to be removed.
	 * @return Success?
	 */
   public Boolean removeDevice(int index)
   {
	   ElectronicConsumerDevice x = devices.get(index);
	   if(db.removeElement(x))
	   {
		   availablePins.add(pinouts.get(x));
		   pinouts.remove(x);
		   return devices.remove(x);
	   }
	   else 
	   {
		   return false;
	   }
   }
   	/**
   	 * Returns the devicetype at a given position in EDController#devices
   	 * @param index The index to be examined.
   	 * @return the {@link DeviceType} at that location, null if ine index is out of bounds.
   	 */
	@Override
	public DeviceType getDeviceType(int index) {

		try {
			return devices.get(index).getType();
		}
		catch (IndexOutOfBoundsException e){
			//System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicConsumerDeviceController");
		}
		return null;
	}

	
	/**
	 * Sets the state of a specific {@link ElectronicDevice}.
	 * @param index The index of the device in EDController#devices.
	 * @param newState The new state to be adopted by the  {@link ElectronicDevice}.
	 * @return Success?
	 */
	@Override
	public Boolean setDeviceState(int index, Boolean newState) {

		try {
			ElectronicConsumerDevice ed = devices.get(index);
			ed.setConsumptionEnabled(newState);
			db.updateDeviceState(ed.hashCode(), newState);
			ProcessBuilder pb = new ProcessBuilder("python", "test1.py", "" + pinouts.get(ed), "" + pinouts.get(ed));
			Process p = pb.start();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			int ret = new Integer(in.readLine()).intValue();
			System.out.println("value is : " + ret);
			return true;
		} catch (IndexOutOfBoundsException e){
			//System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicConsumerDeviceController");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	
	/**
	 * Sets all devices of a certain type to a given state.
	 * @param type The type of device to set the state of.
	 * @param newState The state to set the devices to.
	 */
	@Override
	public void setDevicesOfType(DeviceType type, Boolean newState) {
		for (ElectronicConsumerDevice device : devices)
		{
			if (device.getType()==type)
			{
				device.setConsumptionEnabled(newState);
				db.updateDeviceState(device.getId().hashCode(), newState);
			}
		}
	}
	/**
	 * Registers with an external server.
	 */
	@Override
	public Boolean registerSmartMeter() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * Pushes all data to the local database when being garbage collected.
	 */
	protected void finalize() throws Throwable
	{
		pushToDB();
		super.finalize();
	}
	/**
	 * Updates the local database with the {@link ElectronicConsumerDevice} objects stored in the controller data structures.
	 */
	@Override
	public void pushToDB() {
		for (ElectronicConsumerDevice i : devices)
		{
			db.insertElement(i);
		}
		
	}
	/**
	 * Updates internal data structures with the {@link ElectronicConsumerDevice} objects stored in the local database.
	 */
	@Override
	public void pullFromDB() {
		ArrayList<ElectronicConsumerDevice>temp_array = db.extractAll();
		for (ElectronicConsumerDevice i : temp_array)
		{
			devices.add(i);
		}
	}
}
