package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.electricityprofile.ConsumptionProfile;
import uk.ac.imperial.smartmeter.electricityprofile.ProfileList;
import uk.ac.imperial.smartmeter.impl.HLController;
import uk.ac.imperial.smartmeter.impl.LController;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
/**
 * Helper class to handle manipulation of the ElectricityRequirement database.
 * @author bwindo
 * @see HLController
 * @see LController
 */
public class ReqsDBManager extends IntegratedDBManager<ElectricityRequirement>{
	/**
	 * Ctor that starts a database at the given location with the internally specified name and format.
	 * @param dbLocation The location of the database.
	 */
	public ReqsDBManager(String dbLocation) {
		super(dbLocation,primTable,primFmt);
	    initialiseProfileTable();
	}
	private static String primTable = "REQUIREMENT_TABLE";
	private static String profileTable = "PROFILE_TABLE";

	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private static String profileFmt   = 
			"CREATE TABLE     " +  profileTable + "("   +
			"ID     INT       PRIMARY KEY NOT NULL," +
			"NAME   CHAR(50)              NOT NULL);"
			;
	private static String primFmt = 
			"CREATE    TABLE    " + primTable    +  "("                                 +
			"REQID     INT          PRIMARY KEY  NOT NULL,"                             +
			"START     CHAR(20)                  NOT NULL,"                             +
			"END       CHAR(20)                  NOT NULL,"                             +
			"USERID    TEXT                      NOT NULL, "                            +
			"UUID      TEXT                      NOT NULL, "                            +
			"PRIORITY  INT                       NOT NULL, "                            +
			"PROFILE   INT 		                 NOT NULL, "                            +
			"AMPLITUDE REAL                      NOT NULL,  "                           +
			"FOREIGN   KEY(PROFILE) REFERENCES    PROFILE_TABLE(ID));"/*,"		        +
			"FOREIGN   KEY(USERID)  REFERENCES    USER_TABLE(ID));"*/
			;
	/**
	 * Wipes everything from the database.
	 * @return Success?
	 */
	public boolean wipe()
	{
		return genericDBUpdate("DELETE FROM " + primTable);
	}
	/**
	 * Checks to see if the ProfileTable has been created, does so if this is not the case.
	 * Checks to see if the ProfileTable has been initialised, does so if this is not the case.
	 * @return Success?
	 */
	public boolean initialiseProfileTable()
	{
		LocalSet verifyProfileTable = queryDB("SELECT COUNT(*) FROM " + profileTable);
		int l = ProfileList.getLength();
		int count = -1;
		try {
			if (verifyProfileTable==null)
			{
				createTable(profileTable);
			}
			else
			{
			  count = (int)verifyProfileTable.data.get(0).values().toArray()[0]; //I am so sorry
			}
			if ((verifyProfileTable==null)||(count!=l))
			{
				for (Entry<Integer, Class<? extends ConsumptionProfile>> m : ProfileList.profileMap.entrySet())
				{
					String fmt = "INSERT INTO "+profileTable+"(ID,NAME) " + 
				                 "VALUES (" + m.getKey() + ", '" + m.getValue().getName() + "' );";
					insertValue(profileTable, fmt);
				}
			}
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/** 
	 * Adds a ElectricityRequirement to the database.
	 * @param r The ElectricityRequirement to be inserted into the database.
	 * @return Success?
	 */

	public boolean insertElement(ElectricityRequirement r) {
		String fmt = "INSERT INTO "+primTable+"(REQID, START, END, USERID, UUID, PRIORITY, PROFILE, AMPLITUDE) " + "VALUES ("
				+ r.getId().hashCode() + ", '" + df.format(r.getStartTime()) + "', '" + df.format(r.getEndTime()) 
				+ "', '" + r.getUserID() + "' "+ ", '" + r.getId() + "', "+ r.getPriority() + ", "
				+ r.getProfileCode() + ", " + r.getMaxConsumption()
				+ " );";
		return insertValue(primTable, fmt);
	}
	
	/**
	 * Returns a ElectricityRequirement from a given Map<String, Object> as returned by the database cursor.
	 * @param ls The local set returned after querying the database.
	 * @return The newly extracted ElectricityRequirement.
	 */
	@Override
	public ElectricityRequirement formatMap(Map<String,Object> ls)
	{
		ElectricityRequirement er = null;
		try{
		er = new ElectricityRequirement(
				df.parse((String)ls.get("START")),
				df.parse((String)ls.get("END")),
				new DecimalRating((int)ls.get("PRIORITY")),
				(int)ls.get("PROFILE"),
				(Double)ls.get("AMPLITUDE"),
				(String)ls.get("USERID"),
				(String)ls.get("UUID")
				);
		}
		catch(ParseException e)
		{
			
		}
		return er;
	}
	/**
	 * Creates a new table from a list of preapproved table names, which are mapped to a specific format of table and role.
	 * @param tableName The table type to create.
	 */
	@Override
	public void createTable(String tableName) throws SQLException{
		String fmt = "";
		boolean validTable = false;
		if (tableName == profileTable)
		{
			fmt = profileFmt;
			validTable = true;
		}
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
     * Removes a specific ElectricityRequirement from the database.
     * @param r The ElectricityRequirement to be removed.
     * @return Success?
     */
	@Override
	public boolean removeElement(ElectricityRequirement r) {
		String fmt = "DELETE FROM "+primTable+" WHERE REQID = " + r.getId().hashCode()
				+ " ;";
		return insertValue(primTable, fmt);
	}
}
