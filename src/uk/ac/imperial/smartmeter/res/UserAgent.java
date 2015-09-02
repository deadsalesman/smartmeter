package uk.ac.imperial.smartmeter.res;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

/** 
 * A class to model the properties of an individual user of the system.
 * @author bwindo
 *
 */
public class UserAgent implements UniqueIdentifierIFace {
	private Double socialWorth;
	private ElectricityGeneration generatedPower;
	private Double economicPower;
	private Double averageAllocation;
	/**
	 * A map to indicate which {@link ElectricityRequirement} objects correspond to which {@link ElectricityTicket} objects.
	 */
	private Map<ElectricityRequirement, ElectricityTicket> reqTktMap;
	/**
	 * A set of all the {@link ElectricityRequirement}s associated with the user.
	 */
	private ArraySet<ElectricityRequirement> reqs;
	private UUID id;
	private String name;
	private String hash = "";
	private String salt = "decentralised";
	private String pubKey;
	private String passwd;
	
	/**
	 * Generates a new UserAgent based on authentication details and the distibutive justice parameters.
	 * @param saltNew The salt to be cohashed with the password.
	 * @param userName The username to be used by the controller to represent the UserAgent.
	 * @param password The password associated with signing tickets and authenticating identity.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 */
	public UserAgent(String saltNew, String password, String userName, Double social, Double generation, Double economic)
	{
		this(saltNew, generateHashCode(password,saltNew),UUID.randomUUID().toString(),userName,"",social,generation,economic, 0.,(ArraySet<ElectricityRequirement>)null);
	}
	/**
	 *  Generates a new UserAgent based on authentication details and the distibutive justice parameters.
	 * @param saltNew The salt to be cohashed with the password.
	 * @param userName The username to be used by the controller to represent the UserAgent.
	 * @param password The password associated with signing tickets and authenticating identity.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 * @param allocation The weighting allocated to previous allocations for the user.
	 */
	public UserAgent(String saltNew, String password, String userName, Double social, Double generation, Double economic, Double allocation)
	{
		this(saltNew, generateHashCode(password,saltNew),UUID.randomUUID().toString(),userName,"",social,generation,economic, allocation,(ArraySet<ElectricityRequirement>)null);
	}
	/**
	 * An insecure implementation of password hashing.
	 * @param password The password to be hashed.
	 * @param salt The salt to be co-hashed.
	 * @return The hashed output.
	 */
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
	/**
	 * 
	 *  Generates a new UserAgent based on authentication details and the distibutive justice parameters.
	 * @param saltNew The salt to be cohashed with the password.
	 * @param passwdHash The hash of the password.
	 * @param idString The id of the new UserAgent.
	 * @param username The username to be used by the controller to represent the UserAgent.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 */
	public UserAgent(String saltNew, String passwdHash, String idString, String username, Double social, Double generation, Double economic)
	{
		this(saltNew, passwdHash,idString,username,"",social,generation,economic, 0.,(ArraySet<ElectricityRequirement>)null);
	}
	/**
	 *  Generates a new UserAgent based on authentication details and the distibutive justice parameters.
	 * @param saltNew The salt to be cohashed with the password.
	 * @param passwdHash The hash of the password.
	 * @param idString The id of the new UserAgent.
	 * @param username The username to be used by the controller to represent the UserAgent.
	 * @param pubkey The public key to be associated with the userAgent.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 */
	public UserAgent(String saltNew, String passwdHash, String idString, String username, String pubkey, Double social, Double generation, Double economic)
	{
		this(saltNew, passwdHash,idString,username,pubkey,social,generation,economic, 0.,(ArraySet<ElectricityRequirement>)null);
	}
	/**
	 *  Generates a new UserAgent based on authentication details and the distibutive justice parameters.
	 *  
	 * @param saltNew The salt to be cohashed with the password.
	 * @param passwdHash The hash of the password.
	 * @param idString The id of the new UserAgent.
	 * @param username The username to be used by the controller to represent the UserAgent.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 * @param allocation The weighting allocated to previous allocations for the user.
	 * @param r An {@link ElectricityRequirement} to be added to the user.
	 */
	public UserAgent(String saltNew, String passwdHash, String idString, String username, Double social, Double generation, Double economic, Double allocation, ElectricityRequirement r)
	{
		this(saltNew, passwdHash,UUID.randomUUID().toString(),username,"",social,generation,economic, allocation,new ArraySet<ElectricityRequirement>(r));
	}
	/**
	 *  Generates a new UserAgent based on authentication details and the distibutive justice parameters.
	 *  
	 * @param saltNew The salt to be cohashed with the password.
	 * @param passwdHash The hash of the password.
	 * @param idString The id of the new UserAgent.
	 * @param username The username to be used by the controller to represent the UserAgent.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 * @param allocation The weighting allocated to previous allocations for the user.
	 * @param r A set of {@link ElectricityRequirement}s to be added to the user.
	 */
	public UserAgent(String saltNew, String passwdHash, String idString, String username, Double social, Double generation, Double economic, Double allocation, ArraySet<ElectricityRequirement> r)
	{
		this( saltNew,  passwdHash,  idString,  username, "",  social,  generation,  economic,  allocation, r);
	}
	/**
	 * 
	 *  Generates a new UserAgent based on authentication details and the distibutive justice parameters.
	 *  
	 * @param saltNew The salt to be cohashed with the password.
	 * @param passwdHash The hash of the password.
	 * @param idString The id of the new UserAgent.
	 * @param username The username to be used by the controller to represent the UserAgent.
	 * @param pubkey The public key to be associated with the user.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 * @param allocation The weighting allocated to previous allocations for the user.
	 * @param r A set of {@link ElectricityRequirement}s to be added to the user.
	 */
	public UserAgent(String saltNew, String passwdHash, String idString, String username, String pubkey, Double social, Double generation, Double economic, Double allocation, ArraySet<ElectricityRequirement> r)
	{
		name = username;
		salt = saltNew;
		hash = passwdHash;
		id = UUID.fromString(idString);
		socialWorth = social;
		generatedPower = new ElectricityGeneration(generation);
		economicPower = economic;
		averageAllocation = allocation;
		pubKey = pubkey;

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
	public void setPass(String pass)
	{
		passwd = pass;
	}
	public String getPass()
	{
		return passwd;
	}
	/**
	 * 
	 * @return the number of tickets that have been associated and given to the user.
	 */
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
	/**
	 * Adds a {@link ElectricityRequirement} to the user's log of associated requirements.
	 * @param req The requirement to be added.
	 * @return Success?
	 */
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
	/**
	 * Verifies that the hash of the password is the same as the stored hash.
	 * @param pass The password to be hashed.
	 * @return Correct password?
	 */
	public boolean verify(String pass)
	{
		return (pass.hashCode() ^ salt.hashCode()) == Integer.parseInt(hash);
		
	}
	public ElectricityRequirement getReq(Integer index) {
		return reqs.get(index);
	}
	public String getPubKey() {
		return pubKey;
	}
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}
}
