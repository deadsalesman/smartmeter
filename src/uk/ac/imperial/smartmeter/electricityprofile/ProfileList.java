package uk.ac.imperial.smartmeter.electricityprofile;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


@SuppressWarnings("unchecked")
public abstract class ProfileList {
	public static final Map<Integer, Class<? extends ConsumptionProfile>> profileMap = new HashMap<Integer,Class<? extends ConsumptionProfile>>();

	static {
		int i = 0;
		for (ProfileType profile : ProfileType.values())
		{
			
			try {
				profileMap.put(i, (Class<? extends ConsumptionProfile>) Class.forName("uk.ac.imperial.smartmeter.electricityprofile."+profile.name().substring(0, 1).toUpperCase()+profile.name().substring(1,profile.name().length()).toLowerCase()+"ConsumptionProfile"));
				i++;
			} catch (ClassNotFoundException e) {
				System.out.println("sa");
			}
		}
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
