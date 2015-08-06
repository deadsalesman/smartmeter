package uk.ac.imperial.smartmeter.allocator;

import java.util.Date;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

public class QuantumNode implements TimeNode {
	private ArraySet<ElectricityRequirement> reqs;
	private Double currentCapacity;
	private Double maxCapacity;
	private Date startTime;
	public static final Integer quanta = 360000; //ms, MUST be a factor of 3600 (seconds in an hour)
	private Date endTime;
	public QuantumNode(Double cap, Date start)
	{
		reqs = new ArraySet<ElectricityRequirement>();
		startTime = start;
		endTime = new Date(startTime.getTime()+quanta);
		currentCapacity = cap;
		maxCapacity = cap;
	}
	public Integer getQuanta()
	{
		return quanta;
	}
	public boolean addReq(ElectricityRequirement e)
	{
		
		Double consumption = Math.max(e.getConsumption(startTime),e.getConsumption(endTime));
		if (consumption <= currentCapacity)
		{
			reqs.add(e);
			currentCapacity -= consumption;
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
		currentCapacity += e.getConsumption(startTime);
	}
	public Date getSoonestFinishingTime()
 {
		Date d = null;
		if (reqs.getSize() == 0) {
			d = endTime;
		} else {
			d = reqs.get(0).getEndTime();
			for (ElectricityRequirement e : reqs) {
				if (e.getEndTime().before(d)) {
					d = e.getEndTime();
				}
			}
		}
		return d;
	}
	public Double getCapacity()
	{
		return currentCapacity;
	}
	public Date getStartTime() {
		return startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public Double getMaxCapacity() {
		return maxCapacity;
	}
}
