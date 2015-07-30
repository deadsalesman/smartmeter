package uk.ac.imperial.smartmeter.allocator;

import java.util.UUID;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.User;

public class UserAgent implements UniqueIdentifierIFace {
	private User user;
	private Double socialWorth;
	private ElectricityGeneration generatedPower;
	private ArraySet<ElectricityRequirement> reqs;
	private Double economicPower;
	private Double averageAllocation;
	private UUID id;
	
	public UserAgent(User u, Double worth, Double generation, Double economic, Double allocation)
	{
		this(u,worth,generation,(ArraySet<ElectricityRequirement>)null,economic,allocation);
	}
	public UserAgent(User u, Double worth, Double generation, ElectricityRequirement r, Double economic, Double allocation)
	{
		this(u,worth,generation,new ArraySet<ElectricityRequirement>(r),economic,allocation);
	}
	public UserAgent(User u, Double worth, Double generation, ArraySet<ElectricityRequirement> r, Double economic, Double allocation)
	{
		user = u;
		socialWorth = worth;
		generatedPower = new ElectricityGeneration(generation);
		reqs = r;
		economicPower = economic;
		averageAllocation = allocation;
		if (r!= null)
		{
			reqs = r;
		}
		else
		{
			reqs = new ArraySet<ElectricityRequirement>();
		}
		id = UUID.randomUUID();
	}
	public UserAgent(User u)
	{
		this(u,0.,0.,(ArraySet<ElectricityRequirement>)null,0.,0.);
	}
	public User getUser() {
		return user;
	}
	public Boolean addReq(ElectricityRequirement req)
	{
		if (req.getUserID().equals(user.getId()))
		{
			reqs.add(req);
			return true;
		}
		else return false;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Double getSocialWorth() {
		return socialWorth;
	}
	public void setSocialWorth(Double socialWorth) {
		this.socialWorth = socialWorth;
	}
	public ElectricityGeneration getGeneratedPower() {
		return generatedPower;
	}
	public Double getCurrentPower() {
		return generatedPower.getCurrentOutput();
	}
	public Double getMaxPower() {
		return generatedPower.getMaxOutput();
	}
	public Double getEconomicPower() {
		return economicPower;
	}
	public void setEconomicPower(Double economicPower) {
		this.economicPower = economicPower;
	}
	public Double getAverageAllocation() {
		return averageAllocation;
	}
	public void setAverageAllocation(Double averageAllocation) {
		this.averageAllocation = averageAllocation;
	}
	public void setReq(ElectricityRequirement e, int index)
	{
		reqs.set(e, index);
	}
	public ElectricityRequirement getReq(int index)
	{
		return reqs.get(index);
	}
	public ArraySet<ElectricityRequirement> getReqs() {
		return reqs;
	}
	public void setReqs(ArraySet<ElectricityRequirement> reqs) {
		this.reqs = reqs;
	}
	@Override
	public String getId() {
		return id.toString();
	}
	public void setGeneratedPower(ElectricityGeneration e) {
		generatedPower = e;
	}

}
