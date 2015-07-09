package uk.ac.imperial.smartmeter.allocator;

import java.util.Date;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

public class QuantumNode implements TimeNode {
	private ArraySet<ElectricityRequirement> reqs;
	private Double capacity;
	private Date startTime;
	public static final Integer quanta = 3600000; //ms, MUST be a factor of 3600 (seconds in an hour)
	private Date endTime;
	public QuantumNode(Double cap, Date start)
	{
		reqs = new ArraySet<ElectricityRequirement>();
		startTime = start;
		endTime = new Date(startTime.getTime()+quanta);
		capacity = cap;
	}
	public QuantumNode(Date start)
	{
		this(3.,start);
	}
	public Integer getQuanta()
	{
		return quanta;
	}
	public boolean addReq(ElectricityRequirement e)
	{
		
		Double max = e.getMaxConsumption();
		if (max <= capacity)
		{
			reqs.add(e);
			capacity -= e.getMaxConsumption();
			return true;
		}
		else 
		{
			return false;
		}
	}
	public void removeReq(int index)
	{
		ElectricityRequirement e = reqs.remove(index);
		capacity += e.getMaxConsumption();
	}
	public Double getCapacity()
	{
		return capacity;
	}
	public Date getStartTime() {
		return startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
}
