package uk.ac.imperial.smartmeter.res;

import java.util.ArrayList;

public class ElectricityRequirementList {
	private ArrayList<ElectricityRequirement> reqList;
	
	public void addRequirement(ElectricityRequirement dev) {
		reqList.add(dev);
	}

	public void removeRequirement(int index) {
		try {
		reqList.remove(index);
		}
		catch (IndexOutOfBoundsException e){
			System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectrictyRequirementList");
		}
	}
}
