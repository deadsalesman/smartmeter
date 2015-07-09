package uk.ac.imperial.smartmeter.comparators;

import java.util.Comparator;

import uk.ac.imperial.smartmeter.allocator.UserAgent;

public class demandComparator implements Comparator<UserAgent> {

	public int compare(UserAgent o1, UserAgent o2) {
		int ret = 0;
		Double s = o1.getMaxPower();
		Double p = o2.getMaxPower();
		if (s ==p) { ret =   0;}
		if (s < p) { ret =  -1;}
		if (s > p) { ret =   1;}
		return ret;
	}

}
