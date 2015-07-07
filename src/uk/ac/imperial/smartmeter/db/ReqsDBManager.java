package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.res.ConsumptionProfile;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ProfileList;

public class ReqsDBManager extends DBManager{

	
	public ReqsDBManager(String dbLocation) {
		super(dbLocation);
		initialiseProfileTable();
	    initialiseReqTable();
	}
	private final String reqTable = "REQUIREMENT_TABLE";
	private final String profileTable = "PROFILE_TABLE";
	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	private String profileFmt   = 
			"CREATE TABLE     " +  profileTable + "("   +
			"ID     INT       PRIMARY KEY NOT NULL," +
			"NAME   CHAR(50)              NOT NULL);"
			;
	private String reqFmt = 
			"CREATE    TABLE    " + reqTable    +  "("                                  +
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

	public boolean initialiseReqTable()
	{
		LocalSet verifyProfileTable = queryDB("SELECT COUNT(*) FROM " + reqTable);
		if (verifyProfileTable==null)
		{
			try {
				createTable(reqTable);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
		
	}
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
				System.out.println("filling table");
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

	public boolean insertRequirement(ElectricityRequirement r) {
		String fmt = "INSERT INTO "+reqTable+"(REQID, START, END, PRIORITY, PROFILE, AMPLITUDE, USERID, UUID) " + "VALUES ("
				+ r.getId().hashCode() + ", '" + df.format(r.getStartTime()) + "', '" + df.format(r.getEndTime()) 
				+ "', " + r.getPriority() + ", " + r.getProfileCode() + ", " + r.getMaxConsumption()
				+ ", '" + r.getUserID() + "' "+ ", '" + r.getId() + "' "
				+ " );";
		return insertValue(reqTable, fmt);
	}
	
	private ElectricityRequirement formatMap(Map<String,Object> ls)
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
	public ElectricityRequirement ResToReq(LocalSet res)
	{
		return formatMap(res.data.get(0));
	}
	public ArrayList<ElectricityRequirement> resToReqArray(LocalSet res)
	{
		ArrayList<ElectricityRequirement> array = new ArrayList<ElectricityRequirement>();
		try {
			for (Map<String,Object> ls : res.data)
			{
					array.add(formatMap(ls));
			}
		} catch (NullPointerException e) {

		}
		return array;
		
	}
	public ArrayList<ElectricityRequirement> extractAll()
	{
		LocalSet res = extractAllData(reqTable);
		return resToReqArray(res);
	}
	public ArrayList<ElectricityRequirement> extractMultipleReqs(int lower,int upper)
	{
		LocalSet res = extractSelectedData(reqTable,upper,lower);
		return resToReqArray(res);
	}
	public ElectricityRequirement extractSingleReq(int index)
	{
		LocalSet res = extractSelectedData(reqTable,index+1,index);
		return ResToReq(res);
	}
	public void createTable(String tableName) throws SQLException{
		String fmt = "";
		boolean validTable = false;
		switch(tableName){
		case profileTable:
			fmt = profileFmt;
			validTable = true;
			break;
		case reqTable:
			fmt = reqFmt;
			validTable = true;
			break;
		}
		if (validTable) {
			genericDBUpdate(fmt);
		}
		else
		{
			throw new SQLException("Invalid Table Name");
		}
	}

}


