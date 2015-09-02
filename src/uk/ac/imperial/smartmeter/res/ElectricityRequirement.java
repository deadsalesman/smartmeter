package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;
import uk.ac.imperial.smartmeter.electricityprofile.ElectricityProfileFactory;
import uk.ac.imperial.smartmeter.electricityprofile.ProfileList;
import uk.ac.imperial.smartmeter.electricityprofile.ProfileType;
import uk.ac.imperial.smartmeter.electronicdevices.DeviceList;
import uk.ac.imperial.smartmeter.electronicdevices.DeviceType;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDeviceFactory;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

/**
 * A class representing a requirement for electricity for a particular period of time.
 * @author bwindo
 *
 */
public class ElectricityRequirement implements UniqueIdentifierIFace, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4338678770637216607L;
	private Date startTime;
	private Date endTime;
	private double duration;
	/**
	 * A {@link DecimalRating} representing the importance of the requirement.
	 */
	private DecimalRating priority;
	/**
	 * A {@link ConsumptionProfile} representing the shape of the requirement. For example, it might have a constant demand, or a demand that gradually increases.
	 */
	private ConsumptionProfile profile; //consumption assum
	private UUID reqID;
	private UUID userID;
	private Boolean tampered = false;
	/**
	 * The physical {@link ElectronicConsumerDevice} that needs the requirement described in the associated instance of this object.
	 */
	public ElectronicConsumerDevice device;
	/**
	 * @return the position in {@link ProfileType} that the current {@link ElectricityRequirement#profile} represents.
	 */
	public int getProfileCode()
	{
		return ProfileList.getCode(profile);
	}
	/**
	 * 
	 * @return the position in {@link DeviceType} that the current {@link ElectricityRequirement#device} represents.
	 */
	public int getDeviceCode()
	{
		return DeviceList.getCode(device);
	}
	/**
	 * Debug ctor that is used in testing.
	 * @param amplitude The amplitude to set the device's profile to consume.
	 */
	public ElectricityRequirement(double amplitude)
	{
		this(new Date(), new Date(), new DecimalRating(2),1,amplitude,UUID.randomUUID().toString()); //DEBUG ONLY
	}
	/**
	 * @return A String representation of the current ElectricityRequirement.
	 */
	public String toString()
	{
	    String repr = 
			String.valueOf(this.getStartTime().getTime()) + "," +
			String.valueOf(this.getEndTime().getTime()) + "," +
			this.getPriority() + "," +
			this.getProfileCode() + "," +
			this.getMaxConsumption() + "," +
			this.getUserID() + "," +
			this.getId() + ","
			;
		return repr;
	}
	/**
	 * Debug ctor that is used in testing.
	 * @param start The start time of the requirement.
	 * @param end The end time of the requirement.
	 */
	public ElectricityRequirement(Date start, Date end)
	{
		this(start, end, new DecimalRating(5),1,1.,UUID.randomUUID().toString());
	}
	/**
	 * Returns a new ElectricityRequirement based on the given parameters.
	 * @param start The start time of the requirement.
	 * @param end The end time of the requirement.
	 * @param prio A rating representing the importance of the requirement.
	 * @param profileId The type of {@link ConsumptionProfile} to use, as defined in {@link DeviceType}.
	 * @param amplitude The amplitude to set the device's profile to consume.
	 * @param iDUser The identity of the {@link UserAgent} that owns the requirement.
	 */
	public ElectricityRequirement(Date start, Date end, DecimalRating prio, int profileId, double amplitude, String iDUser)
    {
		this(start, end, prio, profileId, amplitude, iDUser, UUID.randomUUID().toString());
	}
	/**
	 * Returns a new ElectricityRequirement based on the given parameters.
	 * @param start The start time of the requirement.
	 * @param end The end time of the requirement.
	 * @param prio A rating representing the importance of the requirement.
	 * @param profileId The type of {@link ConsumptionProfile} to use, as defined in {@link DeviceType}.
	 * @param amplitude The amplitude to set the device's profile to consume.
	 * @param iDUser The identity of the {@link UserAgent} that owns the requirement.
	 * @param idString A string representing the {@link UUID} to be adopted by the new ElectricityRequirement.
	 */
	public ElectricityRequirement(Date start, Date end, DecimalRating prio, int profileId, double amplitude, String iDUser,String idString )
	{
	 this(start, end, prio, amplitude, iDUser, idString, ElectronicDeviceFactory.getDevice(profileId));
	}
	/**
	 * Returns a new ElectricityRequirement based on the given parameters.
	 * @param start The start time of the requirement.
	 * @param end The end time of the requirement.
	 * @param prio A rating representing the importance of the requirement.
	 * @param amplitude The amplitude to set the device's profile to consume.
	 * @param iDUser The identity of the {@link UserAgent} that owns the requirement.
	 * @param idString A string representing the {@link UUID} to be adopted by the new ElectricityRequirement.
	 * @param d The {@link ElectronicConsumerDevice} that needs the requirement described in the associated instance of this object
	 */
	public ElectricityRequirement(Date start, Date end, DecimalRating prio, double amplitude, String iDUser,String idString, ElectronicConsumerDevice d)
	{
		device = d;
		startTime = start;
		endTime = end;
		duration = end.getTime() - start.getTime();
		priority = prio;
		profile = ElectricityProfileFactory.getProfile(0, duration, amplitude);
		profile.setMaxConsumption(amplitude);
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
	/**
	 * Clones an existing ElectricityRequirement.
	 * @param req The requirement to be cloned. 
	 */
	public ElectricityRequirement(ElectricityRequirement req) {
		device = ElectronicDeviceFactory.getDevice(req.getDeviceCode(),req.device.getId(),req.device.getConsumptionEnabled());
		startTime = DateHelper.clone(req.startTime);
		endTime = DateHelper.clone(req.endTime);
		duration = new Double(req.duration);
		priority = new DecimalRating(req.priority.getValue());
		profile = ElectricityProfileFactory.getProfile(req.getProfileCode(),duration, req.profile.getMaxConsumption());
		userID = UUID.fromString(req.getUserID());
		reqID = UUID.fromString(req.getId());
	}

	/**
	 * 
	 * Returns a new ElectricityRequirement based on the given parameters.
	 * @param start The start time of the requirement.
	 * @param end The end time of the requirement.
	 * @param prio A rating representing the importance of the requirement.
	 * @param amplitude The amplitude to set the device's profile to consume.
	 * @param iDUser The identity of the {@link UserAgent} that owns the requirement.
	 * @param d The {@link ElectronicConsumerDevice} that needs the requirement described in the associated instance of this object
	 */
	public ElectricityRequirement(Date start, Date end, DecimalRating prio, Double amplitude, String iDUser,
			ElectronicConsumerDevice d) {
		this(start, end, prio,  amplitude, iDUser, UUID.randomUUID().toString(), d);
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
	/**
	 * 
	 * @return true if the requirement has been modified since construction.
	 */
	public Boolean getTampered()
	{
		return tampered;
	}
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * Sets the start time to a given {@link Date}, keeps the duration constant.
	 * @param d The given {@link Date}.
	 */
	public void setStartTime(Date d)
	{
		startTime = new Date(d.getTime());
		endTime = DateHelper.dPlus(d, duration/QuantumNode.quanta);
		tampered = true;
	}
	/**
	 * Sets the start time to a given {@link Date}, changes the duration to the given double.
	 * @param d  The given {@link Date}.
	 * @param dur The desired duration in units of {@link QuantumNode} quanta.
	 */
	public void setStartTime(Date d, double dur)
	{
		startTime = new Date(d.getTime());
		duration = dur*QuantumNode.quanta;
		endTime = DateHelper.dPlus(d, duration/QuantumNode.quanta);
		tampered = true;
	}
	public double getMaxConsumption()
	{
		return profile.getMaxConsumption();
	}
	/**
	 * Gets the amplitude of the demand required at a given time.
	 * @param time The time in question.
	 * @return The amplitude required at that time.
	 */
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
	/**
	 * Sets the {@link ElectricityRequirement#device} to a given device. Also updates {@link ElectricityRequirement#profile} to the profile of the given device.
	 * @param d The given device.
	 */
	public void setDevice(ElectronicConsumerDevice d)
	{
		device = d;
		profile = d.getProfile();
	}
	public ElectronicConsumerDevice getDevice() {
		return device;
	}

}
