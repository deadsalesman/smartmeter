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

/**
 * Generic Database Manager Class
 * @author bwindo
 *
 */
public class DBManager implements DBManagerIFace {

	protected String addr;
	/**
	 * Creates a DBManager at the specified location.
	 * @param dbLocation The location the DBManager will be created at.
	 */
	public DBManager(String dbLocation)
	{
		addr = dbLocation;
		connectDB(addr);
	}
/**
 * Extracts all the data hosted in a ResultSet and place it in a LocalSet object.
 * @param res The ResultSet to extract data from.
 * @return The LocalSet containing the extracted information.
 */
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
	/**
	 * Performs an arbitrary SQL statement on the database. This should not be a SELECT statement as there will be no way of returning the data.
	 * Use DBManager#queryDB for information retrieval.
	 * @param fmt The format string containing the SQL instruction.
	 * @return Success?
	 */
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
	/**
	 * Performs an arbitrary sql statement and returns a LocalSet object containing any results of that statement. Most suitable for SELECT statements.
	 * @param fmt The format string containing the SQL instruction.
	 * @return The LocalSet containnig any results.
	 */
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
	/**
	 * Drops the hosted table.
	 * @param tableName The name of the table to drop.
	 * @return Success?
	 */
	public boolean dropTable(String tableName)
	{
		String fmt = "DROP TABLE " + tableName;
		return genericDBUpdate(fmt);
	}
	/**
	 * Connects to an addressed database. In theory. It looks to do nothing.
	 * @param addr The name of the database.
	 * @return The name of the database.
	 */
	public String connectDB(String addr) {
		try {
			Class.forName("org.sqlite.JDBC");
		}
		catch (Exception e){
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		return addr;
	}
	/**
	 * Connects to an existing database, returning a connection to that database.
	 * @return Connection the the existing database.
	 */
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
	/**
	 * Creates a table in the hosted database.
	 * @param tableName The name of the table to be created.
	 * @param fmt The statement to create a new table.
	 * @return Success?
	 */
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
	/**
	 * Inserts a value into the table.
	 * @param fmt The statement to insert a new value.
	 * @return Success?
	 */
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
    /**
     * Prints a given local set to the console.
     * @param res The LocalSet object to print.
     */
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
	/**
	 * Extracts a range of data lines from the table into a LocalSet.
	 * @param tableName The table to extract data from.
	 * @param upperBound The highest value line to extract data from.
	 * @param lowerBound The lowest value line to extract data from.
	 * @return The LocalSet containing the extracted data.
	 */
	@Override
	public LocalSet extractSelectedData(String tableName, int upperBound,
			int lowerBound) {
		String fmt = "SELECT * FROM " + tableName + " LIMIT " + (upperBound-lowerBound) + " OFFSET " + lowerBound + ";";
		LocalSet res = queryDB(fmt);
		return res;
	}
	

	/**
	 * Extracts all data from the table into a LocalSet.
	 * @param tableName The table to extract data from.
	 * @return The LocalSet containing the extracted data.
	 */
	@Override
	public LocalSet extractAllData(String tableName) {
		String fmt = "SELECT * FROM " + tableName  +";";
		LocalSet res = queryDB(fmt);
		return res;
	}
	/**
	 * Deletes a value from the table.
	 * @param fmt The statement to delete an old value.
	 * @return Success?
	 */
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
