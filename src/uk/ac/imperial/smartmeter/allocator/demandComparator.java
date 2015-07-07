package uk.ac.imperial.smartmeter.allocator;

import java.util.Comparator;

public class demandComparator implements Comparator<UserAgent> {

	public int compare(UserAgent o1, UserAgent o2) {
		int ret = 0;
		Double s = o1.getGeneratedPower();
		Double p = o2.getGeneratedPower();
		ret = (s>p)?  1 : 0;
		ret = (s<p)? -1 : 0;
		return ret;
	}

}
