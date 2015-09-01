package uk.ac.imperial.smartmeter.electronicdevices;

import java.io.Serializable;
/**
 * An enum to list all possible types of device that are modelled in the system.
 * @author bwindo
 *
 */
public enum DeviceType implements Serializable{
	UNIFORM,
	LIGHT,
	LED,
	DISHWASHER,
	STOVE,
	BATTERY
}