package uk.ac.imperial.smartmeter.db;

import java.sql.Connection;

public interface DBManagerIFace {
	public boolean createTable(String tableName, String fmt);
	public boolean insertValue(String tableName, String fmt);
	public boolean deleteValue(String tableName, String fmt);
	public LocalSet queryDB(String fmt);
	public String connectDB(String fmt);
	public void spamLocalSet(LocalSet res);
	public LocalSet extractSelectedData(String tableName, int upperBound, int lowerBound);
	public LocalSet extractAllData(String tableName);
	Connection getConnection();
}
