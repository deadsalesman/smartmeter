package uk.ac.imperial.smartmeter.allocator;

import java.util.Date;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

/**
 * Basic unit of time with associated requirements and electrical capacity
 * @author Ben Windo
 * @see DayNode
 * @see CalendarQueue
 */
public class QuantumNode implements TimeNode {
	private ArraySet<ElectricityRequirement> reqs;
	private Double currentCapacity;
	private Double maxCapacity;
	private Date startTime;
	public static final Integer quanta = 36000; //ms, MUST be a factor of 3600 (seconds in an hour)
	private Date endTime;
	/**
	 * Initialises the node with the given parameters
	 * @param cap   Maximum capacity for the node
	 * @param start Date when node 'starts', has a duration of {@value QuantumNode#quanta}
	 */
	public QuantumNode(Double cap, Date start)
	{
		reqs = new ArraySet<ElectricityRequirement>();
		startTime = start;
		endTime = new Date(startTime.getTime()+quanta);
		currentCapacity = cap;
		maxCapacity = cap;
	}
	/**
	 * Creates a clone of an existing node
	 * @param q Node to be cloned
	 */
	public QuantumNode(QuantumNode q) {
		startTime = q.startTime;
		endTime = q.endTime;
		currentCapacity = q.currentCapacity;
		maxCapacity = q.maxCapacity;
		reqs = new ArraySet<ElectricityRequirement>(q.reqs);
	}
	public Integer getQuanta()
	{
		return quanta;
	}
	/**
	 * Adds an ElectricityRequirement to the list of active requirements 
	 * IFF its consumption at this time does not exceed the maximum capacity of the node
	 * @param e ElectricityRequirement in question
	 * @return whether the node has sufficient free capacity
	 */
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
	/**
	 * Removes an ElectricityRequirement from the list of active requirements based on id
	 * Restores current consumption to the state without the ElectricityRequirement
	 * @param id String version of the id of the req to be removed
	 * @return unconditionally true
	 *  
	 */
	public Boolean removeReq(String id)
	{
		for (ElectricityRequirement r : reqs)
		{
			if (r.getId().equals(id))
			{
				reqs.remove(r);
				currentCapacity += Math.max(r.getConsumption(startTime),r.getConsumption(endTime));
				return true;
			}
		}
		return true; //if you want to remove something and it was never there, have you succeeded?
	}
	/**
	 * @deprecated
	 * Removes an ElectricityRequirement from the list of active requirements based on index
	 * Restores current consumption to the state without the ElectricityRequirement
	 * @param index the position in the list of the req to be removed
	 * @return unconditional true
	 */
	public Boolean removeReq(int index)
	{
		ElectricityRequirement e = reqs.remove(index);
		currentCapacity += e.getConsumption(startTime);
		return true;
	}
	/**
	 * Of all the ElectricityRequirements in the array,
	 *  returns the time when the earliest one finishes
	 * 
	 * @return
	 */
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
