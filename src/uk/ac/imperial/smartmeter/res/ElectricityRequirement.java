package uk.ac.imperial.smartmeter.res;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class ElectricityRequirement implements UniqueIdentifierIFace{
	private Date startTime;
	private Date endTime;
	private double duration;
	private DecimalRating priority;
	private UniformConsumptionProfile profile; //consumption assum
	private UUID reqID;
	private UUID userID;
	private Boolean tampered = false;
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
	public String toString()
	{
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	    String repr = 
			df.format(this.getStartTime()) + "," +
			df.format(this.getEndTime()) + "," +
			this.getPriority() + "," +
			this.getProfileCode() + "," +
			this.getMaxConsumption() + "," +
			this.getUserID() + "," +
			this.getId()
			;
		return repr;
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
		return duration/QuantumNode.quanta;
	}

	public Date getStartTime() {
		return startTime;
	}
	public Boolean getTampered()
	{
		return tampered;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setStartTime(Date d)
	{
		startTime = new Date(d.getTime());
		endTime = DateHelper.dPlus(d, duration/QuantumNode.quanta);
		tampered = true;
	}
	public double getMaxConsumption()
	{
		return profile.amplitude;
	}
	public double getConsumption(Date time)
	{
		return profile.getConsumption(startTime, time); //a classic example of where dependency injection might not be a bad idea
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
