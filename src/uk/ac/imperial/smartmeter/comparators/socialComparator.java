package uk.ac.imperial.smartmeter.comparators;

import java.util.Comparator;

import uk.ac.imperial.smartmeter.res.UserAgent;

/**
 * Simple comparator to compare two users by their social worth
 * The user that has a greater social worth is treated as larger.
 * @author bwindo
 *
 */
public class socialComparator implements Comparator<UserAgent> {

	public int compare(UserAgent o1, UserAgent o2) {
		int ret = 0;
		Double s = o1.getSocialWorth();
		Double p = o2.getSocialWorth();
		if (s ==p) {ret =   0;}
		if (s < p) { ret = -1;}
		if (s > p) { ret =  1;}
		return ret;
	}

}
