package uk.ac.imperial.smartmeter.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import uk.ac.imperial.smartmeter.impl.ElectronicDevice;
import uk.ac.imperial.smartmeter.res.DeviceType;

public class DevicesDBManager 
     extends DBManager
	 implements DBManagerIFace{
	public DevicesDBManager(String dbLocation) {
		super(dbLocation);
		initialiseDeviceEnumTable();
	}
	private final String devTable = "DEVICE_TABLE";
	private final String enumTable = "ENUM_TABLE";
	
	private String enumFmt   = 
			"CREATE TABLE     " +  enumTable + "("   +
			"ID     INT       PRIMARY KEY NOT NULL," +
			"NAME   CHAR(50)              NOT NULL);"
			;
	private String deviceFmt = 
			"CREATE  TABLE     " + devTable    +  "("                                 +
			"ID      INT       PRIMARY KEY     NOT NULL,"                             +
			"STATE   INT                       NOT NULL CHECK (STATE IN (0,1)),"      +
			"TYPE    INT                       NOT NULL,"                             +
			"UUID    TEXT                      NOT NULL, "                            +
			"FOREIGN KEY(TYPE) REFERENCES      ENUM_TABLE(ID));"
			;

	public boolean initialiseDeviceEnumTable()
	{
		ResultSet verifyEnumTable = queryDB("SELECT COUNT(*) FROM " + enumTable);
		int l = DeviceType.values().length;
		int count = -1;
		try {
			if (verifyEnumTable==null)
			{
				createTable(enumTable);
			}
			else
			{
			  count = verifyEnumTable.getInt(1);
			}
			if ((verifyEnumTable==null)||(count!=l))
			{
				System.out.println("filling table");
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

	public boolean insertDevice(ElectronicDevice ed) {
		int state = ed.getState() ? 1 : 0;
		String fmt = "INSERT INTO "+devTable+"(ID, STATE, TYPE, UUID) " + "VALUES ("
				+ ed.getId().hashCode() + ", " + state + ", " + ed.getType().ordinal() + ", '" + ed.getId().toString() + "' "
				+ " );";
		if (!insertValue(devTable, fmt)) {
			try {
				createTable(devTable);
				insertDevice(ed);
				return true;
			} catch (SQLException e) {
				return false;
			}

		}
		return true;
	}
	public ElectronicDevice ResToED(ResultSet res)
	{
		ElectronicDevice ed = null;
		try {
			ed = new ElectronicDevice(res.getBoolean("STATE"),res.getInt("TYPE"),res.getString("UUID"));
		} catch (SQLException e) {
		}
		return ed;
		
	}
	public ArrayList<ElectronicDevice> ResToEDArray(ResultSet res)
	{
		ArrayList<ElectronicDevice> array = new ArrayList<ElectronicDevice>();
		try {
			while(res.next())
			{
				array.add(ResToED(res));
			}
		} catch (SQLException e) {
		}
		return array;
		
	}
	public ArrayList<ElectronicDevice> extractAllDevices()
	{
		ResultSet res = extractAllData(devTable);
		return ResToEDArray(res);
	}
	public ArrayList<ElectronicDevice> extractMultipleDevices(int lower,int upper)
	{
		ResultSet res = extractSelectedData(devTable,upper,lower);
		return ResToEDArray(res);
	}
	public ElectronicDevice extractSingleDevice(int index)
	{
		ResultSet res = extractSelectedData(devTable,index+1,index);
		return ResToED(res);
	}
	public void createTable(String tableName) throws SQLException{
		String fmt = "";
		boolean validTable = false;
		switch(tableName){
		case enumTable:
			fmt = enumFmt;
			validTable = true;
			break;
		case devTable:
			fmt = deviceFmt;
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

}
