package uk.ac.imperial.smartmeter.comparators;

import java.util.Comparator;

import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

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

