package uk.ac.imperial.smartmeter.electricityprofile;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public abstract class ProfileList {
	public static final Map<Integer, Class<? extends ConsumptionProfile>> profileMap = new HashMap<Integer,Class<? extends ConsumptionProfile>>();

	static {
		profileMap.put(0, UniformConsumptionProfile.class);
		profileMap.put(1, LightConsumptionProfile.class);
		profileMap.put(2, LEDConsumptionProfile.class);
		profileMap.put(3, DishwasherConsumptionProfile.class);
		profileMap.put(4, StoveConsumptionProfile.class);
		profileMap.put(5, BatteryConsumptionProfile.class);
	}
	static public int getCode(ConsumptionProfile profile)
	{
		int ret = -1;
		for (Entry<Integer, Class<? extends ConsumptionProfile>> entry : profileMap.entrySet()) {
	        if (profile.getClass().equals(entry.getValue())) {
	            ret = entry.getKey();
	        }
	    }
		return ret;
	}
	static public int getLength()
	{
		return profileMap.size();
	}
	ProfileList()
	{
		
	}
}
