package uk.ac.imperial.smartmeter.electricityprofile;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A utility class to contain a mapping from the value of the {@link ProfileType} enum to their respective {@link ConsumptionProfile}.
 * @author bwindo
 *
 */
@SuppressWarnings("unchecked")
public abstract class ProfileList {
	public static final Map<Integer, Class<? extends ConsumptionProfile>> profileMap = new HashMap<Integer,Class<? extends ConsumptionProfile>>();
	/**
	* Populates the immutable mapping from index in ProfileType to Class.
 	*/
	static {
		int i = 0;
		for (ProfileType profile : ProfileType.values())
		{
			
			try {
				profileMap.put(i, (Class<? extends ConsumptionProfile>) Class.forName("uk.ac.imperial.smartmeter.electricityprofile."+profile.name().substring(0, 1).toUpperCase()+profile.name().substring(1,profile.name().length()).toLowerCase()+"ConsumptionProfile"));
				i++;
			} catch (ClassNotFoundException e) {
				System.out.println(profile.name() + " does not have an associated profile");
			}
		}
	}
	/**
	 * Gets the index in {@link ProfileType} that represents the type of the input profile.
	 * @param profile The profile to find the index of.
	 * @return The index of the given profile in {@link ProfileType}.
	 */
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
}
