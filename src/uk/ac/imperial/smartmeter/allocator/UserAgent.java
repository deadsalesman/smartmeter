package uk.ac.imperial.smartmeter.allocator;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.User;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class UserAgent implements UniqueIdentifierIFace {
	private User user;
	private Double socialWorth;
	private Double generatedPower;
	private ArraySet<ElectricityRequirement> reqs;
	private Double economicPower;
	private Double averageAllocation;
	public User getUser() {
		return user;
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
	public Double getGeneratedPower() {
		return generatedPower;
	}
	public void setGeneratedPower(Double generatedPower) {
		this.generatedPower = generatedPower;
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
		return user.getId();
	}

}
