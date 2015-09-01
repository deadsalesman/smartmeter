package uk.ac.imperial.smartmeter.electricityprofile;

import java.lang.reflect.Constructor;


/**
 * Utility class used to generate different types of ConsumptionProfile 
 * @author bwindo
 * @see ProfileType
 * @see ConsumptionProfile
 */
public class ElectricityProfileFactory {

	/**
	 * Gets a ConsumptionProfile based on the index of the associated profile in ProfileType.
	 * @param type The index of the profile.
	 * @return A new consumption profile based on the type determined by the given parameter.
	 */
	public static ConsumptionProfile getProfile(Integer type)
	{
		return getProfile(ProfileType.values()[type].toString());
	}
	/**
	 * Gets a ConsumptionProfile based on the index of the associated profile in ProfileType.
	 * @param type The index of the profile.
	 * @param dur The duration of the new ConsumptionProfile.
	 * @param amp The amplitude of the new ConsumptionProfile.
	 * @return A new consumption profile based on the type determined by the given parameter.
	 */
	public static ConsumptionProfile getProfile(Integer type, Double dur, Double amp)
	{
		ProfileType x;
		try {
			x = ProfileType.values()[type];
		} catch(ArrayIndexOutOfBoundsException e)
		{
			x = ProfileType.values()[0];
		}
		return getProfile(x.toString(),dur, amp);
		
	}
	/**
	 * Gets a ConsumptionProfile based on the name of the associated profile in ProfileType.
	 * @param type The name of the profile.
	 * @param dur The duration of the new ConsumptionProfile.
	 * @param amp The amplitude of the new ConsumptionProfile.
	 * @return A new consumption profile based on the type determined by the given parameter.
	 */
	public static ConsumptionProfile getProfile(String type, Double dur, Double amp)
	{
		ProfileType x = ProfileType.valueOf(type);
		try{
			String name = "uk.ac.imperial.smartmeter.electricityprofile." + x.name().substring(0, 1).toUpperCase()+x.name().substring(1,x.name().length()).toLowerCase()+"ConsumptionProfile";
			@SuppressWarnings("unchecked")
			Constructor<ConsumptionProfile> c = (Constructor<ConsumptionProfile>) Class.forName(
					name
					).getConstructor(Double.TYPE, Double.TYPE);
			ConsumptionProfile foo = c.newInstance(dur, amp);
			return foo;
		} catch (Exception e)
		{
			return new UniformConsumptionProfile(dur, amp);
		}
	}
	/**
	 * Gets a ConsumptionProfile based on the name of the associated profile in ProfileType.
	 * @param type The name of the profile.
	 * @return A new consumption profile based on the type determined by the given parameter.
	 */
	public static ConsumptionProfile getProfile(String type)
	{
		return getProfile(type, 0., 0.);
	}
}
