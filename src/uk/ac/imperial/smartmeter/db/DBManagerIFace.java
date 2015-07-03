package uk.ac.imperial.smartmeter.db;

import java.sql.ResultSet;

public interface DBManagerIFace {
	public boolean createTable(String tableName, String fmt);
	public boolean insertValue(String tableName, String fmt);
	public ResultSet queryDB(String fmt);
	public boolean connectDB(String fmt);
	public void spamResultSet(ResultSet res);
	public ResultSet extractSelectedData(String tableName, int upperBound, int lowerBound);
	public ResultSet extractAllData(String tableName);
}
