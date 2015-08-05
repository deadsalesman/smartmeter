package uk.ac.imperial.smartmeter.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager implements DBManagerIFace {

	protected String addr;
	
	public DBManager(String dbLocation)
	{
		addr = dbLocation;
		connectDB(addr);
	}

	private LocalSet getLocalResultSet(ResultSet res)
 {
		LocalSet ret = null;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> row = null;

		try {
			ResultSetMetaData metaData = res.getMetaData();
			Integer columnCount = metaData.getColumnCount();

			while (res.next()) {
				row = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					row.put(metaData.getColumnName(i), res.getObject(i));
				}
				resultList.add(row);
			}
			ret = new LocalSet(resultList);
		} catch (SQLException e) {
			
		}
		return ret;

	}
	public boolean genericDBUpdate(String fmt) 
	{
		try(Connection conn = 
				getConnection();
		    Statement stmt = 
		    		conn.createStatement())
		{
			stmt.executeUpdate(fmt);
			return true;
		} catch (SQLException e) {
			//e.printStackTrace();
		}
		return false;
	}
	@Override
	public LocalSet queryDB(String fmt)
	{
		LocalSet ret = null;
		try(Connection conn = 
				getConnection();
		    Statement stmt = 
		    		conn.createStatement())
		{
			ret = getLocalResultSet(stmt.executeQuery(fmt));
		} catch (SQLException e) {
		}
		return ret;
	}
	public boolean dropTable(String tableName)
	{
		String fmt = "DROP TABLE " + tableName;
		return genericDBUpdate(fmt);
	}
	public String connectDB(String addr) {
		try {
			Class.forName("org.sqlite.JDBC");
		}
		catch (Exception e){
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		return addr;
	}
	
	@Override
	public Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(addr);
		}
		catch (Exception e){
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		return conn;
	}
	@Override
	public boolean createTable(String tableName, String fmt) {
		if (genericDBUpdate(fmt))
		{
			//System.out.println("Table created");
			return true;
		}
		else
		{
			//System.out.println("Table not created");
		
		}
		return false;
	}
	@Override
	public boolean insertValue(String tableName, String fmt) {
		if (genericDBUpdate(fmt))
		{
			//System.out.println("Value entered");
			return true;
		}
		else
		{
			//System.out.println("Value not entered");
		
		}
		return false;
	}
    
	@Override
	public void spamLocalSet(LocalSet res) {
		try {
			for (Map<String,Object> ls : res.data)
			{
				for (Object o : ls.values().toArray())
				System.out.println(o);
			}
			System.out.println("");
		}
		catch (NullPointerException e)
		{
			//System.out.println("No data present in ResultSet");
		}
		
	}

	@Override
	public LocalSet extractSelectedData(String tableName, int upperBound,
			int lowerBound) {
		String fmt = "SELECT * FROM " + tableName + " LIMIT " + (upperBound-lowerBound) + " OFFSET " + lowerBound + ";";
		LocalSet res = queryDB(fmt);
		return res;
	}
	


	@Override
	public LocalSet extractAllData(String tableName) {
		String fmt = "SELECT * FROM " + tableName  +";";
		LocalSet res = queryDB(fmt);
		return res;
	}

	@Override
	public boolean deleteValue(String tableName, String fmt) {
		if (genericDBUpdate(fmt))
		{
			//System.out.println("Value entered");
			return true;
		}
		else
		{
			//System.out.println("Value not entered");
		
		}
		return false;
	}
}
