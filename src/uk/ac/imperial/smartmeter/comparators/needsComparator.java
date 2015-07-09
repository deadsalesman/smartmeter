package uk.ac.imperial.smartmeter.comparators;

import java.util.Comparator;

import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

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
		if (s ==p) { ret =  0;}
		if (s < p) { ret =  1;}
		if (s > p) { ret = -1;}
		return ret;
	}

}