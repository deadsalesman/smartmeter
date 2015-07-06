package uk.ac.imperial.smartmeter.res;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ProfileList {
	public static final Map<Integer, Class<? extends ConsumptionProfile>> profileMap = new HashMap<Integer,Class<? extends ConsumptionProfile>>();

	static {
		profileMap.put(1, UniformConsumptionProfile.class);
	}
	static public int getCode(UniformConsumptionProfile profile)
	{
		int ret = -1;
		for (Entry<Integer, Class<? extends ConsumptionProfile>> entry : profileMap.entrySet()) {
	        if (profile.getClass().equals(entry.getValue().getClass())) {
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
