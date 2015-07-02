package uk.ac.imperial.smartmeter.res;

import java.util.Date;

public class ElectricityRequirement {
	private final Date startTime;
	private final Date endTime;
	private final double duration;
	private final DecimalRating priority;
	private final ConsumptionProfile profile; //consumption assum
	
	public ElectricityRequirement(Date start, Date end, DecimalRating prio)
	{
		startTime = start;
		endTime = end;
		duration = end.getTime() - start.getTime();
		priority = prio;
		profile = new UniformConsumptionProfile();
	}

	public DecimalRating getPriority() {
		return priority;
	}
	public double getDuration() {
		return duration;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	public double getConsumption(double time)
	{
		return profile.getConsumption(time); //a classic example of where dependency injection might not be a bad idea
	}
}
