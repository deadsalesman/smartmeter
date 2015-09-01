package uk.ac.imperial.smartmeter.comparators;

import java.util.Comparator;

import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
/**
 * Simple comparator to compare two electricity requirements by their end times
 * The requirement that ends last is treated as larger.
 * @author bwindo
 *
 */
public class requirementEndComparator implements Comparator<ElectricityRequirement> {

	@Override
	public int compare(ElectricityRequirement o1, ElectricityRequirement o2) {
		int ret = 0;
		if (o1.getEndTime()==o2.getEndTime()){ret = 0;}
		if (o1.getEndTime().after(o2.getEndTime())){ret = 1;}
		if (o1.getEndTime().before(o2.getEndTime())){ret = -1;}
		return ret;
	}
	

}

