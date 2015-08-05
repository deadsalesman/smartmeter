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
	private Map<ElectricityRequirement, ElectricityTicket> reqTktMap;
	private ArraySet<ElectricityRequirement> reqs;
	private UUID id;
	private String name;
	private String hash = "";
	private String salt = "decentralised";
	
	public UserAgent(String saltNew, String password, String userName, Double worth, Double generation, Double economic)
	{
		this(saltNew, generateHashCode(password,saltNew),UUID.randomUUID().toString(),userName,worth,generation,economic, 0.,(ArraySet<ElectricityRequirement>)null);
	}
	public UserAgent(String saltNew, String password, String userName, Double worth, Double generation, Double economic, Double allocation)
	{
		this(saltNew, generateHashCode(password,saltNew),UUID.randomUUID().toString(),userName,worth,generation,economic, allocation,(ArraySet<ElectricityRequirement>)null);
	}
	private static String generateHashCode(String password, String salt)
	{
		return Integer.toString(password.hashCode() ^ salt.hashCode());
	}
	public Map<ElectricityRequirement, ElectricityTicket> getReqTktMap()
	{
		return reqTktMap;
	}
	public ArraySet<ElectricityRequirement> getReqs()
	{
		return reqs;
	}
	public UserAgent(String saltNew, String passwdHash, String idString, String username, Double social, Double generation, Double economic)
	{
		this(saltNew, passwdHash,idString,username,social,generation,economic, 0.,(ArraySet<ElectricityRequirement>)null);
	}
	
	public UserAgent(String saltNew, String passwdHash, String idString, String username, Double social, Double generation, Double economic, Double allocation, ElectricityRequirement r)
	{
		this(saltNew, passwdHash,UUID.randomUUID().toString(),username,social,generation,economic, allocation,new ArraySet<ElectricityRequirement>(r));
	}
	public UserAgent(String saltNew, String passwdHash, String idString, String username, Double social, Double generation, Double economic, Double allocation, ArraySet<ElectricityRequirement> r)
	{
		name = username;
		salt = saltNew;
		hash = passwdHash;
		id = UUID.fromString(idString);
		socialWorth = social;
		generatedPower = new ElectricityGeneration(generation);
		economicPower = economic;
		averageAllocation = allocation;

		reqTktMap = new HashMap<ElectricityRequirement,ElectricityTicket>();
		if (r!= null)
		{
			reqs = r;
			for (ElectricityRequirement e: r)
			{
				reqTktMap.put(e, null);
			}
		}
		else
		{
			reqs = new ArraySet<ElectricityRequirement>();
		}
	}
	public Integer countTkts()
	{
		//Cannot simply return the count of the tickets, as some tickets are null.
		Integer ret = 0;
		for (ElectricityTicket tkt : reqTktMap.values())
		{
			ret += (tkt==null)? 0 : 1;
		}
		return ret;
	}
	public Boolean addReq(ElectricityRequirement req)
	{
		if (req.getUserID().equals(id.toString()))
		{
			reqTktMap.put(req, null);
			reqs.add(req);
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
	public ElectricityRequirement getReq(Integer index) {
		return reqs.get(index);
	}
}
