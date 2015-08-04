package uk.ac.imperial.smartmeter.res;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class UserAgent implements UniqueIdentifierIFace {
	private Double socialWorth;
	private ElectricityGeneration generatedPower;
	private Double economicPower;
	private Double averageAllocation;
	private Map<ElectricityRequirement, ElectricityTicket> reqs;
	private UUID id;
	private String name;
	private String hash = "";
	private String salt = "decentralised";
	
	public UserAgent(String saltNew, String password, String userName, Double worth, Double generation, Double economic)
	{
		this(saltNew, generateHashCode(password,saltNew),UUID.randomUUID().toString(),userName,worth,generation,economic, 0.,null);
	}
	private static String generateHashCode(String password, String salt)
	{
		return Integer.toString(password.hashCode() ^ salt.hashCode());
	}
	public Map<ElectricityRequirement, ElectricityTicket> getReqs()
	{
		return reqs;
	}
	public UserAgent(String saltNew, String passwdHash, String idString, String username, Double worth, Double generation, Double economic, Double allocation, ArraySet<ElectricityRequirement> r)
	{
		name = username;
		salt = saltNew;
		hash = passwdHash;
		id = UUID.fromString(idString);
		socialWorth = worth;
		generatedPower = new ElectricityGeneration(generation);
		economicPower = economic;
		averageAllocation = allocation;
		if (r!= null)
		{
			for (ElectricityRequirement e: r)
			{
				reqs.put(e, null);
			}
		}
		else
		{
			reqs = new HashMap<ElectricityRequirement,ElectricityTicket>();
		}
	}
	public Boolean addReq(ElectricityRequirement req)
	{
		if (req.getUserID().equals(id.toString()))
		{
			reqs.put(req, null);
			return true;
		}
		else return false;
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
	@Override
	public String getId() {
		return id.toString();
	}
	public void setGeneratedPower(ElectricityGeneration e) {
		generatedPower = e;
	}
	public String getName() {
		return name;
	}
	public String getHash() {
		return hash;
	}
	public String getSalt() {
		return salt;
	}
	public boolean verify(String pass)
	{
		return (pass.hashCode() ^ salt.hashCode()) == Integer.parseInt(hash);
		
	}
}
