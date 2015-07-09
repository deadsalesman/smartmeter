package uk.ac.imperial.smartmeter.res;

import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class ElectricityRequirement implements UniqueIdentifierIFace{
	private final Date startTime;
	private final Date endTime;
	private final double duration;
	private final DecimalRating priority;
	private final UniformConsumptionProfile profile; //consumption assum
	private UUID reqID;
	private UUID userID;
	public int getProfileCode()
	{
		return ProfileList.getCode(profile);
	}
	
	public ElectricityRequirement(Date start, Date end, DecimalRating prio, String user)
	{
		this(start,end,prio,1,1,user,"");
	}
	public ElectricityRequirement(double amplitude)
	{
		this(new Date(), new Date(), new DecimalRating(2),1,amplitude,UUID.randomUUID().toString()); //DEBUG ONLY
	}
	public ElectricityRequirement(Date start, Date end)
	{
		this(start, end, new DecimalRating(5),1,1.,UUID.randomUUID().toString());
	}
	public ElectricityRequirement(Date start, Date end, DecimalRating prio, int profileId, double amplitude, String iDUser)
    {
		this(start, end, prio, profileId, amplitude, iDUser, UUID.randomUUID().toString());
	}
	public ElectricityRequirement(Date start, Date end, DecimalRating prio, int profileId, double amplitude, String iDUser,String idString )
	{
		startTime = start;
		endTime = end;
		duration = end.getTime() - start.getTime();
		priority = prio;
		profile = new UniformConsumptionProfile(duration, amplitude);
		userID = UUID.fromString(iDUser);
		if (idString == "")
		{
			reqID = UUID.randomUUID();
		}
		else
		{
			reqID = UUID.fromString(idString);
		}
	}
	public int getPriority() {
		return priority.getValue();
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
	public double getMaxConsumption()
	{
		return profile.amplitude;
	}
	public double getConsumption(double time)
	{
		return profile.getConsumption(time); //a classic example of where dependency injection might not be a bad idea
	}

	public String getId() {
		return reqID.toString();
	}

	public String getUserID() {
		return userID.toString();
	}

	public void setUserID(String id) {
	   userID = UUID.fromString(id);
	}

}
