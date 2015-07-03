package uk.ac.imperial.smartmeter.db;

import java.sql.*;

public class DBManager implements DBManagerIFace {

	protected Connection conn = null;
	protected Statement stmt = null;
	protected String addr;
	
	public DBManager(String dbLocation)
	{
		addr = dbLocation;
		connectDB(addr);
	}

	public boolean main(String[] args)
	{
		return connectDB(addr);
	}
	public void genericDBUpdate(String fmt) throws SQLException
	{
			stmt = conn.createStatement();
			stmt.executeUpdate(fmt);
	}
	@Override
	public ResultSet queryDB(String fmt)
	{
		ResultSet ret = null;
		try {
			stmt = conn.createStatement();
			ret = stmt.executeQuery(fmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return ret;
	}

	
	@Override
	public boolean connectDB(String addr) {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(addr);
		}
		catch (Exception e){
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      return false;
		}
		return true;
	}
	@Override
	public boolean createTable(String tableName, String fmt) {
		try {
			genericDBUpdate(fmt);
			System.out.println("Table created");
			return true;
		} catch (SQLException e) {
			System.out.println("Table not created");
			return false;
		}

	}
	@Override
	public boolean insertValue(String tableName, String fmt) {
		try {
			genericDBUpdate(fmt);
			System.out.println("Value created");
			return true;
		} catch (SQLException e) {
			System.out.println("Value not created");
			return false;
		}

	}
    
	@Override
	public void spamResultSet(ResultSet res) {
		try {
			int n = res.getMetaData().getColumnCount();
			while (res.next()) {
				for (int i = 1; i <= n; i++) {
					System.out.print(res.getString(i));
					System.out.print(" ");
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			System.out.println("No data present in ResultSet");
		}
		
	}

	@Override
	public ResultSet extractSelectedData(String tableName, int upperBound,
			int lowerBound) {
		String fmt = "SELECT * FROM " + tableName + " LIMIT " + (upperBound-lowerBound) + " OFFSET " + lowerBound + ";";
		ResultSet res = queryDB(fmt);
		return res;
	}
	
	protected void finalise() throws Throwable
	{
		try{
			conn.close();
		}
		finally{
			super.finalize();
		}
	}

	@Override
	public ResultSet extractAllData(String tableName) {
		String fmt = "SELECT * FROM " + tableName  +";";
		ResultSet res = queryDB(fmt);
		return res;
	}
}
