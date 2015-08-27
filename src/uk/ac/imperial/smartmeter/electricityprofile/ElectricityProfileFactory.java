package uk.ac.imperial.smartmeter.electricityprofile;

import java.lang.reflect.Constructor;



public class ElectricityProfileFactory {

	public static ConsumptionProfile getProfile(Integer type)
	{
		return getProfile(ProfileType.values()[type].toString());
	}
	public static ConsumptionProfile getProfile(int type, Double dur, Double amp)
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
	public static ConsumptionProfile getProfile(String type)
	{
		return getProfile(type, 0., 0.);
	}
}
