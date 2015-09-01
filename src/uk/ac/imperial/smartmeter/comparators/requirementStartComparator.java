package uk.ac.imperial.smartmeter.comparators;

import java.util.Comparator;

import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

/**
 * Simple comparator to compare two electricity requirements by their start times
 * The requirement that starts last is treated as larger.
 * @author bwindo
 *
 */
public class requirementStartComparator implements Comparator<ElectricityRequirement> {

	@Override
	public int compare(ElectricityRequirement o1, ElectricityRequirement o2) {
		int ret = 0;
		if (o1.getStartTime()==o2.getStartTime()){ret = 0;}
		if (o1.getStartTime().after(o2.getStartTime())){ret = 1;}
		if (o1.getStartTime().before(o2.getStartTime())){ret = -1;}
		return ret;
	}
	

}
