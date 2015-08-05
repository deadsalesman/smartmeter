package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.UserAgent;

public class AgentDBManager extends IntegratedDBManager<UserAgent>{
	public AgentDBManager(String dbLocation) {

		super(dbLocation, primTable, primFmt);
	}
	private static String primTable = "AGENT_TABLE";
	private static String joinTable = primTable +", USER_TABLE WHERE USER_TABLE.ID = " + primTable+".USERID";
	private static String primFmt   = 
			"CREATE TABLE     " +  primTable + "("   +
			"ID     INT       PRIMARY KEY NOT NULL," +
	        "SALT   TEXT      NOT NULL,"             +
	        "HASH   TEXT      NOT NULL,"             +
	        "UUID   TEXT      NOT NULL,"             +
			"NAME   CHAR(50)  NOT NULL),"            +
			"SOCIAL REAL      NOT NULL,"             +
			"POWER  REAL      NOT NULL,"             +
			"ECON   REAL      NOT NULL,"             +
			"ALLOC  REAL      NOT NULL,"             +
			");"
			;
	
	@Override
	public boolean insertElement(UserAgent r) {
				String fmt = "INSERT INTO "+primTable+"(ID, SALT, HASH, UUID, NAME, SOCIAL, POWER, ECON, ALLOC) " + "VALUES ("
				+ r.getId().hashCode()     + ", '"
				+ r.getSalt()              + "', '"
				+ r.getHash()              + "', '"
				+ r.getId()                + "', '"
				+ r.getName()              + "', "
				+ r.getSocialWorth()       + ", "
				+ r.getMaxPower()          + ", " 
				+ r.getEconomicPower()     + ", " 
				+ r.getAverageAllocation() + ", "
				+ " );";
				
		return insertValue(primTable, fmt);
	}
	public boolean wipe()
	{
		return genericDBUpdate("DELETE FROM " + primTable);
	}
	@Override
	public ArrayList<UserAgent> extractAll() // T
	{
		LocalSet res = extractAllData(joinTable);
		return resToArray(res);
	}
	@Override
	public ArrayList<UserAgent> extractMultiple(int lower, int upper) // T
	{
		LocalSet res = extractSelectedData(joinTable, upper, lower);
		return resToArray(res);
	}
	@Override
	public UserAgent extractSingle(int index) // T
	{
		LocalSet res = extractSelectedData(joinTable, index + 1, index);
		return resToObject(res);
	}
	@Override
	public UserAgent formatMap(Map<String, Object> ls) {
		{
			UserAgent ret = new UserAgent(
					(String)ls.get("SALT"),
					ls.get("HASH").toString(),
					(String)ls.get("UUID"),
					(String)ls.get("NAME"),
					(Double)ls.get("SOCIAL"), 
					(Double)ls.get("POWER"), 
					(Double)ls.get("ECON"),
					(Double)ls.get("ALLOC"),
					(ArraySet<ElectricityRequirement>) null
					);
			return ret;
		}
	}
	@Override
	public void createTable(String tableName) throws SQLException {
		String fmt = "";
		boolean validTable = false;
		if (tableName == primTable)
		{
			fmt = primFmt;
			validTable = true;
		}
		if (validTable) {
			genericDBUpdate(fmt);
		}
		else
		{
			throw new SQLException("Invalid Table Name");
		}
	}

	@Override
	public boolean removeElement(UserAgent r) {
		String fmt = "DELETE FROM "+primTable+" WHERE ID = " + r.getId().hashCode() 
				+ " );";
		return deleteValue(primTable, fmt);
	}
}
