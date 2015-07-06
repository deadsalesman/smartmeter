package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import uk.ac.imperial.smartmeter.res.User;

public class UserDBManager extends DBManager{
	public UserDBManager(String dbLocation) {
		super(dbLocation);
		initialiseUserTable();
	}
	private final String userTable = "USER_TABLE";
	private String profileFmt   = 
			"CREATE TABLE     " +  userTable + "("   +
			"ID     INT       PRIMARY KEY NOT NULL," +
			"SALT   INT       NOT NULL,"             +
			"HASH   INT       NOT NULL,"             +
			"UUID   TEXT      NOT NULL,"             +
			"NAME   CHAR(50)  NOT NULL);"
			;
	
	public boolean insertRequirement(User r) {
		String fmt = null;
		return insertValue(userTable, fmt);
	}
	private User formatMap(Map<String,Object> ls)
	{
		User u = new User(
				(String)ls.get("SALT"),
				(String)ls.get("HASH"),
				(String)ls.get("UUID"),
				(String)ls.get("NAME")
				);
		return u;
	}
	public User resToUser(LocalSet res)
	{
		return formatMap(res.data.get(0));
	}
	public ArrayList<User> resToUserArray(LocalSet res)
	{
		ArrayList<User> array = new ArrayList<User>();
		try {
			for (Map<String,Object> ls : res.data)
			{
					array.add(formatMap(ls));
			}
		} catch (NullPointerException e) {

		}
		return array;
		
	}
	public ArrayList<User> extractAll()
	{
		LocalSet res = extractAllData(userTable);
		return resToUserArray(res);
	}
	public ArrayList<User> extractMultipleUsers(int lower,int upper)
	{
		LocalSet res = extractSelectedData(userTable,upper,lower);
		return resToUserArray(res);
	}
	public User extractSingleUser(int index)
	{
		LocalSet res = extractSelectedData(userTable,index+1,index);
		return resToUser(res);
	}
	public void createTable(String tableName) throws SQLException{
		String fmt = "";
		boolean validTable = false;
		switch(tableName){
		case userTable:
			fmt = profileFmt;
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
	
	public boolean initialiseUserTable()
	{
		LocalSet verifyProfileTable = queryDB("SELECT COUNT(*) FROM " + userTable);
		if (verifyProfileTable==null)
		{
			try {
				createTable(userTable);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
		
	}
}
