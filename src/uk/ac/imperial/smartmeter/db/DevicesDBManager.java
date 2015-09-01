package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.util.Map;

import uk.ac.imperial.smartmeter.electronicdevices.DeviceType;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDeviceFactory;
import uk.ac.imperial.smartmeter.impl.EDController;
/**
 * Helper class to handle manipulation of the devices database.
 * @author bwindo
 * @see EDController
 */
public class DevicesDBManager 
     extends IntegratedDBManager<ElectronicConsumerDevice>{
	/**
	 * Ctor that starts a database at the given location with the internally specified name and format.
	 * @param dbLocation The location of the database.
	 */
	public DevicesDBManager(String dbLocation) {
		super(dbLocation,primTable,primFmt);
		initialiseEnumTable();
	}
	private static String primTable = "DEVICE_TABLE";
	private static String enumTable = "ENUM_TABLE";
	
	private static String enumFmt   = 
			"CREATE TABLE     " +  enumTable + "("   +
			"ID     INT       PRIMARY KEY NOT NULL," +
			"NAME   CHAR(50)              NOT NULL);"
			;
	private static String primFmt = 
			"CREATE  TABLE     " + primTable    +  "("                                 +
			"ID      INT       PRIMARY KEY     NOT NULL,"                             +
			"STATE   INT                       NOT NULL CHECK (STATE IN (0,1)),"      +
			"TYPE    INT                       NOT NULL,"                             +
			"UUID    TEXT                      NOT NULL, "                            +
			"FOREIGN KEY(TYPE) REFERENCES      ENUM_TABLE(ID));"
			;
	/**
	 * Wipes everything from the database.
	 * @return Success?
	 */
	public boolean wipe()
	{
		return genericDBUpdate("DELETE FROM " + primTable);
	}
	/**
	 * Checks to see if the EnumTable has been created, does so if this is not the case.
	 * Checks to see if the EnumTable has been initialised, does so if this is not the case.
	 * @return Success?
	 */
	public boolean initialiseEnumTable()
	{
		LocalSet verifyEnumTable = queryDB("SELECT COUNT(*) FROM " + enumTable);
		int l = DeviceType.values().length;
		int count = -1;
		try {
			if (verifyEnumTable==null)
			{
				createTable(enumTable);
			}
			else
			{
			  count = (int)verifyEnumTable.data.get(0).values().toArray()[0]; //I am so sorry
			}
			if ((verifyEnumTable==null)||(count!=l))
			{
				for (DeviceType D: DeviceType.values())
				{
					String fmt = "INSERT INTO "+enumTable+"(ID,NAME) " + 
				                 "VALUES (" + D.ordinal() + ", '" + D.name() + "' );";
					insertValue(enumTable, fmt);
				}
			}
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Changes the recorded state of a device in the database.
	 * @param id The identity of the device to change the state of.
	 * @param state The new state of the device.
	 * @return Success?
	 */
	public boolean updateDeviceState(int id, boolean state)
	{
		int value = state ? 1 : 0;
		String stmt = "UPDATE " + primTable + " SET STATE = " + value + " WHERE ID = " + id + ";";
		return genericDBUpdate(stmt);
	}
	/**
	 * Creates a new table from a list of preapproved table names, which are mapped to a specific format of table and role.
	 * @param tableName The table type to create.
	 */
	public void createTable(String tableName) throws SQLException{
		String fmt = "";
		boolean validTable = false;
		if (tableName == enumTable)
		{
			fmt = enumFmt;
			validTable = true;
		}
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
	/**
	 * Inserts an ElectronicConsumerDevice into the table.
	 * @param r The new ElectronicConsumerDevice to insert
	 * @return Success?
	 */
	@Override
	public boolean insertElement(ElectronicConsumerDevice r) {
		int state = r.getConsumptionEnabled() ? 1 : 0;
		String fmt = "INSERT INTO "+primTable+"(ID, STATE, TYPE, UUID) " + "VALUES ("
				+ r.getId().hashCode() + ", " + state + ", " + r.getType().ordinal() + ", '" + r.getId().toString() + "' "
				+ " );";
	
		return insertValue(primTable, fmt);
	}
	/**
	 * Returns a ElectronicConsumerDevice from a given Map<String, Object> as returned by the database cursor.
	 * @param ls The local set returned after querying the database.
	 * @return The newly extracted ElectronicConsumerDevice.
	 */
	@Override
	public ElectronicConsumerDevice formatMap(Map<String, Object> ls) {
		ElectronicConsumerDevice ed = null;
		ed = (ElectronicConsumerDevice) ElectronicDeviceFactory.getDevice(
				(int)ls.get("TYPE"),
				(String)ls.get("UUID"),
				((int)ls.get("STATE")==1?true:false)
				);
		return ed;
	}

    /**
     * Removes a specific ElectronicConsumerDevice from the database.
     * @param r The ElectronicConsumerDevice to be removed.
     * @return Success?
     */
	@Override
	public boolean removeElement(ElectronicConsumerDevice r) {
		String fmt = "DELETE FROM "+primTable+" WHERE ID = " + r.getId().hashCode()
				+ ";";
	
		return deleteValue(primTable, fmt);
	}

}
