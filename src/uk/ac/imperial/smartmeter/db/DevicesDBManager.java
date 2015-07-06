package uk.ac.imperial.smartmeter.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import uk.ac.imperial.smartmeter.impl.ElectronicDevice;
import uk.ac.imperial.smartmeter.res.DeviceType;

public class DevicesDBManager 
     extends DBManager{
	public DevicesDBManager(String dbLocation) {
		super(dbLocation);
		initialiseEnumTable();
	    initialiseDeviceTable();
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

	public boolean initialiseDeviceTable()
	{
		LocalSet verifyEnumTable = queryDB("SELECT COUNT(*) FROM " + devTable);
		if (verifyEnumTable==null)
		{
			try {
				createTable(devTable);
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
		
	}
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
	
		return insertValue(devTable, fmt);
	}
	public ElectronicDevice ResToED(LocalSet res)
	{
		ElectronicDevice ed = null;
		ed = new ElectronicDevice((boolean)res.data.get(0).get("STATE"),(int)res.data.get(0).get("TYPE"),(String)res.data.get(0).get("UUID"));
		return ed;
	}
	public ArrayList<ElectronicDevice> ResToEDArray(LocalSet res)
	{
		ArrayList<ElectronicDevice> array = new ArrayList<ElectronicDevice>();
		try {
			for (Map<String,Object> ls : res.data)
			{
				array.add(new ElectronicDevice(((int)ls.get("STATE")==1?true:false),(int)ls.get("TYPE"),(String)ls.get("UUID")));
			}
		} catch (NullPointerException e) {

		}
		return array;
		
	}
	public boolean updateDeviceState(int id, boolean state)
	{
		int value = state ? 1 : 0;
		String stmt = "UPDATE " + devTable + " SET STATE = " + value + " WHERE ID = " + id + ";";
		return genericDBUpdate(stmt);
	}
	public ArrayList<ElectronicDevice> extractAllDevices()
	{
		LocalSet res = extractAllData(devTable);
		return ResToEDArray(res);
	}
	public ArrayList<ElectronicDevice> extractMultipleDevices(int lower,int upper)
	{
		LocalSet res = extractSelectedData(devTable,upper,lower);
		return ResToEDArray(res);
	}
	public ElectronicDevice extractSingleDevice(int index)
	{
		LocalSet res = extractSelectedData(devTable,index+1,index);
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
