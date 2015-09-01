package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.util.Map;

import uk.ac.imperial.smartmeter.res.User;


/**
 * @deprecated
 * @author bwindo
 *
 */
public class UserDBManager extends IntegratedDBManager<User>{
	public UserDBManager(String dbLocation) {

		super(dbLocation, primTable, primFmt);
	}
	private static String primTable = "USER_TABLE";
	private static String primFmt   = 
			"CREATE TABLE     " +  primTable + "("   +
			"ID     INT       PRIMARY KEY NOT NULL," +
			"SALT   INT       NOT NULL,"             +
			"HASH   INT       NOT NULL,"             +
			"UUID   TEXT      NOT NULL,"             +
			"NAME   CHAR(50)  NOT NULL);"
			;
	
	public User formatMap(Map<String,Object> ls) 
	{
		User u = new User(
				);
		return u;
	}
	public void createTable(String tableName) throws SQLException{  
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
	public boolean insertElement(User r) {
		String fmt = "INSERT INTO "+primTable+"(ID, SALT, HASH, UUID, NAME) " + "VALUES ("
				+ r.getId().hashCode() + ", '" + r.getSalt() + "', '" + r.getHash() 
				+ "', '" + r.getId() + "', '" + r.getName() + "' "
				+ " );";
		return insertValue(primTable, fmt);
	}
	@Override
	public boolean removeElement(User r) {
		String fmt = "DELETE FROM "+primTable+" WHERE ID = " + r.getId().hashCode()
				+ " ;";
		return deleteValue(primTable, fmt);
	}
	
}
