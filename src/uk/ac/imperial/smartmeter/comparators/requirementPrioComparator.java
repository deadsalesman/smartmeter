package uk.ac.imperial.smartmeter.comparators;

import java.util.Comparator;

import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

/**
 * Simple comparator to compare two electricity requirements by their priorities
 * The requirement that has a higher priority is treated as larger.
 * @author bwindo
 *
 */
public class requirementPrioComparator implements Comparator<ElectricityRequirement> {

	@Override
	public int compare(ElectricityRequirement o1, ElectricityRequirement o2) {
		int ret = 0;
		if (o1.getPriority()==o2.getPriority()){ret = 0;}
		if (o1.getPriority()>o2.getPriority()){ret = 1;}
		if (o1.getPriority()<o2.getPriority()){ret = -1;}
		return ret;
	}
	

}
