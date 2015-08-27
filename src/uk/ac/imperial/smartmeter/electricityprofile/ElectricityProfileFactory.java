package uk.ac.imperial.smartmeter.electricityprofile;



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
		switch(type)
		{
		case "UNIFORM":
			return new UniformConsumptionProfile(dur, amp);
		case "BATTERY":
			return new BatteryConsumptionProfile(dur, amp);
		case "LED":
			return new LEDConsumptionProfile(dur, amp);
		case "DISHWASHER":
			return new DishwasherConsumptionProfile(dur, amp);
		case "STOVE":
			return new StoveConsumptionProfile(dur, amp);
		case "LIGHT":
			return new LightConsumptionProfile(dur, amp);
		default: 
			return null;
		}
	}
	public static ConsumptionProfile getProfile(String type)
	{
		return getProfile(type, 0., 0.);
	}
}
