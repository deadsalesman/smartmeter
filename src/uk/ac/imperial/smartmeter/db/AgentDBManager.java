package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import uk.ac.imperial.smartmeter.impl.HLController;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.UserAgent;
/**
 * Helper class to handle manipulation of the user database.
 * @author bwindo
 * @see HLController
 */
public class AgentDBManager extends IntegratedDBManager<UserAgent>{
	/**
	 * Ctor that starts a database at the given location with the internally specified name and format.
	 * @param dbLocation The location of the database.
	 */
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
	
	/** 
	 * Adds a user to the database.
	 * @param r The user to be inserted into the database.
	 * @return Success?
	 */

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
	/**
	 * Wipes everything from the database.
	 * @return Success?
	 */
	public boolean wipe()
	{
		return genericDBUpdate("DELETE FROM " + primTable);
	}
	/**
	 * Returns an ArrayList of every user currently stored in the database.
	 * @return The ArrayList containing every user currently stored in the database.
	 */
	@Override
	public ArrayList<UserAgent> extractAll() // T
	{
		LocalSet res = extractAllData(primTable);
		return resToArray(res);
	}
	/**
	 * Extracts a range of users, indexes given by the parameters lower and upper.
	 * @param lower The lower index of the table entries to be selected.
	 * @param upper The upper index of the table entries to be selected.
	 * @return The ArrayList containing the selected users currently stored in the database.
	 */
	@Override
	public ArrayList<UserAgent> extractMultiple(int lower, int upper) // T
	{
		LocalSet res = extractSelectedData(primTable, upper, lower);
		return resToArray(res);
	}
	/**
	 * Extracts a single user from the database.
	 * @param index The index of the user to extract.
	 * @return The user extracted.
	 */
	@Override
	public UserAgent extractSingle(int index) // T
	{
		LocalSet res = extractSelectedData(primTable, index + 1, index);
		return resToObject(res);
	}
	/**
	 * Returns a UserAgent from a given Map<String, Object> as returned by the database cursor.
	 * @param ls The local set returned after querying the database.
	 * @return The newly extracted UserAgent.
	 */
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
	/**
	 * Creates a new table from a list of preapproved table names, which are mapped to a specific format of table and role.
	 * @param tableName The table type to create.
	 */
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
    /**
     * Removes a specific user from the database.
     * @param r The UserAgent to be removed.
     * @return Success?
     */
	@Override
	public boolean removeElement(UserAgent r) {
		String fmt = "DELETE FROM "+primTable+" WHERE ID = " + r.getId().hashCode() 
				+ " );";
		return deleteValue(primTable, fmt);
	}
}
