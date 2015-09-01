package uk.ac.imperial.smartmeter.electricityprofile;

import java.io.Serializable;
/**
 * An enum to list all possible types of profile that are modelled in the system.
 * @author bwindo
 *
 */
public enum ProfileType implements Serializable{
		UNIFORM,
		LIGHT,
		LED,
		DISHWASHER,
		STOVE,
		BATTERY
}
