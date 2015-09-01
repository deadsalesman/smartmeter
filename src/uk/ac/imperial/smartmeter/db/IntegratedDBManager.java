package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
/**
 * Generic DataBaseManager that handles the storage of a specific type of object.
 * @author bwindo
 *
 * @param <T> The type of object to be stored.
 */
public abstract class IntegratedDBManager<T extends UniqueIdentifierIFace> extends DBManager {
	
	/**
	 * Initialises the IntegratedDBManager with a location, name of the table, and the format of the table construction statement.
	 * @param dbLocation  The location of the table.
	 * @param primaryTable The name of the table.
	 * @param primaryFormat A statement to create the table with a specified form.
	 */
	public IntegratedDBManager(String dbLocation, String primaryTable, String primaryFormat) {
		super(dbLocation);
		primTable = primaryTable;
		primFmt = primaryFormat;
		initialisePrimaryTable();
	}

	protected String primTable = null;
	protected String primFmt = null;

	/**
	 * Inserts an object into the database.
	 * @param r The object to insert.
	 * @return Success?
	 */
	public abstract boolean insertElement(T r);
	/**
	 * Removes an object from the database.
	 * @param r The object to remove.
	 * @return Success?
	 */
	public abstract boolean removeElement(T r);
	/**
	 * Returns a T object from a given Map<String, Object> as returned by the database cursor.
	 * @param ls The local set returned after querying the database.
	 * @return The newly extracted T.
	 */
	public abstract T formatMap(Map<String, Object> ls);

	/**
	 * Formats the first element of a LocalSet into an object of type T.
	 * @param res The LocalSet to format.
	 * @return The formatted object.
	 */
	public T resToObject(LocalSet res) // T
	{
		return formatMap(res.data.get(0));
	}

	public String getPrimTable() {
		return primTable;
	}
	/**
	 * Formats all elements of a LocalSet into an ArrayList<T>
	 * @param res
	 * @return The arrayList generated from the LocalSet.
	 */
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
    /**
     * Extracts all the data from the table.
     * @return An ArrayList<T> of all the formatted data.
     */
	public ArrayList<T> extractAll() // T
	{
		LocalSet res = extractAllData(primTable);
		return resToArray(res);
	}
	/**
	 * Extracts a range of objects, indexes given by the parameters lower and upper.
	 * @param lower The lower index of the table entries to be selected.
	 * @param upper The upper index of the table entries to be selected.
	 * @return The ArrayList containing the selected objects currently stored in the database.
	 */
	public ArrayList<T> extractMultiple(int lower, int upper) // T
	{
		LocalSet res = extractSelectedData(primTable, upper, lower);
		return resToArray(res);
	}
	/**
	 * Extracts a single object from the database.
	 * @param index The index of the user to extract.
	 * @return The object extracted.
	 */
	public T extractSingle(int index) // T
	{
		LocalSet res = extractSelectedData(primTable, index + 1, index);
		return resToObject(res);
	}

	public abstract void createTable(String tableName) throws SQLException;
	/**
	 * Checks to see if the table has been created, creates it if it has not been.
	 * @return Success?
	 */
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
	/**
	 * Drops the hosted table.
	 * @return Success?
	 */
	public boolean dropHostedTable()
	{
		return dropTable(primTable);
	}
}
