package uk.ac.imperial.smartmeter.comparators;

import java.util.Comparator;

import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.UserAgent;

/**
 * Simple comparator to compare two users by their electricity required
 * The user that has requested the most electricity is treated as larger.
 * @author bwindo
 * 
 */
public class needsComparator implements Comparator<UserAgent> {

	public int compare(UserAgent o1, UserAgent o2) {
		int ret = 0;
		Double s = 0.;
		Double p = 0.;
		//could totally memoise/refactor this because as it is, ouch?
		for (ElectricityRequirement r : o1.getReqs())
		{
			s += r.getMaxConsumption();
		}
		for (ElectricityRequirement r : o2.getReqs())
		{
			p +=r.getMaxConsumption();
		}
		if (s ==p) {ret =    0;}
		if (s < p) { ret =  -1;}
		if (s > p) { ret =   1;}
		return ret;
	}

}