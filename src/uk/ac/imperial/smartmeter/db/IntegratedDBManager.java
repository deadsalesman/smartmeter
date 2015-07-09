package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public abstract class IntegratedDBManager<T extends UniqueIdentifierIFace> extends DBManager {
	
	public IntegratedDBManager(String dbLocation, String primaryTable, String primaryFormat) {
		super(dbLocation);
		primTable = primaryTable;
		primFmt = primaryFormat;
		initialisePrimaryTable();
	}

	protected String primTable = null;
	protected String primFmt = null;

	public abstract boolean insertElement(T r);

	public abstract T formatMap(Map<String, Object> ls);

	public T resToObject(LocalSet res) // T
	{
		return formatMap(res.data.get(0));
	}

	public String getPrimTable() {
		return primTable;
	}

	public ArrayList<T> resToArray(LocalSet res) // T
	{
		ArrayList<T> array = new ArrayList<T>();
		try {
			for (Map<String, Object> ls : res.data) {
				array.add(formatMap(ls));
			}
		} catch (NullPointerException e) {

		}
		return array;

	}

	public ArrayList<T> extractAll() // T
	{
		LocalSet res = extractAllData(primTable);
		return resToArray(res);
	}

	public ArrayList<T> extractMultiple(int lower, int upper) // T
	{
		LocalSet res = extractSelectedData(primTable, upper, lower);
		return resToArray(res);
	}

	public T extractSingle(int index) // T
	{
		LocalSet res = extractSelectedData(primTable, index + 1, index);
		return resToObject(res);
	}

	public abstract void createTable(String tableName) throws SQLException;

	public boolean initialisePrimaryTable() // T
	{
		LocalSet verifyProfileTable = queryDB("SELECT COUNT(*) FROM "
				+ primTable);
		if (verifyProfileTable == null) {
			try {
				createTable(primTable);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;

	}

}
