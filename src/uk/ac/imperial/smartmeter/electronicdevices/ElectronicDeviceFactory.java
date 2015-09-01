package uk.ac.imperial.smartmeter.electronicdevices;

import java.lang.reflect.Constructor;
import java.util.UUID;
/**
 * Utility class used to generate different types of ElectronicConsumerDevice 
 * @author bwindo
 * @see DeviceType
 * @see ElectronicConsumerDevice
 */
public class ElectronicDeviceFactory {
	/**
	 * Gets a {@link ElectronicConsumerDevice} based on the index of the associated device in {@link DeviceType}.
	 * @param deviceType The index of the device.
	 * @return A new {@link ElectronicConsumerDevice} based on the type determined by the given parameter.
	 */
	public static ElectronicConsumerDevice getDevice(Integer deviceType)
	{
		DeviceType x;
		try {
			x = DeviceType.values()[deviceType];
		} catch(ArrayIndexOutOfBoundsException e)
		{
			x = DeviceType.values()[0];
		}
		return getDevice(x.toString());
		
	}
	/**
	 * Gets a {@link ElectronicConsumerDevice} based on the index of the associated device in {@link DeviceType}.
	 * @param deviceType The index of the device.
	 * @param id The string representation of the {@link UUID} to be used for the returned device.
	 * @param initialState The initial state of the returned device.
	 * @return A new {@link ElectronicConsumerDevice} based on the type determined by the given parameter.
	 */
	public static ElectronicConsumerDevice getDevice(int deviceType, String id, Boolean initialState)
	{
		return getDevice(DeviceType.values()[deviceType].toString(),id, initialState);
	}
	/**
	 * Gets a {@link ElectronicConsumerDevice} based on the name of the associated device in {@link DeviceType}.
	 * @param deviceType The name of the device.
	 * @param id The string representation of the {@link UUID} to be used for the returned device.
	 * @param initialState The initial state of the returned device.
	 * @return A new {@link ElectronicConsumerDevice} based on the type determined by the given parameter.
	 */
	public static ElectronicConsumerDevice getDevice(String deviceType, String id, Boolean initialState)
	{
		if (id == null)
		{
			id = UUID.randomUUID().toString();
		}
		
		DeviceType x = DeviceType.valueOf(deviceType);
		try{

			String name = "uk.ac.imperial.smartmeter.electronicdevices." + x.name().substring(0, 1).toUpperCase()+x.name().substring(1,x.name().length()).toLowerCase();
			@SuppressWarnings("unchecked")
			Constructor<ElectronicConsumerDevice> c = (Constructor<ElectronicConsumerDevice>) Class.forName(
					name
					).getConstructor(String.class, Boolean.class);
			ElectronicConsumerDevice foo = c.newInstance(id, initialState);
			return foo;
		} catch (Exception e)
		{
			return new Uniform(id, initialState);
		}
	}
	/**
	 * Gets a {@link ElectronicConsumerDevice} based on the name of the associated device in {@link DeviceType}.
	 * @param deviceType The name of the device.
	 * @return A new {@link ElectronicConsumerDevice} based on the type determined by the given parameter.
	 */
	public static ElectronicConsumerDevice getDevice(String deviceType)
	{
		return getDevice(deviceType, null, false);
	}
}
